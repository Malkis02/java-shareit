package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IdValidationException extends RuntimeException {
    public IdValidationException(String s) {
        super(s);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
