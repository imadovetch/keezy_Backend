package org.bloomberg.keezy_back.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for updating reservation status with optional reason/notes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationStatusDTO {

    private String reason;  // Optional reason for status change (e.g., block reason, cancellation reason)
}
