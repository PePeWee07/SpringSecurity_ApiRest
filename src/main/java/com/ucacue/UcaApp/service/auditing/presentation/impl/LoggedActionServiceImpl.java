package com.ucacue.UcaApp.service.auditing.presentation.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucacue.UcaApp.service.auditing.presentation.LoggedActionService;

@Service
public class LoggedActionServiceImpl implements LoggedActionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM audit.logged_actions";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> findById(Long id) {
        String sql = "SELECT * FROM audit.logged_actions WHERE event_id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

}
