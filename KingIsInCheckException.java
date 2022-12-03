package com.kmfahey.jchessgame;

/**
 * Thrown when a move would put the king in check, and that possibility eluded
 * notice until right before the move was to be made.
 */
public class KingIsInCheckException extends Exception {

    /**
     * Constructs a KingIsInCheckException with no detail message.
     */
    public KingIsInCheckException() {
        super();
    }

    /**
     * Constructs a KingIsInCheckException with the specified detail message.
     *
     * @param message The detail message.
     */
    public KingIsInCheckException(final String message) {
        super(message);
    }

    /**
     * Constructs a KingIsInCheckException with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     *                the Throwable.getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                Throwable.getCause() method). (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public KingIsInCheckException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new KingIsInCheckException with the specified cause
     * and a detail message of (cause==null ? null : cause.toString()) (which
     * typically contains the class and detail message of cause).
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              Throwable.getCause() method). (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public KingIsInCheckException(final Throwable cause) {
        super(cause);
    }
}
