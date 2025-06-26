package com.ucacue.UcaApp.service.auditing.postgresql.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.service.auditing.postgresql.LoggedActionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class LoggedActionServiceImpl implements LoggedActionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Otener todas las acciones
    @Override
    public Page<Map<String, Object>> findAll(int page, int pageSize) {
        String sql = "SELECT * FROM audit.logged_actions ORDER BY action_tstamp_tx DESC";
        String countSql = "SELECT COUNT(*) FROM audit.logged_actions";
        return Paginate(sql, countSql, page, pageSize);
    }

    //Obtener una accion por id
    @Override
    public Map<String, Object> findById(Long id) {
        String sql = "SELECT * FROM audit.logged_actions WHERE event_id = ? ORDER BY action_tstamp_tx DESC";
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

    // Obtener el relid de una tabla
    @Override
    public String findRelidOfTable(String table) {
        String sql = String.format("SELECT '%s'::regclass::oid;", "auth." + table);
        return jdbcTemplate.queryForObject(sql, String.class);
    }

    // Obtener toda las acciones por relid
    @Override
    public Page<Map<String, Object>> findByRelid(Long relid, int page, int pageSize) {
        String sql = "SELECT * FROM audit.logged_actions WHERE relid = ? ORDER BY action_tstamp_tx DESC";
        String countSql = "SELECT COUNT(*) FROM audit.logged_actions WHERE relid = ?";
        return Paginate(sql, countSql, page, pageSize, relid);
    }

    //Obtener una accion por tabla
    @Override
    public Page<Map<String, Object>> findByTable(String table, int page, int pageSize) {
        String sql = "SELECT * FROM audit.logged_actions WHERE table_name = ? ORDER BY action_tstamp_tx DESC";
        String countSql = "SELECT COUNT(*) FROM audit.logged_actions WHERE table_name = ?";
        return Paginate(sql, countSql, page, pageSize, table);
    }

    // Busqueda Global
    @Override
    public Page<Map<String, Object>> findByGlobalSearch(String searchParam, int page, int pageSize) {
        String sql = "SELECT * FROM audit.logged_actions WHERE " +
                "CAST(event_id AS TEXT) LIKE ? OR " +
                "schema_name LIKE ? OR " +
                "table_name LIKE ? OR " +
                "CAST(relid AS TEXT) LIKE ? OR " +
                "session_user_name LIKE ? OR " +
                "CAST(transaction_id AS TEXT) LIKE ? OR " +
                "application_name LIKE ? OR " +
                "CAST(client_addr AS TEXT) LIKE ? OR " +
                "CAST(client_port AS TEXT) LIKE ? OR " +
                "client_query LIKE ? OR " +
                "action LIKE ? OR " +
                "row_data::text LIKE ? OR " +
                "changed_fields::text LIKE ?";
        
        String searchPattern = "%" + searchParam + "%";
        String countSql = "SELECT COUNT(*) FROM audit.logged_actions WHERE " +
                "CAST(event_id AS TEXT) LIKE ? OR " +
                "schema_name LIKE ? OR " +
                "table_name LIKE ? OR " +
                "CAST(relid AS TEXT) LIKE ? OR " +
                "session_user_name LIKE ? OR " +
                "CAST(transaction_id AS TEXT) LIKE ? OR " +
                "application_name LIKE ? OR " +
                "CAST(client_addr AS TEXT) LIKE ? OR " +
                "CAST(client_port AS TEXT) LIKE ? OR " +
                "client_query LIKE ? OR " +
                "action LIKE ? OR " +
                "row_data::text LIKE ? OR " +
                "changed_fields::text LIKE ?";

        return Paginate(sql, countSql, page, pageSize, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, 
            searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern);
    }

    @Override
    public Page<Map<String, Object>> findByDate(String startDate, String endDate, int page, int pageSize) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
        String sql = "SELECT * FROM audit.logged_actions WHERE action_tstamp_tx BETWEEN ? AND ? ORDER BY action_tstamp_tx DESC";
        String countSql = "SELECT COUNT(*) FROM audit.logged_actions WHERE action_tstamp_tx BETWEEN ? AND ?";
        return Paginate(sql, countSql, page, pageSize, start, end);
    }

   
    public List<Map<String, Object>> countActionsByTable() {
        String sql = "SELECT table_name, COUNT(*) AS count FROM audit.logged_actions GROUP BY table_name";
        return jdbcTemplate.queryForList(sql);
    }


    private Page<Map<String, Object>> Paginate(
            String sqlQuery,
            String countQuery,
            int page,
            int pageSize,
            Object... params
    ) {
        int offset = page * pageSize;

        String paginatedSql = sqlQuery + " LIMIT ? OFFSET ?";

        Object[] fullParams = new Object[params.length + 2];
        System.arraycopy(params, 0, fullParams, 0, params.length);
        fullParams[params.length] = pageSize;
        fullParams[params.length + 1] = offset;

        List<Map<String, Object>> content = jdbcTemplate.queryForList(paginatedSql, fullParams);
        Long total = jdbcTemplate.queryForObject(countQuery, Long.class, params);
        int totalCount = total != null ? total.intValue() : 0;

        Pageable pageable = PageRequest.of(page, pageSize);
        return new PageImpl<>(content, pageable, totalCount);
    }

    
}
