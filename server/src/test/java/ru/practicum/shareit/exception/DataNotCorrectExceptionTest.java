package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataNotCorrectExceptionTest {

    @Test
    void testDefaultConstructor() {
        DataNotCorrectException exception = new DataNotCorrectException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Error message";
        DataNotCorrectException exception = new DataNotCorrectException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Error message";
        Throwable cause = new Throwable();
        DataNotCorrectException exception = new DataNotCorrectException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new Throwable();
        DataNotCorrectException exception = new DataNotCorrectException(cause);
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessageCauseSuppressionAndWritableStackTrace() {
        String message = "Error message";
        Throwable cause = new Throwable();
        boolean enableSuppression = true;
        boolean writableStackTrace = true;
        DataNotCorrectException exception = new DataNotCorrectException(message, cause, enableSuppression, writableStackTrace);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getSuppressed().length == 0); // Check suppression is enabled
        assertNotNull(exception.getStackTrace()); // Check stack trace is writable
    }
}
