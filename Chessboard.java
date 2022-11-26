package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.awt.Image;

public class Chessboard {

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

    public record Move(Piece movingPiece, int fromXCoord, int fromYCoord, int toXCoord, int toYCoord) { };

    public Chessboard(final ImagesManager imagesManager, final String playingColor) {

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

    public String getColorPlaying() {
        return (colorPlaying == WHITE) ? "white" : "black";
    }

    public Piece getPieceAtCoords(final int[] coords) {
        return getPieceAtCoords(coords[0], coords[1]);
    }

    public Piece getPieceAtCoords(final int xCoord, final int yCoord) {
        if (boardArray[xCoord][yCoord] == 0) {
            return null;
        }

        int pieceInt = boardArray[xCoord][yCoord];
        Piece piece = pieceIntsToPieceObjs.get(pieceInt);
        piece = piece.clone();
        piece.setCoords(xCoord, yCoord);
        return piece;
    }

    public Piece pieceIntToPieceObj(final int pieceInt) {
        return pieceIntsToPieceObjs.get(pieceInt).clone();
    }

    public int[][] getValidMoveCoordsArray(final int[] coords) throws AlgorithmBadArgumentException {
        return getValidMoveCoordsArray(coords[0], coords[1]);
    }

    public int[][] getValidMoveCoordsArray(final int xCoord, final int yCoord) throws AlgorithmBadArgumentException {
        int usedArrayLength = 0;
        int coordsIndex = 0;
        int[][] movesArray = new int[32][6];
        int[][] movesCoords;
        usedArrayLength = BoardArrays.generatePieceMoves(boardArray, movesArray, 0, xCoord, yCoord, colorPlaying, colorOnTop);
        movesCoords = new int[usedArrayLength][2];
        for (int moveIdx = 0; moveIdx < usedArrayLength; moveIdx++) {
            int moveXIdx = movesArray[moveIdx][3];
            int moveYIdx = movesArray[moveIdx][4];
            movesCoords[coordsIndex][0] = moveXIdx;
            movesCoords[coordsIndex][1] = moveYIdx;
            coordsIndex++;
        }
        return movesCoords;
    }

    public boolean isSquareThreatened(final int[] coords, final String opposingColorStr) {
        return isSquareThreatened(coords[0], coords[1], opposingColorStr);
    }

    public boolean isSquareThreatened(final int xCoord, final int yCoord, final String opposingColorStr) {
        int otherColor = opposingColorStr.equals("white") ? WHITE : BLACK;
        int thisColor = otherColor == WHITE ? BLACK : WHITE;
        boolean retval;
        /* This operation momentarily changes the boardArray to an invalid state
           to use BoardArray.isKingInCheck() method to detect a threatened
           square. Although this is not a threaded package, synchronized(){} is
           used just in case it's possible that another part of the calling code
           might try to use the boardArray at the same time. */
        synchronized (boardArray) {
            int savedPieceInt = boardArray[xCoord][yCoord];
            boardArray[xCoord][yCoord] = thisColor | KING;
            retval = BoardArrays.isKingInCheck(boardArray, otherColor, colorOnTop);
            boardArray[xCoord][yCoord] = savedPieceInt;
        }
        return retval;
    }

    public Piece movePiece(final Move moveObj) throws KingIsInCheckError {
        return movePiece(moveObj.movingPiece(), moveObj.fromXCoord(), moveObj.fromYCoord(), moveObj.toXCoord(), moveObj.toYCoord());
    }

    public Piece movePiece(final Piece movingPiece, final int[] fromCoords, final int[] toCoords) throws KingIsInCheckError {
        return movePiece(movingPiece, fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
    }

    public Piece movePiece(final Piece movingPiece, final int fromXCoord, final int fromYCoord, final int toXCoord, final int toYCoord) throws KingIsInCheckError {
        int pieceInt = boardArray[fromXCoord][fromYCoord];
        int colorOfPiece = (pieceInt & WHITE) != 0 ? WHITE : BLACK;
        int otherColor = (colorOfPiece == WHITE) ? BLACK : WHITE;

        int capturedPieceInt;
        Piece capturedPiece = null;

        capturedPieceInt = boardArray[toXCoord][toYCoord];
        if (capturedPieceInt != 0) {
            capturedPiece = pieceIntsToPieceObjs.get(pieceInt).clone();
        }

        boardArray[toXCoord][toYCoord] = boardArray[fromXCoord][fromYCoord];
        boardArray[fromXCoord][fromYCoord] = 0;

        if (BoardArrays.isKingInCheck(boardArray, otherColor, colorOnTop)) {
            boardArray[fromXCoord][fromYCoord] = boardArray[toXCoord][toYCoord];
            boardArray[toXCoord][toYCoord] = capturedPieceInt;
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
