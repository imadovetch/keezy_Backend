package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String email = auth.getName();
        try {
            UserDTO current = userService.getUserByEmail(email);
            return current != null ? current.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO dto = userService.getUserById(id);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            dto,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user details by email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        UserDTO dto = userService.getUserByEmail(email);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            dto,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve list of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<UnifiedResponse<java.util.List<UserDTO>>> getAllUsers() {
        java.util.List<UserDTO> list = userService.getAllUsers();
        UnifiedResponse<java.util.List<UserDTO>> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            list,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Get users by role", description = "Retrieve users filtered by role name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<java.util.List<UserDTO>>> getUsersByRole(@PathVariable String roleName) {
        java.util.List<UserDTO> list = userService.getUsersByRole(roleName);
        UnifiedResponse<java.util.List<UserDTO>> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            list,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> updateUser(@PathVariable String id, @Valid @RequestBody UserDTO userDTO) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            UnifiedResponse<UserDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        UserDTO updated = userService.updateUser(id, userDTO, currentUserId);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            updated,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UnifiedResponse<Void>> deleteUser(@PathVariable String id) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            UnifiedResponse<Void> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        userService.deleteUser(id, currentUserId);
        UnifiedResponse<Void> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            null,
            true
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resp);
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Disable user", description = "Disable a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully disabled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> disableUser(@PathVariable String id) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            UnifiedResponse<UserDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        UserDTO updated = userService.disableUser(id, currentUserId);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            updated,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Enable user", description = "Enable a disabled user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully enabled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> enableUser(@PathVariable String id) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            UnifiedResponse<UserDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        UserDTO updated = userService.enableUser(id, currentUserId);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            updated,
            true
        );
        return ResponseEntity.ok(resp);
    }
}

