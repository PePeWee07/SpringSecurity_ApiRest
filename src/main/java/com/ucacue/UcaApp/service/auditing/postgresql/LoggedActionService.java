package com.ucacue.UcaApp.service.auditing.postgresql;

import java.util.*;

public interface LoggedActionService {

    List<Map<String, Object>> findAll();
    Map<String, Object> findById(Long id);

    List<Map<String, Object>> listTables();
    String findRelidOfTable(String table);
    List<Map<String, Object>> findByRelid(Long relid);

    List<Map<String, Object>> findByTable(String table);

    List<Map<String, Object>> findByGlobalSearch(String searchParam);

}
