package com.ucacue.UcaApp.model.mapper;

import java.util.*;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.ucacue.UcaApp.model.dto.role.RoleResponseDto;
import com.ucacue.UcaApp.model.dto.user.AdminUserManagerRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserRequestDto;
import com.ucacue.UcaApp.model.dto.user.UserResponseDto;
import com.ucacue.UcaApp.model.entity.RoleEntity;
import com.ucacue.UcaApp.model.entity.UserEntity;
import com.ucacue.UcaApp.util.RoleEntityFetcher;
import com.ucacue.UcaApp.util.token.PasswordEncoderUtil;

@Mapper(componentModel = "spring")
public interface UserMapper {
   
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    //-----------------MAPEO PARA PRESNTAR DATOS DEL USUARIO-----------------
    default UserResponseDto mapToUserResponseDto(UserEntity userEntity) {
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

    //-----------------MAPEO PARA USUARIO CON PRIVILEGIOS-----------------
    default UserEntity mapToAdminUserEntity(AdminUserManagerRequestDto UserRequestDto, @Context RoleEntityFetcher RoleEntityFetcher, @Context PasswordEncoderUtil passwordEncoderUtil) {
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

        Set<RoleEntity> roles = mapRoleIdsToRoleEntities(UserRequestDto.getRolesIds(), RoleEntityFetcher);

        entity.setRoles(roles);
        return entity;
    }
    
    default Set<RoleEntity> mapRoleIdsToRoleEntities(Set<Long> roleIds, RoleEntityFetcher RoleEntityFetcher) {        
        roleIds.add(2L);

        return roleIds.stream()
                      .map(id -> RoleEntityFetcher.mapRoleIdToRolesEntity(id)) // Usando RoleEntityFetcher para convertir ID a RolesEntity
                      .collect(Collectors.toSet());
    }

    default void updateAdminUserEntityFromDto(AdminUserManagerRequestDto dto, @MappingTarget UserEntity entity, @Context RoleEntityFetcher RoleEntityFetcher, @Context PasswordEncoderUtil passwordEncoderUtil) {
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

        Set<RoleEntity> roles = mapRoleIdsToRoleEntities(dto.getRolesIds(), RoleEntityFetcher);
        entity.setRoles(roles);
    }

    //-----------------MAPEO DE ACTUALIZACION DE PERFIL DEL PROPIO USUARIO-----------------
    default void updateEntityFromDtoUserProfile(UserRequestDto dto, @MappingTarget UserEntity entity, @Context PasswordEncoderUtil passwordEncoderUtil) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getDNI() != null) entity.setDNI(dto.getDNI());
        if (dto.getPassword() != null) entity.setPassword(passwordEncoderUtil.encodePassword(dto.getPassword()));
    }
}
