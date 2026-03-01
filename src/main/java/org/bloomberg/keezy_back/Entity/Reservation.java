package org.bloomberg.keezy_back.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a service reservation by a guest
 * Guest reserves a specific service at a specific date and time at a hotel
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Reference to the guest who made the reservation
     * Must be authenticated user with GUEST role
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", nullable = false)
    private AppUser guest;

    /**
     * Reference to the service being reserved
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    /**
     * Reference to the hotel where service is being reserved
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    /**
     * Reserved date (format: yyyy-MM-dd)
     * Must be one of the available dates in the service
     */
    @Column(nullable = false)
    private String reservationDate;

    /**
     * Reserved time slot (format: HH:mm)
     * Must be one of the available time slots in the service
     */
    @Column(nullable = false)
    private String reservationTime;

    /**
     * Reservation status
     * PENDING , CONFIRMED, CANCELLED
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    /**
     * Number of guests for this reservation
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer numberOfGuests = 1;

    /**
     * Special notes/requests from the guest
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Whether this reservation is a reclamation (true) or regular reservation (false)
     * Defaults to false
     */
    @Column(nullable = false)
    private Boolean reclamation = false;

    // Audit fields
    private String createdBy;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
}
