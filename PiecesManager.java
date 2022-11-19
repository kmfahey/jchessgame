package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import net.sf.repr.Repr;

public class PiecesManager {

    public static final String WHITE = "white";
    public static final String BLACK = "black";

    public static final String BISHOP = "bishop";
    public static final String KING = "king";
    public static final String KNIGHT = "knight";
    public static final String PAWN = "pawn";
    public static final String QUEEN = "queen";
    public static final String ROOK = "rook";

    public static final String RIGHT = "right";
    public static final String LEFT = "left";

    public static final int NORTH = 0;
    public static final int NORTH_NORTH_EAST = 1;
    public static final int NORTH_EAST = 2;
    public static final int EAST_NORTH_EAST = 3;
    public static final int EAST = 4;
    public static final int EAST_SOUTH_EAST = 5;
    public static final int SOUTH_EAST = 6;
    public static final int SOUTH_SOUTH_EAST = 7;
    public static final int SOUTH = 8;
    public static final int SOUTH_SOUTH_WEST = 9;
    public static final int SOUTH_WEST = 10;
    public static final int WEST_SOUTH_WEST = 11;
    public static final int WEST = 12;
    public static final int WEST_NORTH_WEST = 13;
    public static final int NORTH_WEST = 14;
    public static final int NORTH_NORTH_WEST = 15;

    private final HashMap<String, String[]> piecesStartingLocsWhiteBelow = new HashMap<>() {{
        this.put("black-rook", new String[] {"a8", "h8"});
        this.put("black-bishop", new String[] {"c8", "f8"});
        this.put("black-knight-right", new String[] {"g8"});
        this.put("black-king", new String[] {"d8"});
        this.put("black-queen", new String[] {"e8"});
        this.put("black-knight-left", new String[]  {"b8"});
        this.put("black-pawn", new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"});
        this.put("white-rook", new String[] {"a1", "h1"});
        this.put("white-bishop", new String[] {"c1", "f1"});
        this.put("white-knight-right", new String[] {"g1"});
        this.put("white-king", new String[] {"d1"});
        this.put("white-queen", new String[] {"e1"});
        this.put("white-knight-left", new String[]  {"b1"});
        this.put("white-pawn", new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"});
    }};

    private final HashMap<String, String[]> piecesStartingLocsBlackBelow = new HashMap<>() {{
        /* To avoid repeating the above definition with the colors swapped,
           the following for-each loop computes the colorswapped identity and
           assigns the previous mapping's value for the other color identity to
           it in this mapping. */
        for (Entry<String, String[]> identityToPiece : piecesStartingLocsWhiteBelow.entrySet()) {
            StringJoiner joiner = new StringJoiner("-");
            String[] identityPcs = identityToPiece.getKey().split("-");
            joiner.add(identityPcs[0].equals("white") ? "black" : "white").add(identityPcs[1]);
            if (identityPcs.length == 3) {
                joiner.add(identityPcs[2]);
            }
            String otherColorIdentity = joiner.toString();
            this.put(otherColorIdentity, identityToPiece.getValue());
        }
    }};

    private ImagesManager imagesManager;
    private Chessboard chessboard;
    private String colorPlaying;

    public PiecesManager(final ImagesManager imgMgr, final String playingColor) {

        imagesManager = imgMgr;
        colorPlaying = playingColor;

        chessboard = new Chessboard(this);

        HashMap<String, String[]> piecesStartingLocs = (colorPlaying == "white")
                                                       ? piecesStartingLocsWhiteBelow
                                                       : piecesStartingLocsBlackBelow;

        for (Entry<String, String[]> pieceStartingLocs : piecesStartingLocs.entrySet()) {
            Piece[] pieces = new Piece[pieceStartingLocs.getValue().length];
            String[] startingLocs = pieceStartingLocs.getValue();
            for (int index = 0; index < startingLocs.length; index++) {
                Piece piece = new Piece(pieceStartingLocs.getKey(), imagesManager);
                piece.setBoardLocation(startingLocs[index]);
                pieces[index] = piece;
            }
            chessboard.storePiecesByIdentity(pieceStartingLocs.getKey(), pieces);
        }

        for (String pieceIdentity : chessboard.getPiecesIdentities()) {
            for (Piece piece : chessboard.getPiecesByIdentity(pieceIdentity)) {
                chessboard.placePiece(piece, piece.getBoardLocation());
            }
        }
    }

    private String plotDirMove(final String location, final int moveDist, final int moveDir) {
        final String algNotnAlphaChars = "abcdefgh";
        final String algNotnNumChars = "87654321";
        int alphaIndex = algNotnAlphaChars.indexOf(String.valueOf(location.charAt(0)));
        int numIndex = algNotnNumChars.indexOf(String.valueOf(location.charAt(1)));
        switch (moveDir) {
            case NORTH:                                         numIndex -= moveDist;   break;
            case NORTH_EAST:        alphaIndex += moveDist;     numIndex -= moveDist;   break;
            case EAST:              alphaIndex += moveDist;                             break;
            case SOUTH_EAST:        alphaIndex += moveDist;     numIndex += moveDist;   break;
            case SOUTH:                                         numIndex += moveDist;   break;
            case SOUTH_WEST:        alphaIndex -= moveDist;     numIndex += moveDist;   break;
            case WEST:              alphaIndex -= moveDist;                             break;
            case NORTH_WEST:        alphaIndex -= moveDist;     numIndex -= moveDist;   break;
            /* These directions are for knights' moves */
            case NORTH_NORTH_EAST:  alphaIndex += 1;            numIndex -= 2;          break;
            case EAST_NORTH_EAST:   alphaIndex += 2;            numIndex -= 1;          break;
            case EAST_SOUTH_EAST:   alphaIndex += 2;            numIndex += 1;          break;
            case SOUTH_SOUTH_EAST:  alphaIndex += 1;            numIndex += 2;          break;
            case SOUTH_SOUTH_WEST:  alphaIndex -= 1;            numIndex += 2;          break;
            case WEST_SOUTH_WEST:   alphaIndex -= 2;            numIndex += 1;          break;
            case WEST_NORTH_WEST:   alphaIndex -= 2;            numIndex -= 1;          break;
            case NORTH_NORTH_WEST:  alphaIndex -= 1;            numIndex -= 2;          break;
            default: break;
        }
        if (alphaIndex < 0 || alphaIndex >= algNotnAlphaChars.length()
            || numIndex < 0 || numIndex >= algNotnNumChars.length()) {
            return null;
        }
        return String.valueOf(algNotnAlphaChars.charAt(alphaIndex))
               + String.valueOf(algNotnNumChars.charAt(numIndex));
    }

    private HashSet<String> rooksMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, EAST, SOUTH, WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(pieceLocation, moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> knightsMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH_NORTH_EAST, EAST_NORTH_EAST, EAST_SOUTH_EAST, SOUTH_SOUTH_EAST,
                                          SOUTH_SOUTH_WEST, WEST_SOUTH_WEST, WEST_NORTH_WEST, NORTH_NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(pieceLocation, 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> bishopsMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(pieceLocation, moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> queensMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(pieceLocation, moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> kingsMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(pieceLocation, 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> pawnsMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        if (pieceLocation.charAt(1) == '2' || pieceLocation.charAt(1) == '7') {
            moveRangesByDir.put(NORTH, new String[] {plotDirMove(pieceLocation, 1, NORTH),
                                                     plotDirMove(pieceLocation, 2, NORTH)});
        } else {
            moveRangesByDir.put(NORTH, new String[] {plotDirMove(pieceLocation, 1, NORTH)});
        }
        for (int moveDir : new int[] {NORTH_EAST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(pieceLocation, 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        if (moveRangesByDir.containsKey(NORTH)) {
            if (moveRangesByDir.get(NORTH).length >= 1) {
                if (!Objects.isNull(chessboard.getPieceAtLoc(moveRangesByDir.get(NORTH)[0]))) {
                    moveRangesByDir.remove(NORTH);
                } else if (moveRangesByDir.get(NORTH).length == 2
                           && !Objects.isNull(chessboard.getPieceAtLoc(moveRangesByDir.get(NORTH)[1]))) {
                    moveRangesByDir.put(NORTH, new String[] {moveRangesByDir.get(NORTH)[0]});
                }
            }
        }
        for (int moveDir : new int[] {NORTH_EAST, NORTH_WEST}) {
            if (moveRangesByDir.containsKey(moveDir)
                && (Objects.isNull(chessboard.getPieceAtLoc(moveRangesByDir.get(moveDir)[0]))
                    || chessboard.getPieceAtLoc(moveRangesByDir.get(moveDir)[0])
                                      .getColor().equals(piece.getColor()))) {
                moveRangesByDir.remove(moveDir);
            }
        }
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> moveMapToSet(final HashMap<Integer, String[]> moveMap) {
        HashSet<String> moveSet = new HashSet<>();
        for (String[] dirLocnArray : moveMap.values()) {
            for (int index = 0; index < dirLocnArray.length; index++) {
                moveSet.add(dirLocnArray[index]);
            }
        }
        return moveSet;
    }

    private void pruneMoveMap(final Piece piece, final HashMap<Integer, String[]> moveMap) {
        for (Object moveDirObj : moveMap.keySet().toArray()) {
            int moveDir = (int) moveDirObj;
            boolean moveCrossesOccupiedSquare = false;
            for (int index = 0; index < moveMap.get(moveDir).length; index++) {
                if (Objects.isNull(moveMap.get(moveDir)[index])) {
                    if (index == 0) {
                        moveMap.remove(moveDir);
                    } else {
                        moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index));
                    }
                    break;
                } else if (!Objects.isNull(chessboard.getPieceAtLoc(moveMap.get(moveDir)[index]))) {
                    if (chessboard.getPieceAtLoc(moveMap.get(moveDir)[index])
                                  .getColor().equals(piece.getColor())) {
                        if (index == 0) {
                            moveMap.remove(moveDir);
                        } else {
                            moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index));
                        }
                        break;
                    } else if (!chessboard.getPieceAtLoc(moveMap.get(moveDir)[index])
                                          .getColor().equals(piece.getColor())) {
                        moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index + 1));
                        break;
                    }
                }
            }
        }
    }

    public HashSet<String> getValidMoveSet(final Piece piece) {
        String pieceLocation = piece.getBoardLocation();
        if (Objects.isNull(piece)) {
            return null;
        }
        HashSet<String> retval;
        switch (piece.getRole()) {
            case "rook":
                retval = rooksMoves(piece, pieceLocation); break;
            case "knight":
                retval = knightsMoves(piece, pieceLocation); break;
            case "bishop":
                retval = bishopsMoves(piece, pieceLocation); break;
            case "queen":
                retval = queensMoves(piece, pieceLocation); break;
            case "king":
                retval = kingsMoves(piece, pieceLocation); break;
            case "pawn":
                retval = pawnsMoves(piece, pieceLocation); break;
            default:
                retval = null;
        }
        return retval;
    }

    public HashSet<String> getValidMoveSet(final String pieceLocation) {
        Piece piece = chessboard.getPieceAtLoc(pieceLocation);
        return getValidMoveSet(piece);
    }

    public boolean isSquareThreatened(final String squareLoc) {
        for (String piecesIdentity : chessboard.getPiecesIdentities()) {
            if (piecesIdentity.startsWith(colorPlaying)) {
                continue;
            }
            for (Piece piece : chessboard.getPiecesByIdentity(piecesIdentity)) {
                if (getValidMoveSet(piece).contains(squareLoc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Piece getPiece(final String pieceLoc) {
        return chessboard.getPieceAtLoc(pieceLoc);
    }

    public String numericIndexesToAlgNotnLoc(final int horizIndex, final int vertIndex) {
        final String alphaCharVals = "abcdefgh";
        final String numCharVals = "87654321";
        return String.valueOf(alphaCharVals.charAt(horizIndex)) + String.valueOf(numCharVals.charAt(vertIndex));
    }

    public void movePiece(final String pieceCurrentLoc, final String movingToLoc) {
        Piece movedPiece = chessboard.getPieceAtLoc(pieceCurrentLoc);
        chessboard.movePieceToLoc(movedPiece, movingToLoc);
    }

    public Piece[] getPiecesByIdentity(final String piecesIdentity) {
        return chessboard.getPiecesByIdentity(piecesIdentity);
    }

    public String[] getPiecesIdentities() {
        return chessboard.getPiecesIdentities();
    }

    public String[] getPiecesIdentities(final String substringToMatch) {
        Object[] objectArray = Arrays.asList(chessboard.getPiecesIdentities()).stream()
                                     .filter(strval -> strval.contains(substringToMatch)).map(String::new)
                                     .collect(Collectors.toList()).toArray();
        return Arrays.copyOf(objectArray, objectArray.length, String[].class);
    }

    public String getColorPlaying() {
        return colorPlaying;
    }

    public void checkIfKingIsInCheck(final String kingColor) {
        HashSet<Piece> checkedPieces = new HashSet<Piece>();
        Piece kingPiece = chessboard.getPiecesByIdentity(kingColor + "-king")[0];
        String kingLocation = kingPiece.getBoardLocation();
        if (kingPiece.isInCheck()) {
            HashSet<Piece> kingInCheckByPieces = kingPiece.getInCheckByPieces();
            for (Piece threateningPiece : kingInCheckByPieces) {
                if (!getValidMoveSet(threateningPiece).contains(kingLocation)) {
                    kingPiece.noLongerInCheckBy(threateningPiece);
                }
                checkedPieces.add(threateningPiece);
            }
        }
        for (String otherSidePieceIdentity : getPiecesIdentities((kingColor.equals("white") ? "black" : "white"))) {
            for (Piece otherSidePiece : getPiecesByIdentity(otherSidePieceIdentity)) {
                if (checkedPieces.contains(otherSidePiece)) {
                    continue;
                } else if (getValidMoveSet(otherSidePiece).contains(kingLocation)) {
                    kingPiece.inCheckByPiece(otherSidePiece);
                }
            }
        }
    }
}
