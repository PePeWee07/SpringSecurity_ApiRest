package com.ucacue.UcaApp.controller.V2.auditing;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.service.auditing.postgresql.impl.LoggedActionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v2/audit")
@Tag(name = "LoggedActionController", description = "Controlador para gestionar Auditoria")
public class LoggedActionController {

    @Autowired
    private LoggedActionServiceImpl loggedActionService;

    // Obtener todas las acciones
    @GetMapping("/actions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista de Acciones", description = "Listado de toda las Acciones.")
    public ResponseEntity<List<Map<String, Object>>> getAllLoggedActions() {
        try {
            return ResponseEntity.ok(loggedActionService.findAll());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener una accion por id
    @GetMapping("/actions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busqueda de Accion por ID", description = "Obtiene los datos de la Accion.")
    public ResponseEntity<Map<String, Object>> getLoggedActionById(@PathVariable Long id) {
        Map<String, Object> action = loggedActionService.findById(id);
        if (action != null) {
            return ResponseEntity.ok(action);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener tablas de base de datos
    @GetMapping("/actions/tables")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las tablas", description = "Obtiene una lista de todas las tablas en la base de datos.")
    public ResponseEntity<List<Map<String, Object>>> listTables() {
        List<Map<String, Object>> tables = loggedActionService.listTables();
        if (tables != null && !tables.isEmpty()) {
            return ResponseEntity.ok(tables);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Obtener relid por tabla
    @GetMapping("/actions/relid/{table}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busqueda de Relid por Tabla", description = "Obtiene el Relid de la Tabla.")
    public ResponseEntity<String> getRelidOfTable(@PathVariable String table) {
        String relid = loggedActionService.findRelidOfTable(table);
        if (relid != null) {
            return ResponseEntity.ok(relid);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // !(BUG)Obtener acciones por relid
    @GetMapping("/actions/by-relid/{relid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar Acciones por Relid", description = "Obtiene una lista de todas las acciones por Relid.")
    public ResponseEntity<List<Map<String, Object>>> getActionsByRelid(@PathVariable Long relid) {
        List<Map<String, Object>> actions = loggedActionService.findByRelid(relid);
        if (actions != null && !actions.isEmpty()) {
            return ResponseEntity.ok(actions);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Obtener acciones por tabla
    @GetMapping("/actions/by-table/{table}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar Acciones por Tabla", description = "Obtiene una lista de todas las acciones por Tabla.")
    public ResponseEntity<List<Map<String, Object>>> getActionsByTable(@PathVariable String table) {
        List<Map<String, Object>> actions = loggedActionService.findByTable(table);
        if (actions != null && !actions.isEmpty()) {
            return ResponseEntity.ok(actions);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // Filtrar por Id, user_id, email, dni
    @GetMapping("/actions/row-data/{table}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar Acciones por Tabla", description = "Obtiene una lista de todas las acciones por Tabla.")
    public ResponseEntity<List<Map<String, Object>>> getActionsByTable(
            @PathVariable String table,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String dni) {

        List<Map<String, Object>> actions = loggedActionService.findByRowData(table, userId, email, dni);
        if (actions != null && !actions.isEmpty()) {
            return ResponseEntity.ok(actions);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
}