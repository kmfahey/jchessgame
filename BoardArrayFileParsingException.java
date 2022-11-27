package com.kmfahey.jchessgame;

public class BoardArrayFileParsingException extends Exception {

    public BoardArrayFileParsingException() {
        super();
    }

    public BoardArrayFileParsingException(final String message) {
        super(message);
    }

    public BoardArrayFileParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BoardArrayFileParsingException(final Throwable cause) {
        super(cause);
    }
}
