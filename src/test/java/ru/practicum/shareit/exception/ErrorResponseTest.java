package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseConstructorAndGetError() {
        String errorMessage = "Some error message";

        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getError());
    }

    @Test
    void testErrorResponseWithNullError() {
        String errorMessage = null;

        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertNotNull(errorResponse);
        assertNull(errorResponse.getError());
    }

    @Test
    void testErrorResponseWithEmptyError() {
        String errorMessage = "";

        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getError());
    }
}
