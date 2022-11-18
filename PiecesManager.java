package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Objects;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.StringJoiner;
import net.sf.repr.Repr;

public class PiecesManager implements Iterable<String> {

    public static String WHITE = "white";
    public static String BLACK = "black";

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

    private HashMap<String, Piece> piecesLocations = new HashMap<>() {{
        for (String algNotnAlphaChar : "abcdefgh".split("(?=.)")) {
            for (String algNotnNumChar : "87654321".split("(?=.)")) {
                this.put(algNotnAlphaChar + algNotnNumChar, null);
            }
        }
     /* This yields results identical to:
        this.put("a8", null); this.put("a7", null); this.put("a6", null); this.put("a5", null);
        this.put("a4", null); this.put("a3", null); this.put("a2", null); this.put("a1", null);
        this.put("b8", null); this.put("b7", null); this.put("b6", null); this.put("b5", null);
        ... and so on */
    }};

    private ImagesManager imagesManager;

    private HashMap<String, Piece[]> piecesMap;

    private String playingColor;

    public PiecesManager(final ImagesManager imgMgr, final String colorPlaying) {

        imagesManager = imgMgr;
        playingColor = colorPlaying;
        piecesMap = new HashMap<String, Piece[]>();

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
            piecesMap.put(pieceStartingLocs.getKey(), pieces);
        }

        for (Entry<String, Piece[]> identityTopiece : piecesMap.entrySet()) {
            for (Piece piece : identityTopiece.getValue()) {
                piecesLocations.put(piece.getBoardLocation(), piece);
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
            for (int moveDist : new int[] {1,2,3,4,5,6,7}) {
                moveRangesByDir.get(moveDir)[moveDist-1] = plotDirMove(pieceLocation, moveDist, moveDir);
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
            for (int moveDist : new int[] {1,2,3,4,5,6,7}) {
                moveRangesByDir.get(moveDir)[moveDist-1] = plotDirMove(pieceLocation, moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> queensMoves(final Piece piece, final String pieceLocation) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1,2,3,4,5,6,7}) {
                moveRangesByDir.get(moveDir)[moveDist-1] = plotDirMove(pieceLocation, moveDist, moveDir);
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
                if (!Objects.isNull(piecesLocations.get(moveRangesByDir.get(NORTH)[0]))) {
                    moveRangesByDir.remove(NORTH);
                } else if (moveRangesByDir.get(NORTH).length == 2
                           && !Objects.isNull(piecesLocations.get(moveRangesByDir.get(NORTH)[1]))) {
                    moveRangesByDir.put(NORTH, new String[] {moveRangesByDir.get(NORTH)[0]});
                }
            }
        }
        for (int moveDir : new int[] {NORTH_EAST, NORTH_WEST}) {
            if (moveRangesByDir.containsKey(moveDir)
                && (Objects.isNull(piecesLocations.get(moveRangesByDir.get(moveDir)[0]))
                    || piecesLocations.get(moveRangesByDir.get(moveDir)[0])
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
                } else if (!Objects.isNull(piecesLocations.get(moveMap.get(moveDir)[index]))) {
                    if (piecesLocations.get(moveMap.get(moveDir)[index]).getColor().equals(piece.getColor())) {
                        if (index == 0) {
                            moveMap.remove(moveDir);
                        } else {
                            moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index));
                        }
                        break;
                    } else if (!piecesLocations.get(moveMap.get(moveDir)[index]).getColor().equals(piece.getColor())) {
                        moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index+1));
                        break;
                    }
                }
            }
        }
    }

    public HashSet<String> getValidMoveSet(final String pieceLocation) {
        Piece piece = piecesLocations.get(pieceLocation);
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

    public void movePiece(final String pieceCurrentLoc, final String pieceMoveToLoc) {
        piecesLocations.put(pieceMoveToLoc, piecesLocations.get(pieceCurrentLoc));
        piecesLocations.put(pieceCurrentLoc, null);
    }

    public Piece[] getPieces(final String pieceIdentity) {
        return piecesMap.get(pieceIdentity);
    }

    @Override
    public Iterator<String> iterator() {
        Iterator<String> iterator = new Iterator<String>() {
            private Object[] piecesFlags = piecesMap.keySet().toArray();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < piecesFlags.length;
            }

            @Override
            public String next() {
                String retval = (String) piecesFlags[index];
                index += 1;
                return retval;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return iterator;
    }
}
