package com.ucacue.UcaApp.service.auditing.postgresql.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.service.auditing.postgresql.LoggedActionService;

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

    // Obtener toda las acciones por relid
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

    // Busqueda Global
    @Override
    public List<Map<String, Object>> findByGlobalSearch(String searchParam) {
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

        return jdbcTemplate.queryForList(sql, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, 
            searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern, searchPattern);
    }

}
