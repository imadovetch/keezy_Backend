package org.bloomberg.keezy_back.Repositery;

import org.bloomberg.keezy_back.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    /**
     * Find all reservations for a specific guest
     */
    List<Reservation> findByGuestId(String guestId);

    /**
     * Find all reservations for a specific service
     */
    List<Reservation> findByServiceId(String serviceId);

    /**
     * Find all reservations for a specific hotel by UUID
     */
    @Query("SELECT r FROM Reservation r WHERE r.hotel.uuid = :hotelUuid")
    List<Reservation> findByHotelUuid(String hotelUuid);

    /**
     * Find reservations for a specific service on a specific date
     */
    List<Reservation> findByServiceIdAndReservationDate(String serviceId, String reservationDate);

    /**
     * Find existing reservation for same service, date and time
     * Used to prevent double booking
     */
    @Query("SELECT r FROM Reservation r WHERE r.service.id = :serviceId " +
           "AND r.reservationDate = :reservationDate " +
           "AND r.reservationTime = :reservationTime " +
           "AND r.status != 'CANCELLED'")
    List<Reservation> findExistingReservations(
        @Param("serviceId") String serviceId,
        @Param("reservationDate") String reservationDate,
        @Param("reservationTime") String reservationTime
    );

    /**
     * Find all active reservations for a guest
     */
    @Query("SELECT r FROM Reservation r WHERE r.guest.id = :guestId " +
           "AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsByGuest(@Param("guestId") String guestId);

    /**
     * Find all reservations for a hotel with pagination
     */
    List<Reservation> findByHotelUuidOrderByCreatedAtDesc(String hotelUuid);
}
