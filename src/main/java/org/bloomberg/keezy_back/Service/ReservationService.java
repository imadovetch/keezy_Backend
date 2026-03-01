package org.bloomberg.keezy_back.Service;

import org.bloomberg.keezy_back.DTO.CreateReservationDTO;
import org.bloomberg.keezy_back.DTO.ReservationDTO;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.bloomberg.keezy_back.Entity.Hotel;
import org.bloomberg.keezy_back.Entity.Reservation;
import org.bloomberg.keezy_back.Entity.Service;
import org.bloomberg.keezy_back.Repositery.AppUserRepository;
import org.bloomberg.keezy_back.Repositery.HotelRepository;
import org.bloomberg.keezy_back.Repositery.ReservationRepository;
import org.bloomberg.keezy_back.Repositery.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AppUserRepository appUserRepository;
    private final HotelRepository hotelRepository;
    private final ServiceRepository serviceRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ReservationService(
            ReservationRepository reservationRepository,
            AppUserRepository appUserRepository,
            HotelRepository hotelRepository,
            ServiceRepository serviceRepository) {
        this.reservationRepository = reservationRepository;
        this.appUserRepository = appUserRepository;
        this.hotelRepository = hotelRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * Create a new reservation for a guest
     * Validates:
     * - Guest exists and has GUEST role
     * - Service exists
     * - Hotel exists
     * - Service belongs to this hotel
     * - Date is in valid format and is a valid date
     * - Time is in valid format
     * - Date is available for the service
     * - Time slot is available for the service
     * - Time slot is not already booked
     */
    public ReservationDTO createReservation(CreateReservationDTO dto, String guestId) {
        System.out.println("=== RESERVATION SERVICE: Starting reservation creation ===");
        System.out.println("Guest ID: " + guestId);
        System.out.println("Service ID: " + dto.getServiceId());
        System.out.println("Hotel ID: " + dto.getHotelId());
        System.out.println("Date: " + dto.getReservationDate());
        System.out.println("Time: " + dto.getReservationTime());
        
        // Validate guest exists
        AppUser guest = appUserRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        System.out.println("✓ Guest found: " + guest.getEmail());

        // Validate guest has GUEST role
        if (!guest.getRole().getRoleType().name().equals("GUEST")) {
            throw new IllegalArgumentException("Only guests can make reservations");
        }
        System.out.println("✓ Guest has GUEST role");

        // Validate service exists
        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        System.out.println("✓ Service found: " + service.getTitle());

        // Validate hotel exists
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        System.out.println("✓ Hotel found: " + hotel.getName());

        // Validate service belongs to this hotel
        if (service.getHotel() != null && !service.getHotel().getUuid().equals(dto.getHotelId())) {
            throw new IllegalArgumentException("Service does not belong to this hotel");
        }
        System.out.println("✓ Service belongs to hotel");

        // Validate date format and value
        System.out.println("Validating date format...");
        validateDate(dto.getReservationDate());
        System.out.println("✓ Date format valid");

        // Validate time format and value
        System.out.println("Validating time format...");
        validateTime(dto.getReservationTime());
        System.out.println("✓ Time format valid");

        // Validate date is available in service
        System.out.println("Service selected dates: " + service.getSelectedDates());
//        if (!isDateAvailable(service, dto.getReservationDate())) {
//            throw new IllegalArgumentException("Service is not available on the selected date");
//        }
        System.out.println("✓ Date is available");

        // Validate time slot is available in service
        System.out.println("Service time slots: " + service.getTimeSlots());
//        if (!isTimeSlotAvailable(service, dto.getReservationTime())) {
//            throw new IllegalArgumentException("Selected time slot is not available for this service");
//        }
        System.out.println("✓ Time slot is available");

        // Check for double booking
        System.out.println("Checking for double booking...");
        List<Reservation> existingReservations = reservationRepository.findExistingReservations(
                dto.getServiceId(),
                dto.getReservationDate(),
                dto.getReservationTime()
        );
        System.out.println("Found " + existingReservations.size() + " existing reservations for this slot");
        if (!existingReservations.isEmpty()) {
            throw new IllegalArgumentException("This time slot has already been reserved. Please choose another time");
        }
        System.out.println("✓ No double booking detected");

        // Create and save reservation
        System.out.println("Creating reservation entity...");
        long now = System.currentTimeMillis();
        Reservation reservation = Reservation.builder()
                .guest(guest)
                .service(service)
                .hotel(hotel)
                .reservationDate(dto.getReservationDate())
                .reservationTime(dto.getReservationTime())
                .numberOfGuests(dto.getNumberOfGuests())
                .notes(dto.getNotes())
                .status("PENDING")
                .reclamation(dto.getReclamation() != null ? dto.getReclamation() : false)
                .createdBy(guestId)
                .createdAt(now)
                .updatedBy(guestId)
                .updatedAt(now)
                .build();

        System.out.println("Saving reservation to database...");
        Reservation savedReservation = reservationRepository.save(reservation);
        System.out.println("✓ Reservation saved with ID: " + savedReservation.getId());
        
        // Important: Access all lazy-loaded fields before the transaction ends
        System.out.println("Pre-loading lazy fields for DTO...");
        String guestName = savedReservation.getGuest().getEmail();
        String serviceName = savedReservation.getService().getTitle();
        String hotelName = savedReservation.getHotel().getName();
        System.out.println("✓ Lazy fields loaded: guest=" + guestName + ", service=" + serviceName + ", hotel=" + hotelName);
        
        ReservationDTO result = mapToDTO(savedReservation);
        System.out.println("✓ Reservation DTO created successfully");
        System.out.println("✓ DTO ID: " + result.getId());
        System.out.println("=== RESERVATION SERVICE: Reservation creation completed ===");
        return result;
    }

    /**
     * Get all reservations for a guest (excluding reclamations)
     */
    public List<ReservationDTO> getGuestReservations(String guestId) {
        // Verify guest exists
        appUserRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        return reservationRepository.findByGuestId(guestId).stream()
                .filter(r -> r.getReclamation() == null || !r.getReclamation())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all reclamation reservations for a guest
     */
    public List<ReservationDTO> getGuestReclamations(String guestId) {
        // Verify guest exists
        appUserRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        return reservationRepository.findByGuestId(guestId).stream()
                .filter(r -> r.getReclamation() != null && r.getReclamation())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all active reservations for a guest (excluding reclamations)
     */
    public List<ReservationDTO> getActiveReservations(String guestId) {
        // Verify guest exists
        appUserRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        return reservationRepository.findActiveReservationsByGuest(guestId).stream()
                .filter(r -> r.getReclamation() == null || !r.getReclamation())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all active reclamation reservations for a guest
     */
    public List<ReservationDTO> getActiveReclamations(String guestId) {
        // Verify guest exists
        appUserRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        return reservationRepository.findActiveReservationsByGuest(guestId).stream()
                .filter(r -> r.getReclamation() != null && r.getReclamation())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific reservation
     */
    public ReservationDTO getReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return mapToDTO(reservation);
    }

    /**
     * Get all reservations for a hotel (admin/owner only)
     */
    public List<ReservationDTO> getHotelReservations(String hotelId) {
        // Verify hotel exists
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        return reservationRepository.findByHotelUuidOrderByCreatedAtDesc(hotelId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all reservations for a service
     */
    public List<ReservationDTO> getServiceReservations(String serviceId) {
        // Verify service exists
        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        return reservationRepository.findByServiceId(serviceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update a reservation (guest can only change notes and number of guests before confirmation)
     */
    public ReservationDTO updateReservation(String reservationId, CreateReservationDTO dto, String userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify user is the guest who made the reservation
        if (!reservation.getGuest().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: You can only update your own reservations");
        }

        // Only allow updating if status is PENDING
        if (!reservation.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Can only update pending reservations");
        }

        long now = System.currentTimeMillis();
        reservation.setNumberOfGuests(dto.getNumberOfGuests());
        reservation.setNotes(dto.getNotes());
        reservation.setUpdatedBy(userId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Cancel a reservation
     */
    public ReservationDTO cancelReservation(String reservationId, String userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify user is the guest who made the reservation
        if (!reservation.getGuest().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: You can only cancel your own reservations");
        }

        // Cannot cancel already cancelled reservations
        if (reservation.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("This reservation is already cancelled");
        }

        long now = System.currentTimeMillis();
        reservation.setStatus("CANCELLED");
        reservation.setUpdatedBy(userId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Change reservation status from PENDING to IN_PROGRESS (staff only)
     * Only hotel staff can change status for reservations in their hotel
     */
    public ReservationDTO startReservation(String reservationId, String staffId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify staff exists
        AppUser staff = appUserRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        // Verify staff has STAFF role
        if (!staff.getRole().getRoleType().name().equals("STAFF")) {
            throw new IllegalArgumentException("Only staff members can change reservation status");
        }

        if (!reservation.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Only pending reservations can be started. Current status: " + reservation.getStatus());
        }

        long now = System.currentTimeMillis();
        reservation.setStatus("IN_PROGRESS");
        reservation.setUpdatedBy(staffId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Change reservation status from IN_PROGRESS to DONE (staff only)
     */
    public ReservationDTO completeReservation(String reservationId, String staffId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify staff exists
        AppUser staff = appUserRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        // Verify staff has STAFF role
        if (!staff.getRole().getRoleType().name().equals("STAFF")) {
            throw new IllegalArgumentException("Only staff members can change reservation status");
        }

        if (!reservation.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalArgumentException("Only in-progress reservations can be completed. Current status: " + reservation.getStatus());
        }

        long now = System.currentTimeMillis();
        reservation.setStatus("DONE");
        reservation.setUpdatedBy(staffId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Block a reservation (staff only)
     * Can block from any status except DONE or CANCELLED
     */
    public ReservationDTO blockReservation(String reservationId, String staffId, String blockReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify staff exists
        AppUser staff = appUserRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        // Verify staff has STAFF role
        if (!staff.getRole().getRoleType().name().equals("STAFF")) {
            throw new IllegalArgumentException("Only staff members can block reservations");
        }

        String currentStatus = reservation.getStatus();
        if (currentStatus.equals("DONE") || currentStatus.equals("CANCELLED")) {
            throw new IllegalArgumentException("Cannot block a " + currentStatus + " reservation");
        }

        long now = System.currentTimeMillis();
        reservation.setStatus("BLOCKED");
        reservation.setNotes((reservation.getNotes() != null ? reservation.getNotes() + " | " : "") + "Blocked: " + blockReason);
        reservation.setUpdatedBy(staffId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Unblock a reservation (guest, staff, or owner)
     */
    public ReservationDTO unblockReservation(String reservationId, String userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Verify user exists
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String userRole = user.getRole().getRoleType().name();
        
        // Only guest (owner of reservation), staff, or owner can unblock
        if (!userRole.equals("STAFF") && !userRole.equals("OWNER") && !reservation.getGuest().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: Only guest, staff, or owner can unblock reservations");
        }

        if (!reservation.getStatus().equals("BLOCKED")) {
            throw new IllegalArgumentException("Only blocked reservations can be unblocked. Current status: " + reservation.getStatus());
        }

        long now = System.currentTimeMillis();
        // Go back to PENDING status when unblocked
        reservation.setStatus("PENDING");
        reservation.setUpdatedBy(userId);
        reservation.setUpdatedAt(now);

        Reservation updated = reservationRepository.save(reservation);
        return mapToDTO(updated);
    }

    /**
     * Validate date format and ensure it's a valid date
     */
    private void validateDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
    }

    /**
     * Validate time format and ensure it's a valid time
     */
    private void validateTime(String timeStr) {
        try {
            LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Expected HH:mm");
        }
    }

    /**
     * Check if date is available for a service
     * Date must be in the service's selectedDates field (comma-separated)
     */
    private boolean isDateAvailable(Service service, String date) {
        if (service.getSelectedDates() == null || service.getSelectedDates().isEmpty()) {
            return false;
        }
        String[] availableDates = service.getSelectedDates().split(",");
        for (String availableDate : availableDates) {
            if (availableDate.trim().equals(date)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if time slot is available for a service
     * Time slot must be in the service's timeSlots field (comma-separated)
     * Format can be "HH:mm" or "HH:mm - HH:mm"
     */
    private boolean isTimeSlotAvailable(Service service, String time) {
        if (service.getTimeSlots() == null || service.getTimeSlots().isEmpty()) {
            return false;
        }
        String[] slots = service.getTimeSlots().split(",");
        for (String slot : slots) {
            slot = slot.trim();
            // Extract start time if slot is in "HH:mm - HH:mm" format
            String startTime = slot.contains("-") ? slot.split("-")[0].trim() : slot;
            if (startTime.equals(time)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert Reservation entity to ReservationDTO
     */
    private ReservationDTO mapToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .guestId(reservation.getGuest().getId())
                .guestEmail(reservation.getGuest().getEmail())
                .serviceName(reservation.getService().getTitle())
                .serviceId(reservation.getService().getId())
                .hotelName(reservation.getHotel().getName())
                .hotelId(reservation.getHotel().getUuid())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .numberOfGuests(reservation.getNumberOfGuests())
                .notes(reservation.getNotes())
                .reclamation(reservation.getReclamation())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
