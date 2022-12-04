package org.magentatobe.jchessgame;

/**
 * Thrown by Chessboard.movePiece() when processing a castling move if it turns
 * out castling is impossible.
 *
 * @see Chessboard#movePiece
 */
public class CastlingNotPossibleException extends Exception {

    /**
     * An integer flag indicating the precise reason castling wasn't possible.
     * Uses the int flag constants defined in the MovesLog.MoveError record
     * class.
     */
    private int reason = -1;

    /**
     * An accessor for the reason instance variable. Uses the
     * int flag constants defined in the MovesLog.MoveError record class.
     *
     * @return Int flag, one of
     *         MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED,
     *         MovesLog.MoveError.CASTLING_KING_IN_CHECK,
     *         MovesLog.MoveError.CASTLING_PATH_IS_THREATENED, or
     *         MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED.
     */
    public int getReason() {
        return reason;
    }

    /**
     * Constructs a CastlingNotPossibleException with no detail message.
     */
    public CastlingNotPossibleException() {
        super();
    }

    /**
     * Constructs a CastlingNotPossibleException with the specified detail
     * message.
     *
     * @param message The detail message.
     */
    public CastlingNotPossibleException(final String message) {
        super(message);
    }

    /**
     * Constructs a CastlingNotPossibleException with the specified detail
     * message, and the reason code why castling is impossible.
     *
     * @param message   The detail message.
     * @param reasonVal Int flag, the reason castling is impossible. One of 
     *                  MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED,
     *                  MovesLog.MoveError.CASTLING_KING_IN_CHECK,
     *                  MovesLog.MoveError.CASTLING_PATH_IS_THREATENED, or
     *                  MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED.
     */
    public CastlingNotPossibleException(final String message, final int reasonVal) {
        super(message);
        reason = reasonVal;
    }

    /**
     * Constructs a CastlingNotPossibleException with the specified detail
     * message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by
     *                the Throwable.getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the
     *                Throwable.getCause() method). (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public CastlingNotPossibleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CastlingNotPossibleException with the specified cause
     * and a detail message of (cause==null ? null : cause.toString()) (which
     * typically contains the class and detail message of cause).
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              Throwable.getCause() method). (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public CastlingNotPossibleException(final Throwable cause) {
        super(cause);
    }
}
