package org.bloomberg.keezy_back.Controller;

import org.bloomberg.keezy_back.DTO.ServiceDTO;
import org.bloomberg.keezy_back.DTO.UnifiedResponse;
import org.bloomberg.keezy_back.Service.ServiceService;
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
@RequestMapping("/api/services")
@Tag(name = "Services", description = "Hotel services/amenities management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(@RequestHeader(value = "Authorization", required = false) String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "Get all services for authenticated owner",
            description = "Retrieve all services created by the authenticated owner with details about hotel associations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UnifiedResponse<List<ServiceDTO>>> getOwnerServices(
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
            List<ServiceDTO> services = serviceService.getOwnerServices(jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    services,
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

    @PostMapping("/{hotelId}")
    @Operation(summary = "Create a new service for a hotel",
            description = "Create a new service (amenity) for the hotel owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not the owner of this hotel")
    })
    public ResponseEntity<UnifiedResponse<ServiceDTO>> createService(
            @PathVariable String hotelId,
            @Valid @RequestBody ServiceDTO serviceDTO,
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
            serviceDTO.setHotelId(hotelId);
            ServiceDTO created = serviceService.createService(serviceDTO, jwt);
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
    @Operation(summary = "Get all services for a hotel",
            description = "Retrieve all services for a hotel owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<UnifiedResponse<List<ServiceDTO>>> getHotelServices(
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
            List<ServiceDTO> services = serviceService.getHotelServices(hotelId, jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    services,
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

    @GetMapping("/{hotelId}/by-type")
    @Operation(summary = "Get services by hotel and type",
            description = "Retrieve all services for a specific hotel filtered by service type (guest access)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<UnifiedResponse<List<ServiceDTO>>> getServicesByType(
            @PathVariable String hotelId,
            @RequestParam String type) {

        if (type == null || type.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UnifiedResponse<>(
                            Arrays.asList("Service type is required"),
                            Collections.emptyList(),
                            null,
                            false
                    ));
        }

        try {
            List<ServiceDTO> services = serviceService.getServicesByHotelAndType(hotelId, type);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    services,
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

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Get a specific service",
            description = "Retrieve details of a specific service owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<UnifiedResponse<ServiceDTO>> getService(
            @PathVariable String serviceId,
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
            ServiceDTO service = serviceService.getService(serviceId, jwt);
            return ResponseEntity.ok(new UnifiedResponse<>(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    service,
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

    @PutMapping("/service/{serviceId}")
    @Operation(summary = "Update a service",
            description = "Update details of a service owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<UnifiedResponse<ServiceDTO>> updateService(
            @PathVariable String serviceId,
            @Valid @RequestBody ServiceDTO serviceDTO,
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
            ServiceDTO updated = serviceService.updateService(serviceId, serviceDTO, jwt);
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

    @DeleteMapping("/service/{serviceId}")
    @Operation(summary = "Delete a service",
            description = "Delete a service owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Service successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<Void> deleteService(
            @PathVariable String serviceId,
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        String jwt = getJwtFromRequest(bearerToken);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            serviceService.deleteService(serviceId, jwt);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
