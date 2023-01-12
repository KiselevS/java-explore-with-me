package ru.practicum.excption;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
