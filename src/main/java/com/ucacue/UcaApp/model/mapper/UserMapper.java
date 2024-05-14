package com.ucacue.UcaApp.model.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
//import org.mapstruct.Mapping; // Solo necesitas especificar mapeos customizados para campos que no coinciden
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.model.dto.cliente.UserResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.util.MapperHelper;



@Mapper(componentModel = "spring")
public interface UserMapper {
   
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    default UserResponseDto toUserResponseDto(UserEntity userEntity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userEntity.getId());
        dto.setNombre(userEntity.getNombre());
        dto.setApellido(userEntity.getApellido());
        dto.setEmail(userEntity.getEmail());
        dto.setTelefono(userEntity.getTelefono());
        dto.setDireccion(userEntity.getDireccion());
        dto.setCedula(userEntity.getCedula());
        // password no se debe devolver
        dto.setEnabled(userEntity.isEnabled());
        dto.setAccountNoExpired(userEntity.isAccountNoExpired());
        dto.setAccountNoLocked(userEntity.isAccountNoLocked());
        dto.setCredentialNoExpired(userEntity.isCredentialNoExpired());
        dto.setFechaCreacion(userEntity.getFechaCreacion());
        List<String> roles = userEntity.getRoles().stream()
                                          .map(RolesEntity::getName)
                                          .collect(Collectors.toList());
        dto.setRoles(roles);
        return dto;
    }


    default UserEntity toUserEntity(UserRequestDto UserRequestDto, @Context MapperHelper mapperHelper) {
        UserEntity entity = new UserEntity();
        entity.setId(UserRequestDto.getId());
        entity.setNombre(UserRequestDto.getNombre());
        entity.setApellido(UserRequestDto.getApellido());
        entity.setEmail(UserRequestDto.getEmail());
        entity.setTelefono(UserRequestDto.getTelefono());
        entity.setDireccion(UserRequestDto.getDireccion());
        entity.setCedula(UserRequestDto.getCedula());
        entity.setPassword(UserRequestDto.getPassword());
        entity.setEnabled(UserRequestDto.isEnabled());
        entity.setAccountNoExpired(UserRequestDto.isAccountNoExpired());
        entity.setAccountNoLocked(UserRequestDto.isAccountNoLocked());
        entity.setCredentialNoExpired(UserRequestDto.isCredentialNoExpired());
        entity.setFechaCreacion(UserRequestDto.getFechaCreacion());

        // Ahora pasamos el mapperHelper a rolesIdsToRolesEntities
        Set<RolesEntity> roles = rolesIdsToRolesEntities(UserRequestDto.getRolesIds(), mapperHelper);

        entity.setRoles(roles);
        return entity;
    }
    
    default Set<RolesEntity> rolesIdsToRolesEntities(Set<Long> roleIds, MapperHelper mapperHelper) {
        // Agregar siempre el ID 2 del ROL_USER al conjunto de roles
        //roleIds.add(2L);

        return roleIds.stream()
                      .map(id -> mapperHelper.mapRoleIdToRolesEntity(id)) // Usando MapperHelper para convertir ID a RolesEntity
                      .collect(Collectors.toSet());
    }

    //void updateEntityFromDto(ClienteRequestDto dto, @MappingTarget UserEntity entity);
    default void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserEntity entity, @Context MapperHelper mapperHelper) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getApellido() != null) entity.setApellido(dto.getApellido());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) entity.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
        if (dto.getCedula() != null) entity.setCedula(dto.getCedula());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        entity.setEnabled(dto.isEnabled());
        entity.setAccountNoExpired(dto.isAccountNoExpired());
        entity.setAccountNoLocked(dto.isAccountNoLocked());
        entity.setCredentialNoExpired(dto.isCredentialNoExpired());
        if (dto.getFechaCreacion() != null) entity.setFechaCreacion(dto.getFechaCreacion());

        Set<RolesEntity> roles = rolesIdsToRolesEntities(dto.getRolesIds(), mapperHelper);
        entity.setRoles(roles);
    }
}
