package com.ucacue.UcaApp.model.dto.catia;

public record CatiaUserChatUpdateRequest(
        Integer limitQuestions,
        String previousResponseId,
        Integer limitStrike,
        Boolean block,
        String blockingReason) {
}
