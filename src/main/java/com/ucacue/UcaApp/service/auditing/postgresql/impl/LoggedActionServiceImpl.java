package com.ucacue.UcaApp.service.auditing.postgresql.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.model.dto.auditing.AuditLogPageDto;
import com.ucacue.UcaApp.service.auditing.postgresql.LoggedActionService;

@Service
public class LoggedActionServiceImpl implements LoggedActionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String LOGGED_ACTIONS_COLUMNS =
            "event_id, schema_name, table_name, relid, session_user_name, " +
            "action_tstamp_tx, action_tstamp_stm, action_tstamp_clk, transaction_id, " +
            "application_name, client_addr::text AS client_addr, client_port, " +
            "client_query, action, row_data, changed_fields, statement_only";

    // Obtener una accion por id
    @Override
    public Map<String, Object> findById(Long id) {
        String sql = "SELECT " + LOGGED_ACTIONS_COLUMNS + " FROM audit.logged_actions WHERE event_id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    // Listar todas las tablas en la base de datos
    @Override
    public List<Map<String, Object>> listTables() {
        String sql = "SELECT table_schema, table_name " +
                     "FROM information_schema.tables " +
                     "WHERE table_catalog = 'ucaapp' " +
                     "AND table_type = 'BASE TABLE' " +
                     "AND table_schema NOT IN ('pg_catalog', 'information_schema') " +
                     "ORDER BY table_schema, table_name";
        return jdbcTemplate.queryForList(sql);
    }

    // Listado paginado con filtros opcionales (rango de fechas, tabla, accion, busqueda global)
    @Override
    public AuditLogPageDto findPaged(int page, int size, LocalDate from, LocalDate to,
                                     String table, String action, String search) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 50 : Math.min(size, 500);

        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (from != null) {
            where.append(" AND action_tstamp_tx >= ?");
            params.add(Timestamp.valueOf(from.atStartOfDay()));
        }
        if (to != null) {
            where.append(" AND action_tstamp_tx < ?");
            params.add(Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
        }
        if (table != null && !table.isBlank()) {
            where.append(" AND table_name = ?");
            params.add(table);
        }
        if (action != null && !action.isBlank()) {
            where.append(" AND action = ?");
            params.add(action.toUpperCase());
        }
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search + "%";
            where.append(" AND (")
                .append("CAST(event_id AS TEXT) ILIKE ? OR ")
                .append("schema_name ILIKE ? OR ")
                .append("table_name ILIKE ? OR ")
                .append("session_user_name ILIKE ? OR ")
                .append("application_name ILIKE ? OR ")
                .append("CAST(client_addr AS TEXT) ILIKE ? OR ")
                .append("client_query ILIKE ? OR ")
                .append("action ILIKE ? OR ")
                .append("row_data::text ILIKE ? OR ")
                .append("changed_fields::text ILIKE ?")
                .append(")");
            for (int i = 0; i < 10; i++) {
                params.add(pattern);
            }
        }

        String countSql = "SELECT COUNT(*) FROM audit.logged_actions" + where;
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
        long total = totalElements == null ? 0L : totalElements;

        String dataSql = "SELECT " + LOGGED_ACTIONS_COLUMNS + " FROM audit.logged_actions" + where
                + " ORDER BY action_tstamp_tx DESC, event_id DESC LIMIT ? OFFSET ?";
        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(safeSize);
        dataParams.add((long) safePage * safeSize);

        List<Map<String, Object>> content = total == 0
                ? Collections.emptyList()
                : jdbcTemplate.queryForList(dataSql, dataParams.toArray());

        int totalPages = (int) Math.ceil((double) total / safeSize);

        return AuditLogPageDto.builder()
                .content(content)
                .totalElements(total)
                .totalPages(totalPages)
                .page(safePage)
                .size(safeSize)
                .numberOfElements(content.size())
                .build();
    }

}
