package com.ucacue.UcaApp.controller.V2.auditing;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.service.auditing.postgresql.impl.LoggedActionServiceImpl;

@RestController
@RequestMapping("/api/v2/audit")
public class LoggedActionController {

    @Autowired
    private LoggedActionServiceImpl loggedActionService;

    @GetMapping("/actions")
    public ResponseEntity<List<Map<String, Object>>> getAllLoggedActions() {
        try {
            return ResponseEntity.ok(loggedActionService.findAll());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/actions/{id}")
    public ResponseEntity<Map<String, Object>> getLoggedActionById(@PathVariable Long id) {
        Map<String, Object> action = loggedActionService.findById(id);
        if (action != null) {
            return ResponseEntity.ok(action);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}