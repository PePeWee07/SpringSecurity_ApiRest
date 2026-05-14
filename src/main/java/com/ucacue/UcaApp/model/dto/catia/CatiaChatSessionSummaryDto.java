package com.ucacue.UcaApp.model.dto.catia;

import java.time.LocalDateTime;

public record CatiaChatSessionSummaryDto(
        Long id,
        Integer messageCount,
        LocalDateTime startTime) {
}
