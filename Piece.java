package com.kmfahey.jchessgame;

import java.util.Arrays;
import java.util.HashSet;
import java.awt.Image;

public class Piece implements Cloneable {

    public static final int X_COORD = 0;
    public static final int Y_COORD = 1;

    private String pieceIdentity;
    private final int[] pieceCoords;

    private boolean hasBeenCaptured = false;
    private Image pieceImage;

    public Piece(final String identity, final Image pcImage) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        pieceCoords = new int[] {-1, -1};
    }

    public Piece(final String identity, final Image pcImage, final int xCoord, final int yCoord) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        pieceCoords = new int[] {xCoord, yCoord};
    }

    @Override
    public Piece clone() {
        return new Piece(pieceIdentity, pieceImage, pieceCoords[X_COORD], pieceCoords[Y_COORD]);
    }

    public int[] getCoords() {
        /* The array is mutable, so we return a copy to prevent handing the
           calling code a reference to one of our private instance vars. */
        return Arrays.copyOf(pieceCoords, 2);
    }

    public String getIdentity() {
        return pieceIdentity;
    }

    public String getColor() {
        return pieceIdentity.split("-")[0];
    }

    public String getRole() {
        return pieceIdentity.split("-")[1];
    }

    public String getChirality() {
        return pieceIdentity.split("-")[2];
    }

    public void setCoords(final int xIdx, final int yIdx) {
        pieceCoords[X_COORD] = xIdx;
        pieceCoords[Y_COORD] = yIdx;
    }

    public Image getImage() {
        return pieceImage;
    }
}

