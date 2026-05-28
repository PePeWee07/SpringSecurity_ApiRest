package com.ucacue.UcaApp.model.dto.routepermission;

import java.io.Serializable;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePermissionAssignDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Long> roleIds;
}
