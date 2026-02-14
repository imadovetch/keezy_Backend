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
    
    // Services belonging to a specific owner (either specific hotel or free for all hotels)
    List<Service> findByOwnerId(String ownerId);
    
    // Services for a specific hotel belonging to an owner
    @Query("SELECT s FROM Service s WHERE s.owner.id = :ownerId AND (s.hotel.uuid = :hotelId OR s.hotel IS NULL)")
    List<Service> findOwnerServicesByHotel(@Param("ownerId") String ownerId, @Param("hotelId") String hotelId);
    
    // All services for a specific hotel (for guest access)
    @Query("SELECT s FROM Service s WHERE s.hotel.uuid = :hotelId AND (s.hotel IS NOT NULL)")
    List<Service> findAllServicesByHotel(@Param("hotelId") String hotelId);
    
    // Services for a specific hotel by type (for guest access)
    @Query("SELECT s FROM Service s WHERE s.hotel.uuid = :hotelId AND s.type = :type AND (s.hotel IS NOT NULL)")
    List<Service> findServicesByHotelAndType(@Param("hotelId") String hotelId, @Param("type") String type);
}
