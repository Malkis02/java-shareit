package ru.practicum.shareit.exception;


public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(String s) {
        super(s);
    }

    public ItemNotAvailableException() {
    }

    public String getMessage() {
        return super.getMessage();
    }
}
