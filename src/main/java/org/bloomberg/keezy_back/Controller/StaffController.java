package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.CreateStaffDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.Service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff Management", description = "Hotel staff management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/{hotelId}")
    @Operation(summary = "Create a new staff member",
            description = "Create a new staff member for a hotel. Only hotel owner can create staff for their hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Staff successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not the owner of this hotel")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> createStaff(
            @PathVariable String hotelId,
            @Valid @RequestBody CreateStaffDTO createStaffDTO,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UnifiedResponse<>(
                            Arrays.asList("Missing or invalid Authorization header"),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }

        try {
            // Set hotel ID from path
            createStaffDTO.setHotelId(hotelId);
            UserDTO created = staffService.createStaff(createStaffDTO, jwt);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UnifiedResponse<>(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            created,
                            true
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UnifiedResponse<>(
                            Arrays.asList(e.getMessage()),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }
    }

    @GetMapping("/{hotelId}")
    @Operation(summary = "Get all staff members for a hotel",
            description = "Retrieve all staff members for a hotel owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff members retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<UnifiedResponse<List<UserDTO>>> getHotelStaff(
            @PathVariable String hotelId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UnifiedResponse<>(
                            Arrays.asList("Missing or invalid Authorization header"),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }

        try {
            List<UserDTO> staff = staffService.getHotelStaff(hotelId, jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    staff,
                    true
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UnifiedResponse<>(
                            Arrays.asList(e.getMessage()),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }
    }

    @GetMapping("/member/{staffId}")
    @Operation(summary = "Get staff member details",
            description = "Retrieve details of a specific staff member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff member retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Staff not found")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> getStaffDetails(
            @PathVariable String staffId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UnifiedResponse<>(
                            Arrays.asList("Missing or invalid Authorization header"),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }

        try {
            UserDTO staff = staffService.getStaffDetails(staffId, jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    staff,
                    true
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UnifiedResponse<>(
                            Arrays.asList(e.getMessage()),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }
    }

    @PutMapping("/member/{staffId}")
    @Operation(summary = "Update staff member",
            description = "Update details of a staff member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff member successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Staff not found")
    })
    public ResponseEntity<UnifiedResponse<UserDTO>> updateStaff(
            @PathVariable String staffId,
            @Valid @RequestBody CreateStaffDTO updateStaffDTO,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UnifiedResponse<>(
                            Arrays.asList("Missing or invalid Authorization header"),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }

        try {
            UserDTO updated = staffService.updateStaff(staffId, updateStaffDTO, jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    updated,
                    true
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UnifiedResponse<>(
                            Arrays.asList(e.getMessage()),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }
    }

    @DeleteMapping("/member/{staffId}")
    @Operation(summary = "Delete staff member",
            description = "Delete a staff member from the hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Staff member successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Staff not found")
    })
    public ResponseEntity<Void> deleteStaff(
            @PathVariable String staffId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            staffService.deleteStaff(staffId, jwt);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
