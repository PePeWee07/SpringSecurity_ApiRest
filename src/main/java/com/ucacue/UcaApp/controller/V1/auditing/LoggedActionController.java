package com.ucacue.UcaApp.controller.V1.auditing;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucacue.UcaApp.model.dto.auditing.AuditLogPageDto;
import com.ucacue.UcaApp.service.auditing.postgresql.impl.LoggedActionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.*;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "controlador de Auditorias", description = "Controlador para gestionar Auditoria")
public class LoggedActionController {

    private static final Logger logger = LoggerFactory.getLogger(LoggedActionController.class);

    @Autowired
    private LoggedActionServiceImpl loggedActionService;

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

    // Listado paginado con filtros opcionales
    @GetMapping("/actions/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Auditorias paginadas",
               description = "Listado paginado con filtros opcionales: rango de fechas, tabla, accion y busqueda global. Devuelve metadata de paginacion para consumir desde la UI.")
    public ResponseEntity<AuditLogPageDto> getPagedActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String table,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String search) {
        try {
            return ResponseEntity.ok(loggedActionService.findPaged(page, size, from, to, table, action, search));
        } catch (Exception e) {
            logger.info("Error: {@GET /audit/actions/page} {}", e.getMessage());
            throw e;
        }
    }

}
