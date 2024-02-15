package ru.practicum.shareit.exception;

public class DataNotCorrectException extends RuntimeException{
    public DataNotCorrectException() {
    }

    public DataNotCorrectException(String message) {
        super(message);
    }

    public DataNotCorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataNotCorrectException(Throwable cause) {
        super(cause);
    }

    public DataNotCorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
