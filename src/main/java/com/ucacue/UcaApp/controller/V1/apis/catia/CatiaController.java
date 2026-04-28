package com.ucacue.UcaApp.controller.V1.apis.catia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.ucacue.UcaApp.model.dto.catia.CatiaSendWhatsAppMessageRequest;
import com.ucacue.UcaApp.model.dto.catia.CatiaUserChatUpdateRequest;
import com.ucacue.UcaApp.service.apis.catia.CatiaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/catia")
@Tag(name = "Controlador CATIA", description = "Controlador proxy para consumir la API de CATIA desde el Front End")
public class CatiaController {

    private static final Logger logger = LoggerFactory.getLogger(CatiaController.class);
    private static final int MAX_USER_CHAT_PAGE_SIZE = 100;
    private static final int MAX_TEMPLATE_PAGE_SIZE = 100;

    private final CatiaService catiaService;

    public CatiaController(CatiaService catiaService) {
        this.catiaService = catiaService;
    }

    @GetMapping("/core/health")
    @Operation(summary = "Health Check CATIA", description = "Consulta el health check de la API de CATIA")
    public ResponseEntity<JsonNode> healthCheck() {
        try {
            return ResponseEntity.ok(catiaService.healthCheck());
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/health}", e);
            throw e;
        }
    }

    @DeleteMapping("/core/cache/flush")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Limpiar cache CATIA", description = "Elimina todas las claves de Redis en la API de CATIA")
    public ResponseEntity<Void> flushCache() {
        try {
            catiaService.flushCache();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error: {@DELETE /api/v1/catia/core/cache/flush}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Historial mensajes CATIA", description = "Obtiene el historial paginado de mensajes de un telefono")
    public ResponseEntity<JsonNode> getMessageHistory(
            @RequestParam String phone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "desc") String direction) {
        try {
            return ResponseEntity.ok(catiaService.getMessageHistory(phone, page, size, direction));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/history}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/{id}/pricing")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Pricing mensaje CATIA", description = "Obtiene el detalle de facturacion de un mensaje")
    public ResponseEntity<JsonNode> getMessagePricing(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getMessagePricing(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/{id}/pricing}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/{id}/error")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Error mensaje CATIA", description = "Obtiene el detalle del error de un mensaje")
    public ResponseEntity<JsonNode> getMessageError(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getMessageError(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/{id}/error}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/{id}/address")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Direccion mensaje CATIA", description = "Obtiene el detalle de direccion de un mensaje")
    public ResponseEntity<JsonNode> getMessageAddress(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getMessageAddress(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/{id}/address}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/{id}/ai-response")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respuestas IA mensaje CATIA", description = "Obtiene las respuestas de IA asociadas a un mensaje")
    public ResponseEntity<JsonNode> getAiResponses(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getAiResponses(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/{id}/ai-response}", e);
            throw e;
        }
    }

    @GetMapping("/core/messages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Detalle mensaje CATIA", description = "Obtiene el detalle completo de un mensaje")
    public ResponseEntity<JsonNode> getMessageDetails(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getMessageDetails(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/{id}}", e);
            throw e;
        }
    }

    @GetMapping(value = "/core/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Stream mensajes CATIA", description = "Abre un stream SSE de mensajes de CATIA por telefono")
    public SseEmitter streamMessages(@RequestParam("phone") String phone) {
        try {
            return catiaService.streamMessages(phone);
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/messages/stream}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/user/find")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar usuario chat CATIA", description = "Busca un usuario por identificacion o telefono de WhatsApp")
    public ResponseEntity<JsonNode> findUserChat(
            @RequestParam(value = "identificacion", required = false) String identificacion,
            @RequestParam(value = "whatsappPhone", required = false) String whatsAppPhone) {
        validateUserFindParams(identificacion, whatsAppPhone);

        try {
            return ResponseEntity.ok(catiaService.findUserChat(identificacion, whatsAppPhone));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/user/find}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/page/users/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Usuarios chat CATIA", description = "Obtiene el listado paginado de usuarios con sesiones de chat")
    public ResponseEntity<JsonNode> listUserChats(
            @PathVariable("page") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "lastInteraction") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        validatePage(page);

        try {
            int size = Math.min(pageSize, MAX_USER_CHAT_PAGE_SIZE);
            return ResponseEntity.ok(catiaService.listUserChats(page, size, sortBy, direction));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/page/users/{page}}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/page/users/{page}/byChatSessionStart")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Usuarios chat CATIA por fecha", description = "Obtiene usuarios por rango de fecha de inicio de sesion de chat")
    public ResponseEntity<JsonNode> listUserChatsBySessionStart(
            @PathVariable("page") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "lastInteraction") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        validatePage(page);

        try {
            int size = Math.min(pageSize, MAX_USER_CHAT_PAGE_SIZE);
            return ResponseEntity.ok(
                    catiaService.listUserChatsBySessionStart(page, size, sortBy, direction, startDate, endDate));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/page/users/{page}/byChatSessionStart}", e);
            throw e;
        }
    }

    @PatchMapping("/core/whatsapp/update/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario chat CATIA", description = "Actualiza los campos editables de un usuario de chat")
    public ResponseEntity<JsonNode> updateUserChat(
            @PathVariable("id") Long userId,
            @RequestBody CatiaUserChatUpdateRequest request) {
        validateUserId(userId);

        try {
            return ResponseEntity.ok(catiaService.updateUserChat(userId, request));
        } catch (Exception e) {
            logger.error("Error: {@PATCH /api/v1/catia/core/whatsapp/update/user/{id}}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar mensaje WhatsApp CATIA", description = "Envia un mensaje de WhatsApp a traves del core CATIA")
    public ResponseEntity<JsonNode> sendWhatsAppMessage(@RequestBody CatiaSendWhatsAppMessageRequest payload) {
        try {
            return ResponseEntity.ok(catiaService.sendWhatsAppMessage(payload));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send}", e);
            throw e;
        }
    }

    @PostMapping(value = "/core/whatsapp/upload-media-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Subir media WhatsApp CATIA", description = "Carga un archivo multimedia al servidor de WhatsApp a traves del core CATIA")
    public ResponseEntity<String> uploadWhatsAppMedia(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(catiaService.uploadWhatsAppMedia(file));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/upload-media-file}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/media/donwload/{mediaId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Descargar media WhatsApp CATIA", description = "Obtiene el binario de un archivo multimedia por ID")
    public ResponseEntity<byte[]> downloadWhatsAppMedia(@PathVariable("mediaId") String mediaId) {
        validateMediaId(mediaId);

        try {
            ResponseEntity<byte[]> result = catiaService.downloadWhatsAppMedia(mediaId);
            MediaType contentType = result.getHeaders().getContentType() != null
                    ? result.getHeaders().getContentType()
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(result.getBody());
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/media/donwload/{mediaId}}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/template/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respuestas templates CATIA", description = "Obtiene todas las respuestas de templates de WhatsApp")
    public ResponseEntity<JsonNode> getAllTemplateResponses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "message.sentAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(defaultValue = "false") Boolean onlyAnswered) {
        validatePage(page);

        try {
            int size = Math.min(pageSize, MAX_TEMPLATE_PAGE_SIZE);
            return ResponseEntity.ok(catiaService.getAllTemplateResponses(page, size, sort, dir, onlyAnswered));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/template/all}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/template/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respuestas templates CATIA por fecha", description = "Obtiene respuestas de templates por rango de fecha de envio")
    public ResponseEntity<JsonNode> getTemplateResponsesByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        try {
            JsonNode response = catiaService.getTemplateResponsesByDateRange(start, end);
            return responseHasNoContent(response) ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/template/date-range}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/template/name/{templateName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respuestas templates CATIA por nombre", description = "Obtiene respuestas de templates por nombre de plantilla")
    public ResponseEntity<JsonNode> getTemplateResponsesByName(@PathVariable("templateName") String templateName) {
        validateRequiredText(templateName, "templateName");

        try {
            JsonNode response = catiaService.getTemplateResponsesByName(templateName);
            return responseHasNoContent(response) ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/template/name/{templateName}}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/template/messages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Template CATIA por mensaje", description = "Obtiene el resultado de plantilla por ID de mensaje")
    public ResponseEntity<JsonNode> getTemplateByMessageId(@PathVariable("id") Long messageId) {
        validateMessageId(messageId);

        try {
            return ResponseEntity.ok(catiaService.getTemplateByMessageId(messageId));
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/template/messages/{id}}", e);
            throw e;
        }
    }

    @GetMapping("/core/whatsapp/template/{toPhone}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Respuestas templates CATIA por telefono", description = "Obtiene respuestas de templates por numero de telefono")
    public ResponseEntity<JsonNode> getTemplateResponsesByPhone(@PathVariable("toPhone") String toPhone) {
        validateRequiredText(toPhone, "toPhone");

        try {
            JsonNode response = catiaService.getTemplateResponsesByPhone(toPhone);
            return responseHasNoContent(response) ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error: {@GET /api/v1/catia/core/whatsapp/template/{toPhone}}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-image-by-id")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar imagen por ID CATIA", description = "Envia una imagen de WhatsApp usando un media ID")
    public ResponseEntity<JsonNode> sendImageById(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String imageId) {
        try {
            return ResponseEntity.ok(catiaService.sendImageById(payload, imageId));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-image-by-id}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-image-by-url")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar imagen por URL CATIA", description = "Envia una imagen de WhatsApp usando una URL")
    public ResponseEntity<JsonNode> sendImageByUrl(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String imageUrl) {
        try {
            return ResponseEntity.ok(catiaService.sendImageByUrl(payload, imageUrl));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-image-by-url}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-video-by-id")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar video por ID CATIA", description = "Envia un video de WhatsApp usando un media ID")
    public ResponseEntity<JsonNode> sendVideoById(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String videoId) {
        try {
            return ResponseEntity.ok(catiaService.sendVideoById(payload, videoId));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-video-by-id}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-video-by-url")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar video por URL CATIA", description = "Envia un video de WhatsApp usando una URL")
    public ResponseEntity<JsonNode> sendVideoByUrl(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String videoUrl) {
        try {
            return ResponseEntity.ok(catiaService.sendVideoByUrl(payload, videoUrl));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-video-by-url}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-document-by-id")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar documento por ID CATIA", description = "Envia un documento de WhatsApp usando un media ID")
    public ResponseEntity<JsonNode> sendDocumentById(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String documentId,
            @RequestParam String filename) {
        try {
            return ResponseEntity.ok(catiaService.sendDocumentById(payload, documentId, filename));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-document-by-id}", e);
            throw e;
        }
    }

    @PostMapping("/core/whatsapp/send-document-by-url")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar documento por URL CATIA", description = "Envia un documento de WhatsApp usando una URL")
    public ResponseEntity<JsonNode> sendDocumentByUrl(
            @RequestBody CatiaSendWhatsAppMessageRequest payload,
            @RequestParam String documentUrl,
            @RequestParam String filename) {
        try {
            return ResponseEntity.ok(catiaService.sendDocumentByUrl(payload, documentUrl, filename));
        } catch (Exception e) {
            logger.error("Error: {@POST /api/v1/catia/core/whatsapp/send-document-by-url}", e);
            throw e;
        }
    }

    private void validateMessageId(Long messageId) {
        if (messageId == null || messageId <= 0) {
            throw new IllegalArgumentException("Invalid message ID");
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }

    private void validatePage(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("Invalid page");
        }
    }

    private void validateMediaId(String mediaId) {
        if (mediaId == null || mediaId.isBlank()) {
            throw new IllegalArgumentException("Invalid media ID");
        }
    }

    private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Invalid " + fieldName);
        }
    }

    private boolean responseHasNoContent(JsonNode response) {
        return response == null || (response.isArray() && response.isEmpty());
    }

    private void validateUserFindParams(String identificacion, String whatsAppPhone) {
        if (identificacion != null && whatsAppPhone != null) {
            throw new IllegalArgumentException(
                    "Debe indicar solo un parametro de busqueda: identificacion o whatsappPhone");
        }

        if (identificacion == null && whatsAppPhone == null) {
            throw new IllegalArgumentException(
                    "Debe indicar un parametro de busqueda: identificacion o whatsappPhone");
        }
    }
}
