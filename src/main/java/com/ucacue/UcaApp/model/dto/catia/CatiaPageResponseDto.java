package com.ucacue.UcaApp.model.dto.catia;

import java.util.List;

public record CatiaPageResponseDto<T>(
        List<T> content,
        CatiaPageMetadataDto page) {
}
