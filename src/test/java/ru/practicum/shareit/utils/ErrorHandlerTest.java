package ru.practicum.shareit.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.ErrorMessage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found");
        ErrorMessage result = errorHandler.notFoundException(exception);

        assertEquals("Not found", result.getError());
    }

    @Test
    void testConstraintViolationException() {
        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", null);
        ErrorMessage result = errorHandler.constraintViolationException(exception);

        assertEquals("Constraint violation", result.getError());
    }

    @Test
    void testUnknownStateException() {
        UnknownStateException exception = new UnknownStateException("Unknown state");
        ErrorMessage result = errorHandler.unknownStateException(exception);

        assertEquals("Unknown state: Unknown state", result.getError());
    }

    @Test
    void testEmailDuplicateException() {
        EmailDuplicateException exception = new EmailDuplicateException("Email duplicate");
        ErrorMessage result = errorHandler.emailDuplicateException(exception);

        assertEquals("Email duplicate", result.getError());
    }

    @Test
    void testIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ErrorMessage result = errorHandler.illegalArgumentException(exception);

        assertEquals("Invalid argument", result.getError());
    }

    @Test
    void testMethodArgumentNotValidException() {
        when(bindingResult.getFieldError()).thenReturn(new FieldError("objectName", "fieldName", "Field error"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        ErrorMessage result = errorHandler.defaultHandlerExceptionResolver(exception);

        assertEquals("fieldName Field error", result.getError());

        verify(bindingResult, times(2)).getFieldError();
    }
}
