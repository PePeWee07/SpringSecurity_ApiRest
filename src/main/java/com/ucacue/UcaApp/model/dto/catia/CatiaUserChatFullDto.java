package com.ucacue.UcaApp.model.dto.catia;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public record CatiaUserChatFullDto(
        Long id,
        String whatsappPhone,
        String previousResponseId,
        Integer limitQuestions,
        LocalDateTime firstInteraction,
        LocalDateTime lastInteraction,
        LocalDateTime nextResetDate,
        String conversationState,
        Integer limitStrike,
        Boolean block,
        String blockingReason,
        Integer validQuestionCount,
        String identificacion,
        List<CatiaChatSessionSummaryDto> chatSessions,
        List<JsonNode> userTickets,
        CatiaErpUserDto erpUser) {
}
