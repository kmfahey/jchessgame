package com.kmfahey.jchessgame;

public class CastlingNotPossibleException extends Exception {

    public CastlingNotPossibleException() {
        super();
    }

    public CastlingNotPossibleException(final String message) {
        super(message);
    }

    public CastlingNotPossibleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CastlingNotPossibleException(final Throwable cause) {
        super(cause);
    }
}
