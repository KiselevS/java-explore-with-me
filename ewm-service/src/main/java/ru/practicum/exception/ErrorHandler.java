package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleBadRequestException(BadRequestException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.FORBIDDEN)
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDateTimeParseException(DateTimeParseException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Can't parse string to date")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Validation failed")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowableExceptions(Throwable e) {
        return new ApiError.ApiErrorBuilder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Error occurred")
                .message(e.getMessage())
                .build();
    }
}
