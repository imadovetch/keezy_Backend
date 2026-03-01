package org.bloomberg.keezy_back.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning reservation details to client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private String id;
    private String guestId;
    private String guestEmail;
    private String serviceName;
    private String serviceId;
    private String hotelName;
    private String hotelId;
    private String reservationDate;
    private String reservationTime;
    private String status;
    private Integer numberOfGuests;
    private String notes;
    private Boolean reclamation;
    private Long createdAt;
    private Long updatedAt;
}
