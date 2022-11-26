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

    public static final HashSet<Integer> VALID_PIECE_INTS = new HashSet<Integer>() {{ 
        this.add(BLACK | KING); this.add(BLACK | QUEEN); this.add(BLACK | ROOK); this.add(BLACK | BISHOP);
        this.add(BLACK | KNIGHT | RIGHT); this.add(BLACK | KNIGHT | LEFT); this.add(BLACK | PAWN);
        this.add(WHITE | KING); this.add(WHITE | QUEEN); this.add(WHITE | ROOK); this.add(WHITE | BISHOP);
        this.add(WHITE | KNIGHT | RIGHT); this.add(WHITE | KNIGHT | LEFT); this.add(WHITE | PAWN);
    }};

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
        put(BLACK | KING,           new Integer[][] {new Integer[] {3, 0}});
        put(BLACK | QUEEN,          new Integer[][] {new Integer[] {4, 0}});
        put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                     new Integer[] {2, 1}, new Integer[] {3, 1},
                                                     new Integer[] {4, 1}, new Integer[] {5, 1},
                                                     new Integer[] {6, 1}, new Integer[] {7, 1}});
        put(WHITE | KING,           new Integer[][] {new Integer[] {3, 7}});
        put(WHITE | QUEEN,          new Integer[][] {new Integer[] {4, 7}});
        put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                     new Integer[] {2, 6}, new Integer[] {3, 6},
                                                     new Integer[] {4, 6}, new Integer[] {5, 6},
                                                     new Integer[] {6, 6}, new Integer[] {7, 6}});
    }};

    private static final HashMap<Integer, Integer[][]> piecesStartingLocsBlackBelow = new HashMap<>() {{
        put(WHITE | KING,           new Integer[][] {new Integer[] {3, 0}});
        put(WHITE | QUEEN,          new Integer[][] {new Integer[] {4, 0}});
        put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                     new Integer[] {2, 1}, new Integer[] {3, 1},
                                                     new Integer[] {4, 1}, new Integer[] {5, 1},
                                                     new Integer[] {6, 1}, new Integer[] {7, 1}});
        put(BLACK | KING,           new Integer[][] {new Integer[] {3, 7}});
        put(BLACK | QUEEN,          new Integer[][] {new Integer[] {4, 7}});
        put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                     new Integer[] {2, 6}, new Integer[] {3, 6},
                                                     new Integer[] {4, 6}, new Integer[] {5, 6},
                                                     new Integer[] {6, 6}, new Integer[] {7, 6}});
    }};

    public static final HashMap<String, Integer> pieceStrsToInts = new HashMap<>() {{
        put("white-king",           WHITE | KING);
        put("white-queen",          WHITE | QUEEN);
        put("white-rook",           WHITE | ROOK);
        put("white-bishop",         WHITE | BISHOP);
        put("white-knight-right",   WHITE | KNIGHT | RIGHT);
        put("white-knight-left",    WHITE | KNIGHT | LEFT);
        put("white-pawn",           WHITE | PAWN);
        put("black-king",           BLACK | KING);
        put("black-queen",          BLACK | QUEEN);
        put("black-rook",           BLACK | ROOK);
        put("black-bishop",         BLACK | BISHOP);
        put("black-knight-right",   BLACK | KNIGHT | RIGHT);
        put("black-knight-left",    BLACK | KNIGHT | LEFT);
        put("black-pawn",           BLACK | PAWN);
    }};

    private HashMap<Integer, Image> pieceImages;

    private int colorOnTop;
    private int colorPlaying;
    private static HashMap<String, Piece> piecesLocations;
    private int[][] boardArray;

    public record Move(Piece movingPiece, int fromXCoord, int fromYCoord, int toXCoord, int toYCoord) { };

    public record Piece(int pieceInt, Image pieceImage, int xCoord, int yCoord) { };

    public Chessboard(final ImagesManager imagesManager, final String playingColor) {
        this(null, imagesManager, playingColor);
    }

    public Chessboard(final int[][] boardArrayVal, final ImagesManager imagesManager, final String playingColor) {

        /* We only need the ImagesManager object to instantiate Piece objects
           with the correct Image 2nd argument. It's not saved to an instance
           variable since it's never used again after this constructor. */
        colorPlaying = playingColor == "white" ? WHITE : BLACK;

        if (Objects.isNull(boardArrayVal)) {
            boardArray = new int[8][8];
        } else {
            boardArray = boardArrayVal;
        }

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

        pieceImages = new HashMap<Integer, Image>();

        for (int pieceInt : VALID_PIECE_INTS) {
            pieceImages.put(pieceInt, imagesManager.getImageByPieceInt(pieceInt));
        }
    }

    public int[][] getBoardArray() {
        return boardArray;
    }

    public int getColorPlaying() {
        return colorPlaying;
    }

    public Piece getPieceAtCoords(final int[] coords) {
        return getPieceAtCoords(coords[0], coords[1]);
    }

    public Piece getPieceAtCoords(final int xCoord, final int yCoord) {
        if (boardArray[xCoord][yCoord] == 0) {
            return null;
        }
        int pieceInt = boardArray[xCoord][yCoord];
        return new Piece(pieceInt, pieceImages.get(pieceInt), xCoord, yCoord);
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

    public boolean isSquareThreatened(final int[] coords, final int opposingColor) {
        return isSquareThreatened(coords[0], coords[1], opposingColor);
    }

    public boolean isSquareThreatened(final int xCoord, final int yCoord, final int opposingColor) {
        int otherColor = opposingColor;
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

    public void movePiece(final Move moveObj) throws KingIsInCheckError {
        movePiece(moveObj.movingPiece(), moveObj.fromXCoord(), moveObj.fromYCoord(), moveObj.toXCoord(), moveObj.toYCoord());
    }

    public void movePiece(final Piece movingPiece, final int[] fromCoords, final int[] toCoords) throws KingIsInCheckError {
        movePiece(movingPiece, fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);
    }

    public void movePiece(final Piece movingPiece, final int fromXCoord, final int fromYCoord, final int toXCoord, final int toYCoord) throws KingIsInCheckError {
        int pieceInt = boardArray[fromXCoord][fromYCoord];
        int colorOfPiece = (pieceInt & WHITE) != 0 ? WHITE : BLACK;
        int otherColor = (colorOfPiece == WHITE) ? BLACK : WHITE;

        int capturedPieceInt = boardArray[toXCoord][toYCoord];

        boardArray[toXCoord][toYCoord] = boardArray[fromXCoord][fromYCoord];
        boardArray[fromXCoord][fromYCoord] = 0;

        if (BoardArrays.isKingInCheck(boardArray, otherColor, colorOnTop)) {
            boardArray[fromXCoord][fromYCoord] = boardArray[toXCoord][toYCoord];
            boardArray[toXCoord][toYCoord] = capturedPieceInt;
            throw new KingIsInCheckError("Move would place this color'a King in check or this color's King is in check "
                                         + "and this move doesn't fix that; move can't be made.");
        }
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
