package org.bloomberg.keezy_back.Repositery;

import org.bloomberg.keezy_back.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {
    
    List<Service> findByHotelUuid(String hotelUuid);
    
    List<Service> findByHotelUuidAndCategory(String hotelUuid, String category);
    
    Optional<Service> findByIdAndHotelUuid(String serviceId, String hotelUuid);
    
    @Query("SELECT COUNT(s) FROM Service s WHERE s.hotel.uuid = :hotelUuid")
    int countByHotelUuid(@Param("hotelUuid") String hotelUuid);
}
