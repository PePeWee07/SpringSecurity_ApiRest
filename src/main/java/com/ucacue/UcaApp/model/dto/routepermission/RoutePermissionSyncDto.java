package com.ucacue.UcaApp.model.dto.routepermission;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePermissionSyncDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "paths cannot be empty")
    private List<String> paths;
}
