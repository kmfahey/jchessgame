package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Chessboard {

    public static final int DOUBLED = 0;
    public static final int ISOLATED = 1;
    public static final int BLOCKED = 2;

    private HashMap<String, Piece> piecesLocations;
    private HashMap<String, Piece[]> piecesMap;
    private PiecesManager piecesManager;

    public Chessboard(final PiecesManager piecesMgr) {

        piecesManager = piecesMgr;

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

    public void removePieceFromPlay(final Piece piece) {
        Piece[] oldPiecesArray;
        Piece[] newPiecesArray;
        int fromIndex = 0;
        int toIndex = 0;

        oldPiecesArray = piecesMap.get(piece.getIdentity());
        newPiecesArray = new Piece[oldPiecesArray.length - 1];

        if (oldPiecesArray.length > 1) {
            while (fromIndex < oldPiecesArray.length) {
                if (oldPiecesArray[fromIndex] == piece) {
                    fromIndex++;
                    continue;
                }
                newPiecesArray[toIndex] = oldPiecesArray[fromIndex];
                fromIndex++;
                toIndex++;
            }
        }
        piecesMap.put(piece.getIdentity(), newPiecesArray);
    }

    public Piece movePieceToLoc(final Piece movingPiece, final String movingToLoc) {
        Piece capturedPiece = null;
        HashSet<String> validMoves = null;

        if (!Objects.isNull(piecesLocations.get(movingToLoc))) {
            removePieceFromPlay(piecesLocations.get(movingToLoc));
        }

        piecesLocations.put(movingPiece.getBoardLocation(), null);
        movingPiece.setBoardLocation(movingToLoc);
        piecesLocations.put(movingToLoc, movingPiece);
        /* Possibilities:
           * Due to this move, this side's king is now in check (because this
             piece got out of the way).
           * Due to this move, this side's king is no longer in check (because
             this piece got in the way).
           * Due to this move, the other side's king is now in check by this
             piece.
           * Due to this move, the other side's king is now in check by another
             piece on this side.
           * Due to this move, the other side's king is no longer in check
             because this piece got in the way. */
        piecesManager.checkIfKingIsInCheck(movingPiece.getColor());
        piecesManager.checkIfKingIsInCheck(movingPiece.getColor().equals("white") ? "black" : "white");
        return capturedPiece;
    }

    public Piece[] getPiecesByIdentity(final String piecesIdentity) {
        return piecesMap.get(piecesIdentity);
    }

    public String[] getPiecesIdentities() {
        return piecesMap.keySet().stream().toArray(String[]::new);
    }

    public Piece getPieceAtLoc(final String algNotnLoc) {
        return piecesLocations.get(algNotnLoc);
    }

    public float evaluateBoard(final String thisColor) {
        String otherColor = (thisColor.equals("white")) ? "black" : "white";

        float thisKingCount = piecesMap.get(thisColor + "-king")[0].isInCheck() ? 0F : 1F;
        float otherKingCount = piecesMap.get(otherColor + "-king")[0].isInCheck() ? 0F : 1F;
        float thisQueenCount = (float) piecesMap.get(thisColor + "-queen").length;
        float otherQueenCount = (float) piecesMap.get(otherColor + "-queen").length;
        float thisRookCount = (float) piecesMap.get(thisColor + "-rook").length;
        float otherRookCount = (float) piecesMap.get(otherColor + "-rook").length;
        float thisBishopCount = (float) piecesMap.get(thisColor + "-bishop").length;
        float otherBishopCount = (float) piecesMap.get(otherColor + "-bishop").length;
        float thisKnightCount = (float) (piecesMap.get(thisColor + "-knight-left").length
                                         + piecesMap.get(thisColor + "-knight-right").length);
        float otherKnightCount = (float) (piecesMap.get(otherColor + "-knight-left").length
                                          + piecesMap.get(otherColor + "-knight-right").length);
        float thisPawnCount = (float) piecesMap.get(thisColor + "-pawn").length;
        float otherPawnCount = (float) piecesMap.get(otherColor + "-pawn").length;
        float[] thisColorSpecialPawnsTally = tallySpecialPawns(thisColor);
        float[] otherColorSpecialPawnsTally = tallySpecialPawns(otherColor);
        float thisMobility = (float) colorMobility(thisColor);
        float otherMobility = (float) colorMobility(otherColor);

        float isolatedPawnDifference = thisColorSpecialPawnsTally[ISOLATED]
                                       - otherColorSpecialPawnsTally[ISOLATED];
        float blockedPawnDifference = thisColorSpecialPawnsTally[BLOCKED]
                                      - otherColorSpecialPawnsTally[BLOCKED];
        float doubledPawnDifference = thisColorSpecialPawnsTally[DOUBLED]
                                      - otherColorSpecialPawnsTally[DOUBLED];
        float kingScore = 200 * (thisKingCount - otherKingCount);
        float queenScore = 9 * (thisQueenCount - otherQueenCount);
        float rookScore = 5 * (thisRookCount - otherRookCount);
        float bishopAndKnightScore = 3 * ((thisBishopCount - otherBishopCount) + (thisKnightCount - otherKnightCount));
        float generalPawnScore = (thisPawnCount - otherPawnCount);
        float specialPawnScore = 0.5F * (isolatedPawnDifference + blockedPawnDifference + doubledPawnDifference);
        float mobilityScore = 0.1F * (thisMobility - otherMobility);
        return (kingScore + queenScore + rookScore + bishopAndKnightScore
                + generalPawnScore + specialPawnScore + mobilityScore);
    }

    private float colorMobility(final String thisColor) {
        float possibleMovesCount = 0;
        for (String piecesIdentity : getPiecesIdentities()) {
            if (!piecesIdentity.startsWith(thisColor)) {
                continue;
            }
            for (Piece piece : getPiecesByIdentity(piecesIdentity)) {
                possibleMovesCount += piecesManager.getValidMoveSet(piece).size();
            }
        }
        return possibleMovesCount;
    }

    private float[] tallySpecialPawns(final String thisColor) {
        String otherColor = thisColor.equals("white") ? "black" : "white";
        float[] retval = new float[3];
        float doubledPawns = 0;
        float isolatedPawns = 0;
        float blockedPawns = 0;

        for (Piece thisPawn : piecesMap.get(thisColor + "-pawn")) {
            String thisPawnLoc = thisPawn.getBoardLocation();
            boolean hasAdjacentPawn = false;
            for (Piece otherPawn : piecesMap.get(thisColor + "-pawn")) {
                if (thisPawn == otherPawn) {
                    continue;
                }
                String otherPawnLoc = otherPawn.getBoardLocation();
                if (thisPawnLoc.charAt(0) == otherPawnLoc.charAt(0)
                        && thisPawnLoc.charAt(1) + 1 == otherPawnLoc.charAt(1)) {
                    doubledPawns += 1;
                }
                if (thisPawnLoc.charAt(0) - otherPawnLoc.charAt(0) == 1
                        || otherPawnLoc.charAt(0) - thisPawnLoc.charAt(0) == 1) {
                    hasAdjacentPawn = true;
                }
            }
            isolatedPawns += (!hasAdjacentPawn) ? 1 : 0;
            if (thisPawnLoc.charAt(1) != '8') {
                Piece pieceInFront = getPieceAtLoc(String.valueOf(thisPawnLoc.charAt(0))
                                                   + String.valueOf((char) (thisPawnLoc.charAt(1) + 1)));
                if (!Objects.isNull(pieceInFront)
                    /* checking that it isn't one of this side's pawn, since that would be 'doubled' instead */
                    && !pieceInFront.getIdentity().equals(thisColor + "-pawn")) {
                    blockedPawns += 1;
                }
            }
        }
        retval[DOUBLED] = doubledPawns;
        retval[BLOCKED] = blockedPawns;
        retval[ISOLATED] = isolatedPawns;
        return retval;
    }
}
