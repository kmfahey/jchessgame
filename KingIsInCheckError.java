package com.kmfahey.jchessgame;

public class KingIsInCheckError extends Exception {

    public KingIsInCheckError() {
        super();
    }

    public KingIsInCheckError(final String message) {
        super(message);
    }

    public KingIsInCheckError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KingIsInCheckError(final Throwable cause) {
        super(cause);
    }
}
