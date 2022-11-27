package com.kmfahey.jchessgame;

public class KingIsInCheckmateException extends Exception {

    public KingIsInCheckmateException() {
        super();
    }

    public KingIsInCheckmateException(final String message) {
        super(message);
    }

    public KingIsInCheckmateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KingIsInCheckmateException(final Throwable cause) {
        super(cause);
    }
}
