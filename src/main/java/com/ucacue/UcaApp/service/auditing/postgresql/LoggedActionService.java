package com.ucacue.UcaApp.service.auditing.postgresql;

import java.util.*;

public interface LoggedActionService {

    List<Map<String, Object>> findAll();

    Map<String, Object> findById(Long id);
}
