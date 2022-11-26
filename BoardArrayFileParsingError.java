package com.kmfahey.jchessgame;

public class BoardArrayFileParsingError extends Exception {

    public BoardArrayFileParsingError() {
        super();
    }

    public BoardArrayFileParsingError(final String message) {
        super(message);
    }

    public BoardArrayFileParsingError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BoardArrayFileParsingError(final Throwable cause) {
        super(cause);
    }
}
