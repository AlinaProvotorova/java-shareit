package ru.practicum.shareit.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.ErrorMessage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testUnknownStateException() {
        UnknownStateException exception = new UnknownStateException("Unknown state");
        ErrorMessage result = errorHandler.handleBadRequestException(exception);

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
        ErrorMessage result = errorHandler.handleBadRequestException(exception);

        assertEquals("Invalid argument", result.getError());
    }
}
