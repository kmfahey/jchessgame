package com.kmfahey.jchessgame;

import java.util.HashSet;
import java.awt.Image;

public class Piece implements Cloneable {

    private String pieceIdentity;
    private String boardLocation;

    private HashSet<String> inCheckByPieces = null;
    private HashSet<String> inCheckmateByPieces = null;
    private boolean hasBeenCaptured = false;
    private Image pieceImage;

    public Piece(final String identity, final Image pcImage) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        if (pieceIdentity.endsWith("king")) {
            inCheckByPieces = new HashSet<String>();
            inCheckmateByPieces = new HashSet<String>();
        }
    }

    public Piece(final String identity, final Image pcImage, final boolean beenCaptured) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        hasBeenCaptured = beenCaptured;
    }
    
    public Piece(final String identity, final Image pcImage, final boolean beenCaptured,
                 final HashSet<String> inCheckByPcs, final HashSet<String> inCheckmateByPcs) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        hasBeenCaptured = beenCaptured;
        inCheckByPieces = inCheckByPcs;
        inCheckmateByPieces = inCheckmateByPcs;
    }

    @Override
    public Piece clone() {
        if (pieceIdentity.endsWith("king")) {
            return new Piece(pieceIdentity, pieceImage, hasBeenCaptured, new HashSet<String>(inCheckByPieces), new HashSet<String>(inCheckmateByPieces));
        } else {
            return new Piece(pieceIdentity, pieceImage, hasBeenCaptured);
        }
    }

    public String getBoardLocation() {
        return boardLocation;
    }

    public String getIdentity() {
        return pieceIdentity;
    }

    public HashSet<String> getInCheckByPieces() {
        return inCheckByPieces;
    }

    public HashSet<String> getInCheckmateByPieces() {
        return inCheckmateByPieces;
    }

    public boolean isInCheck() {
        return inCheckByPieces.size() > 0;
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

    public boolean isInCheckmate() {
        return inCheckmateByPieces.size() > 0;
    }

    public boolean getHasBeenCaptured() {
        return hasBeenCaptured;
    }

    public void setHasBeenCaptured() {
        hasBeenCaptured = true;
    }

    public void setBoardLocation(final String boardLocationStr) {
        boardLocation = boardLocationStr;
    }

    public void inCheckmateByPiece(final Piece pieceInCheckmateBy) {
        inCheckmateByPieces.add(pieceInCheckmateBy.getBoardLocation());
    }

    public void inCheckByPiece(final Piece pieceInCheckBy) {
        inCheckByPieces.add(pieceInCheckBy.getBoardLocation());
    }

    public void noLongerInCheckBy(final Piece pieceNoLongerInCheckBy) {
        inCheckByPieces.remove(pieceNoLongerInCheckBy.getBoardLocation());
    }

    public Image getImage() {
        return pieceImage;
    }
}

