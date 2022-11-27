package com.kmfahey.jchessgame;

public class KingIsInCheckException extends Exception {

    public KingIsInCheckException() {
        super();
    }

    public KingIsInCheckException(final String message) {
        super(message);
    }

    public KingIsInCheckException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KingIsInCheckException(final Throwable cause) {
        super(cause);
    }
}
