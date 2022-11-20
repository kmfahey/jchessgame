package com.kmfahey.jchessgame;

import java.util.HashSet;
import java.awt.Image;

public class Piece implements Cloneable {

    private String pieceIdentity;
    private String currentLocation;
    private String lastLocation;

    private HashSet<String> inCheckByPcsAtLocs = null;
    private HashSet<String> inCheckmateByPcsAtLocs = null;
    private boolean hasBeenCaptured = false;
    private Image pieceImage;

    public Piece(final String identity, final Image pcImage) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        if (pieceIdentity.endsWith("king")) {
            inCheckByPcsAtLocs = new HashSet<String>();
            inCheckmateByPcsAtLocs = new HashSet<String>();
        }
    }

    public Piece(final String identity, final Image pcImage, final boolean beenCaptured,
                 final String lastLoc, final String currentLoc) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        hasBeenCaptured = beenCaptured;
        currentLocation = currentLoc;
        lastLocation = lastLoc;
    }
    
    public Piece(final String identity, final Image pcImage, final boolean beenCaptured,
                 final String lastLoc, final String currentLoc,
                 final HashSet<String> inCheckByPcs, final HashSet<String> inCheckmateByPcs) {
        pieceImage = pcImage;
        pieceIdentity = identity;
        currentLocation = currentLoc;
        lastLocation = lastLoc;
        hasBeenCaptured = beenCaptured;
        inCheckByPcsAtLocs = inCheckByPcs;
        inCheckmateByPcsAtLocs = inCheckmateByPcs;
    }

    @Override
    public Piece clone() {
        if (pieceIdentity.endsWith("king")) {
            return new Piece(pieceIdentity, pieceImage, hasBeenCaptured, lastLocation, currentLocation,
                             new HashSet<String>(inCheckByPcsAtLocs), new HashSet<String>(inCheckmateByPcsAtLocs));
        } else {
            return new Piece(pieceIdentity, pieceImage, hasBeenCaptured, lastLocation, currentLocation);
        }
    }

    public String getLocation() {
        return currentLocation;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public String getIdentity() {
        return pieceIdentity;
    }

    public HashSet<String> getInCheckByPcsAtLocs() {
        return inCheckByPcsAtLocs;
    }

    public HashSet<String> getInCheckmateByPcsAtLocs() {
        return inCheckmateByPcsAtLocs;
    }

    public boolean isInCheck() {
        return inCheckByPcsAtLocs.size() > 0;
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
        return inCheckmateByPcsAtLocs.size() > 0;
    }

    public boolean getHasBeenCaptured() {
        return hasBeenCaptured;
    }

    public void setHasBeenCaptured() {
        hasBeenCaptured = true;
    }

    public void setLocation(final String locationStr) {
        lastLocation = currentLocation;
        currentLocation = locationStr;
    }

    public void inCheckmateByPiece(final Piece pieceInCheckmateBy) {
        inCheckmateByPcsAtLocs.add(pieceInCheckmateBy.getLocation());
    }

    public void inCheckByPiece(final Piece pieceInCheckBy) {
        inCheckByPcsAtLocs.add(pieceInCheckBy.getLocation());
    }

    public void noLongerInCheckBy(final Piece pieceNoLongerInCheckBy) {
        inCheckByPcsAtLocs.remove(pieceNoLongerInCheckBy.getLocation());
    }

    public Image getImage() {
        return pieceImage;
    }
}

