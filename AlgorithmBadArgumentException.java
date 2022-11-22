package com.kmfahey.jchessgame;

public class AlgorithmBadArgumentException extends Exception {

    public AlgorithmBadArgumentException() {
        super();
    }

    public AlgorithmBadArgumentException(final String message) {
        super(message);
    }

    public AlgorithmBadArgumentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlgorithmBadArgumentException(final Throwable cause) {
        super(cause);
    }
}
