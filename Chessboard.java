package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.awt.Image;

public class Chessboard2 {

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

    private static final int DOUBLED = 0;
    private static final int ISOLATED = 1;
    private static final int BLOCKED = 2;

    private static final int NORTH = 0;
    private static final int NORTH_NORTH_EAST = 1;
    private static final int NORTH_EAST = 2;
    private static final int EAST_NORTH_EAST = 3;
    private static final int EAST = 4;
    private static final int EAST_SOUTH_EAST = 5;
    private static final int SOUTH_EAST = 6;
    private static final int SOUTH_SOUTH_EAST = 7;
    private static final int SOUTH = 8;
    private static final int SOUTH_SOUTH_WEST = 9;
    private static final int SOUTH_WEST = 10;
    private static final int WEST_SOUTH_WEST = 11;
    private static final int WEST = 12;
    private static final int WEST_NORTH_WEST = 13;
    private static final int NORTH_WEST = 14;
    private static final int NORTH_NORTH_WEST = 15;

    private static final String ALG_NOTN_ALPHA_CHARS = "abcdefgh";
    private static final String ALG_NOTN_NUM_CHARS = "87654321";

    private static final HashMap<Integer, Integer[][]> piecesStartingLocsWhiteBelow = new HashMap<>() {{
        this.put(BLACK | KING,           new Integer[][] {new Integer[] {3, 0}});
        this.put(BLACK | QUEEN,          new Integer[][] {new Integer[] {4, 0}});
        this.put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        this.put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        this.put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        this.put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        this.put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                          new Integer[] {2, 1}, new Integer[] {3, 1},
                                                          new Integer[] {4, 1}, new Integer[] {5, 1},
                                                          new Integer[] {6, 1}, new Integer[] {7, 1}});
        this.put(WHITE | KING,           new Integer[][] {new Integer[] {3, 7}});
        this.put(WHITE | QUEEN,          new Integer[][] {new Integer[] {4, 7}});
        this.put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        this.put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        this.put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        this.put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        this.put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                          new Integer[] {2, 6}, new Integer[] {3, 6},
                                                          new Integer[] {4, 6}, new Integer[] {5, 6},
                                                          new Integer[] {6, 6}, new Integer[] {7, 6}});
   }};

    private static final HashMap<Integer, Integer[][]> piecesStartingLocsBlackBelow = new HashMap<>() {{
        this.put(WHITE | KING,           new Integer[][] {new Integer[] {3, 0}});
        this.put(WHITE | QUEEN,          new Integer[][] {new Integer[] {4, 0}});
        this.put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        this.put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        this.put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        this.put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        this.put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                          new Integer[] {2, 1}, new Integer[] {3, 1},
                                                          new Integer[] {4, 1}, new Integer[] {5, 1},
                                                          new Integer[] {6, 1}, new Integer[] {7, 1}});
        this.put(BLACK | KING,           new Integer[][] {new Integer[] {3, 7}});
        this.put(BLACK | QUEEN,          new Integer[][] {new Integer[] {4, 7}});
        this.put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        this.put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        this.put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        this.put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        this.put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                          new Integer[] {2, 6}, new Integer[] {3, 6},
                                                          new Integer[] {4, 6}, new Integer[] {5, 6},
                                                          new Integer[] {6, 6}, new Integer[] {7, 6}});
   }};

    private static final HashMap<Integer, String> pieceIntsToIdentities = new HashMap<>() {{
        this.put(WHITE | KING,           "white-king");
        this.put(WHITE | QUEEN,          "white-queen");
        this.put(WHITE | ROOK,           "white-rook");
        this.put(WHITE | BISHOP,         "white-bishop");
        this.put(WHITE | KNIGHT | RIGHT, "white-knight-right");
        this.put(WHITE | KNIGHT | LEFT,  "white-knight-left");
        this.put(WHITE | PAWN,           "white-pawn");
        this.put(BLACK | KING,           "black-king");
        this.put(BLACK | QUEEN,          "black-queen");
        this.put(BLACK | ROOK,           "black-rook");
        this.put(BLACK | BISHOP,         "black-bishop");
        this.put(BLACK | KNIGHT | RIGHT, "black-knight-right");
        this.put(BLACK | KNIGHT | LEFT,  "black-knight-left");
        this.put(BLACK | PAWN,           "black-pawn");
   }};

    private HashMap<Integer, Piece> pieceIntsToPieceObjs;

    private int colorOnTop;
    private int colorPlaying;
    private Piece lastMovedPiece = null;
    private static HashMap<String, Piece> piecesLocations;
    private int[][] boardArray;

    public Chessboard2(final ImagesManager imagesManager, final String playingColor) {

        /* We only need the ImagesManager object to instantiate Piece objects
           with the correct Image 2nd argument. It's not saved to an instance
           variable since it's never used again after this constructor. */
        colorPlaying = playingColor == "white" ? WHITE : BLACK;

        boardArray = new int[8][8];

        colorOnTop = colorPlaying == WHITE ? BLACK : WHITE;

        HashMap<Integer, Integer[][]> piecesStartingCoords = (colorPlaying == WHITE)
                                                             ? piecesStartingLocsWhiteBelow
                                                             : piecesStartingLocsBlackBelow;

        for (Entry<Integer, Integer[][]> pieceToCoords : piecesStartingCoords.entrySet()) {
            int pieceInt = pieceToCoords.getKey();
            for (Integer[] coords : pieceToCoords.getValue()) {
                int xIdx = coords[0];
                int yIdx = coords[1];
                boardArray[xIdx][yIdx] = pieceInt;
           }
       }

        pieceIntsToPieceObjs = new HashMap<>();

        for (int pieceInt : pieceIntsToIdentities.keySet()) {
            String pieceIdentity = pieceIntsToIdentities.get(pieceInt);
            Image pieceImage = imagesManager.getImageByIdentity(pieceIdentity);
            Piece pieceObj = new Piece(pieceIdentity, pieceImage);
            pieceIntsToPieceObjs.put(pieceInt, pieceObj);
            pieceObj = pieceIntsToPieceObjs.get(pieceInt);
            assert !Objects.isNull(pieceObj);
       }
   }

    public int[][] getBoardArray() {
        return boardArray;
   }

    public String numericIndexesToAlgNotnLoc(final int horizIndex, final int vertIndex) {
        return String.valueOf(ALG_NOTN_ALPHA_CHARS.charAt(horizIndex))
               + String.valueOf(ALG_NOTN_NUM_CHARS.charAt(vertIndex));
   }

    public int[] algNotnLocToNumericIndexes(final String location) {
        int[] coordinates = new int[2];
        coordinates[0] = ALG_NOTN_ALPHA_CHARS.indexOf(String.valueOf(location.charAt(0)));
        coordinates[1] = ALG_NOTN_NUM_CHARS.indexOf(String.valueOf(location.charAt(1)));
        return coordinates;
   }

    public String getColorPlaying() {
        return (colorPlaying == WHITE) ? "white" : "black";
   }

    public Piece getPieceAtLocation(final String location) {
        int[] coords = algNotnLocToNumericIndexes(location);
        int xIdx = coords[0];
        int yIdx = coords[1];
        if (boardArray[xIdx][yIdx] == 0) {
            return null;
       }

        int pieceInt = boardArray[xIdx][yIdx];
        Piece piece = pieceIntsToPieceObjs.get(pieceInt);
        piece = piece.clone();
        piece.setLocation(location);
        return piece;
   }

    public Piece getPieceAtCoords(final int horizCoord, final int vertCoord) {
        if (boardArray[horizCoord][vertCoord] == 0) {
            return null;
       }
        int pieceInt = boardArray[horizCoord][vertCoord];
        Piece piece = pieceIntsToPieceObjs.get(pieceInt).clone();
        String location = numericIndexesToAlgNotnLoc(horizCoord, vertCoord);
        piece.setLocation(location);
        return piece;
   }

    public Piece pieceIntToPieceObj(final int pieceInt) {
        return pieceIntsToPieceObjs.get(pieceInt).clone();
   }

    public HashSet<String> getValidMoveSet(final String location) throws AlgorithmBadArgumentException {
        int[] coords = algNotnLocToNumericIndexes(location);
        int xIdx = coords[0];
        int yIdx = coords[1];
        int usedArrayLength = 0;
        int[][] movesArray = new int[32][6];
        HashSet<String> moveSet = new HashSet<>();
        usedArrayLength = BoardArrays.generatePieceMoves(boardArray, movesArray, 0, xIdx, yIdx, colorPlaying, colorOnTop);
        for (int moveIdx = 0; moveIdx < usedArrayLength; moveIdx++) {
            int moveXIdx = movesArray[moveIdx][3];
            int moveYIdx = movesArray[moveIdx][4];
            moveSet.add(numericIndexesToAlgNotnLoc(moveXIdx, moveYIdx));
       }
        return moveSet;
   }

    public boolean isSquareThreatened(final String squareLoc, final String opposingColorStr) {
        int otherColor = opposingColorStr.equals("white") ? WHITE : BLACK;
        int thisColor = otherColor == WHITE ? BLACK : WHITE;
        int[] coords = algNotnLocToNumericIndexes(squareLoc);
        int xIdx = coords[0];
        int yIdx = coords[1];
        boolean retval;
        synchronized (boardArray) {
            int savedPieceInt = boardArray[xIdx][yIdx];
            boardArray[xIdx][yIdx] = thisColor | KING;
            retval = BoardArrays.isKingInCheck(boardArray, otherColor, colorOnTop);
            boardArray[xIdx][yIdx] = savedPieceInt;
       }
        return retval;
   }

    public Piece movePiece(final Piece movingPiece, final String pieceCurrentLoc, final String movingToLoc) throws KingIsInCheckError {
        int[] fromCoords = algNotnLocToNumericIndexes(pieceCurrentLoc);
        int fromXIdx = fromCoords[0];
        int fromYIdx = fromCoords[1];
        int pieceInt = boardArray[fromXIdx][fromYIdx];
        int colorOfPiece = (pieceInt & WHITE) != 0 ? WHITE : BLACK;
        int otherColor = (colorOfPiece == WHITE) ? BLACK : WHITE;

        int[] toCoords = algNotnLocToNumericIndexes(movingToLoc);
        int toXIdx = toCoords[0];
        int toYIdx = toCoords[1];
        int capturedPieceInt;
        Piece capturedPiece = null;

        capturedPieceInt = boardArray[toXIdx][toYIdx];
        if (capturedPieceInt != 0) {
            capturedPiece = pieceIntsToPieceObjs.get(pieceInt).clone();
       }

        boardArray[toXIdx][toYIdx] = boardArray[fromXIdx][fromYIdx];
        boardArray[fromXIdx][fromYIdx] = 0;

        if (BoardArrays.isKingInCheck(boardArray, otherColor, colorOnTop)) {
            boardArray[fromXIdx][fromYIdx] = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = capturedPieceInt;
            throw new KingIsInCheckError("move would place this color'a King in check or this color's King is in check and this move doesn't fix that; move can't be made");
       }

        return capturedPiece;
   }

    public int[][] occupiedSquareCoords() {
        int pieceCount = 0;
        int pieceIndex = 0;

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] != 0) {
                    pieceCount++;
               }
           }
       }

        int[][] squareCoords = new int[pieceCount][2];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] != 0) {
                    squareCoords[pieceIndex][0] = xIdx;
                    squareCoords[pieceIndex][1] = yIdx;
                    pieceIndex++;
               }
           }
       }

        return squareCoords;
   }
}
