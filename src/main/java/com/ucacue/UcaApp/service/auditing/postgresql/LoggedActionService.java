package com.ucacue.UcaApp.service.auditing.postgresql;

import java.time.LocalDate;
import java.util.*;

import com.ucacue.UcaApp.model.dto.auditing.AuditLogPageDto;

public interface LoggedActionService {

    Map<String, Object> findById(Long id);

    List<Map<String, Object>> listTables();

    AuditLogPageDto findPaged(int page, int size, LocalDate from, LocalDate to,
                              String table, String action, String search);

}
