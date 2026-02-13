package org.bloomberg.keezy_back.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String firstName;
    private String lastName;
    private String phone;

    private String roleId;
    private String roleName;

    private Boolean enabled;

    private String createdBy;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
}

