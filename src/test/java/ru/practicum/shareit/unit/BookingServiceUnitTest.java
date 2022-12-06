package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    private final User ownerUser = new User(1L, "Name", "test@test.ru");
    private final User someUser = new User(2L, "Name 2", "test2@test.ru");
    private final Item item = new Item(1L, "Клей", "Секундный клей момент", true, ownerUser,
            null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            item, someUser, BookingStatus.WAITING);
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    BookingService bookingService;

    @BeforeEach
    public void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void shouldValidateExceptionItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Exception thrown = assertThrows(ValidationException.class, () -> bookingService.create(1L, booking));
        assertEquals("Товар недоступен", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionBookerIsOwnerItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(ownerUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setBooker(ownerUser);
        Exception thrown = assertThrows(ObjectNotFoundException.class, () -> bookingService.create(1L, booking));
        assertEquals("Пользователь является владельцем предмета", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionStartInPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setStart(LocalDateTime.now().minusDays(1));
        Exception thrown = assertThrows(ValidationException.class, () -> bookingService.create(1L, booking));
        assertEquals("Время начала бронирования некорректно", thrown.getMessage());
    }

    @Test
    public void shouldValidateExceptionEndBeforeStart() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        Exception thrown = assertThrows(ValidationException.class, () -> bookingService.create(1L, booking));
        assertEquals("Время окончания бронирования некорректно", thrown.getMessage());
    }

    @Test
    public void shouldExceptionBookingNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Бронирование не найдено", thrown.getMessage());
    }

    @Test
    public void shouldExceptionGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getByIdEndUserId(1, 2));
        assertEquals("Бронирование не найдено", thrown.getMessage());

    }

    @Test
    public void shouldExceptionUserNotFoundGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getByIdEndUserId(1, 2));
        assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    public void shouldExceptionUserNotFound() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    public void shouldExceptionApprovedIsNull() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(ownerUser));
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.approveBooking(1, 2, null));
        assertEquals("Одобрение пустое", thrown.getMessage());
    }

    @Test
    public void shouldExceptionUserNotOwnerItem() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.approveBooking(2, 2, true));
        assertEquals("Пользователь не владелец предмета", thrown.getMessage());
    }

    @Test
    public void shouldExceptionBookingStatusIsApprovedTrue() {
        Booking bookingApproved = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingApproved));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Статус бронирования уже одобрен", thrown.getMessage());
        bookingApproved.setStatus(BookingStatus.APPROVED);
        Exception thrownExcept = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1, 2, true));
        assertEquals("Статус бронирования уже одобрен", thrownExcept.getMessage());
    }

    @Test
    public void shouldExceptionBookingStatusIsApprovedFalse() {
        Booking bookingRejected = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, someUser, BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingRejected));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Exception thrown = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1, 2, false));
        assertEquals("Статус бронирования уже отклонен", thrown.getMessage());
        bookingRejected.setStatus(BookingStatus.WAITING);
        bookingRejected.setStatus(BookingStatus.REJECTED);
        Exception thrownExcept = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1, 2, false));
        assertEquals("Статус бронирования уже отклонен", thrownExcept.getMessage());
    }

    @Test
    public void shouldExceptionСreate() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.create(1, booking));
        assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    public void shouldExceptionCreateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.create(1, booking));
        assertEquals("Предмет не найден", thrown.getMessage());
    }

    @Test
    public void shouldExceptionGetById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.getByIdEndUserId(1, 1));
        assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    public void shouldGetByIdEndUserId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(someUser));
        Booking bookingTest = bookingService.getByIdEndUserId(1, 2);
        assertEquals(booking, bookingTest);
        assertEquals(2, booking.getBooker().getId());
        assertEquals(1, booking.getItem().getOwner().getId());
    }
}
