package com.ucacue.UcaApp.service.auditing.postgresql.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.service.auditing.postgresql.LoggedActionService;

import io.micrometer.common.util.StringUtils;

@Service
public class LoggedActionServiceImpl implements LoggedActionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Otener todas las acciones
    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM audit.logged_actions";
        return jdbcTemplate.queryForList(sql);
    }

    //Obtener una accion por id
    @Override
    public Map<String, Object> findById(Long id) {
        String sql = "SELECT * FROM audit.logged_actions WHERE event_id = ? ORDER BY action_tstamp_tx";
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

    // !(BUG) Obtener toda las acciones por relid
    @Override
    public List<Map<String, Object>> findByRelid(Long relid) {
        String sql = "SELECT * FROM audit.logged_actions WHERE relid = ? ORDER BY action_tstamp_tx";
        return jdbcTemplate.queryForList(sql, relid);
    }

    //Obtener una accion por tabla
    @Override
    public List<Map<String, Object>> findByTable(String table) {
        String sql = "SELECT * FROM audit.logged_actions WHERE table_name = ? ORDER BY action_tstamp_tx";
        return jdbcTemplate.queryForList(sql, table);
    }

    // Filtrar por Id, user_id, email, dni
    private boolean hasText(String str) {
        return (str != null && !str.trim().isEmpty());
    }
    @Override
    public List<Map<String, Object>> findByRowData(String table, String userId, String email, String dni) {
        String sql = "SELECT * FROM audit.logged_actions WHERE table_name = ?";
        List<Object> params = new ArrayList<>();
        params.add(table);

        if (hasText(userId)) {
            sql += " AND row_data->'user_id' = ?";
            params.add(userId);
        }
        if (hasText(email)) {
            sql += " AND row_data->'email' = ?";
            params.add(email);
        }
        if (hasText(dni)) {
            sql += " AND row_data->'dni' = ?";
            params.add(dni);
        }
        sql += " ORDER BY action_tstamp_tx";

        return jdbcTemplate.queryForList(sql, params.toArray());
    }

}
