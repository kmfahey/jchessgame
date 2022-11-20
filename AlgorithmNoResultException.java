package com.kmfahey.jchessgame;

public class AlgorithmNoResultException extends Exception {

    public AlgorithmNoResultException() {
        super();
    }

    public AlgorithmNoResultException(final String message) {
        super(message);
    }

    public AlgorithmNoResultException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlgorithmNoResultException(final Throwable cause) {
        super(cause);
    }
}
