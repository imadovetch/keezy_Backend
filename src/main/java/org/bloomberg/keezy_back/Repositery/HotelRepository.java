package org.bloomberg.keezy_back.Repositery;

import org.bloomberg.keezy_back.Entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {
    Optional<Hotel> findByOperaPropertyId(String operaPropertyId);
    List<Hotel> findByOwnerId(String ownerId);
    Optional<Hotel> findByEmail(String email);
    boolean existsByEmail(String email);
}

