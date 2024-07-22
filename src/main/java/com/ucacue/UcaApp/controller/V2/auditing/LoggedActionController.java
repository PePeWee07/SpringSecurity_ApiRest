package com.ucacue.UcaApp.controller.V2.auditing;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
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

import org.slf4j.*;

@RestController
@RequestMapping("/api/v2/audit")
@Tag(name = "LoggedActionController", description = "Controlador para gestionar Auditoria")
public class LoggedActionController {

    private static final Logger logger = LoggerFactory.getLogger(LoggedActionController.class);

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
            logger.info("Error: {@GET /audit/actions}", e.getMessage());
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
            logger.info("Error: {@GET /audit/actions/{id}}", "No se encontró la acción con ID: " + id);
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
            logger.info("Error: {@GET /audit/actions/tables}", "No se encontraron tablas en la base de datos.");
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
            logger.info("Error: {@GET /audit/actions/relid/{table}}", "No se encontró el Relid de la tabla: " + table);
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener acciones por relid
    @GetMapping("/actions/by-relid/{relid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar Acciones por Relid", description = "Obtiene una lista de todas las acciones por Relid.")
    public ResponseEntity<List<Map<String, Object>>> getActionsByRelid(@PathVariable Long relid) {
        List<Map<String, Object>> actions = loggedActionService.findByRelid(relid);
        if (actions != null && !actions.isEmpty()) {
            return ResponseEntity.ok(actions);
        } else {
            logger.info("Error: {@GET /audit/actions/by-relid/{relid}}", "No se encontraron acciones con Relid: " + relid);
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
            logger.info("Error: {@GET /audit/actions/by-table/{table}}", "No se encontraron acciones con Tabla: " + table);
            return ResponseEntity.noContent().build();
        }
    }

    // Busqueda global
    @GetMapping("/actions/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Búsqueda global en acciones", description = "Obtiene una lista de todas las acciones que coinciden con el parámetro de búsqueda en cualquier columna.")
    public ResponseEntity<List<Map<String, Object>>> searchActions(
            @RequestParam String searchParam) {

        List<Map<String, Object>> actions = loggedActionService.findByGlobalSearch(searchParam);
        if (actions != null && !actions.isEmpty()) {
            return ResponseEntity.ok(actions);
        } else {
            logger.info("Error: {@GET /audit/actions/search}", "No se encontraron acciones con el parámetro de búsqueda: " + searchParam);
            return ResponseEntity.noContent().build();
        }
    }
    
}