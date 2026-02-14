package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.AuthResponseDTO;
import org.bloomberg.keezy_back.DTO.CreateStaffDTO;
import org.bloomberg.keezy_back.DTO.LoginDTO;
import org.bloomberg.keezy_back.DTO.RegisterDTO;
import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.Service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register/owner")
    @Operation(summary = "Register a new owner", description = "Create a new owner account with email and password. Owner role is automatically assigned")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Owner successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> registerOwner(@Valid @RequestBody RegisterDTO registerDTO) {
        UserDTO user = authenticationService.registerOwner(registerDTO);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            user,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/register/guest")
    @Operation(summary = "Register a new guest", description = "Create a new guest account with email and password. Guest role is automatically assigned")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Guest successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> registerGuest(@Valid @RequestBody RegisterDTO registerDTO) {
        UserDTO user = authenticationService.registerGuest(registerDTO);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            user,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserDTO user = authenticationService.register(registerDTO);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            user,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<UnifiedResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = authenticationService.login(loginDTO);
        UnifiedResponse<AuthResponseDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            response,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/create-staff")
    @Operation(summary = "Create staff member", description = "Create a staff account associated with current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Staff account created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> createStaff(@Valid @RequestBody CreateStaffDTO createStaffDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserDTO staff = authenticationService.createStaff(createStaffDTO, userId);
        UnifiedResponse<UserDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            staff,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}

