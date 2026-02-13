package org.bloomberg.keezy_back.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating and managing hotel services/amenities
 * Used to create service offerings like breakfast, dining, activities, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {

    private String uuid;
    
    private String title;
    
    private String category;  // e.g., "Food & Beverage"
    
    private String subcategory;  // e.g., optional subcategory
    
    private String type;  // e.g., "Breakfast"
    
    private String description;
    
    private String hotelId;  // The hotel this service belongs to
    
    private Double price;
    
    // Array of images (base64 encoded or file paths)
    private List<String> images;
    
    // Selected dates for availability (e.g., ["05 Feb 2026", "10 Feb 2026"])
    private List<String> selectedDates;
    
    // Time slots for the service (e.g., ["09:00 - 09:30", "08:00 - 08:30"])
    private List<String> timeSlots;
    
    // Audit fields
    private String createdBy;  // User ID of hotel owner
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
    
}
