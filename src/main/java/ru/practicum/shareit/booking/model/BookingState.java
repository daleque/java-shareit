package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> fromStringToState(String state) {
        if ((BookingState.valueOf(state)) != null) {
            return Optional.of(BookingState.valueOf(state));
        } else {
            return Optional.empty();
        }
    }
}
