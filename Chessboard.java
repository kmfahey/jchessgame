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

    public static final HashSet<Integer> VALID_PIECE_INTS = BoardArrays.VALID_PIECE_INTS;

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
    private int colorOpposing;
    private static HashMap<String, Piece> piecesLocations;
    private int[][] boardArray;
    private boolean blackKingsRookHasMoved = false;
    private boolean blackQueensRookHasMoved = false;
    private boolean whiteKingsRookHasMoved = false;
    private boolean whiteQueensRookHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean whiteKingHasMoved = false;

    public record Piece(int pieceInt, Image pieceImage, int xCoord, int yCoord) { };

    public record Move(Chessboard.Piece movingPiece, int fromXCoord, int fromYCoord,
                int toXCoord, int toYCoord, int capturedPieceInt, boolean isCastlingKingside,
                boolean isCastlingQueenside, int promotedToPieceInt) {
        public String toString() {
            if (isCastlingKingside) {
                return "0-0";
            } else if (isCastlingQueenside) {
                return "0-0-0";
            }
            int pieceInt = movingPiece.pieceInt();
            pieceInt = (pieceInt & WHITE) != 0 ? pieceInt ^ WHITE : pieceInt ^ BLACK;
            String retval = BoardArrays.PIECES_ABBRS.get(pieceInt);
            retval += BoardArrays.coordsToAlgNotn(fromXCoord, fromYCoord);
            retval += (capturedPieceInt != 0) ? "x" : "-";
            retval += BoardArrays.coordsToAlgNotn(toXCoord, toYCoord);
            if (promotedToPieceInt != 0) {
                int promotedToPieceIntWoColor = (promotedToPieceInt & WHITE) != 0
                                                ? promotedToPieceInt ^ WHITE
                                                : promotedToPieceInt ^ BLACK;
                retval += BoardArrays.PIECES_ABBRS.get(promotedToPieceIntWoColor);
            }
            return retval;
        }
    };

    public Chessboard(final ImagesManager imagesManager, final int playingColor, final int onTopColor) {
        this(null, imagesManager, playingColor, onTopColor);
    }

    public Chessboard(final int[][] boardArrayVal, final ImagesManager imagesManager, final int playingColor,
                      final int onTopColor) {

        setColors(playingColor, onTopColor);

        if (Objects.nonNull(boardArrayVal)) {
            boardArray = boardArrayVal;
        } else {
            layOutPieces();
        }

        /* We only need the ImagesManager object to instantiate Piece objects
           with the correct Image 2nd argument. It's not saved to an instance
           variable since it's never used again after this constructor. */

        pieceImages = new HashMap<Integer, Image>();

        for (int pieceInt : VALID_PIECE_INTS) {
            pieceImages.put(pieceInt, imagesManager.getImageByPieceInt(pieceInt));
        }
    }

    public void setColors(final int playingColor, final int onTopColor) {
        colorPlaying = playingColor;
        colorOnTop = onTopColor;
        colorOpposing = colorPlaying == WHITE ? BLACK : WHITE;
    }

    public void layOutPieces() {
        HashMap<Integer, Integer[][]> piecesStartingCoords;

        if (colorPlaying == WHITE) {
            piecesStartingCoords = piecesStartingLocsWhiteBelow;
        } else {
            piecesStartingCoords = piecesStartingLocsBlackBelow;
        }

        boardArray = new int[8][8];

        for (Entry<Integer, Integer[][]> pieceToCoords : piecesStartingCoords.entrySet()) {
            int pieceInt = pieceToCoords.getKey();
            for (Integer[] coords : pieceToCoords.getValue()) {
                int xIdx = coords[0];
                int yIdx = coords[1];
                boardArray[xIdx][yIdx] = pieceInt;
            }
        }
    }

    public int[][] getBoardArray() {
        return boardArray;
    }

    public void setBoardArray(int[][] arrayBoard) {
        boardArray = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                boardArray[xIdx][yIdx] = arrayBoard[xIdx][yIdx];
            }
        }
    }

    public int getColorOnTop() {
        return colorOnTop;
    }

    public int getColorPlaying() {
        return colorPlaying;
    }

    public int getColorOpposing() {
        return colorOpposing;
    }

    public void promotePawn(final int xCoord, final int yCoord, final int newPiece) {
        int pieceInt = boardArray[xCoord][yCoord];
        if ((pieceInt & PAWN) == 0) {
            throw new IllegalArgumentException("Chessboard.promotePawn() called with invalid coordinates "
                                               + "(" + xCoord + ", " + yCoord + "): no pawn at that location.");
        }
        int pieceColor = pieceInt ^ PAWN;
        boardArray[xCoord][yCoord] = pieceColor | newPiece;
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

    public HashMap<String, HashSet<String>> getPossibleMovesMapOfSets() {
        int[][] movesArray = new int[128][7];

        int movesArrayUsedLength = BoardArrays.generatePossibleMoves(boardArray, movesArray, colorPlaying, colorOnTop);

        HashMap<String, HashSet<String>> movesMapOfSets = new HashMap<String, HashSet<String>>();

        for (int index = 0; index < movesArrayUsedLength; index++) {
            int[] moveArray = movesArray[index];
            int fromXIdx = moveArray[1];
            int fromYIdx = moveArray[2];
            int toXIdx = moveArray[3];
            int toYIdx = moveArray[4];
            String fromLocation = BoardArrays.coordsToAlgNotn(fromXIdx, fromYIdx);
            String toLocation = BoardArrays.coordsToAlgNotn(toXIdx, toYIdx);
            if (!movesMapOfSets.containsKey(fromLocation)) {
                HashSet<String> newSet = new HashSet<String>();
                newSet.add(toLocation);
                movesMapOfSets.put(fromLocation, newSet);
            } else {
                movesMapOfSets.get(fromLocation).add(toLocation);
            }
        }

        return movesMapOfSets;
    }

    public int[][] getValidMoveCoordsArray(final int[] coords) throws IllegalArgumentException {
        return getValidMoveCoordsArray(coords[0], coords[1]);
    }

    public int[][] getValidMoveCoordsArray(final int xCoord, final int yCoord) throws IllegalArgumentException {
        int usedArrayLength = 0;
        int coordsIndex = 0;
        int[][] movesArray = new int[32][7];
        int[][] movesCoords;
        usedArrayLength = BoardArrays.generatePieceMoves(boardArray, movesArray, 0, xCoord, yCoord,
                                                         colorPlaying, colorOnTop);
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

    public boolean isCastlingPossible(final int colorOfKing, final int kingOrQueen) throws IllegalArgumentException {
        switch (colorOfKing | kingOrQueen) {
            case BLACK | KING -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                if (blackKingHasMoved || blackKingsRookHasMoved) {
                    return false;
                } else if (boardArray[1][yIdx] != 0 || boardArray[2][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            case BLACK | QUEEN -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                if (blackKingHasMoved || blackQueensRookHasMoved) {
                    return false;
                } else if (boardArray[4][yIdx] != 0 || boardArray[5][yIdx] != 0 || boardArray[6][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            case WHITE | KING -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                if (whiteKingHasMoved || whiteKingsRookHasMoved) {
                    return false;
                } else if (boardArray[1][yIdx] != 0 || boardArray[2][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            case WHITE | QUEEN -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                if (whiteKingHasMoved || whiteQueensRookHasMoved) {
                    return false;
                } else if (boardArray[4][yIdx] != 0 || boardArray[5][yIdx] != 0 || boardArray[6][7] != 0) {
                    return false;
                }
                return true;
            }
            default -> {
                throw new IllegalArgumentException("could not resolve arguments to isCastlingPossible()");
            }
        }
    }

    private void movePieceCastling(final Chessboard.Move moveObj
                                  ) throws KingIsInCheckException, IllegalArgumentException,
                                           CastlingNotPossibleException {
        int kingXCoord = moveObj.fromXCoord();
        int kingYCoord = moveObj.fromYCoord();
        int rookXCoord = moveObj.toXCoord();
        int rookYCoord = moveObj.fromYCoord();
        int pieceInt = moveObj.movingPiece().pieceInt();
        boolean isCastlingKingside = moveObj.isCastlingKingside();
        boolean isCastlingQueenside = moveObj.isCastlingQueenside();
        int colorOfPiece = (pieceInt & WHITE) != 0 ? WHITE : BLACK;
        int otherColor = (colorOfPiece == WHITE) ? BLACK : WHITE;
        int rookPieceInt = boardArray[rookXCoord][rookYCoord];
        int savedPieceIntNo1 = 0;
        int savedPieceIntNo2 = 0;
        String colorOfPieceStr = (colorOfPiece == WHITE ? "White" : "Black");

        if ((pieceInt & KING) == 0 || rookPieceInt != (colorOfPiece | ROOK)) {
            throw new IllegalArgumentException("Invalid castling movePiece() parameters.");
        } else if (isCastlingKingside) {
            if (!isCastlingPossible(colorOfPiece, KING)) {
                throw new CastlingNotPossibleException("Castling kingside is not possible for " + colorOfPieceStr);
            }

            if (BoardArrays.wouldKingBeInCheck(boardArray, 2, kingYCoord, colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException("Castling kingside would place " + colorOfPieceStr + "'s king in "
                                                 + "check or " + colorOfPieceStr + "'s king is in check and this move "
                                                 + "doesn't fix that. Move can't be made.");
            }

            savedPieceIntNo1 = boardArray[2][rookYCoord];
            boardArray[2][rookYCoord] = boardArray[rookXCoord][rookYCoord];
            boardArray[rookXCoord][rookYCoord] = 0;

            savedPieceIntNo2 = boardArray[1][kingYCoord];
            boardArray[1][kingYCoord] = boardArray[kingXCoord][kingYCoord];
            boardArray[kingXCoord][kingYCoord] = 0;

            if (colorOfPiece == WHITE) {
                whiteKingHasMoved = true;
                whiteKingsRookHasMoved = true;
            } else {
                blackKingHasMoved = true;
                blackKingsRookHasMoved = true;
            }
        } else if (isCastlingQueenside) {
            if (!isCastlingPossible(colorOfPiece, QUEEN)) {
                throw new CastlingNotPossibleException("Castling queenside is not possible for " + colorOfPieceStr);
            }

            if (BoardArrays.wouldKingBeInCheck(boardArray, 6, kingYCoord, colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException("Castling queenside would place " + colorOfPieceStr + "'s king in "
                                                 + "check or " + colorOfPieceStr + "'s king is in check and this move "
                                                 + "doesn't fix that. Move can't be made.");
            }

            savedPieceIntNo1 = boardArray[5][rookYCoord];
            boardArray[5][rookYCoord] = boardArray[rookXCoord][rookYCoord];
            boardArray[rookXCoord][rookYCoord] = 0;

            savedPieceIntNo2 = boardArray[6][kingYCoord];
            boardArray[6][kingYCoord] = boardArray[kingXCoord][kingYCoord];
            boardArray[kingXCoord][kingYCoord] = 0;
            
            if (colorOfPiece == WHITE) {
                whiteKingHasMoved = true;
                whiteQueensRookHasMoved = true;
            } else {
                blackKingHasMoved = true;
                blackQueensRookHasMoved = true;
            }
        }
    }

    /* 
     * This method executes a Chessboard.Move object and moves the given piece
     * on the object's internal chessboard model.
     *
     * @param moveObj The Chessboard.Move object describing the movement of a
     *                piece that movePiece will execute.
     */
    private void movePieceNonCastling(final Chessboard.Move moveObj) throws KingIsInCheckException,
                                                                            IllegalArgumentException {
        int fromXCoord = moveObj.fromXCoord();
        int fromYCoord = moveObj.fromYCoord();
        int toXCoord = moveObj.toXCoord();
        int toYCoord = moveObj.toYCoord();
        int pieceInt = moveObj.movingPiece().pieceInt();
        int colorOfPiece = (pieceInt & WHITE) != 0 ? WHITE : BLACK;
        int otherColor = (colorOfPiece == WHITE) ? BLACK : WHITE;
        int capturedPieceInt = boardArray[toXCoord][toYCoord];
        String thisColorStr = (colorOfPiece == WHITE ? "White" : "Black");

        /* It's illegal in chess to make a move that leaves one's king in check.
        /* BoardArrays.wouldKingBeInCheck() is used to test if this move would
        /* do that; if so, a KingIsInCheckException is thrown. */
        if (BoardArrays.wouldKingBeInCheck(boardArray, fromXCoord, fromYCoord, toXCoord, toYCoord,
                                           colorOfPiece, colorOnTop)) {
            throw new KingIsInCheckException(
                          "Move would place " + thisColorStr + "'s king in check or " + thisColorStr + "'s King is in "
                          + "check and this move doesn't fix that; move can't be made.");
        }

        /* If the moveObj has a promotedToPieceInt attribute set, this is a pawn
           promotion move; so the desintration square is set to the new piece. */
        if (moveObj.promotedToPieceInt() != 0) {
            int promotedToPieceInt = moveObj.promotedToPieceInt();
            int promotedFromPieceInt = boardArray[fromXCoord][fromYCoord];
            boardArray[toXCoord][toYCoord] = promotedToPieceInt;
            boardArray[fromXCoord][fromYCoord] = 0;
        } else {
            /* Otherwise this is a normal move. */
            boardArray[toXCoord][toYCoord] = boardArray[fromXCoord][fromYCoord];
            boardArray[fromXCoord][fromYCoord] = 0;
        }

        /* Castling is only possible if the king and the rook involved both
           haven't moved since the start of play. This switch statement detects
           if the piece is a king or rook, and updates the instance booleans
           accordingly. */
        switch (pieceInt) {
            case BLACK | KING -> {
                    if (!blackKingHasMoved) {
                        blackKingHasMoved = true;
                    }
                }
            case WHITE | KING -> {
                    if (!whiteKingHasMoved) {
                        whiteKingHasMoved = true;
                    }
                }
            case BLACK | ROOK -> {
                    if (!blackKingsRookHasMoved && fromXCoord == 0) {
                        blackKingsRookHasMoved = true;
                    } else if (!blackQueensRookHasMoved && fromXCoord == 7) {
                        blackQueensRookHasMoved = true;
                    }
                }
            case WHITE | ROOK -> {
                    if (!whiteKingsRookHasMoved && fromXCoord == 0) {
                        whiteKingsRookHasMoved = true;
                    } else if (!whiteQueensRookHasMoved && fromXCoord == 7) {
                        whiteQueensRookHasMoved = true;
                    }
                }
            default -> { }
        }
    }

    /**
     * This method executes a Chessboard.Move object and moves the given piece
     * (or pieces, if it's castling) on the object's internal chessboard model.
     *
     * @param moveObj The Chessboard.Move object describing the movement of a
     *                piece that movePiece will execute.
     */
    public void movePiece(final Chessboard.Move moveObj) throws KingIsInCheckException, IllegalArgumentException,
                                                                CastlingNotPossibleException {
        if (moveObj.isCastlingKingside() || moveObj.isCastlingQueenside()) {
            movePieceCastling(moveObj);
        } else {
            movePieceNonCastling(moveObj);
        }
    }

    /**
     * This method derives an int[][2] array of the coordinates for every square
     * on the board that is occupied by a piece of either color.
     *
     * @return An array of 2-element arrays as long as the number of pieces on
     *         the board.
     */
    public int[][] occupiedSquareCoords() {
        int pieceCount = 0;
        int pieceIndex = 0;

        /* The first iteration across the board is just to count the number of
           pieces so the array can be sized correctly. */
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] != 0) {
                    pieceCount++;
                }
            }
        }

        int[][] squareCoords = new int[pieceCount][2];

        /* The second iteration across the board saves the coordinate of every
        /* square that's found to be occupied. */
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
