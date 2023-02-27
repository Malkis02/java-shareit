package java.ru.practicum.shareit.booking.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemEntity item;

    private UserEntity booker;

    @BeforeEach
    void setUp() {
        this.item = createItem("Дрель","Электрическая дрель",false,
                createUser(1L,"Иван","ivan@yandex.ru"));
        this.booker = createUser(2L,"Петр","petr@yandex.ru");

    }

    @Test
    void testFindAllByBooker() {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);

        var result = repository.findAllByBookerOrderByStartDesc(booker, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindCurrentByBooker() {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findCurrentByBookerOrderByStartDesc(booker,now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindPastByBooker() {
        var start = LocalDateTime.now().minusDays(2);
        var end = LocalDateTime.now().minusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findPastByBookerOrderByStartDesc(booker,now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindFutureByBooker() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(5);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findFutureByBookerOrderByStartDesc(booker,now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindWaitingByBooker() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(5);
        var booking = createBooking(start,end,item,booker,BookingStatus.WAITING);

        var result = repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.WAITING, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindRejectedByBooker() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(5);
        var booking = createBooking(start,end,item,booker,BookingStatus.REJECTED);

        var result = repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.REJECTED, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindAllByOwnerItems() {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);

        var result = repository.findAllByOwnerItemsOrderByStartDesc(item.getOwner(), Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindCurrentByOwnerItems() {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findCurrentByOwnerItemsOrderByStartDesc(item.getOwner(),now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindPastByOwnerItems() {
        var start = LocalDateTime.now().minusDays(2);
        var end = LocalDateTime.now().minusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findPastByOwnerItemsOrderByStartDesc(item.getOwner(),now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindFutureByOwnerItems() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(4);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.findFutureByOwnerItemsOrderByStartDesc(item.getOwner(),now, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindWaitingByOwnerItems() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(4);
        var booking = createBooking(start,end,item,booker,BookingStatus.WAITING);

        var result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(item.getOwner(),BookingStatus.WAITING, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindRejectedByOwnerItems() {
        var start = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(4);
        var booking = createBooking(start,end,item,booker,BookingStatus.REJECTED);

        var result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(item.getOwner(),BookingStatus.REJECTED, Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testFindAllByItem() {
        var start = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);

        var result = repository.findAllByItem(item);
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(booking.getId(),result.get(0).getId());
        assertEquals(booking.getStatus(),result.get(0).getStatus());
    }

    @Test
    void testExistBookingByItem() {
        var start = LocalDateTime.now().minusDays(3);
        var end = LocalDateTime.now().minusDays(2);
        var booking = createBooking(start,end,item,booker,BookingStatus.APPROVED);
        var now = LocalDateTime.now();

        var result = repository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(item.getId(),booker.getId(),BookingStatus.APPROVED,now);
        assertTrue(result);
    }


    private ItemEntity createItem(String name, String description, boolean available, UserEntity owner) {
        var itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setName(name);
        itemEntity.setDescription(description);
        itemEntity.setAvailable(available);
        itemEntity.setOwner(owner);
        return itemRepository.save(itemEntity);
    }

    private UserEntity createUser(Long userId, String name, String email) {
        var bookerEntity = new UserEntity();
        bookerEntity.setId(userId);
        bookerEntity.setName(name);
        bookerEntity.setEmail(email);
        return userRepository.save(bookerEntity);
    }

    private BookingEntity createBooking(LocalDateTime start, LocalDateTime end, ItemEntity item, UserEntity booker, BookingStatus status) {
        var booking = new BookingEntity();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return repository.save(booking);
    }
}
