package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.Objects;

public class Chessboard {

    private HashMap<String, Piece> piecesLocations;
    private HashMap<String, Piece[]> piecesMap;

    public Chessboard() {

        piecesMap = new HashMap<String, Piece[]>();
        piecesLocations = new HashMap<String, Piece>();

        for (String algNotnAlphaChar : "abcdefgh".split("(?=.)")) {
            for (String algNotnNumChar : "87654321".split("(?=.)")) {
                piecesLocations.put(algNotnAlphaChar + algNotnNumChar, null);
            }
        }
    }

    public void storePiecesByIdentity(final String piecesIdentity, final Piece[] piecesArray) {
        piecesMap.put(piecesIdentity, piecesArray);
    }

    public void placePiece(final Piece piece, final String algNotnLoc) {
        piecesLocations.put(algNotnLoc, piece);
    }

    public Piece movePieceToLoc(final Piece movingPiece, final String movingToLoc) {
        Piece capturedPiece = null;
        piecesLocations.put(movingPiece.getBoardLocation(), null);
        movingPiece.setBoardLocation(movingToLoc);
        if (!Objects.isNull(piecesLocations.get(movingToLoc))) {
            Piece[] oldPiecesArray;
            Piece[] newPiecesArray;
            int fromIndex = 0;
            int toIndex = 0;

            capturedPiece = piecesLocations.get(movingToLoc);

            oldPiecesArray = piecesMap.get(capturedPiece.getIdentity());
            newPiecesArray = new Piece[oldPiecesArray.length-1];

            if (oldPiecesArray.length > 1) {
                while (fromIndex < oldPiecesArray.length) {
                    if (oldPiecesArray[fromIndex] == capturedPiece) {
                        fromIndex++;
                        continue;
                    }
                    newPiecesArray[toIndex] = oldPiecesArray[fromIndex];
                    fromIndex++;
                    toIndex++;
                }
            }
            piecesMap.put(capturedPiece.getIdentity(), newPiecesArray);

        }
        piecesLocations.put(movingToLoc, movingPiece);
        return capturedPiece;
    }

    public Piece[] getPiecesByIdentity(String piecesIdentity) {
        return piecesMap.get(piecesIdentity);
    }

    public String[] getPiecesIdentities() {
        return piecesMap.keySet().stream().toArray(String[]::new);
    }

    public Piece pieceAtLoc(final String algNotnLoc) {
        return piecesLocations.get(algNotnLoc);
    }
}
