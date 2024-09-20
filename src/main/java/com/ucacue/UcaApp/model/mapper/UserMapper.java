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
        dto.setDni(userEntity.getDni());
        // password no se debe devolver
        dto.setEnabled(userEntity.isEnabled());
        dto.setAccountNonExpired(userEntity.isAccountNonExpired());
        dto.setAccountNonLocked(userEntity.isAccountNonLocked());
        dto.setCredentialsNonExpired(userEntity.isCredentialsNonExpired());
        dto.setAccountExpiryDate(userEntity.getAccountExpiryDate());
        dto.setUsername(userEntity.getUsername());

        // Mapear authorities
        List<Map<String, String>> authorities = userEntity.getAuthorities().stream()
            .map(authority -> {
                Map<String, String> map = new HashMap<>();
                map.put("name", authority.getAuthority());
                return map;
            })
            .collect(Collectors.toList());
        dto.setAuthorities(authorities);


        List<RoleResponseDto> roleDTOList = userEntity.getRoles().stream()
            .map(RoleMapper.INSTANCE::rolesEntityToRoleResponseDto)
            .collect(Collectors.toList());

        dto.setRoles(roleDTOList);

        return dto;
    }

    // ? Se separan los mapeadores ya que unos tienen la posibilidad de cambair el 
    // ? estado de la cuentas la cual el propio usuario no debe poder hacerlo el mismo

    //-----------------MAPEO PARA USUARIO SOLO CON PRIVILEGIOS-----------------
    default UserEntity mapToAdminUserEntity(AdminUserManagerRequestDto UserRequestDto, @Context RoleEntityFetcher RoleEntityFetcher, @Context PasswordEncoderUtil passwordEncoderUtil) {
        UserEntity entity = new UserEntity();
        entity.setId(UserRequestDto.getId());
        entity.setName(UserRequestDto.getName());
        entity.setLastName(UserRequestDto.getLastName());
        entity.setEmail(UserRequestDto.getEmail());
        entity.setPhoneNumber(UserRequestDto.getPhoneNumber());
        entity.setAddress(UserRequestDto.getAddress());
        entity.setDni(UserRequestDto.getDni());
        entity.setPassword(passwordEncoderUtil.encodePassword(UserRequestDto.getPassword()));
        entity.setEnabled(UserRequestDto.isEnabled());
        entity.setAccountNonExpired(UserRequestDto.isAccountNonExpired());
        entity.setAccountNonLocked(UserRequestDto.isAccountNonLocked());
        entity.setCredentialsNonExpired(UserRequestDto.isCredentialsNonExpired());
        entity.setAccountExpiryDate(UserRequestDto.getAccountExpiryDate());

        Set<RoleEntity> roles = mapRoleIdsToRoleEntities(UserRequestDto.getRolesIds(), RoleEntityFetcher);

        entity.setRoles(roles);
        return entity;
    }
    
    default Set<RoleEntity> mapRoleIdsToRoleEntities(Set<Long> roleIds, RoleEntityFetcher RoleEntityFetcher) {   
        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = new HashSet<>();
            roleIds.add(2L);
        }

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
        if (dto.getDni() != null) entity.setDni(dto.getDni());
        if (dto.getPassword() != null) entity.setPassword(passwordEncoderUtil.encodePassword(dto.getPassword()));
        entity.setEnabled(dto.isEnabled());
        entity.setAccountNonExpired(dto.isAccountNonExpired());
        entity.setAccountNonLocked(dto.isAccountNonLocked());
        entity.setCredentialsNonExpired(dto.isCredentialsNonExpired());
        if (dto.getAccountExpiryDate() != null) entity.setAccountExpiryDate(dto.getAccountExpiryDate());
        

        Set<RoleEntity> roles = mapRoleIdsToRoleEntities(dto.getRolesIds(), RoleEntityFetcher);
        entity.setRoles(roles);
    }

    //-----------------MAPEO DE ACTUALIZACION DE PERFIL POR EL MISMO USUARIO-----------------
    default UserEntity mapToUserEntity(UserRequestDto UserRequestDto, @Context PasswordEncoderUtil passwordEncoderUtil) {
        UserEntity entity = new UserEntity();
        entity.setId(UserRequestDto.getId());
        entity.setName(UserRequestDto.getName());
        entity.setLastName(UserRequestDto.getLastName());
        entity.setEmail(UserRequestDto.getEmail());
        entity.setPhoneNumber(UserRequestDto.getPhoneNumber());
        entity.setAddress(UserRequestDto.getAddress());
        entity.setDni(UserRequestDto.getDni());
        entity.setPassword(passwordEncoderUtil.encodePassword(UserRequestDto.getPassword()));
        return entity;
    }

    default void updateEntityFromDtoUserProfile(UserRequestDto dto, @MappingTarget UserEntity entity, @Context PasswordEncoderUtil passwordEncoderUtil) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getDni() != null) entity.setDni(dto.getDni());
        if (dto.getPassword() != null) entity.setPassword(passwordEncoderUtil.encodePassword(dto.getPassword()));
    }
}
