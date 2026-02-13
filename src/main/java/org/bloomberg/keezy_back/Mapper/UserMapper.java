package org.bloomberg.keezy_back.Mapper;

import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Mapping manuel simple pour éviter la dépendance externe
    public UserDTO toDTO(AppUser user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedBy(user.getUpdatedBy());
        dto.setUpdatedAt(user.getUpdatedAt());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getRoleType().name());
        }
        return dto;
    }

    public AppUser toEntity(UserDTO dto) {
        if (dto == null) return null;
        AppUser user = new AppUser();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        user.setCreatedBy(dto.getCreatedBy());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedBy(dto.getUpdatedBy());
        user.setUpdatedAt(dto.getUpdatedAt());
        if (dto.getRoleName() != null) {
            // Note: Role must be set by service layer after resolving from DB
            Role role = new Role();
            role.setRoleType(Role.RoleType.valueOf(dto.getRoleName()));
            user.setRole(role);
        }
        return user;
    }
}


