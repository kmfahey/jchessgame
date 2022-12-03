package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.awt.Image;

/**
 * This class represents a chessboard. It stores a data structure, an int[8][8],
 * that it uses to keep track of the state of the board; that structure is
 * available via Chessboard.getBoardArray() so it can be manipulated directly,
 * such as by methods in BoardsArray or MinimaxRunner.
 */
public class Chessboard {

    /* These int flags are Or'd together to generate the integers that are used
       in an int[8][8] boardArray to represent pieces on the chessboard. Every
       piece is Or'd together from one of WHITE or BLACK, and one of KING,
       QUEEN, BISHOP, KNIGHT, ROOK, or PAWN. Additionally, since there are two
       Knight icons in the icon set this package uses, a knight's piece int will
       also be Or'd with either LEFT or RIGHT. */

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

    /**
     * This set of valid piece int values is used by Chessboard's constructor to
     * iterate across when querying ImagesManager for piece icons to store to
     * pieceImages.
     */
    public static final HashSet<Integer> VALID_PIECE_INTS = BoardArrays.VALID_PIECE_INTS;

    /**
     * This mapping associates piece integer values with an Integer[][2]
     * array of coordinates for squares the piece should be placed at by
     * layOutPieces() when setting up the board for a user playing White.
     */
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

    /**
     * This mapping associates piece integer values with an Integer[][2]
     * array of coordinates for squares the piece should be placed at by
     * layOutPieces() when setting up the board for a user playing Black.
     */
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

    /**
     * This mapping associates the piece strings that ImagesManager transforms
     * the filenames of the icon files it loads into the piece integer values it
     * should associate them with.
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
     * This mapping is used to track the correct piece icon Image object to use
     * for each piece (represented by an integer value) in play.
     */
    private HashMap<Integer, Image> pieceImages;

    /* Integer color values used to track which colors are set to certain
       relevant roles in the game. */
    private int colorOnTop;
    private int colorPlaying;
    private int colorOpposing;

    /**
     * This int[8][8] array is used to represent the board. It's also a valid argument to
     * most of the methods in BoardArray, whose utility methods this class uses
     * to manipulate the board array.
     *
     * @see BoardArrays
     */
    private int[][] boardArray;

    /* These booleans are used to track if the kings or rooks have moved yet
       during play. They're used by isCastlingPossible() to verify that a
       castling move is allowed, since castling isn't allowed if either the rook
       or the king has moved. */
    private boolean blackKingsRookHasMoved = false;
    private boolean blackQueensRookHasMoved = false;
    private boolean whiteKingsRookHasMoved = false;
    private boolean whiteQueensRookHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean whiteKingHasMoved = false;

    /**
     * This record comprises a class that represents a Piece on the board.
     *
     * @param pieceInt   The piece's integer representation on the board,
     *                   comprised of &lt;WHITE or BLACK&gt; | &lt;PAWN, ROOK,
     *                   BISHOP, KING, QUEEN&lt; or &lt;KNIGHT | LEFT&gt; or
     *                   &lt;KNIGHT | RIGHT&gt;.
     * @param pieceImage An Image object of the icon for the piece sourced from
     *                   ImagesManager.
     * @param xCoord     The int of the piece's x coordinate on the board.
     * @param yCoord     The int of the piece's x coordinate on the board.
     * @see ImagesManager
     */
    public record Piece(int pieceInt, Image pieceImage, int xCoord, int yCoord) { };

    /**
     * This record comprises a class that represents a possible Move on the
     * board. The method Chessboard.movePiece() accepts a Move object as
     * an argument and executes that move on the internal board array (if
     * possible).
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

    /**
     * This constructor instantiates the Chessboard object.
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
     * This constructor instantiates the Chessboard object, including an
     * initializing value for the board array (unlike the normal constructor
     * which doesn't specify that value).
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

        pieceImages = new HashMap<Integer, Image>();

        for (int pieceInt : VALID_PIECE_INTS) {
            pieceImages.put(pieceInt, imagesManager.getImageByPieceInt(pieceInt));
        }
    }

    /**
     * This mutator method is used to set the colorPlaying and colorOnTop values.
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
        colorOpposing = colorPlaying == WHITE ? BLACK : WHITE;
    }

    /**
     * This method resets the internal board array and populates it with the
     * appropriate pieces, placing the color the user is playing at the bottom
     * of the board and the color the AI is playing at the top.
     */
    public void layOutPieces() {
        HashMap<Integer, Integer[][]> piecesStartingCoords;

        /* The piecesStartingCoords variable is set to the correct one of
           piecesStartingLocsWhiteBelow or piecesStartingLocsBlackBelow such
           that the color the user is playing gets arrayed at the bottom of the
           board. */
        if (colorPlaying == WHITE) {
            piecesStartingCoords = piecesStartingLocsWhiteBelow;
        } else {
            piecesStartingCoords = piecesStartingLocsBlackBelow;
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
     * An accessor method that returns the value of the boardArray instance
     * variable, which is the internal int[8][8] array used to represent the
     * chessboard.
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
     * A mutator method that sets the boardArray instance variable to the
     * supplied value.
     *
     * @param boardArrayVal The int[8][8] array to set the boardArray instance
     *                      variable to.
     */
    public void setBoardArray(final int[][] boardArrayVal) {
        boardArray = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                boardArray[xIdx][yIdx] = boardArrayVal[xIdx][yIdx];
            }
        }
    }

    /**
     * An accessor method that returns the value of the colorOnTop instance
     * variable, which indicates the color that is playing from the top of the
     * board.
     *
     * @return The value of colorOnTop, either BoardArrays.WHITE or
     *         BoardArrays.BLACK;
     */
    public int getColorOnTop() {
        return colorOnTop;
    }

    /**
     * An accessor method that returns the value of the colorPlaying instance
     * variable, which indicates the color the user is playing.
     *
     * @return The value of colorPlaying, either BoardArrays.WHITE or
     *         BoardArrays.BLACK;
     */
    public int getColorPlaying() {
        return colorPlaying;
    }

    /**
     * An accessor method that returns the value of the colorOpposing instance
     * variable, which indicates the color the AI is playing.
     *
     * @return The value of colorOpposing, either BoardArrays.WHITE or
     *         BoardArrays.BLACK;
     */
    public int getColorOpposing() {
        return colorOpposing;
    }

    /**
     * This method is used to promote a pawn at the specified location. The
     * pawn's integer value in the internal board array is replaced with the new
     * value given, Or'd with the color of the original piece.
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
     * This method accepts coordinates to a square on the board, and returns a
     * Chessboard.Piece object that represents the piece in that location.
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
     * This method accepts coordinates to a square on the board, and returns a
     * Chessboard.Piece object that represents the piece in that location.
     *
     * @param xCoord The x coordinate of the piece to instance a Piece object of.
     * @param yCoord The y coordinate of the piece to instance a Piece object of.
     * @return       A Chessboard.Piece object representing the piece at that location.
     * @see Chessboard.Move
     */
    public Piece getPieceAtCoords(final int xCoord, final int yCoord) {
        if (boardArray[xCoord][yCoord] == 0) {
            return null;
        }
        int pieceInt = boardArray[xCoord][yCoord];
        return new Piece(pieceInt, pieceImages.get(pieceInt), xCoord, yCoord);
    }

    /**
     * This method accepts a Chessboard.Move object and consults a list
     * of all possible moves for the piece at the starting coordinates
     * listed in (Move.fromXCoord, Move.fromYCoord) to determine if a
     * move with the ending coordinates (Move.toXCoord, Move.toYCoord) is
     * among them. It returns true if so, false otherwise. It is used by
     * BoardView.mouseClickedTestForMoveErrors().
     *
     * @param moveObj The Chessboard.Move object to test for validity.
     * @return        A boolean, true if the move is a legal one for the piece at that
     *                location, false otherwise.
     * @see BoardView.mouseClickedTestForMoveErrors
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
    private boolean isCastlingPossible(final int colorOfKing, final int kingOrQueen) throws IllegalArgumentException {
        switch (colorOfKing | kingOrQueen) {
            case BLACK | KING -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (blackKingHasMoved || blackKingsRookHasMoved) {
                    return false;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[1][yIdx] != 0 || boardArray[2][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            case BLACK | QUEEN -> {
                int yIdx = colorOnTop == BLACK ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (blackKingHasMoved || blackQueensRookHasMoved) {
                    return false;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[4][yIdx] != 0 || boardArray[5][yIdx] != 0 || boardArray[6][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            case WHITE | KING -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (whiteKingHasMoved || whiteKingsRookHasMoved) {
                    return false;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
                } else if (boardArray[1][yIdx] != 0 || boardArray[2][yIdx] != 0) {
                    return false;
                }
                return true;
            }
            /* Testing whether castling queenside is possible for the White king. */
            case WHITE | QUEEN -> {
                int yIdx = colorOnTop == WHITE ? 0 : 7;
                /* One or both of the pieces have moved, so castling can't be
                   done. */
                if (whiteKingHasMoved || whiteQueensRookHasMoved) {
                    return false;
                /* One or more of the squares between the king and the rook are
                   occupied, so castling is impossible. */
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

    /**
     * This method executes a Chessboard.Move object that describes a castling move
     * on the object's internal chessboard model.
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

        int rookNewXCoord = -1;
        int kingNewXCoord = -1;

        /* A basic check to confirm this method was called with the right
           pieces. */
        if ((pieceInt & KING) == 0 || rookPieceInt != (colorOfPiece | ROOK)) {
            throw new IllegalArgumentException("Invalid castling movePiece() parameters.");
        } else if (isCastlingKingside) {
            /* Castling is only possible if the rook and king both haven't
               moved since the start of play. Boolean state variables are used
               to track these conditions, and isCastlingPossible checks those
               values to see if castling can be done. */
            if (!isCastlingPossible(colorOfPiece, KING)) {
                throw new CastlingNotPossibleException("Castling kingside is not possible for " + colorOfPieceStr);
            /* A test to see if the move would somehow put the king in check. */
            } else if (BoardArrays.wouldKingBeInCheck(boardArray, 2, kingYCoord, colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException("Castling kingside would place " + colorOfPieceStr + "'s king in "
                                                 + "check or " + colorOfPieceStr + "'s king is in check and this move "
                                                 + "doesn't fix that. Move can't be made.");
            }

            rookNewXCoord = 2;
            kingNewXCoord = 1;
        } else if (isCastlingQueenside) {
            /* Castling is only possible if the rook and king both haven't
               moved since the start of play. Boolean state variables are used
               to track these conditions, and isCastlingPossible checks those
               values to see if castling can be done. */
            if (!isCastlingPossible(colorOfPiece, QUEEN)) {
                throw new CastlingNotPossibleException("Castling queenside is not possible for " + colorOfPieceStr);
            /* A test to see if the move would somehow put the king in check. */
            } else if (BoardArrays.wouldKingBeInCheck(boardArray, 6, kingYCoord, colorOfPiece, colorOnTop)) {
                throw new KingIsInCheckException("Castling queenside would place " + colorOfPieceStr + "'s king in "
                                                 + "check or " + colorOfPieceStr + "'s king is in check and this move "
                                                 + "doesn't fix that. Move can't be made.");
            }

            rookNewXCoord = 5;
            kingNewXCoord = 6;
        } else {
            /* A can't happen error, but here for completeness. */
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
        int capturedPieceInt = boardArray[toXCoord][toYCoord];
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
           promotion move; so the desintration square is set to the new piece. */
        if (moveObj.promotedToPieceInt() != 0) {
            boardArray[toXCoord][toYCoord] = moveObj.promotedToPieceInt();
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
