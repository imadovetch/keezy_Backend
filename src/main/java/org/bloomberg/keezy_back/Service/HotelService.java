package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.HotelDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Hotel;
import org.bloomberg.keezy_back.Entity.Role;
import org.bloomberg.keezy_back.Mapper.HotelMapper;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.bloomberg.keezy_back.Repositery.HotelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AppUserRepository userRepository;
    private final HotelMapper hotelMapper;

    @Value("${hotel.uploads.directory:uploads}")
    private String uploadsDirectory;

    public HotelService(HotelRepository hotelRepository, AppUserRepository userRepository,
                       HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.hotelMapper = hotelMapper;
    }

    /**
     * Save hotel photo to disk and return the file path
     */
    private String savePhoto(String photoData) {
        if (photoData == null || photoData.isBlank()) {
            return null;
        }

        try {
            // Create uploads directory if not exists
            File uploadsDir = new File(uploadsDirectory);
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + ".png";
            String filepath = Paths.get(uploadsDirectory, filename).toString();

            // If photoData is base64, decode it; otherwise treat as raw bytes
            byte[] decodedBytes;
            try {
                // Try to decode as base64
                decodedBytes = Base64.getDecoder().decode(photoData);
            } catch (IllegalArgumentException e) {
                // If not base64, use as-is
                decodedBytes = photoData.getBytes();
            }

            Files.write(Paths.get(filepath), decodedBytes);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotel photo: " + e.getMessage(), e);
        }
    }

    public HotelDTO createHotel(HotelDTO hotelDTO, String userId) {
        AppUser owner = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user has permission to create hotels (only OWNER/ADMIN/USER can create)
        // GUEST and STAFF cannot create hotels
        Role.RoleType roleType = owner.getRole().getRoleType();
        if (!roleType.equals(Role.RoleType.OWNER) 
            && !roleType.equals(Role.RoleType.ADMIN)
            && !roleType.equals(Role.RoleType.USER)) {
            throw new RuntimeException("Unauthorized: Only owners, admins and users can create hotels");
        }

        // If operaPropertyId is provided we allow creation with only that field
        boolean hasOperaOnly = hotelDTO.getOperaPropertyId() != null && !hotelDTO.getOperaPropertyId().isBlank()
            && (hotelDTO.getName() == null || hotelDTO.getName().isBlank());

        // If not opera-only, validate required fields
        if (!hasOperaOnly) {
            if (hotelDTO.getEmail() == null || hotelDTO.getEmail().isBlank()) {
                throw new RuntimeException("Email is required when not creating from Opera property ID");
            }
            if (hotelDTO.getName() == null || hotelDTO.getName().isBlank()) {
                throw new RuntimeException("Hotel name is required when not creating from Opera property ID");
            }
            if (hotelDTO.getAddressLine1() == null || hotelDTO.getAddressLine1().isBlank()) {
                throw new RuntimeException("Address is required when not creating from Opera property ID");
            }
            if (hotelDTO.getCity() == null || hotelDTO.getCity().isBlank()) {
                throw new RuntimeException("City is required when not creating from Opera property ID");
            }
            if (hotelDTO.getCountry() == null || hotelDTO.getCountry().isBlank()) {
                throw new RuntimeException("Country is required when not creating from Opera property ID");
            }
            if (hotelDTO.getPhone() == null || hotelDTO.getPhone().isBlank()) {
                throw new RuntimeException("Phone is required when not creating from Opera property ID");
            }
            // basic email sanity check
            if (!hotelDTO.getEmail().contains("@")) {
                throw new RuntimeException("Email should be valid");
            }
        }

        // Check if hotel with this email already exists (only if email provided)
        if (hotelDTO.getEmail() != null && !hotelDTO.getEmail().isBlank()) {
            if (hotelRepository.existsByEmail(hotelDTO.getEmail())) {
                throw new RuntimeException("Hotel with this email already exists");
            }
        }

        // Check if operaPropertyId is provided and already exists
        if (hotelDTO.getOperaPropertyId() != null && !hotelDTO.getOperaPropertyId().isBlank()) {
            if (hotelRepository.findByOperaPropertyId(hotelDTO.getOperaPropertyId()).isPresent()) {
                throw new RuntimeException("Hotel with this Opera Property ID already exists");
            }
        }

        // Save photo if provided
        String photoFilename = null;
        if (hotelDTO.getPhoto() != null && !hotelDTO.getPhoto().isBlank()) {
            photoFilename = savePhoto(hotelDTO.getPhoto());
        }

        Hotel hotel = Hotel.builder()
            .operaPropertyId(hotelDTO.getOperaPropertyId())
            .name(hotelDTO.getName())
            .owner(owner)
            .addressLine1(hotelDTO.getAddressLine1())
            .city(hotelDTO.getCity())
            .country(hotelDTO.getCountry())
            .description(hotelDTO.getDescription())
            .phone(hotelDTO.getPhone())
            .email(hotelDTO.getEmail())
            .rating(hotelDTO.getRating())
            .photo(photoFilename)
            .createdBy(owner.getEmail())
            .createdAt(System.currentTimeMillis())
            .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelMapper.toDTO(savedHotel);
    }

    public HotelDTO createHotelFromOperaId(String operaPropertyId, String userId) {
        AppUser owner = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user has permission to create hotels (only OWNER/ADMIN/USER can create)
        // GUEST and STAFF cannot create hotels
        Role.RoleType roleType = owner.getRole().getRoleType();
        if (!roleType.equals(Role.RoleType.OWNER) 
            && !roleType.equals(Role.RoleType.ADMIN)
            && !roleType.equals(Role.RoleType.USER)) {
            throw new RuntimeException("Unauthorized: Only owners, admins and users can create hotels");
        }

        // Check if hotel with this Opera ID already exists
        if (hotelRepository.findByOperaPropertyId(operaPropertyId).isPresent()) {
            throw new RuntimeException("Hotel with this Opera Property ID already exists");
        }

        // In a real scenario, you would fetch data from OPERA system here
        // For now, we create a basic hotel with Opera ID
        Hotel hotel = Hotel.builder()
            .operaPropertyId(operaPropertyId)
            .name("Hotel from OPERA ID: " + operaPropertyId)
            .owner(owner)
            .createdBy(owner.getEmail())
            .createdAt(System.currentTimeMillis())
            .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelMapper.toDTO(savedHotel);
    }

    public HotelDTO getHotelById(String id) {
        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
        return hotelMapper.toDTO(hotel);
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
            .map(hotelMapper::toDTO)
            .collect(Collectors.toList());
    }

    public List<HotelDTO> getHotelsByOwner(String ownerId) {
        return hotelRepository.findByOwnerId(ownerId).stream()
            .map(hotelMapper::toDTO)
            .collect(Collectors.toList());
    }

    public HotelDTO updateHotel(String id, HotelDTO hotelDTO, String userId) {
        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        // Check if user is owner or admin
        if (!hotel.getOwner().getId().equals(userId)
            && !user.getRole().getRoleType().equals(Role.RoleType.ADMIN)) {
            throw new RuntimeException("You don't have permission to update this hotel");
        }

        hotel.setName(hotelDTO.getName());
        hotel.setAddressLine1(hotelDTO.getAddressLine1());
        hotel.setCity(hotelDTO.getCity());
        hotel.setCountry(hotelDTO.getCountry());
        hotel.setDescription(hotelDTO.getDescription());
        hotel.setPhone(hotelDTO.getPhone());
        hotel.setRating(hotelDTO.getRating());

        if (!hotel.getEmail().equals(hotelDTO.getEmail())) {
            if (hotelRepository.existsByEmail(hotelDTO.getEmail())) {
                throw new RuntimeException("Hotel with this email already exists");
            }
            hotel.setEmail(hotelDTO.getEmail());
        }

        // Handle photo update
        if (hotelDTO.getPhoto() != null && !hotelDTO.getPhoto().isBlank()) {
            String photoFilename = savePhoto(hotelDTO.getPhoto());
            hotel.setPhoto(photoFilename);
        }

        hotel.setUpdatedBy(user.getEmail());
        hotel.setUpdatedAt(System.currentTimeMillis());

        Hotel updatedHotel = hotelRepository.save(hotel);
        return hotelMapper.toDTO(updatedHotel);
    }

    public void deleteHotel(String id, String userId) {
        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Hotel hotel = hotelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));

        // Check if user is owner or admin
        if (!hotel.getOwner().getId().equals(userId)
            && !user.getRole().getRoleType().equals(Role.RoleType.ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this hotel");
        }

        hotelRepository.delete(hotel);
    }
}



