package com.ucacue.UcaApp.util;

import org.springframework.stereotype.Component;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.service.rol.RolService;


import org.springframework.beans.factory.annotation.Autowired;

@Component
public class MapperHelper {

    @Autowired
    private RolService rolService;
 
    public RolesEntity mapRoleIdToRolesEntity(Long id) {
        return rolService.getMapperHelpRoleById(id);
    }

}
