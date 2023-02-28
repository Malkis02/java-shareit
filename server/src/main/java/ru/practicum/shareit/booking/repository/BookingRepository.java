package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findAllByBookerOrderByStartDesc(UserEntity booker,
                                                        Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.booker = :booker and :now between b.start and b.end order by b.start desc ")
    List<BookingEntity> findCurrentByBookerOrderByStartDesc(@Param("booker") UserEntity booker, @Param("now") LocalDateTime now,
                                            Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.booker = :booker and b.end < :now order by b.start desc ")
    List<BookingEntity> findPastByBookerOrderByStartDesc(@Param("booker") UserEntity booker, @Param("now") LocalDateTime now,
                                         Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.booker = :booker and b.start > :now order by b.start desc ")
    List<BookingEntity> findFutureByBookerOrderByStartDesc(@Param("booker") UserEntity booker, @Param("now") LocalDateTime now,
                                           Pageable pageable);

    List<BookingEntity> findAllByBookerAndStatusOrderByStartDesc(
            @Param("booker") UserEntity booker,
            @Param("status") BookingStatus status,
            Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.item.owner = :owner and b.status = :status order by b.start desc ")
    List<BookingEntity> findAllByOwnerItemsAndStatusOrderByStartDesc(
            @Param("owner") UserEntity owner,
            @Param("status") BookingStatus status,
            Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.item.owner = :owner order by b.start desc ")
    List<BookingEntity> findAllByOwnerItemsOrderByStartDesc(@Param("owner") UserEntity owner,
                                            Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.item.owner = :owner and :now between b.start and b.end order by b.start desc ")
    List<BookingEntity> findCurrentByOwnerItemsOrderByStartDesc(@Param("owner") UserEntity owner, @Param("now") LocalDateTime now,
                                                Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.item.owner = :owner and b.end < :now order by b.start desc ")
    List<BookingEntity> findPastByOwnerItemsOrderByStartDesc(@Param("owner") UserEntity owner, @Param("now") LocalDateTime now,
                                             Pageable pageable);

    @Query("select b from BookingEntity b " +
            "where b.item.owner = :owner and b.start > :now order by b.start desc")
    List<BookingEntity> findFutureByOwnerItemsOrderByStartDesc(@Param("owner") UserEntity owner, @Param("now") LocalDateTime now,
                                               Pageable pageable);

    List<BookingEntity> findAllByItem(ItemEntity item);

    Optional<BookingEntity> findFirstByItemAndStatusIsOrderByStartAsc(ItemEntity item, BookingStatus status);

    Optional<BookingEntity> findFirstByItemAndStatusIsOrderByEndDesc(ItemEntity item, BookingStatus status);

    boolean existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime end);
}
