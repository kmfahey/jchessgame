package com.kmfahey.jchessgame;

public class KingIsInCheckmateError extends Exception {

    public KingIsInCheckmateError() {
        super();
    }

    public KingIsInCheckmateError(final String message) {
        super(message);
    }

    public KingIsInCheckmateError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KingIsInCheckmateError(final Throwable cause) {
        super(cause);
    }
}
