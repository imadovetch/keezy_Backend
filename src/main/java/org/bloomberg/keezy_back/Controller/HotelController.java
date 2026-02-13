package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.HotelDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.Service.HotelService;
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
@RequestMapping("/api/hotels")
@Tag(name = "Hotels", description = "Hotel management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class HotelController {

    private final HotelService hotelService;
    private final UserService userService;

    public HotelController(HotelService hotelService, UserService userService) {
        this.hotelService = hotelService;
        this.userService = userService;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String email = auth.getName();
        try {
            return userService.getUserByEmail(email).getId();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping
    @Operation(summary = "Create a new hotel", description = "Create a new hotel with provided details or via Opera property ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hotel successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<HotelDTO>> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            UnifiedResponse<HotelDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        HotelDTO created = hotelService.createHotel(hotelDTO, userId);
        UnifiedResponse<HotelDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            created,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/from-opera/{operaPropertyId}")
    @Operation(summary = "Create hotel from Opera property ID", description = "Create a new hotel using Opera property ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hotel successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid Opera property ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<HotelDTO>> createHotelFromOpera(@PathVariable String operaPropertyId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            UnifiedResponse<HotelDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        HotelDTO created = hotelService.createHotelFromOperaId(operaPropertyId, userId);
        UnifiedResponse<HotelDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            created,
            true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieve hotel details by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel found"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<HotelDTO>> getHotelById(@PathVariable String id) {
        HotelDTO dto = hotelService.getHotelById(id);
        UnifiedResponse<HotelDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            dto,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Retrieve a list of all hotels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of hotels retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<java.util.List<HotelDTO>>> getAllHotels() {
        java.util.List<HotelDTO> list = hotelService.getAllHotels();
        UnifiedResponse<java.util.List<HotelDTO>> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            list,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/owner/my-hotels")
    @Operation(summary = "Get my hotels", description = "Retrieve hotels owned by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's hotels retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<java.util.List<HotelDTO>>> getMyHotels() {
        String userId = getCurrentUserId();
        if (userId == null) {
            UnifiedResponse<java.util.List<HotelDTO>> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        java.util.List<HotelDTO> list = hotelService.getHotelsByOwner(userId);
        UnifiedResponse<java.util.List<HotelDTO>> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            list,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Update hotel details by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<UnifiedResponse<HotelDTO>> updateHotel(@PathVariable String id, @Valid @RequestBody HotelDTO hotelDTO) {
        String userId = getCurrentUserId();
        if (userId == null) {
            UnifiedResponse<HotelDTO> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        HotelDTO updated = hotelService.updateHotel(id, hotelDTO, userId);
        UnifiedResponse<HotelDTO> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            updated,
            true
        );
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel", description = "Delete a hotel by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Hotel successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only admin can delete"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<UnifiedResponse<Void>> deleteHotel(@PathVariable String id) {
        String userId = getCurrentUserId();
        if (userId == null) {
            UnifiedResponse<Void> err = new UnifiedResponse<>(
                java.util.Arrays.asList("Unauthorized"),
                java.util.Collections.emptyList(),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        hotelService.deleteHotel(id, userId);
        UnifiedResponse<Void> resp = new UnifiedResponse<>(
            java.util.Collections.emptyList(),
            java.util.Collections.emptyList(),
            null,
            true
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resp);
    }
}
