package com.ucacue.UcaApp.model.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.cliente.UserRequestDto;
import com.ucacue.UcaApp.model.dto.cliente.UserResponseDto;
import com.ucacue.UcaApp.model.dto.permission.PermissionResponseDto;
import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.entity.RolesEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.util.MapperHelper;



@Mapper(componentModel = "spring")
public interface UserMapper {
   
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    default UserResponseDto toUserResponseDto(UserEntity userEntity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userEntity.getId());
        dto.setName(userEntity.getName());
        dto.setLastName(userEntity.getLastName());
        dto.setEmail(userEntity.getEmail());
        dto.setPhoneNumber(userEntity.getPhoneNumber());
        dto.setAddress(userEntity.getAddress());
        dto.setDNI(userEntity.getDNI());
        // password no se debe devolver
        dto.setEnabled(userEntity.isEnabled());
        dto.setAccountNoExpired(userEntity.isAccountNoExpired());
        dto.setAccountNoLocked(userEntity.isAccountNoLocked());
        dto.setCredentialNoExpired(userEntity.isCredentialNoExpired());
        dto.setCreationDate(userEntity.getCreationDate());

        List<RoleResponseDto> roleDTOList = userEntity.getRoles().stream().map(role -> {
            RoleResponseDto roleDTO = new RoleResponseDto();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
    
            List<PermissionResponseDto> permissionDTOList = role.getPermissionList().stream()
                .map(permission -> {
                    PermissionResponseDto permissionDTO = new PermissionResponseDto();
                    permissionDTO.setId(permission.getId());
                    permissionDTO.setName(permission.getName());
                    return permissionDTO;
                })
                .collect(Collectors.toList());
    
            roleDTO.setPermissionList(permissionDTOList);
            return roleDTO;
        }).collect(Collectors.toList());
    
        dto.setRoles(roleDTOList);

        return dto;
    }


    default UserEntity toUserEntity(UserRequestDto UserRequestDto, @Context MapperHelper mapperHelper) {
        UserEntity entity = new UserEntity();
        entity.setId(UserRequestDto.getId());
        entity.setName(UserRequestDto.getName());
        entity.setLastName(UserRequestDto.getLastName());
        entity.setEmail(UserRequestDto.getEmail());
        entity.setPhoneNumber(UserRequestDto.getPhoneNumber());
        entity.setAddress(UserRequestDto.getAddress());
        entity.setDNI(UserRequestDto.getDNI());
        entity.setPassword(UserRequestDto.getPassword());
        entity.setEnabled(UserRequestDto.isEnabled());
        entity.setAccountNoExpired(UserRequestDto.isAccountNoExpired());
        entity.setAccountNoLocked(UserRequestDto.isAccountNoLocked());
        entity.setCredentialNoExpired(UserRequestDto.isCredentialNoExpired());
        entity.setCreationDate(UserRequestDto.getCreationDate());

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

    default void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserEntity entity, @Context MapperHelper mapperHelper) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getDNI() != null) entity.setDNI(dto.getDNI());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        entity.setEnabled(dto.isEnabled());
        entity.setAccountNoExpired(dto.isAccountNoExpired());
        entity.setAccountNoLocked(dto.isAccountNoLocked());
        entity.setCredentialNoExpired(dto.isCredentialNoExpired());
        if (dto.getCreationDate() != null) entity.setCreationDate(dto.getCreationDate());

        Set<RolesEntity> roles = rolesIdsToRolesEntities(dto.getRolesIds(), mapperHelper);
        entity.setRoles(roles);
    }
}
