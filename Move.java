package com.kmfahey.jchessgame;

public class Move {

    Piece movingPiece;
    String currentLocation;
    String moveToLocation;

    public Move(final Piece pieceMoving, final String currentLoc, final String moveToLoc) {
        movingPiece = pieceMoving;
        currentLocation = currentLoc;
        moveToLocation = moveToLoc;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getMoveToLocation() {
        return moveToLocation;
    }
}
