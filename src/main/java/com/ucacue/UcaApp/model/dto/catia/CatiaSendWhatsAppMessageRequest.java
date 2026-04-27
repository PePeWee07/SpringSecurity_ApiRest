package com.ucacue.UcaApp.model.dto.catia;

public record CatiaSendWhatsAppMessageRequest(
        Long number,
        String message,
        String sentBy,
        CatiaMessageSource source,
        Long businessPhoneNumber,
        CatiaMessageType type,
        String contextId) {
}
