package com.ucacue.UcaApp.model.dto.routepermission;

import java.io.Serializable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRoleSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;
}
