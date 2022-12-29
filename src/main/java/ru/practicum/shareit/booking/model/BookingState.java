package ru.practicum.shareit.booking.model;


public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNSUPPORTED_STATUS;

//    public static BookingState of(String value) {
//        return Arrays.stream(values())
//                .filter(item -> Objects.equals(value,item.name()))
//                .findFirst()
//                .orElse(UNKNOWN);
//    }
}
