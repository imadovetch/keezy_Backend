package org.bloomberg.keezy_back.Mapper;

import org.bloomberg.keezy_back.DTO.ServiceDTO;
import org.bloomberg.keezy_back.Entity.Service;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Service Entity and ServiceDTO
 * Handles conversion of list fields (images, dates, time slots) between String and List<String>
 */
@Component
public class ServiceMapper {

    /**
     * Convert Service Entity to ServiceDTO
     */
    public ServiceDTO toDTO(Service service) {
        if (service == null) {
            return null;
        }

        return ServiceDTO.builder()
                .uuid(service.getId())
                .title(service.getTitle())
                .category(service.getCategory())
                .subcategory(service.getSubcategory())
                .type(service.getType())
                .description(service.getDescription())
                .hotelId(service.getHotel() != null ? service.getHotel().getUuid() : null)
                .price(service.getPrice())
                .images(stringToList(service.getImages()))
                .selectedDates(stringToList(service.getSelectedDates()))
                .timeSlots(stringToList(service.getTimeSlots()))
                .createdBy(service.getCreatedBy())
                .createdAt(service.getCreatedAt())
                .updatedBy(service.getUpdatedBy())
                .updatedAt(service.getUpdatedAt())
                .build();
    }

    /**
     * Convert ServiceDTO to Service Entity
     * Note: Hotel entity must be set separately
     */
    public Service toEntity(ServiceDTO dto) {
        if (dto == null) {
            return null;
        }

        return Service.builder()
                .id(dto.getUuid())
                .title(dto.getTitle())
                .category(dto.getCategory())
                .subcategory(dto.getSubcategory())
                .type(dto.getType())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .images(listToString(dto.getImages()))
                .selectedDates(listToString(dto.getSelectedDates()))
                .timeSlots(listToString(dto.getTimeSlots()))
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .updatedBy(dto.getUpdatedBy())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    /**
     * Convert comma-separated string to List<String>
     */
    private List<String> stringToList(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Convert List<String> to comma-separated string
     */
    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }
}
