package com.kmfahey.jchessgame;

import java.util.ArrayList;
import java.util.StringJoiner;
import javax.swing.JTextArea;

public class MovesLog extends JTextArea {

    private ArrayList<Chessboard.Move> movesList;
    private ArrayList<MoveError> errorsList;

    private record MoveError(Chessboard.Move moveObj, int issueFlag) {
        public static final int WOULD_BE_IN_CHECK = 0;
        public static final int IS_IN_CHECK = 1;
        public static final int NOT_A_VALID_MOVE = 2;
        public static final int IS_A_FRIENDLY_PIECE = 3;

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
                default:
                    break;
            }
            return retval;
        }
    }

    public MovesLog() {
        movesList = new ArrayList<>();
        errorsList = new ArrayList<>();
        setEditable(false);
    }

    public void redraw() {
        int oldTextLength = getText().length();
        StringJoiner joiner = new StringJoiner("\n", "", "\n");
        for (int index = 0; index <= movesList.size(); index++) {
            Chessboard.Move moveObj = movesList.get(index);
            int moveNumber = index + 1;
            joiner.add(moveNumber + ". " + moveObj.toString());
        }
        for (int index = 0; index <= errorsList.size(); index++) {
            MoveError errorObj = errorsList.get(index);
            joiner.add(errorObj.toString());
        }
        String newText = joiner.toString();
        replaceRange(newText, 0, oldTextLength);
    }

    public void clear() {
        movesList.clear();
        errorsList.clear();
        redraw();
    }

    public void addMove(final Chessboard.Move moveObj) {
        movesList.add(moveObj);
        errorsList.clear();
        redraw();
    }

    public void addError(final Chessboard.Move moveObj, final int issueFlag) {
        errorsList.add(new MoveError(moveObj, issueFlag));
        redraw();
    }
}
