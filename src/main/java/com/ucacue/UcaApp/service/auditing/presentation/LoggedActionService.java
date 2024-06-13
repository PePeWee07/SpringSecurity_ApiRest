package com.ucacue.UcaApp.service.auditing.presentation;

import java.util.*;

public interface LoggedActionService {

    List<Map<String, Object>> findAll();

    Map<String, Object> findById(Long id);
}
