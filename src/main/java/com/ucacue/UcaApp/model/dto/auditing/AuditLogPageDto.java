package com.ucacue.UcaApp.model.dto.auditing;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogPageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Map<String, Object>> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private int numberOfElements;
}
