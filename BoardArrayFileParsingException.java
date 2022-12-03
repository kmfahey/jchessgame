package com.kmfahey.jchessgame;

/**
 * Thrown while parsing a CSV file describing a chessboard that was passed as a
 * commandline argument.
 *
 * @see ChessGame#actionPerformed
 */
public class BoardArrayFileParsingException extends Exception {

    /**
     * Constructs a BoardArrayFileParsingException with no detail message.
     */
    public BoardArrayFileParsingException() {
        super();
    }

    /**
     * Constructs a BoardArrayFileParsingException with the specified detail message.
     *
     * @param message The detail message.
     */
    public BoardArrayFileParsingException(final String message) {
        super(message);
    }

    /**
     * Constructs a BoardArrayFileParsingException with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     *                the Throwable.getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                Throwable.getCause() method). (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public BoardArrayFileParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BoardArrayFileParsingException with the specified cause
     * and a detail message of (cause==null ? null : cause.toString()) (which
     * typically contains the class and detail message of cause).
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              Throwable.getCause() method). (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public BoardArrayFileParsingException(final Throwable cause) {
        super(cause);
    }
}
