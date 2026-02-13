package org.bloomberg.keezy_back.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(unique = true)
    private String operaPropertyId;   // OPERA ID not required

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    private String addressLine1;
    private String city;
    private String country;
    private String phone;
    private String description;
    private String email;
    private String rating;
    private String photo;



    private String createdBy;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
}

