package org.bloomberg.keezy_back.Mapper;

import org.bloomberg.keezy_back.DTO.HotelDTO;
import org.bloomberg.keezy_back.Entity.Hotel;
import org.bloomberg.keezy_back.Entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    // Mapping manuel pour éviter la dépendance externe
    public HotelDTO toDTO(Hotel hotel) {
        if (hotel == null) return null;
        HotelDTO dto = new HotelDTO();
        dto.setUuid(hotel.getUuid());
        dto.setOperaPropertyId(hotel.getOperaPropertyId());
        dto.setName(hotel.getName());
        dto.setAddressLine1(hotel.getAddressLine1());
        dto.setCity(hotel.getCity());
        dto.setCountry(hotel.getCountry());
        dto.setDescription(hotel.getDescription());
        dto.setPhone(hotel.getPhone());
        dto.setEmail(hotel.getEmail());
        dto.setRating(hotel.getRating());
        dto.setPhoto(hotel.getPhoto());
        dto.setCreatedBy(hotel.getCreatedBy());
        dto.setCreatedAt(hotel.getCreatedAt());
        dto.setUpdatedBy(hotel.getUpdatedBy());
        dto.setUpdatedAt(hotel.getUpdatedAt());
        return dto;
    }

    public Hotel toEntity(HotelDTO dto) {
        if (dto == null) return null;
        Hotel hotel = new Hotel();
        hotel.setUuid(dto.getUuid());
        hotel.setOperaPropertyId(dto.getOperaPropertyId());
        hotel.setName(dto.getName());
        hotel.setAddressLine1(dto.getAddressLine1());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setDescription(dto.getDescription());
        hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail());
        hotel.setRating(dto.getRating());
        hotel.setPhoto(dto.getPhoto());
        hotel.setCreatedBy(dto.getCreatedBy());
        hotel.setCreatedAt(dto.getCreatedAt());
        hotel.setUpdatedBy(dto.getUpdatedBy());
        hotel.setUpdatedAt(dto.getUpdatedAt());
        // owner must be set by service layer after resolving the user entity
        return hotel;
    }
}
