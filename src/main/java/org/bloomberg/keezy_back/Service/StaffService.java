package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.CreateStaffDTO;
import org.bloomberg.keezy_back.DTO.UserDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Hotel;
import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Mapper.UserMapper;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.bloomberg.keezy_back.Repositery.HotelRepository;
import org.bloomberg.keezy_back.Repositery.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing hotel staff
 * Handles staff CRUD operations with JWT-based hotel owner verification
 */
@Service
@Transactional
public class StaffService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public StaffService(AppUserRepository userRepository,
                       RoleRepository roleRepository,
                       HotelRepository hotelRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hotelRepository = hotelRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Extract hotel owner ID from JWT token
     */
    public String extractHotelOwnerFromToken(String jwtToken) {
        String userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        
        if (userId != null && !userId.isBlank()) {
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("User from token not found in database");
            }
            return userId;
        }
        
        String email = jwtTokenProvider.getEmailFromToken(jwtToken);
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Invalid token: could not extract user information");
        }
        
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        
        return user.getId();
    }

    /**
     * Create a new staff member for a hotel
     * Only the hotel owner can create staff for their hotel
     */
    public UserDTO createStaff(CreateStaffDTO createStaffDTO, String jwtToken) {
        // Extract hotel owner from token
        String hotelOwnerId = extractHotelOwnerFromToken(jwtToken);
        
        // Verify hotel exists
        String hotelId = createStaffDTO.getHotelId();
        if (hotelId == null || hotelId.isBlank()) {
            throw new RuntimeException("Hotel ID is required");
        }
        
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));
        
        // Verify the hotel belongs to the owner
        if (!hotel.getOwner().getId().equals(hotelOwnerId)) {
            throw new RuntimeException("Unauthorized: you are not the owner of this hotel");
        }
        
        // Check if staff email already exists
        if (userRepository.existsByEmail(createStaffDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Get STAFF role
        Role staffRole = roleRepository.findByRoleType(Role.RoleType.STAFF)
                .orElseThrow(() -> new RuntimeException("STAFF role not found"));
        
        // Create staff user
        AppUser staff = AppUser.builder()
                .email(createStaffDTO.getEmail())
                .password(passwordEncoder.encode(createStaffDTO.getPassword()))
                .firstName(createStaffDTO.getFirstName())
                .lastName(createStaffDTO.getLastName())
                .phone(createStaffDTO.getPhone())
                .role(staffRole)
                .enabled(true)
                .createdBy(hotelOwnerId)
                .createdAt(System.currentTimeMillis())
                .build();
        
        AppUser savedStaff = userRepository.save(staff);
        return userMapper.toDTO(savedStaff);
    }

    /**
     * Get all staff members for a hotel
     */
    public List<UserDTO> getHotelStaff(String hotelId, String jwtToken) {
        // Extract and verify hotel owner
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        if (!hotel.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: you are not the owner of this hotel");
        }
        
        // Get STAFF role
        Role staffRole = roleRepository.findByRoleType(Role.RoleType.STAFF)
                .orElseThrow(() -> new RuntimeException("STAFF role not found"));
        
        // Find all staff created by the owner
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equals(staffRole) && user.getCreatedBy().equals(ownerId))
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific staff member details
     */
    public UserDTO getStaffDetails(String staffId, String jwtToken) {
        // Extract hotel owner from token
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        AppUser staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Verify ownership - the staff must be created by the owner
        if (!staff.getCreatedBy().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: this staff does not belong to your hotel");
        }
        
        return userMapper.toDTO(staff);
    }

    /**
     * Update a staff member
     */
    public UserDTO updateStaff(String staffId, CreateStaffDTO updateStaffDTO, String jwtToken) {
        // Extract hotel owner from token
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        AppUser staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Verify ownership
        if (!staff.getCreatedBy().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: this staff does not belong to your hotel");
        }
        
        // Update fields
        if (updateStaffDTO.getFirstName() != null) {
            staff.setFirstName(updateStaffDTO.getFirstName());
        }
        if (updateStaffDTO.getLastName() != null) {
            staff.setLastName(updateStaffDTO.getLastName());
        }
        if (updateStaffDTO.getPhone() != null) {
            staff.setPhone(updateStaffDTO.getPhone());
        }
        if (updateStaffDTO.getPassword() != null && !updateStaffDTO.getPassword().isBlank()) {
            staff.setPassword(passwordEncoder.encode(updateStaffDTO.getPassword()));
        }
        
        staff.setUpdatedBy(ownerId);
        staff.setUpdatedAt(System.currentTimeMillis());
        
        AppUser updatedStaff = userRepository.save(staff);
        return userMapper.toDTO(updatedStaff);
    }

    /**
     * Delete a staff member
     */
    public void deleteStaff(String staffId, String jwtToken) {
        // Extract hotel owner from token
        String ownerId = extractHotelOwnerFromToken(jwtToken);
        
        AppUser staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Verify ownership
        if (!staff.getCreatedBy().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: this staff does not belong to your hotel");
        }
        
        userRepository.delete(staff);
    }
}
