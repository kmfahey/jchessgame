package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.awt.Image;

/**
 * Represents a chessboard, storing a data structure, an int[8][8] array,
 * that it uses to keep track of the state of the board. That structure is
 * available via Chessboard.getBoardArray() so it can be manipulated directly,
 * such as by methods in BoardsArray or MinimaxRunner.
 */
public class Chessboard {

    /* These int flags are or'd together to generate the integers that are used
       in an int[8][8] boardArray to represent pieces on the chessboard. Every
       piece is or'd together from one of WHITE or BLACK, and one of KING,
       QUEEN, BISHOP, KNIGHT, ROOK, or PAWN. Additionally, since there are two
       Knight icons in the icon set this package uses, a knight's piece int will
       also be or'd with either LEFT or RIGHT. */

    /** Flag for black pieces. */
    public static final int BLACK = BoardArrays.BLACK;

    /** Flag for white pieces. */
    public static final int WHITE = BoardArrays.WHITE;

    /** Flag for kings. */
    public static final int KING = BoardArrays.KING;

    /** Flag for queens. */
    public static final int QUEEN = BoardArrays.QUEEN;

    /** Flag for bishops. */
    public static final int BISHOP = BoardArrays.BISHOP;

    /** Flag for knights. */
    public static final int KNIGHT = BoardArrays.KNIGHT;

    /** Flag for rooks. */
    public static final int ROOK = BoardArrays.ROOK;

    /** Flag for pawns. */
    public static final int PAWN = BoardArrays.PAWN;

    /** Flag for right-facing knights. */
    public static final int RIGHT = BoardArrays.RIGHT;

    /** Flag for left-facing knights. */
    public static final int LEFT = BoardArrays.LEFT;

    /**
     * Comprises all the valid piece integer values.
     */
    public static final HashSet<Integer> VALID_PIECE_INTS = BoardArrays.VALID_PIECE_INTS;

    /**
     * Associates piece integer values with an Integer[][2] array of coordinates
     * for squares the piece should be placed at when the player is playing
     * White.
     */
    private static final HashMap<Integer, Integer[][]> PIECES_STARTING_LOCS_WHITE_BELOW = new HashMap<>() {{
        put(BLACK | KING,           new Integer[][] {new Integer[] {4, 0}});
        put(BLACK | QUEEN,          new Integer[][] {new Integer[] {3, 0}});
        put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                     new Integer[] {2, 1}, new Integer[] {3, 1},
                                                     new Integer[] {4, 1}, new Integer[] {5, 1},
                                                     new Integer[] {6, 1}, new Integer[] {7, 1}});
        put(WHITE | KING,           new Integer[][] {new Integer[] {4, 7}});
        put(WHITE | QUEEN,          new Integer[][] {new Integer[] {3, 7}});
        put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                     new Integer[] {2, 6}, new Integer[] {3, 6},
                                                     new Integer[] {4, 6}, new Integer[] {5, 6},
                                                     new Integer[] {6, 6}, new Integer[] {7, 6}});
    }};

    /**
     * Associates piece integer values with an Integer[][2] array of coordinates
     * for squares the piece should be placed at when the player is playing
     * Black.
     */
    private static final HashMap<Integer, Integer[][]> PIECES_STARTING_LOCS_BLACK_BELOW = new HashMap<>() {{
        put(WHITE | KING,           new Integer[][] {new Integer[] {4, 0}});
        put(WHITE | QUEEN,          new Integer[][] {new Integer[] {3, 0}});
        put(WHITE | ROOK,           new Integer[][] {new Integer[] {0, 0}, new Integer[] {7, 0}});
        put(WHITE | BISHOP,         new Integer[][] {new Integer[] {2, 0}, new Integer[] {5, 0}});
        put(WHITE | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 0}});
        put(WHITE | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 0}});
        put(WHITE | PAWN,           new Integer[][] {new Integer[] {0, 1}, new Integer[] {1, 1},
                                                     new Integer[] {2, 1}, new Integer[] {3, 1},
                                                     new Integer[] {4, 1}, new Integer[] {5, 1},
                                                     new Integer[] {6, 1}, new Integer[] {7, 1}});
        put(BLACK | KING,           new Integer[][] {new Integer[] {4, 7}});
        put(BLACK | QUEEN,          new Integer[][] {new Integer[] {3, 7}});
        put(BLACK | ROOK,           new Integer[][] {new Integer[] {0, 7}, new Integer[] {7, 7}});
        put(BLACK | BISHOP,         new Integer[][] {new Integer[] {2, 7}, new Integer[] {5, 7}});
        put(BLACK | KNIGHT | RIGHT, new Integer[][] {new Integer[] {6, 7}});
        put(BLACK | KNIGHT | LEFT,  new Integer[][] {new Integer[] {1, 7}});
        put(BLACK | PAWN,           new Integer[][] {new Integer[] {0, 6}, new Integer[] {1, 6},
                                                     new Integer[] {2, 6}, new Integer[] {3, 6},
                                                     new Integer[] {4, 6}, new Integer[] {5, 6},
                                                     new Integer[] {6, 6}, new Integer[] {7, 6}});
    }};

    /**
     * Associates piece strings with the piece integer values that they're
     * equivalent to.
     */
    public static final HashMap<String, Integer> PIECE_STRS_TO_INTS = new HashMap<>() {{
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

    /**
     * Associates each piece integer with the piece icon Image object to use for
     * it.
     */
    private final HashMap<Integer, Image> pieceImages;

    /* Integer color values used to track which colors are set to certain
       relevant roles in the game. */
    private int colorOnTop;
    private int colorPlaying;

    /**
     * Represent the chessboard. It's also a valid argument to most of the
     * methods in BoardArray, whose utility methods this class uses to
     * manipulate the board array.
     *
     * @see BoardArrays
     */
    private int[][] boardArray;

    /* These booleans are used to track if the kings or rooks have moved yet
       during play. They're used by isCastlingPossible() to discover if a
       castling move is allowed, since castling isn't allowed if either the
       rook or the king has moved. */
    private boolean blackKingsRookHasMoved = false;
    private boolean blackQueensRookHasMoved = false;
    private boolean whiteKingsRookHasMoved = false;
    private boolean whiteQueensRookHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean whiteKingHasMoved = false;

    /**
     * Represents a Piece on the chessboard.
     *
     * @param pieceInt   The piece's integer representation on the board.
     * @param pieceImage An Image object of the icon for the piece sourced from
     *                   ImagesManager.
     * @param xCoord     The int of the piece's x coordinate on the board.
     * @param yCoord     The int of the piece's x coordinate on the board.
     * @see ImagesManager
     */
    public record Piece(int pieceInt, Image pieceImage, int xCoord, int yCoord) { }

    /**
     * Represents a possible move on the board. The method
     * Chessboard.movePiece() accepts a Move object as an argument and executes
     * that move on the internal board array (if possible).
     *
     * @param movingPiece         A Chessboard.Piece object of the piece to
     *                            move.
     * @param fromXCoord          The x coordinate of the piece's square on the
     *                            board.
     * @param fromYCoord          The y coordinate of the piece's square on the
     *                            board.
     * @param toXCoord            The x coordinate of square to move the piece
     *                            to.
     * @param toYCoord            The y coordinate of to move the piece to.
     * @param capturedPieceInt    If this is a capturing move, an integer
     *                            representing the piece the move is capturing;
     *                            0 otherwise.
     * @param isCastlingKingside  A boolean, true if this move is a kingside
     *                            castling, false otherwise.
     * @param isCastlingQueenside A boolean, true if this move is a queenside
     *                            castling, false otherwise.
     * @param promotedToPieceInt  If this is a pawn promotion move, an integer
     *                            representing the piece to promote the pawn to;
     *                            0 otherwise.
     */
    public record Move(Chessboard.Piece movingPiece, int fromXCoord, int fromYCoord,
                int toXCoord, int toYCoord, int capturedPieceInt, boolean isCastlingKingside,
                boolean isCastlingQueenside, int promotedToPieceInt) {

        /**
         * Renders the object into a string that describes the move in algebraic
         * notation.
         *
         * @return A String representing the object's move in algebraic
         *         notation.
         */
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
    }

    /**
     * Instantiates the Chessboard object.
     *
     * @param imagesManager The program's ImagesManager object, which is used
     *                      to retrieve piece images.
     * @param playingColor  An integer representing the color the user is
     *                      playing as, either BoardArrays.WHITE or
     *                      BoardArrays.BLACK.
     * @param onTopColor    An integer representing the color that's playing
     *                      from the top of the board, either BoardArrays.WHITE
     *                      or BoardArrays.BLACK.
     */
    public Chessboard(final ImagesManager imagesManager, final int playingColor, final int onTopColor) {
        this(null, imagesManager, playingColor, onTopColor);
    }

    /**
     * Instantiates the Chessboard object, including an initializing value for
     * the board array.
     *
     * @param boardArrayVal The value for the board array.
     * @param imagesManager The program's ImagesManager object, which is used
     *                      to retrieve piece images.
     * @param playingColor  An integer representing the color the user is
     *                      playing as, either BoardArrays.WHITE or
     *                      BoardArrays.BLACK.
     * @param onTopColor    An integer representing the color that's playing
     *                      from the top of the board, either BoardArrays.WHITE
     *                      or BoardArrays.BLACK.
     */
    public Chessboard(final int[][] boardArrayVal, final ImagesManager imagesManager, final int playingColor,
                      final int onTopColor) {

        setColors(playingColor, onTopColor);

        if (Objects.nonNull(boardArrayVal)) {
            boardArray = boardArrayVal;
        } else {
            boardArray = new int[8][8];
            layOutPieces();
        }

        /* We only need the ImagesManager object to instantiate Piece objects
           with the correct Image 2nd argument. It's not saved to an instance
           variable since it's never used again after this constructor. */

        pieceImages = new HashMap<>();

        for (int pieceInt : VALID_PIECE_INTS) {
            pieceImages.put(pieceInt, imagesManager.getImageByPieceInt(pieceInt));
        }
    }

    /**
     * Mutator method for the colorPlaying and colorOnTop instance variables.
     *
     * @param colorPlayingVal The new integer value representing the color the
     *                        user is playing. One of either BoardArrays.WHITE
     *                        or BoardArrays.BLACK.
     * @param colorOnTopVal   The new integer value representing the color
     *                        playing from the top of the board. One of either
     *                        BoardArrays.WHITE or BoardArrays.BLACK.
     */
    public void setColors(final int colorPlayingVal, final int colorOnTopVal) {
        colorPlaying = colorPlayingVal;
        colorOnTop = colorOnTopVal;
    }

    /**
     * Resets the internal board array and populates it with the
     * appropriate pieces. It places the color the user is playing at the bottom
     * of the board and the color the AI is playing at the top.
     */
    public void layOutPieces() {
        HashMap<Integer, Integer[][]> piecesStartingCoords;

        /* The piecesStartingCoords variable is set to the correct one of
           piecesStartingLocsWhiteBelow or piecesStartingLocsBlackBelow such
           that the color the user is playing gets arrayed at the bottom of the
           board. */
        if (colorPlaying == WHITE) {
            piecesStartingCoords = PIECES_STARTING_LOCS_WHITE_BELOW;
        } else {
            piecesStartingCoords = PIECES_STARTING_LOCS_BLACK_BELOW;
        }

        /* All cells in the board array are set to zero as a starting point. */
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                boardArray[xIdx][yIdx] = 0;
            }
        }

        /* The HashMap<Integer, Integer[2]> is iterated over. The key is the
           piece integer value to set, and the value is an int[2] array of the
           coordinates to set it at. Each integer is set at its coordinates in
           the board array. */
        for (Entry<Integer, Integer[][]> pieceToCoords : piecesStartingCoords.entrySet()) {
            int pieceInt = pieceToCoords.getKey();
            for (Integer[] coords : pieceToCoords.getValue()) {
                int xIdx = coords[0];
                int yIdx = coords[1];
                boardArray[xIdx][yIdx] = pieceInt;
            }
        }
    }

    /**
     * Accessor for the boardArray instance variable, which is the internal
     * int[8][8] array used to represent the chessboard.
     *
     * @return An int[8][8] array representing a chessboard. This value is not
     *         deepcopied; this is a reference to the Chessboard object's
     *         boardArray. Changes to this array will be reflected in the
     *         Chessboard object's state, and Chessboard operations that affect
     *         its board state will be reflected in this array.
     */
    public int[][] getBoardArray() {
        return boardArray;
    }

    /**
     * Mutator for the boardArray instance variable that represents the
     * chessboard in play.
     *
     * @param boardArrayVal The int[8][8] array to set the boardArray instance
     *                      variable to.
     */
    public void setBoardArray(final int[][] boardArrayVal) {
        boardArray = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            System.arraycopy(boardArrayVal[xIdx], 0, boardArray[xIdx], 0, 8);
        }
    }

    /**
     * Accessor for the colorOnTop instance variable, which indicates the color
     * that is playing from the top of the board.
     *
     * @return The value of colorOnTop, either BoardArrays.WHITE or
     *         BoardArrays.BLACK;
     */
    public int getColorOnTop() {
        return colorOnTop;
    }

    /**
     * Accessor for the colorPlaying instance variable, which indicates the
     * color the user is playing.
     *
     * @return The value of colorPlaying, either BoardArrays.WHITE or
     *         BoardArrays.BLACK;
     */
    public int getColorPlaying() {
        return colorPlaying;
    }

    /**
     * Promotes the pawn at the specified location. The pawn's integer value in
     * the internal board array is replaced with the new value given, or'd with
     * the color of the original piece.
     *
     * @param xCoord   The x coordinate of the pawn to promote.
     * @param yCoord   The y coordinate of the pawn to promote.
     * @param newPiece The integer value of the new piece to replace it with,
     *                 not including the color flag.
     */
    public void promotePawn(final int xCoord, final int yCoord, final int newPiece) {
        int pieceInt = boardArray[xCoord][yCoord];
        if ((pieceInt & PAWN) == 0) {
            throw new IllegalArgumentException("Chessboard.promotePawn() called with invalid coordinates "
                                               + "(" + xCoord + ", " + yCoord + "): no pawn at that location.");
        }
        int pieceColor = pieceInt ^ PAWN;
        boardArray[xCoord][yCoord] = pieceColor | newPiece;
    }

    /**
     * Instances a Chessboard.Piece object that represents the piece on the
     * board in the square specified by the argument.
     *
     * @param coords An int[2] comprising the coordinates of the piece to
     *               instance a Piece object of.
     * @return       A Chessboard.Piece object representing the piece at that location.
     * @see Chessboard.Move
     */
    public Piece getPieceAtCoords(final int[] coords) {
        return getPieceAtCoords(coords[0], coords[1]);
    }

    /**
     * Instances a Chessboard.Piece object that represents the piece on the
     * board in the square specified by the arguments.
     *
     * @param xCoord The x coordinate of the piece to instance a Piece object of.
     * @param yCoord The y coordinate of the piece to instance a Piece object of.
     * @return       A Chessboard.Piece object representing the piece at that location.
     * @see Chessboard.Piece
     */
    public Piece getPieceAtCoords(final int xCoord, final int yCoord) {
        if (boardArray[xCoord][yCoord] == 0) {
            return null;
        }
        int pieceInt = boardArray[xCoord][yCoord];
        return new Piece(pieceInt, pieceImages.get(pieceInt), xCoord, yCoord);
    }

    /**
     * Returns true if the Chessboard.Move argument represents a valid move for
     * the piece involved, false if the move is illegal.
     *
     * @param moveObj The Chessboard.Move object to test for validity.
     * @return        A boolean, true if the move is a legal one for the piece at that
     *                location, false otherwise.
     * @see Chessboard.Move
     */
    public boolean isMovePossible(final Chessboard.Move moveObj) {
        int[][] movesArray = new int[32][7];
        int pieceColor = ((moveObj.movingPiece().pieceInt() & WHITE) != 0) ? WHITE : BLACK;
        int xCoord = moveObj.fromXCoord();
        int yCoord = moveObj.fromYCoord();
        int usedLengthMovesArray = BoardArrays.generatePieceMoves(boardArray, movesArray, 0, xCoord, yCoord,
                                                                  pieceColor, colorOnTop);

        for (int index = 0; index < usedLengthMovesArray; index++) {
            if (movesArray[index][3] == moveObj.toXCoord() && movesArray[index][4] == moveObj.toYCoord()) {
                return true;
            }
        }

        return false;
    }

    /*
     * This utility method is used by movePieceCastling to check whether the
     * requirements for castling have been met: the two pieces must both have
     * not moved since start of play, and the squares between them must be
     * empty.
     *
     * @param colorOfKing An integer referring to the color of the king to
     *                    check, either Chessboard.WHITE or Chessboard.BLACK
     * @param kingOrQueen An integer referring to king or queen, either
     *                    Chessboard.KING or Chessboard.QUEEN. King indicates
     *                    kingside castling, and queen indicates queenside
     *                    castling.
     */
    public int isCastlingPossible(final int colorOfKing, final int kingOrQueen) throws IllegalArgumentException {
        switch (colorOfKing | kingOrQueen) {
            case BLACK | KING -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (blackKingHasMoved || blackKingsRookHasMoved) {
                    return MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[5][yIdx] != 0 || boardArray[6][yIdx] != 0) {
                    return MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED;
                } else {
                    for (int xIdx = 4; xIdx <= 6; xIdx++) {
                        if (BoardArrays.wouldKingBeInCheck(boardArray, xIdx, yIdx,
                                                           colorPlaying, colorOnTop)) {
                            if ((boardArray[xIdx][yIdx] & KING) != 0) {
                                return MovesLog.MoveError.CASTLING_KING_IN_CHECK;
                            } else {
                                return MovesLog.MoveError.CASTLING_PATH_IS_THREATENED;
                            }
                        }
                    }
                }
                return 0;
            }
            case BLACK | QUEEN -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (blackKingHasMoved || blackQueensRookHasMoved) {
                    return MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[3][yIdx] != 0 || boardArray[2][yIdx] != 0 || boardArray[1][yIdx] != 0) {
                    return MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED;
                } else {
                    for (int xIdx = 1; xIdx <= 4; xIdx++) {
                        if (BoardArrays.wouldKingBeInCheck(boardArray, xIdx, yIdx,
                                                           colorPlaying, colorOnTop)) {
                            if ((boardArray[xIdx][yIdx] & KING) != 0) {
                                return MovesLog.MoveError.CASTLING_KING_IN_CHECK;
                            } else {
                                return MovesLog.MoveError.CASTLING_PATH_IS_THREATENED;
                            }
                        }
                    }
                }
                return 0;
            }
            case WHITE | KING -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (whiteKingHasMoved || whiteKingsRookHasMoved) {
                    return MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[5][yIdx] != 0 || boardArray[6][yIdx] != 0) {
                    return MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED;
                } else {
                    for (int xIdx = 4; xIdx <= 6; xIdx++) {
                        if (BoardArrays.wouldKingBeInCheck(boardArray, xIdx, yIdx,
                                                           colorPlaying, colorOnTop)) {
                            if ((boardArray[xIdx][yIdx] & KING) != 0) {
                                return MovesLog.MoveError.CASTLING_KING_IN_CHECK;
                            } else {
                                return MovesLog.MoveError.CASTLING_PATH_IS_THREATENED;
                            }
                        }
                    }
                }
                return 0;
            }
            /* Testing whether castling queenside is possible for the White king. */
            case WHITE | QUEEN -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (whiteKingHasMoved || whiteQueensRookHasMoved) {
                    return MovesLog.MoveError.CASTLING_PIECE_HAS_MOVED;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[3][yIdx] != 0 || boardArray[2][yIdx] != 0 || boardArray[1][yIdx] != 0) {
                    return MovesLog.MoveError.CASTLING_INTERVENING_SPACE_OCCUPIED;
                } else {
                    for (int xIdx = 1; xIdx <= 4; xIdx++) {
                        if (BoardArrays.wouldKingBeInCheck(boardArray, xIdx, yIdx,
                                                           colorPlaying, colorOnTop)) {
                            if ((boardArray[xIdx][yIdx] & KING) != 0) {
                                return MovesLog.MoveError.CASTLING_KING_IN_CHECK;
                            } else {
                                return MovesLog.MoveError.CASTLING_PATH_IS_THREATENED;
                            }
                        }
                    }
                }
                return 0;
            }
            default -> throw new IllegalArgumentException("could not resolve arguments to isCastlingPossible()");
        }
    }

    /**
     * Executes a Chessboard.Move object that describes a castling move on the
     * object's internal chessboard model.
     *
     * @param moveObj The Chessboard.Move object describing the move to be made.
     * @see Chessboard.Move
     */
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
        int rookPieceInt = boardArray[rookXCoord][rookYCoord];
        String colorOfPieceStr = (colorOfPiece == WHITE ? "White" : "Black");

        int rookNewXCoord;
        int kingNewXCoord;

        /* A basic check to confirm this method was called with the right
           pieces. */
        if ((pieceInt & KING) == 0 || rookPieceInt != (colorOfPiece | ROOK)) {
            throw new IllegalArgumentException("Invalid castling movePiece() parameters.");
        } else if (isCastlingKingside) {
            /* Castling is only possible if the rook and king both haven't
               moved since the start of play. Boolean state variables are used
               to track these conditions, and isCastlingPossible() checks
               those values to see if castling can be done. The king also must
               not be in check, their destination must not be threatened,
               and the intervening squares must also not be threatened.
               isCastlingPossible() checks those conditions as well. */
            int castlingAssessment = isCastlingPossible(colorOfPiece, KING);
            if (castlingAssessment != 0) {
                throw new CastlingNotPossibleException("Castling kingside is not possible for " + colorOfPieceStr, castlingAssessment);
            }

            rookNewXCoord = 5;
            kingNewXCoord = 6;
        } else if (isCastlingQueenside) {
            /* Castling is only possible if the rook and king both haven't
               moved since the start of play. Boolean state variables are used
               to track these conditions, and isCastlingPossible() checks
               those values to see if castling can be done. The king also must
               not be in check, their destination must not be threatened,
               and the intervening squares must also not be threatened.
               isCastlingPossible() checks those conditions as well. */
            int castlingAssessment = isCastlingPossible(colorOfPiece, QUEEN);
            if (castlingAssessment != 0) {
                throw new CastlingNotPossibleException("Castling queenside is not possible for " + colorOfPieceStr, castlingAssessment);
            }

            rookNewXCoord = 3;
            kingNewXCoord = 2;
        } else {
            /* This is a can't happen error, but here for completeness. */
            throw new IllegalStateException("movePieceCastling() called with a Move object that doesn't indicate "
                                            + "castling");
        }

        /* The actual exchange is done. The values of rookNewXCoord and
           kingNewXCoord administer either kingside or queenside castling
         * depending on their values (set above). */
        boardArray[rookNewXCoord][rookYCoord] = boardArray[rookXCoord][rookYCoord];
        boardArray[rookXCoord][rookYCoord] = 0;
        boardArray[kingNewXCoord][kingYCoord] = boardArray[kingXCoord][kingYCoord];
        boardArray[kingXCoord][kingYCoord] = 0;

        /* The state variables that indicate a rook or a king has moved are
           updated. */
        if (colorOfPiece == WHITE) {
            whiteKingHasMoved = true;
            whiteQueensRookHasMoved = true;
        } else {
            blackKingHasMoved = true;
            blackQueensRookHasMoved = true;
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
        String thisColorStr = (colorOfPiece == WHITE ? "White" : "Black");

        /* It's illegal in chess to make a move that leaves one's king in check.
           BoardArrays.wouldKingBeInCheck() is used to test if this move would
           do that; if so, a KingIsInCheckException is thrown. */
        if ((pieceInt & KING) != 0) {
            if (BoardArrays.wouldKingBeInCheck(boardArray, toXCoord, toYCoord, colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException(
                              "Move would place " + thisColorStr + "'s king in check or " + thisColorStr
                              + "'s King is in check and this move doesn't fix that; move can't be made.");
            }
        } else {
            if (BoardArrays.wouldKingBeInCheck(boardArray, fromXCoord, fromYCoord, toXCoord, toYCoord,
                                              colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException(
                              "Move would place " + thisColorStr + "'s king in check or " + thisColorStr
                              + "'s King is in check and this move doesn't fix that; move can't be made.");
            }
        }


        /* If the moveObj has a promotedToPieceInt attribute set, this is a pawn
           promotion move; so the destination square is set to the new piece. */
        if (moveObj.promotedToPieceInt() != 0) {
            boardArray[toXCoord][toYCoord] = moveObj.promotedToPieceInt();
        } else {
            /* Otherwise this is a normal move. */
            boardArray[toXCoord][toYCoord] = boardArray[fromXCoord][fromYCoord];
        }
        boardArray[fromXCoord][fromYCoord] = 0;

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
     * Executes a Chessboard.Move object and moves the given piece or pieces on
     * the object's internal chessboard model.
     *
     * @param moveObj The Chessboard.Move object describing the movement of a
     *                piece that movePiece will execute.
     * @throws KingIsInCheckException If the move would put the friendly king in
     *                                check, or the king is in check and this
     *                                move doesn't change that.
     * @throws CastlingNotPossibleException If the move is a castling but the
     *                                      castling is not possible because
     *                                      an intervening square is occupied
     *                                      or either the king or the rook has
     *                                      already moved.
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
     * Returns a int[][2] array of the coordinates for every square on the
     * board that is occupied by a piece of either color.
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
