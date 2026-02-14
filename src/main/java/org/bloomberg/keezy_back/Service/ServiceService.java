package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.ServiceDTO;
import org.bloomberg.keezy_back.Entity.Hotel;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Mapper.ServiceMapper;
import org.bloomberg.keezy_back.Repositery.ServiceRepository;
import org.bloomberg.keezy_back.Repositery.HotelRepository;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing hotel services/amenities
 * Handles creation, update, retrieval of services with JWT-based owner verification
 */
@Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final HotelRepository hotelRepository;
    private final AppUserRepository userRepository;
    private final ServiceMapper serviceMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public ServiceService(ServiceRepository serviceRepository, 
                         HotelRepository hotelRepository,
                         AppUserRepository userRepository,
                         ServiceMapper serviceMapper,
                         JwtTokenProvider jwtTokenProvider) {
        this.serviceRepository = serviceRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.serviceMapper = serviceMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Extract hotel owner ID from JWT token
     * The token contains the email (subject), we use it to fetch the user and verify ownership
     * 
     * @param jwtToken the JWT token from the Authorization header
     * @return the userId of the hotel owner
     * @throws RuntimeException if token is invalid or user not found
     */
    public String extractHotelOwnerFromToken(String jwtToken) {
        // Extract userId directly from token if available
        String userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        
        if (userId != null && !userId.isBlank()) {
            // Verify user exists
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("User from token not found in database");
            }
            return userId;
        }
        
        // Fallback: extract email and get user
        String email = jwtTokenProvider.getEmailFromToken(jwtToken);
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Invalid token: could not extract user information");
        }
        
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        
        return user.getId();
    }

    /**
     * Verify that the user is an OWNER or ADMIN role (required to create/modify services)
     * Guests cannot create services
     * 
     * @param userId the user ID
     * @throws RuntimeException if user doesn't have permission
     */
    private void verifyServiceCreationPermission(String userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role.RoleType roleType = user.getRole().getRoleType();
        if (!roleType.equals(Role.RoleType.OWNER) && !roleType.equals(Role.RoleType.ADMIN) 
            && !roleType.equals(Role.RoleType.USER)) {
            throw new RuntimeException("Unauthorized: Only owners can create services");
        }
    }

    /**
     * Get all services for the authenticated owner
     * Returns services with details about which hotel they belong to (or null if free)
     * 
     * @param jwtToken the JWT token containing owner information
     * @return list of all services owned by this user
     */
    public List<ServiceDTO> getOwnerServices(String jwtToken) {
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        return serviceRepository.findByOwnerId(ownerId)
                .stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new service for a hotel or for all hotels of the owner
     * 
     * @param serviceDTO the service data
     * @param jwtToken the JWT token containing owner information
     * @return the created service DTO
     */
    public ServiceDTO createService(ServiceDTO serviceDTO, String jwtToken) {
        // Extract owner from JWT token
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        // Verify user has permission to create services
        verifyServiceCreationPermission(ownerId);
        
        // Get the owner user
        AppUser owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Validate required fields
        if (serviceDTO.getTitle() == null || serviceDTO.getTitle().isBlank()) {
            throw new RuntimeException("Service title is required");
        }
        if (serviceDTO.getCategory() == null || serviceDTO.getCategory().isBlank()) {
            throw new RuntimeException("Service category is required");
        }
        if (serviceDTO.getType() == null || serviceDTO.getType().isBlank()) {
            throw new RuntimeException("Service type is required");
        }
        if (serviceDTO.getPrice() == null || serviceDTO.getPrice() < 0) {
            throw new RuntimeException("Valid price is required");
        }
        
        // Hotel is optional - if provided, verify ownership
        Hotel hotel = null;
        String hotelId = serviceDTO.getHotelId();
        
        if (hotelId != null && !hotelId.isBlank()) {
            hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
            
            // Verify the hotel belongs to the owner
            if (!hotel.getOwner().getId().equals(ownerId)) {
                throw new RuntimeException("Unauthorized: you are not the owner of this hotel");
            }
        }
        
        // Create service entity
        org.bloomberg.keezy_back.Entity.Service service = org.bloomberg.keezy_back.Entity.Service.builder()
                .title(serviceDTO.getTitle())
                .category(serviceDTO.getCategory())
                .subcategory(serviceDTO.getSubcategory())
                .type(serviceDTO.getType())
                .description(serviceDTO.getDescription())
                .hotel(hotel)  // Can be null if service is for all hotels
                .owner(owner)  // Owner reference
                .price(serviceDTO.getPrice())
                .images(convertListToString(serviceDTO.getImages()))
                .selectedDates(convertListToString(serviceDTO.getSelectedDates()))
                .timeSlots(convertListToString(serviceDTO.getTimeSlots()))
                .createdBy(ownerId)
                .createdAt(System.currentTimeMillis())
                .build();
        
        org.bloomberg.keezy_back.Entity.Service savedService = serviceRepository.save(service);
        return serviceMapper.toDTO(savedService);
    }

    /**
     * Get services for a hotel based on user role
     * - OWNER: sees their own services for this hotel + services marked for all their hotels
     * - GUEST: sees all services for this specific hotel
     * 
     * @param hotelId the hotel ID
     * @param jwtToken the JWT token (for ownership verification and role checking)
     * @return list of service DTOs
     */
    public List<ServiceDTO> getHotelServices(String hotelId, String jwtToken) {
        String userId = extractHotelOwnerFromToken(jwtToken);
        
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role.RoleType roleType = user.getRole().getRoleType();
        
        // For OWNER/ADMIN: show only their services for this hotel (specific + free)
        if (roleType.equals(Role.RoleType.OWNER) || roleType.equals(Role.RoleType.ADMIN) 
            || roleType.equals(Role.RoleType.USER)) {
            // Verify hotel belongs to the user
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Hotel not found"));
            
            if (!hotel.getOwner().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized: you are not the owner of this hotel");
            }
            
            return serviceRepository.findOwnerServicesByHotel(userId, hotelId)
                    .stream()
                    .map(serviceMapper::toDTO)
                    .collect(Collectors.toList());
        }
        
        // For GUEST: show all services for this hotel (anyone can view)
        return serviceRepository.findAllServicesByHotel(hotelId)
                .stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get services for a hotel filtered by type
     * Guests can access this without authentication
     * 
     * @param hotelId the hotel ID
     * @param type the service type to filter by
     * @return list of services matching the hotel and type
     */
    public List<ServiceDTO> getServicesByHotelAndType(String hotelId, String type) {
        if (type == null || type.isBlank()) {
            throw new RuntimeException("Service type is required");
        }
        
        return serviceRepository.findServicesByHotelAndType(hotelId, type)
                .stream()
                .map(serviceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific service
     * 
     * @param serviceId the service ID
     * @param jwtToken the JWT token (for ownership verification)
     * @return the service DTO
     */
    public ServiceDTO getService(String serviceId, String jwtToken) {
        String userId = extractHotelOwnerFromToken(jwtToken);
        
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        org.bloomberg.keezy_back.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        Role.RoleType roleType = user.getRole().getRoleType();
        
        // OWNER/ADMIN/USER: can only access their own services
        if (roleType.equals(Role.RoleType.OWNER) || roleType.equals(Role.RoleType.ADMIN) 
            || roleType.equals(Role.RoleType.USER)) {
            if (!service.getOwner().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized: this service does not belong to you");
            }
        }
        // GUEST: can access any service
        
        return serviceMapper.toDTO(service);
    }

    /**
     * Update a service
     * 
     * @param serviceId the service ID
     * @param serviceDTO the updated service data
     * @param jwtToken the JWT token (for ownership verification)
     * @return the updated service DTO
     */
    public ServiceDTO updateService(String serviceId, ServiceDTO serviceDTO, String jwtToken) {
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        org.bloomberg.keezy_back.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        // Verify ownership - only owner can update their service
        if (!service.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: this service does not belong to you");
        }
        
        // Update fields
        if (serviceDTO.getTitle() != null) service.setTitle(serviceDTO.getTitle());
        if (serviceDTO.getCategory() != null) service.setCategory(serviceDTO.getCategory());
        if (serviceDTO.getSubcategory() != null) service.setSubcategory(serviceDTO.getSubcategory());
        if (serviceDTO.getType() != null) service.setType(serviceDTO.getType());
        if (serviceDTO.getDescription() != null) service.setDescription(serviceDTO.getDescription());
        if (serviceDTO.getPrice() != null) service.setPrice(serviceDTO.getPrice());
        
        service.setImages(convertListToString(serviceDTO.getImages()));
        service.setSelectedDates(convertListToString(serviceDTO.getSelectedDates()));
        service.setTimeSlots(convertListToString(serviceDTO.getTimeSlots()));
        service.setUpdatedBy(ownerId);
        service.setUpdatedAt(System.currentTimeMillis());
        
        org.bloomberg.keezy_back.Entity.Service updatedService = serviceRepository.save(service);
        return serviceMapper.toDTO(updatedService);
    }

    /**
     * Delete a service
     * 
     * @param serviceId the service ID
     * @param jwtToken the JWT token (for ownership verification)
     */
    public void deleteService(String serviceId, String jwtToken) {
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        org.bloomberg.keezy_back.Entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        // Verify ownership - only owner can delete their service
        if (!service.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: this service does not belong to you");
        }
        
        serviceRepository.delete(service);
    }

    /**
     * Helper method to convert List<String> to comma-separated string
     */
    private String convertListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }
}
