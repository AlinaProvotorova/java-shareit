package ru.practicum.shareit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.ErrorMessage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundException(final NotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage constraintViolationException(final ConstraintViolationException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage unknownStateException(final UnknownStateException e) {
        return new ErrorMessage("Unknown state: " + e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(EmailDuplicateException.class)
    public ErrorMessage emailDuplicateException(final EmailDuplicateException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorMessage illegalArgumentException(final IllegalArgumentException e) {
        log.info(e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage defaultHandlerExceptionResolver(final MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String errorMessage = Objects.requireNonNull(
                result.getFieldError()).getField() + " " + result.getFieldError().getDefaultMessage();
        log.info(errorMessage);
        return new ErrorMessage(errorMessage);
    }
}
