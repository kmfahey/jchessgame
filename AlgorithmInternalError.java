package com.kmfahey.jchessgame;

public class AlgorithmInternalError extends Exception {

    public AlgorithmInternalError() {
        super();
    }

    public AlgorithmInternalError(final String message) {
        super(message);
    }

    public AlgorithmInternalError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlgorithmInternalError(final Throwable cause) {
        super(cause);
    }
}
