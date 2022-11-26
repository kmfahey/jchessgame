package com.kmfahey.jchessgame;

import java.util.Arrays;
import java.util.HashSet;
import java.awt.Image;

public class Piece implements Cloneable {

    public static final int WHITE = BoardArrays.WHITE;
    public static final int BLACK = BoardArrays.BLACK;

    public static final int PAWN = BoardArrays.PAWN;
    public static final int ROOK = BoardArrays.ROOK;
    public static final int KNIGHT = BoardArrays.KNIGHT;
    public static final int BISHOP = BoardArrays.BISHOP;
    public static final int QUEEN = BoardArrays.QUEEN;
    public static final int KING = BoardArrays.KING;

    public static final int LEFT = BoardArrays.LEFT;
    public static final int RIGHT = BoardArrays.RIGHT;

    public static final int X_COORD = 0;
    public static final int Y_COORD = 1;

    private String pieceIdentity;
    private final int[] pieceCoords;

    private boolean hasBeenCaptured = false;
    private Image pieceImage;
    private int pieceInt;

    public Piece(final int pcInt, final Image pcImage) {
        pieceInt = pcInt;
        pieceImage = pcImage;
        pieceCoords = new int[] {-1, -1};
    }

    public Piece(final int pcInt, final Image pcImage, final int xCoord, final int yCoord) {
        pieceInt = pcInt;
        pieceImage = pcImage;
        pieceCoords = new int[] {xCoord, yCoord};
    }

    @Override
    public Piece clone() {
        return new Piece(pieceInt, pieceImage, pieceCoords[X_COORD], pieceCoords[Y_COORD]);
    }

    public int[] getCoords() {
        /* The array is mutable, so we return a copy to prevent handing the
           calling code a reference to one of our private instance vars. */
        return Arrays.copyOf(pieceCoords, 2);
    }

    public int getPieceInt() {
        return pieceInt;
    }

    public void setCoords(final int xIdx, final int yIdx) {
        pieceCoords[X_COORD] = xIdx;
        pieceCoords[Y_COORD] = yIdx;
    }

    public Image getImage() {
        return pieceImage;
    }
}

