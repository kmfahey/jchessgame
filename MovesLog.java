package org.magentatobe.jchessgame;

import java.util.ArrayList;
import java.util.StringJoiner;
import javax.swing.JTextArea;

/**
 * Implements a readonly textarea that sits to the right of BoardView's
 * chessboard in the GUI, listing every move that's made in algebraic notation
 * format, followed by any recent errors made. Any errors made by the player
 * since their last move accumulate as error messages in this class's textarea,
 * so the player gets feedback on why their clicks didn't do anything. When a
 * successful move is committed to this class's object, all errors are wiped
 * from the display.
 */
public class MovesLog extends JTextArea {

    /**
     * Stores the Chessboard.Move objects recorded by the object. redraw()
     * prints the toString() value of each of them in sequence to the textarea,
     * followed by the toString() of any MoveError objects in errorsList.
     */
    private final ArrayList<Chessboard.Move> movesList;

    /**
     * Stores the MoveError objects accumulated by the object. It is cleared
     * every time a valid Move is stored to the object. After redraw() prints
     * the toString() value of every Chessboard.Move object in movesList, it
     * prints the toString() of any MoveError objects in this list.
     */
    private final ArrayList<MoveError> errorsList;

    /**
     * Implements a record that represents an error made in the course of
     * picking a move to play in the chessgame this program runs.
     *
     * @param moveObj   The Move object that was formed in error.
     * @param issueFlag An integer flag representing the type of error made, one
     *                  of MovesLog.MoveError.WOULD_BE_IN_CHECK,
     *                  MovesLog.MoveError.IS_IN_CHECK,
     *                  MovesLog.MoveError.NOT_A_VALID_MOVE,
     *                  MovesLog.MoveError.IS_A_FRIENDLY_PIECE,
     *                  MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED,
     *                  MovesLog.MoveError.CASTLING_KING_IN_CHECK,
     *                  MovesLog.MoveError.CASTLING_PATH_IS_THREATENED, or
     *                  MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED.
     */
    public record MoveError(Chessboard.Move moveObj, int issueFlag) {
        /** Flag, move would place the king in check. */
        public static final int WOULD_BE_IN_CHECK = 0;     

        /** Flag, the king is in check and this move doesn't fix that. */
        public static final int IS_IN_CHECK = 1;           

        /** Flag, the move is illegal for the piece. */
        public static final int NOT_A_VALID_MOVE = 2;      

        /** Flag, the move captures a friendly piece. */
        public static final int IS_A_FRIENDLY_PIECE = 3;   

        /** Flag, that castling is illegal because an intervening space is occupied. */
        public static final int CASTLING_INTERVENING_SPACE_OCCUPIED = 4;

        /** Flag, that castling is illegal because the king is in check. */
        public static final int CASTLING_KING_IN_CHECK = 5;

        /** Flag, that castling is illegal because a square on the way is threatened. */
        public static final int CASTLING_PATH_IS_THREATENED = 6;

        /** Flag, that castling is illegal because a square on the way is threatened. */
        public static final int CASTLING_PIECE_HAS_MOVED = 7;

        /**
         * Renders the error and the Chessboard.Move object it references in
         * plain english.
         *
         * @return A String representing the object's move in algebraic notation
         *         format.
         */
        public String toString() {
            String pieceStr = BoardArrays.pieceIntToString(moveObj.movingPiece().pieceInt());
            String fromLocStr = BoardArrays.coordsToAlgNotn(moveObj.fromXCoord(), moveObj.fromYCoord());
            String toLocStr = BoardArrays.coordsToAlgNotn(moveObj.toXCoord(), moveObj.toYCoord());
            String retval = "Moving " + pieceStr + " from " + fromLocStr + " to " + toLocStr + ": ";
            switch (issueFlag) {
                case WOULD_BE_IN_CHECK:
                    retval += "that move would place your king in check."; break;
                case IS_IN_CHECK:
                    retval += "your king is in check and move doesn't resolve that."; break;
                case NOT_A_VALID_MOVE:
                    retval += "that's not a valid move for that piece."; break;
                case IS_A_FRIENDLY_PIECE:
                    retval += "can't capture a friendly piece."; break;
                case CASTLING_INTERVENING_SPACE_OCCUPIED:
                    retval += "castling is impossible because an intervening space is occupied."; break;
                case CASTLING_KING_IN_CHECK:
                    retval += "castling is impossible because king is in check."; break;
                case CASTLING_PATH_IS_THREATENED:
                    retval += "castling is impossible because a traversed square is threatened."; break;
                case CASTLING_PIECE_HAS_MOVED:
                    retval += "castling is impossible because one the pieces has already moved."; break;
                default:
                    break;
            }
            return retval;
        }
    }

    /**
     * Initializes the MovesLog object. It is set to be uneditable, to use
     * linewrap, and to use wordwrap.
     */
    public MovesLog() {
        movesList = new ArrayList<>();
        errorsList = new ArrayList<>();
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    /**
     * Recreates the text of the logged moves plus the logged errors to, and
     * replaces the current contents of the textarea wth the new text.
     */
    public void redraw() {
        int oldTextLength = getText().length();
        StringJoiner joiner = new StringJoiner("\n", "", "\n");
        for (int index = 0; index < movesList.size(); index++) {
            Chessboard.Move moveObj = movesList.get(index);
            int moveNumber = index + 1;
            joiner.add(moveNumber + ". " + moveObj.toString());
        }
        for (MoveError errorObj : errorsList) {
            joiner.add(errorObj.toString());
        }
        String newText = joiner.toString();
        replaceRange(newText, 0, oldTextLength);
    }

    /**
     * Clears the log. All errors and all moves are removed, leaving the
     * textarea blank.
     */
    public void clear() {
        movesList.clear();
        errorsList.clear();
        redraw();
    }

    /**
     * Adds a move to the log. The output of Chessboard.Move.toString() will
     * appear in the textarea adjacent to a move number.
     *
     * @param moveObj The Chessboard.Move object to add to the loge.
     */
    public void addMove(final Chessboard.Move moveObj) {
        movesList.add(moveObj);
        errorsList.clear();
        redraw();
    }

    /**
     * Adds an error to the log. The Move object that was formed in error
     * is accepted, along with an int flag (taken from the constants in
     * MovesLog.MoveError) indicating what type of error it was.
     * <p>
     * Whenever a valid move is added to the log, the object's list of
     * MoveError objects is cleared. Error messages don't accumulate; they're
     * wiped out whenever a valid move is completed.
     *
     * @param moveObj   The Chessboard.Move object that was in error.
     * @param issueFlag An integer flag indicating the nature of the error, one
     *                  of MovesLog.MoveError.WOULD_BE_IN_CHECK,
     *                  MovesLog.MoveError.IS_IN_CHECK,
     *                  MovesLog.MoveError.NOT_A_VALID_MOVE,
     *                  MovesLog.MoveError.IS_A_FRIENDLY_PIECE, or
     *                  MovesLog.MoveError.CASTLING_NOT_POSSIBLE.
     */
    public void addError(final Chessboard.Move moveObj, final int issueFlag) {
        errorsList.add(new MoveError(moveObj, issueFlag));
        redraw();
    }
}
