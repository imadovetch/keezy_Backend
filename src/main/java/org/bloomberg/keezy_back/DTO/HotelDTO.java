package org.bloomberg.keezy_back.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {

    private String uuid;

    // Either this can be provided alone (opera property flow)
    // or the detailed hotel fields can be provided
    private String operaPropertyId;

    private String name;
    private String addressLine1;
    private String city;
    private String country;
    private String description;
    private String phone;
    private String email;
    private String rating;
    private String photo;


    private String createdBy;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;

}


