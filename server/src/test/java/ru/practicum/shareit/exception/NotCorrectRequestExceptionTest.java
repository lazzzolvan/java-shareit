package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotCorrectRequestExceptionTest {

    @Test
    void testDefaultConstructor() {
        // Act
        NotCorrectRequestException exception = new NotCorrectRequestException();

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String message = "Custom error message";

        // Act
        NotCorrectRequestException exception = new NotCorrectRequestException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String message = "Custom error message";
        Throwable cause = new RuntimeException("Cause of the exception");

        // Act
        NotCorrectRequestException exception = new NotCorrectRequestException(message, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        // Arrange
        Throwable cause = new RuntimeException("Cause of the exception");

        // Act
        NotCorrectRequestException exception = new NotCorrectRequestException(cause);

        // Assert
        assertNotNull(exception);
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        String message = "Custom error message";
        Throwable cause = new RuntimeException("Cause of the exception");
        boolean enableSuppression = true;
        boolean writableStackTrace = true;

        // Act
        NotCorrectRequestException exception = new NotCorrectRequestException(message, cause, enableSuppression, writableStackTrace);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(writableStackTrace, exception.getStackTrace().length > 0);
    }
}
