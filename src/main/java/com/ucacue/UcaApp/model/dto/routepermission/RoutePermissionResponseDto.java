package com.ucacue.UcaApp.model.dto.routepermission;

import java.io.Serializable;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePermissionResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String path;

    private String description;

    private List<RouteRoleSummaryDto> roleList;
}
