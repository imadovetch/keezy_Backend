package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.CreateReservationDTO;
import org.bloomberg.keezy_back.DTO.ReservationDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.DTO.UpdateReservationStatusDTO;
import org.bloomberg.keezy_back.Service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Service reservation management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class ReservationController {

    private final ReservationService reservationService;
    private final AppUserRepository appUserRepository;

    public ReservationController(ReservationService reservationService, AppUserRepository appUserRepository) {
        this.reservationService = reservationService;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Helper method to get user ID from authentication token
     * Token contains email/username, we need to get the UUID from database
     */
    private String getUserIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    /**
     * Create a new reservation
     * Authenticated users can create reservations
     */
    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Create a service reservation with selected date and time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - must be authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only guests can make reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> createReservation(
            @Valid @RequestBody CreateReservationDTO createReservationDTO) {
        try {
            String guestId = getUserIdFromAuth();

            ReservationDTO reservation = reservationService.createReservation(createReservationDTO, guestId);
            
            System.out.println("=== CONTROLLER: Reservation DTO received from service ===");
            System.out.println("Reservation ID: " + reservation.getId());
            System.out.println("Reservation status: " + reservation.getStatus());
            
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservation,
                true
            );
            
            System.out.println("=== CONTROLLER: UnifiedResponse created ===");
            System.out.println("Returning HTTP 201 CREATED");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            System.out.println("=== CONTROLLER: IllegalArgumentException ===");
            System.out.println("Error: " + e.getMessage());
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        } catch (Exception e) {
            System.out.println("=== CONTROLLER: Exception caught ===");
            System.out.println("Error type: " + e.getClass().getName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList("Error: " + e.getMessage()),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResp);
        }
    }

    /**
     * Get all reservations for the current guest
     */
    @GetMapping("/my-reservations")
    @Operation(summary = "Get my reservations", description = "Get all reservations made by the current guest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getMyReservations() {
        try {
            String guestId = getUserIdFromAuth();
            List<ReservationDTO> reservations = reservationService.getGuestReservations(guestId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Get all active reservations for the current guest
     */
    @GetMapping("/my-active-reservations")
    @Operation(summary = "Get my active reservations", description = "Get all pending and confirmed reservations for the current guest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active reservations retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getMyActiveReservations() {
        try {
            String guestId = getUserIdFromAuth();
            List<ReservationDTO> reservations = reservationService.getActiveReservations(guestId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Get all reclamation reservations for the current guest
     */
    @GetMapping("/my-reclamations")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Get my reclamations", description = "Get all reclamation reservations made by the current guest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reclamations retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getMyReclamations() {
        try {
            String guestId = getUserIdFromAuth();
            List<ReservationDTO> reclamations = reservationService.getGuestReclamations(guestId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reclamations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Get all active reclamation reservations for the current guest
     */
    @GetMapping("/my-active-reclamations")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Get my active reclamations", description = "Get all active reclamation reservations for the current guest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active reclamations retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getMyActiveReclamations() {
        try {
            String guestId = getUserIdFromAuth();
            List<ReservationDTO> reclamations = reservationService.getActiveReclamations(guestId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reclamations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Get a specific reservation by ID
     */
    @GetMapping("/{reservationId}")
    @Operation(summary = "Get reservation details", description = "Get details of a specific reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> getReservation(
            @PathVariable String reservationId) {
        try {
            ReservationDTO reservation = reservationService.getReservation(reservationId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservation,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
        }
    }

    /**
     * Update a reservation (guest can only update notes and number of guests for pending reservations)
     */
    @PutMapping("/{reservationId}")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Update a reservation", description = "Update notes and number of guests for a pending reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - can only update own reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> updateReservation(
            @PathVariable String reservationId,
            @Valid @RequestBody CreateReservationDTO updateReservationDTO) {
        try {
            String guestId = getUserIdFromAuth();

            ReservationDTO updated = reservationService.updateReservation(reservationId, updateReservationDTO, guestId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                updated,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Cancel a reservation
     */
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Cancel a reservation", description = "Cancel an existing reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - can only cancel own reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> cancelReservation(
            @PathVariable String reservationId) {
        try {
            String guestId = getUserIdFromAuth();

            ReservationDTO cancelled = reservationService.cancelReservation(reservationId, guestId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                cancelled,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Get all reservations for a hotel (OWNER only)
     */
    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get hotel reservations", description = "Get all reservations for a hotel (owner only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Hotel not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only owners can view hotel reservations")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getHotelReservations(
            @PathVariable String hotelId) {
        try {
            List<ReservationDTO> reservations = reservationService.getHotelReservations(hotelId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
        }
    }

    /**
     * Get all reservations for a service (OWNER only)
     */
    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Get service reservations", description = "Get all reservations for a service (owner only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Service not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only owners can view service reservations")
    })
    public ResponseEntity<UnifiedResponse<List<ReservationDTO>>> getServiceReservations(
            @PathVariable String serviceId) {
        try {
            List<ReservationDTO> reservations = reservationService.getServiceReservations(serviceId);
            UnifiedResponse<List<ReservationDTO>> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                reservations,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<List<ReservationDTO>> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResp);
        }
    }

    /**
     * Confirm a pending reservation (OWNER only)
     */
    @PostMapping("/{reservationId}/confirm")
    @Operation(summary = "Confirm a reservation", description = "Confirm a pending reservation (owner only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only owners can confirm reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> confirmReservation(
            @PathVariable String reservationId) {
        try {
            ReservationDTO confirmed = reservationService.startReservation(reservationId, SecurityContextHolder.getContext().getAuthentication().getName());
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                confirmed,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Start a reservation (change status from PENDING to IN_PROGRESS)
     * Only STAFF can perform this action
     */
    @PostMapping("/{reservationId}/start")
    @Operation(summary = "Start a reservation", description = "Change reservation status from PENDING to IN_PROGRESS (staff only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation started successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only staff can start reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> startReservation(
            @PathVariable String reservationId) {
        try {
            String staffId = getUserIdFromAuth();

            ReservationDTO updated = reservationService.startReservation(reservationId, staffId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                updated,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Complete a reservation (change status from IN_PROGRESS to DONE)
     * Only STAFF can perform this action
     */
    @PostMapping("/{reservationId}/complete")
    @Operation(summary = "Complete a reservation", description = "Change reservation status from IN_PROGRESS to DONE (staff only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation completed successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only staff can complete reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> completeReservation(
            @PathVariable String reservationId) {
        try {
            String staffId = getUserIdFromAuth();

            ReservationDTO updated = reservationService.completeReservation(reservationId, staffId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                updated,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Block a reservation (mark as BLOCKED)
     * Only STAFF can perform this action
     */
    @PostMapping("/{reservationId}/block")
    @Operation(summary = "Block a reservation", description = "Block a reservation with a reason (staff only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation blocked successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only staff can block reservations")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> blockReservation(
            @PathVariable String reservationId,
            @RequestBody UpdateReservationStatusDTO statusDTO) {
        try {
            String staffId = getUserIdFromAuth();

            String blockReason = statusDTO.getReason() != null ? statusDTO.getReason() : "Blocked by staff";
            ReservationDTO blocked = reservationService.blockReservation(reservationId, staffId, blockReason);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                blocked,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }

    /**
     * Unblock a reservation (restore to PENDING)
     * GUEST (owner), STAFF, or OWNER can perform this action
     */
    @PostMapping("/{reservationId}/unblock")
    @Operation(summary = "Unblock a reservation", description = "Unblock a blocked reservation and restore to PENDING (guest, staff, or owner)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation unblocked successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - only guest, staff, or owner can unblock")
    })
    public ResponseEntity<UnifiedResponse<ReservationDTO>> unblockReservation(
            @PathVariable String reservationId) {
        try {
            String userId = getUserIdFromAuth();

            ReservationDTO unblocked = reservationService.unblockReservation(reservationId, userId);
            UnifiedResponse<ReservationDTO> resp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.emptyList(),
                unblocked,
                true
            );
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            UnifiedResponse<ReservationDTO> errorResp = new UnifiedResponse<>(
                Collections.emptyList(),
                Collections.singletonList(e.getMessage()),
                null,
                false
            );
            return ResponseEntity.badRequest().body(errorResp);
        }
    }
}
