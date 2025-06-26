package com.ucacue.UcaApp.service.auditing.postgresql;

import java.util.*;
import org.springframework.data.domain.Page;

public interface LoggedActionService {

    Page<Map<String, Object>> findAll(int page, int pageSize);
    Map<String, Object> findById(Long id);

    List<Map<String, Object>> listTables();
    String findRelidOfTable(String table);
    Page<Map<String, Object>> findByRelid(Long relid, int page, int size);

    Page<Map<String, Object>> findByTable(String table, int page, int size);

    Page<Map<String, Object>> findByGlobalSearch(String searchParam, int page, int size);

    Page<Map<String, Object>> findByDate(String startDate, String endDate, int page, int size);
}
