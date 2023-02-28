package ru.practicum.shareit.exception;


public class IllegalTimeException extends RuntimeException {
    public IllegalTimeException(String s) {
        super(s);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
