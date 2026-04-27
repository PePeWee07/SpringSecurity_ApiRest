package com.ucacue.UcaApp.service.apis.catia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.ucacue.UcaApp.model.dto.catia.CatiaSendWhatsAppMessageRequest;
import com.ucacue.UcaApp.model.dto.catia.CatiaUserChatUpdateRequest;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CatiaService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestClient publicRestClient;
    private final RestClient authenticatedRestClient;
    private final HttpClient httpClient;
    private final ExecutorService sseExecutor;
    private final String urlBase;
    private final String apiHeader;
    private final String apiKey;

    public CatiaService (
        @Value("${api.service.catia.base-url}") @NonNull String urlBase,
        @Value("${api.service.catia.api-header}") @NonNull String apiHeader,
        @Value("${api.service.catia.api-key}") String apiKey
    ) {
        this.urlBase = urlBase;
        this.apiHeader = apiHeader;
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.sseExecutor = Executors.newCachedThreadPool();

        this.publicRestClient = RestClient.builder()
                .baseUrl(urlBase)
                .defaultHeader("Content-Type", "application/json")
                .build();

        this.authenticatedRestClient = RestClient.builder()
                .baseUrl(urlBase)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader(apiHeader, apiKey)
                .build();
    }

    public JsonNode healthCheck() {
        return publicRestClient.get()
                .uri("/api/health")
                .retrieve()
                .body(JsonNode.class);
    }

    public void flushCache() {
        logger.warn("Consuming CATIA cache flush");

        authenticatedRestClient.delete()
                .uri("/api/cache/flush")
                .retrieve()
                .toBodilessEntity();
    }

    public JsonNode getMessageHistory(String phone, int page, int size, String direction) {
        return authenticatedRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/messages/history")
                        .queryParam("phone", phone)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("direction", direction)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getMessagePricing(Long messageId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/messages/{id}/pricing", messageId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getMessageError(Long messageId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/messages/{id}/error", messageId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getMessageAddress(Long messageId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/messages/{id}/address", messageId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getAiResponses(Long messageId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/messages/{id}/ai-response", messageId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode getMessageDetails(Long messageId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/messages/{id}", messageId)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode findUserChat(String identificacion, String whatsAppPhone) {
        return authenticatedRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/api/v1/whatsapp/user/find");

                    if (identificacion != null) {
                        builder.queryParam("identificacion", identificacion);
                    }

                    if (whatsAppPhone != null) {
                        builder.queryParam("whatsappPhone", whatsAppPhone);
                    }

                    return builder.build();
                })
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode listUserChats(int page, int pageSize, String sortBy, String direction) {
        return authenticatedRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/page/users/{page}")
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortBy", sortBy)
                        .queryParam("direction", direction)
                        .build(page))
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode listUserChatsBySessionStart(
            int page,
            int pageSize,
            String sortBy,
            String direction,
            String startDate,
            String endDate) {
        return authenticatedRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/page/users/{page}/byChatSessionStart")
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortBy", sortBy)
                        .queryParam("direction", direction)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .build(page))
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode updateUserChat(Long userId, CatiaUserChatUpdateRequest request) {
        return authenticatedRestClient.patch()
                .uri("/api/v1/whatsapp/update/user/{id}", userId)
                .body(request)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendWhatsAppMessage(CatiaSendWhatsAppMessageRequest payload) {
        return authenticatedRestClient.post()
                .uri("/api/v1/whatsapp/send")
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendImageById(CatiaSendWhatsAppMessageRequest payload, String imageId) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-image-by-id")
                        .queryParam("imageId", imageId)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendImageByUrl(CatiaSendWhatsAppMessageRequest payload, String imageUrl) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-image-by-url")
                        .queryParam("imageUrl", imageUrl)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendVideoById(CatiaSendWhatsAppMessageRequest payload, String videoId) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-video-by-id")
                        .queryParam("videoId", videoId)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendVideoByUrl(CatiaSendWhatsAppMessageRequest payload, String videoUrl) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-video-by-url")
                        .queryParam("videoUrl", videoUrl)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendDocumentById(CatiaSendWhatsAppMessageRequest payload, String documentId, String filename) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-document-by-id")
                        .queryParam("documentId", documentId)
                        .queryParam("filename", filename)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode sendDocumentByUrl(CatiaSendWhatsAppMessageRequest payload, String documentUrl, String filename) {
        return authenticatedRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/whatsapp/send-document-by-url")
                        .queryParam("documentUrl", documentUrl)
                        .queryParam("filename", filename)
                        .build())
                .body(payload)
                .retrieve()
                .body(JsonNode.class);
    }

    public String uploadWhatsAppMedia(MultipartFile file) {
        try {
            Resource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(
                    file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE));

            HttpEntity<Resource> filePart = new HttpEntity<>(fileResource, fileHeaders);

            MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
            multipartBody.add("file", filePart);

            return authenticatedRestClient.post()
                    .uri("/api/v1/whatsapp/upload-media-file")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(multipartBody)
                    .retrieve()
                    .body(String.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error processing media file", e);
        }
    }

    public ResponseEntity<byte[]> downloadWhatsAppMedia(String mediaId) {
        return authenticatedRestClient.get()
                .uri("/api/v1/whatsapp/media/donwload/{mediaId}", mediaId)
                .accept(MediaType.ALL)
                .retrieve()
                .toEntity(byte[].class);
    }

    public SseEmitter streamMessages(String phone) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> consumeMessageStream(phone, emitter), sseExecutor);

        return emitter;
    }

    private void consumeMessageStream(String phone, SseEmitter emitter) {
        URI uri = UriComponentsBuilder.fromHttpUrl(urlBase)
                .path("/api/v1/messages/stream")
                .queryParam("phone", phone)
                .build()
                .encode()
                .toUri();

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "text/event-stream")
                .header(apiHeader, apiKey)
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                emitter.completeWithError(new IllegalStateException(
                        "CATIA SSE request failed with status " + response.statusCode()));
                return;
            }

            forwardSsePayload(response.body(), emitter);
            emitter.complete();
        } catch (Exception e) {
            logger.error("Error consuming CATIA message stream", e);
            emitter.completeWithError(e);
        }
    }

    private void forwardSsePayload(InputStream payload, SseEmitter emitter) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(payload, StandardCharsets.UTF_8))) {
            String line;
            String eventName = null;
            StringBuilder data = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    sendSseEvent(emitter, eventName, data);
                    eventName = null;
                    data.setLength(0);
                    continue;
                }

                if (line.startsWith("event:")) {
                    eventName = line.substring("event:".length()).trim();
                } else if (line.startsWith("data:")) {
                    if (data.length() > 0) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                }
            }

            sendSseEvent(emitter, eventName, data);
        }
    }

    private void sendSseEvent(SseEmitter emitter, String eventName, StringBuilder data) throws IOException {
        if (data.length() == 0) {
            return;
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data(data.toString());

        if (eventName != null && !eventName.isBlank()) {
            event.name(eventName);
        }

        emitter.send(event);
    }

    @PreDestroy
    public void shutdownSseExecutor() {
        sseExecutor.shutdown();
    }
}
