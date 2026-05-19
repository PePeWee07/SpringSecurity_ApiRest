package com.ucacue.UcaApp.model.dto.catia;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CatiaResponseMediaMetadata(
        String url,
        @JsonProperty("mime_type") String mimeType,
        String sha256,
        @JsonProperty("file_size") Long fileSize,
        String id,
        @JsonProperty("messaging_product") String messagingProduct) {
}
