package org.bloomberg.keezy_back.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * DTO for creating a new reservation
 * Guest provides: service ID, hotel ID, date, time, and number of guests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationDTO {

    @NotBlank(message = "Service ID is required")
    private String serviceId;

    @NotBlank(message = "Hotel ID is required")
    private String hotelId;

    @NotBlank(message = "Reservation date is required (format: yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd format")
    private String reservationDate;

    @NotBlank(message = "Reservation time is required (format: HH:mm)")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in HH:mm format")
    private String reservationTime;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 100, message = "Number of guests cannot exceed 100")
    private Integer numberOfGuests;

    private String notes;

    /**
     * Whether this is a reclamation (true) or regular reservation (false)
     * Optional, defaults to false
     */
    private Boolean reclamation;

}