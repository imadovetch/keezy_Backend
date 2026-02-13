package org.bloomberg.keezy_back.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity representing a hotel service/amenity
 * Examples: Breakfast, Dinner, Spa services, Activities, etc.
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String category;  // e.g., "Food & Beverage", "Activities", "Wellness"

    private String subcategory;  // Optional subcategory

    private String type;  // e.g., "Breakfast", "Lunch", "Dinner"

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    private Double price;

    // Comma-separated list of image filenames
    @Column(columnDefinition = "TEXT")
    private String images;

    // Comma-separated list of available dates
    @Column(columnDefinition = "TEXT")
    private String selectedDates;

    // Comma-separated list of time slots (e.g., "09:00 - 09:30,08:00 - 08:30")
    @Column(columnDefinition = "TEXT")
    private String timeSlots;

    // Audit fields
    private String createdBy;  // User ID of hotel owner
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
}
