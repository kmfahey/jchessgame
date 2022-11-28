
package com.kmfahey.jchessgame;

import java.util.HashMap;

public class Move {

    public static final String ALG_NOTN_ALPHA = "abcdefgh";
    public static final String ALG_NOTN_NUM = "87654321";

    public static final HashMap<Integer, String> pieceIntsToLetters = new HashMap<>() {{
        put(Chessboard.WHITE | Chessboard.KING,                      "K");
        put(Chessboard.WHITE | Chessboard.QUEEN,                     "Q");
        put(Chessboard.WHITE | Chessboard.ROOK,                      "R");
        put(Chessboard.WHITE | Chessboard.BISHOP,                    "B");
        put(Chessboard.WHITE | Chessboard.KNIGHT | Chessboard.RIGHT, "K");
        put(Chessboard.WHITE | Chessboard.KNIGHT | Chessboard.LEFT,  "K");
        put(Chessboard.WHITE | Chessboard.PAWN,                      "P");
        put(Chessboard.BLACK | Chessboard.KING,                      "K");
        put(Chessboard.BLACK | Chessboard.QUEEN,                     "Q");
        put(Chessboard.BLACK | Chessboard.ROOK,                      "R");
        put(Chessboard.BLACK | Chessboard.BISHOP,                    "B");
        put(Chessboard.BLACK | Chessboard.KNIGHT | Chessboard.RIGHT, "K");
        put(Chessboard.BLACK | Chessboard.KNIGHT | Chessboard.LEFT,  "K");
        put(Chessboard.BLACK | Chessboard.PAWN,                      "P");
    }};

    Chessboard.Piece movingPiece;
    int fromXCoord;
    int fromYCoord;
    int toXCoord;
    int toYCoord;
    int capturedPiece;
    boolean isCastlingKingside;
    boolean isCastlingQueenside;
    boolean isInCheck;
    boolean isInCheckmate;

    public Move(Chessboard.Piece movingPiecePc, int fromXCoordInt, int fromYCoordInt,
                int toXCoordInt, int toYCoordInt, int capturedPieceInt, boolean isCastlingKingsideBool,
                boolean isCastlingQueensideBool, boolean isInCheckBool, boolean isInCheckmateBool) {
        movingPiece = movingPiecePc;
        fromXCoord = fromXCoordInt;
        fromYCoord = fromYCoordInt;
        toXCoord = toXCoordInt;
        toYCoord = toYCoordInt;
        capturedPiece = capturedPieceInt;
        isCastlingKingside = isCastlingKingsideBool;
        isCastlingQueenside = isCastlingQueensideBool;
        isInCheck = isInCheckBool;
        isInCheckmate = isInCheckmateBool;
    }

    public Move(Chessboard.Piece movingPiecePc, int fromXCoordInt, int fromYCoordInt,
                int toXCoordInt, int toYCoordInt, int capturedPieceInt, boolean isCastlingKingsideBool,
                boolean isCastlingQueensideBool) {
        this(movingPiecePc, fromXCoordInt, fromYCoordInt, toXCoordInt, toYCoordInt, capturedPieceInt,
             isCastlingKingsideBool, isCastlingQueensideBool, false, false);
    }

    public Move(Chessboard.Piece movingPiecePc, int fromXCoordInt, int fromYCoordInt,
                int toXCoordInt, int toYCoordInt, int capturedPieceInt) {
        this(movingPiecePc, fromXCoordInt, fromYCoordInt, toXCoordInt, toYCoordInt, capturedPieceInt,
             false, false, false, false);
    }

    public Chessboard.Piece getMovingPiece() {
        return movingPiece;
    }

    public int getFromXCoord() {
        return fromXCoord;
    }

    public int getFromYCoord() {
        return fromYCoord;
    }

    public int getToXCoord() {
        return toXCoord;
    }

    public int getToYCoord() {
        return toYCoord;
    }

    public int getCapturedPiece() {
        return capturedPiece;
    }

    public boolean getIsCastlingKingside() {
        return isCastlingKingside;
    }

    public boolean getIsCastlingQueenside() {
        return isCastlingQueenside;
    }

    public boolean getIsInCheck() {
        return isInCheck;
    }

    public boolean getIsInCheckmate() {
        return isInCheckmate;
    }

    public void setIsInCheck(boolean isInCheckBool) {
        isInCheck = isInCheckBool;
    }

    public void setIsInCheckmate(boolean isInCheckmateBool) {
        isInCheckmate = isInCheckmateBool;
    }

    private String toAlgNotn() {
        if (isCastlingKingside) {
            return "0 0";
        } else if (isCastlingQueenside) {
            return "0 0 0";
        }
        String pieceComp = pieceIntsToLetters.get(movingPiece.pieceInt());
        String fromAlphaComp = String.valueOf(ALG_NOTN_ALPHA.charAt(fromXCoord));
        String fromNumComp = String.valueOf(ALG_NOTN_NUM.charAt(fromYCoord));
        String captureComp = capturedPiece != 0 ? "x" : "";
        String toAlphaComp = String.valueOf(ALG_NOTN_ALPHA.charAt(toXCoord));
        String toNumComp = String.valueOf(ALG_NOTN_NUM.charAt(toYCoord));
        String isInCheckComp = isInCheck ? "+" : "";
        String isInCheckmateComp = isInCheckmate ? "#" : "";
        return pieceComp + fromAlphaComp + fromNumComp + captureComp + toAlphaComp + toNumComp
               + isInCheckComp + isInCheckmateComp;
    }
}
