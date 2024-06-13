package com.ucacue.UcaApp.model.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.util.RoleEntityFetcher;
import com.ucacue.UcaApp.util.token.PasswordEncoderUtil;

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

        List<RoleResponseDto> roleDTOList = userEntity.getRoles().stream()
            .map(RoleMapper.INSTANCE::rolesEntityToRoleResponseDto)
            .collect(Collectors.toList());

        dto.setRoles(roleDTOList);

        return dto;
    }

    default UserEntity toUserEntity(UserRequestDto UserRequestDto, @Context RoleEntityFetcher RoleEntityFetcher, @Context PasswordEncoderUtil passwordEncoderUtil) {
        UserEntity entity = new UserEntity();
        entity.setId(UserRequestDto.getId());
        entity.setName(UserRequestDto.getName());
        entity.setLastName(UserRequestDto.getLastName());
        entity.setEmail(UserRequestDto.getEmail());
        entity.setPhoneNumber(UserRequestDto.getPhoneNumber());
        entity.setAddress(UserRequestDto.getAddress());
        entity.setDNI(UserRequestDto.getDNI());
        entity.setPassword(passwordEncoderUtil.encodePassword(UserRequestDto.getPassword()));
        entity.setEnabled(UserRequestDto.isEnabled());
        entity.setAccountNoExpired(UserRequestDto.isAccountNoExpired());
        entity.setAccountNoLocked(UserRequestDto.isAccountNoLocked());
        entity.setCredentialNoExpired(UserRequestDto.isCredentialNoExpired());

        // Ahora pasamos el RoleEntityFetcher a rolesIdsToRolesEntities
        Set<RoleEntity> roles = rolesIdsToRolesEntities(UserRequestDto.getRolesIds(), RoleEntityFetcher);

        entity.setRoles(roles);
        return entity;
    }
    
    default Set<RoleEntity> rolesIdsToRolesEntities(Set<Long> roleIds, RoleEntityFetcher RoleEntityFetcher) {
        // Agregar siempre el ID 2 del ROL_USER al conjunto de roles
        //roleIds.add(2L);

        return roleIds.stream()
                      .map(id -> RoleEntityFetcher.mapRoleIdToRolesEntity(id)) // Usando RoleEntityFetcher para convertir ID a RolesEntity
                      .collect(Collectors.toSet());
    }

    default void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserEntity entity, @Context RoleEntityFetcher RoleEntityFetcher, @Context PasswordEncoderUtil passwordEncoderUtil) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getDNI() != null) entity.setDNI(dto.getDNI());
        if (dto.getPassword() != null) entity.setPassword(passwordEncoderUtil.encodePassword(dto.getPassword()));
        entity.setEnabled(dto.isEnabled());
        entity.setAccountNoExpired(dto.isAccountNoExpired());
        entity.setAccountNoLocked(dto.isAccountNoLocked());
        entity.setCredentialNoExpired(dto.isCredentialNoExpired());

        Set<RoleEntity> roles = rolesIdsToRolesEntities(dto.getRolesIds(), RoleEntityFetcher);
        entity.setRoles(roles);
    }
}
