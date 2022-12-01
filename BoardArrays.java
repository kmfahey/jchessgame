package com.kmfahey.jchessgame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;

/**
 * This static class contains a collection of utility methods for manipulating
 * 2d int arrays that represent chessboards, plus a few other methods. The 8x8
 * boardArray that's used as the internal chessboard representation by the
 * Chessboard class is manipulated explicitly by methods in this class.
 */
public final class BoardArrays {

    /* These int flags are Or'd together to generate the integers that are used
       in an int[8][8] boardArray to represent pieces on the chessboard. Every
       piece is Or'd together from one of WHITE or BLACK, and one of KING,
       QUEEN, BISHOP, KNIGHT, ROOK, or PAWN. Additionally, since there are two
       Knight icons in the icon set this package uses, a knight's piece int will
       also be Or'd with either LEFT or RIGHT. */

    public static final int LEFT =      0b0000000001;
    public static final int RIGHT =     0b0000000010;

    public static final int PAWN =      0b0000000100;
    public static final int ROOK =      0b0000001000;
    public static final int KNIGHT =    0b0000010000;
    public static final int BISHOP =    0b0000100000;
    public static final int QUEEN =     0b0001000000;
    public static final int KING =      0b0010000000;

    public static final int WHITE =     0b0100000000;
    public static final int BLACK =     0b1000000000;

    /* This set of valid piece int values is used by the debugging method
       fileNameToBoardArray() to verify that field values in the board CSV its
       parsing are valid piece ints. */
    public static final HashSet<Integer> VALID_PIECE_INTS = new HashSet<Integer>() {{
        this.add(BLACK | KING); this.add(BLACK | QUEEN); this.add(BLACK | ROOK); this.add(BLACK | BISHOP);
        this.add(BLACK | KNIGHT | RIGHT); this.add(BLACK | KNIGHT | LEFT); this.add(BLACK | PAWN);
        this.add(WHITE | KING); this.add(WHITE | QUEEN); this.add(WHITE | ROOK); this.add(WHITE | BISHOP);
        this.add(WHITE | KNIGHT | RIGHT); this.add(WHITE | KNIGHT | LEFT); this.add(WHITE | PAWN);
    }};

    /* These three constants are indexes used by the return value of
       tallySpecialPawns(). */
    public static final int DOUBLED = 0;
    public static final int ISOLATED = 1;
    public static final int BLOCKED = 2;

    /* These strings are used by algNotnToCoords() and coordsToAlgNotn() to
       translate between algebraic notation and numeric coordinates. */
    public static String ALG_NOTN_ALPHA = "abcdefgh";
    public static String ALG_NOTN_NUM = "87654321";

    /* This array contains the pieces that a pawn can be promoted to. */
    public static int[] PAWN_PROMOTION_PIECES = new int[] {ROOK, KNIGHT, BISHOP, QUEEN};

    /* This mapping is used by evaluateBoard to memoize its results to specific
       board states, using a very long String uniquely represents a specific
       boardArray configuration. */
    private static HashMap<String, Double> evaluateBoardMemoizeMap = new HashMap<String, Double>();

    /* A Random object, used for a few cases where a coin toss is needed. */
    private static Random randomNumberGenerator = new Random();

    /**
     * This method accepts the algebraic notation for a square on a notional
     * chessboard, and returns an int[2] array containing the coordinates for
     * the same square.
     * square.
     *
     * @param location A 2-character String representing a chessboard square in
     *                 algebraic notation.
     * @return         A int[2] array containing the coordinates for the same
     *                 square.
     * @see coordsToAlgNotn
     */
    public static int[] algNotnToCoords(final String location) {
        int xCoord = ALG_NOTN_ALPHA.indexOf(location.charAt(0));
        int yCoord = ALG_NOTN_NUM.indexOf(location.charAt(1));
        return new int[] {xCoord, yCoord};
    }

    /**
     * This method accepts coordinates of a square on a notional chessboard, and
     * returns the 2-character algebraic notation String that refers to the same
     * square.
     *
     * @param coords A int[2] array containing the coordinates of the square.
     * @return       The algebraic notation for the same square (a 2-character
     *               String).
     * @see algNotnToCoords
     */
    public static String coordsToAlgNotn(final int[] coords) {
        return coordsToAlgNotn(coords[0], coords[1]);
    }

    /**
     * This method accepts coordinates of a square on a notional chessboard, and
     * returns the 2-character algebraic notation String that refers to the same
     * square.
     *
     * @param xCoord The x coordinate of the square.
     * @param yCoord The y coordinate of the square.
     * @return       The algebraic notation for the same square (a 2-character
     *               String).
     * @see algNotnToCoords
     */
    public static String coordsToAlgNotn(final int xCoord, final int yCoord) {
        String alphaComponent = String.valueOf(ALG_NOTN_ALPHA.charAt(xCoord));
        String numComponent = String.valueOf(ALG_NOTN_NUM.charAt(yCoord));
        return alphaComponent + numComponent;
    }

    /**
     * This method iterates over the given board array until the king of the
     * specified color is found. It returns the coordinates the king was found
     * at.
     *
     * @param boardArray An int[8][8] array that is the chessboard
     *                   representation used explicitly by methods in this
     *                   static class, and internally by the Chessboard object.
     * @param kingColor  The color of the king to search for.
     * @return           An int[2] containing the x and y coordinates the king
     *                   was found at.
     */
    public static int[] findKing(final int[][] boardArray, final int kingColor) {
        int xIdx = -1;
        int yIdx = -1;
        for (xIdx = 0; xIdx < 8; xIdx++) {
            for (yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (kingColor | KING)) {
                    return new int[] {xIdx, yIdx};
                }
            }
        }

        /* The loops completed without matching the King, so null is returned as
           an error value. */
        return null;
    }

    /**
     * This method selects the first n arrays in the given array-of-arrays, and
     * shuffles them using the Fisher-Yates in-place shuffling algorithm.
     *
     * @param movesArray The moves array to shuffle a subsequence of.
     * @param usedLength The length of the subsequence of the moves array to
     *                   shuffle, counting from the beginning of the
     *                   array-of-arrays.
     */
    public static void shuffleMovesArray(final int[][] movesArray, final int usedLength) {
        /* This is Fisher–Yates shuffle I'm told. idk I just copied code off
           stackoverflow as is best practice :) */
        for (int startingIndex = usedLength - 1; startingIndex > 0; startingIndex--) {
            int randomIndex = randomNumberGenerator.nextInt(startingIndex + 1);
            // Simple swap
            int[] swapValue = movesArray[randomIndex];
            movesArray[randomIndex] = movesArray[startingIndex];
            movesArray[startingIndex] = swapValue;
        }
    }

    /**
     * This method tests whether the king of the specified color is in
     * checkmate. It does this by executing a method that populates an array
     * with all possible moves, and testing if its return value of the index
     * of the first empty array is still 0 (and therefore the list of possible
     * moves is of length 0). The methods used to generate moves are constrained
     * that they may not leave their side's king in check after the move is
     * completed. If no move satisfies those constraints, the king is in
     * checkmate.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param colorsTurnItIs  The color of the piece.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                True if the specified king is in checkmate,
     *                        false otherwise.
     * @see generatePawnsMoves
     * @see generateRooksMoves
     * @see generateBishopsMoves
     * @see generateKnightsMoves
     * @see generateQueensMoves
     * @see generateKingsMoves
     */
    public static boolean isKingInCheckmate(final int[][] boardArray, final int colorsTurnItIs, final int colorOnTop) {
        int[][] movesArray = new int[128][7];
        return generatePossibleMoves(boardArray, movesArray, colorsTurnItIs, colorOnTop) == 0;
    }

    /**
     * This method is used to generate possible moves for every piece on the
     * supplied board array of the specified color. It returns the index of the
     * first empty array in movesArray after it has filled one or more arrays
     * with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array to fill moves into.
     * @param colorsTurnItIs  The color of the piece.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     * @see generatePawnsMoves
     * @see generateRooksMoves
     * @see generateBishopsMoves
     * @see generateKnightsMoves
     * @see generateQueensMoves
     * @see generateKingsMoves
     */
    public static int generatePossibleMoves(final int[][] boardArray, final int[][] movesArray,
                                            final int colorsTurnItIs, final int colorOnTop
                                            ) throws IllegalArgumentException {
        int colorOpposing = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int moveIdx = 0;

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt = boardArray[xIdx][yIdx];
                if (pieceInt == 0 || (pieceInt & colorOpposing) != 0) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            }
        }

        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the piece at the
     * specified location on the boardArray passed as the first argument. It
     * returns the index of the first empty array in movesArray after it has
     * filled one or more arrays with moves.
     *
     * @param boardArray     An int[8][8] array that is the chessboard
     *                       representation used explicitly by methods in this
     *                       static class, and internally by the Chessboard object.
     * @param movesArray     The working int[][7] array to fill moves into.
     * @param moveIdx        The index of the first empty array in movesArray.
     *                       This value is incremented each time a new move is
     *                       saved to movesArray, and the final value (still
     *                       pointing to the
     * @param xIdx           The x index of the location of the piece.
     * @param yIdx           The y index of the location of the piece.
     * @param colorsTurnItIs The color of the piece.
     * @param colorOnTop     The color playing from the top of the board.
     * @return               The new value for the index of the first empty
     *                       array in the int[][7] movesArray.
     * @see generatePawnsMoves
     * @see generateRooksMoves
     * @see generateBishopsMoves
     * @see generateKnightsMoves
     * @see generateQueensMoves
     * @see generateKingsMoves
     */
    public static int generatePieceMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdx,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                  ) throws IllegalArgumentException {
        int pieceInt = boardArray[xIdx][yIdx];

        /* The color of the piece is Not'd off of the pieceInt found in
           boardArray at the given indexes, and the value is switched against;
           whatever piece it is, the corresponding generate*sMoves() method is
           called. The return value of that method is returned directly. */
        switch (pieceInt ^ colorsTurnItIs) {
            case PAWN:
                return generatePawnsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            case ROOK:
                return generateRooksMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            case KNIGHT | LEFT: case KNIGHT | RIGHT:
                return generateKnightsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            case BISHOP:
                return generateBishopsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            case QUEEN:
                return generateQueensMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            case KING:
                return generateKingsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs, colorOnTop);
            default:
                throw new IllegalArgumentException(
                              "The integer value found in the board array at the specified indexes doesn't parse as a "
                              + "piece int value.");
        }
    }

    /**
     * This method is used to generate possible moves for the pawn, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the pawn.
     * @param yIdx            The y index of the location of the pawn.
     * @param colorsTurnItIs  The color of the pawn.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     */
    public static int generatePawnsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                  ) throws IllegalArgumentException {
        int colorOnBottom = colorOnTop == WHITE ? BLACK : WHITE;
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pawnPieceInt;
        int newPieceInt;
        int xIdxMod;
        int yIdxMod;
        int moveIdx = moveIdxArg;

        pawnPieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a pawn of the matching color. If
           not, an exception is thrown. */
        if (pawnPieceInt != (colorsTurnItIs | PAWN)) {
            throw new IllegalArgumentException(
                          "generatePawnsMoves() called with coordinates that point to cell on board that's not a pawn "
                          + "or not the color whose turn it is");
        }

        /* If checking the top color, pawns advance down the board, in the order
           of increasing Y values. Otherwise, pawns advance up the board, in the
           order of decreasing Y values. The appropriate modded y index yIdxMod
           is picked for the checks in this method. */
        if (yIdx < 7 && colorOnTop == colorsTurnItIs) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && colorOnTop != colorsTurnItIs) {
            yIdxMod = yIdx - 1;
        } else {
            return moveIdx;
        }

        /* This for loop checks both the forward diagonals from the pawn's
           square for a capturing move. */
        for (xIdxMod = xIdx - 1; xIdxMod <= xIdx + 2; xIdxMod++) {
            /* If the xIdxMod value is off either side of the board, this
               iteration is skipped. */
            if (xIdxMod < 0 || xIdxMod > 7) {
                continue;
            }
            /* If the current xIdxMod value is equal to xIdx and the square
               ahead is occupied, */
            if (xIdxMod == xIdx && boardArray[xIdxMod][yIdxMod] != 0
                // Or it's not equal to xIdx (so it's a diagonal)
                || xIdxMod != xIdx
                    // and the square on the diagonal isn't occupied,
                    && ((boardArray[xIdxMod][yIdxMod] & otherColor) == 0
                        // or it's occupied by the other side's king,
                        || (boardArray[xIdxMod][yIdxMod] ^ otherColor) == KING)
                // or xIdxMod is off the board in either direction,
                || xIdxMod < 0 || xIdxMod > 7) {
                // Then skip this iteration of the loop.
                continue;
            }

            /* If the move would put this side's king in check (or fail to
               get it out of check), it's discarded. */
            if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                continue;
            }

            /* If yIdxMod indicates the bottom rank of the board and the color
               is playing from the top, */
            if (yIdxMod == 7 && colorsTurnItIs == colorOnTop
                /* If yIdxMod indicates the top rank of the board and the color
                   is playing from the bottom, */
                || yIdxMod == 0 && colorsTurnItIs == colorOnBottom) {
                /* Then it's time for pawn promotion. All four possibilities are
                   generated and the AI chooses between them using the minimax
                   algorithm. */
                for (int newPieceBase : PAWN_PROMOTION_PIECES) {
                    newPieceInt = newPieceBase | colorsTurnItIs;
                    /* If the piece is a knight, a random choice between
                       LEFT and RIGHT is made and that value is Or'd onto
                       newPieceInt. */
                    if (newPieceBase == KNIGHT) {
                        newPieceInt = newPieceInt | (randomNumberGenerator.nextInt(2) == 1 ? LEFT : RIGHT);
                    }
                    /* The pawn promotion move is saved to movesArray, using the
                       7th array element to indicate the piece promoted to. */
                    setMoveToMovesArray(
                        movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                        boardArray[xIdxMod][yIdxMod], newPieceInt);
                    moveIdx++;
                }
            } else {
                /* Otherwise this isn't a pawn promotion move and the move is
                   saved to movesArray as normal. */
                setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                moveIdx++;
            }
        }

        /* If the pawn is on the 2nd rank from the top, and is the color playing
           from the top, and the square two ahead is empty, */
        if (yIdx == 1 && colorsTurnItIs == colorOnTop && boardArray[xIdx][yIdx + 2] == 0
            /* or the pawn is on the 7th rank from the top, and is the color
               playing from the bottom, and the square two ahead is empty, */
            || yIdx == 6 && colorsTurnItIs == colorOnBottom && boardArray[xIdx][yIdx - 2] == 0) {
            /* Then a move of two ahead is possible. */
            yIdxMod = yIdx == 1 ? yIdx + 2 : yIdx - 2;

            /* If the move wouldn't put this side's king in check (or fail to
               get it out of check), it's saved to the movesArray. */
            if (!wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdx, yIdxMod, colorsTurnItIs, colorOnTop)) {
                setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx, yIdxMod, 0);
                moveIdx++;
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the rook, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the rook.
     * @param yIdx            The y index of the location of the rook.
     * @param colorsTurnItIs  The color of the rook.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     */
    public static int generateRooksMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                  ) throws IllegalArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int rookPieceInt;
        int moveIdx = moveIdxArg;

        rookPieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a rook of the matching color. If
           not, an exception is thrown. */
        if (rookPieceInt != (colorsTurnItIs | ROOK)) {
            throw new IllegalArgumentException(
                          "generateRooksMoves() called with coordinates that point to cell on board that's not a rook "
                          + "or not the color whose turn it is");
        }

        if (yIdx < 7) {
            /* This for loop generates moves for the rook in the south
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int yIdxMod = yIdx + 1;
                 yIdxMod < 8 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdx, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdx][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                        boardArray[xIdx][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdx][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            /* This for loop generates moves for the rook in the north
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int yIdxMod = yIdx - 1;
                 yIdxMod >= 0 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdx, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdx][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                        boardArray[xIdx][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdx][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7) {
            /* This for loop generates moves for the rook in the east
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx + 1;
                 xIdxMod < 8 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdx, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdx] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                        boardArray[xIdxMod][yIdx]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdx] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            /* This for loop generates moves for the rook in the west
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx - 1;
                 xIdxMod >= 0 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdx, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdx] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                        boardArray[xIdxMod][yIdx]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdx] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the bishop, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the bishop.
     * @param yIdx            The y index of the location of the bishop.
     * @param colorsTurnItIs  The color of the bishop.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     */
    public static int generateBishopsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                    ) throws IllegalArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int bishopPieceInt;
        int moveIdx = moveIdxArg;

        bishopPieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a bishop of the matching color. If
           not, an exception is thrown. */
        if (bishopPieceInt != (colorsTurnItIs | BISHOP)) {
            throw new IllegalArgumentException(
                          "generateBishopsMoves() called with coordinates that point to cell on board that's not a "
                          + "bishop or not the color whose turn it is");
        }

        if (xIdx < 7 && yIdx < 7) {
            /* This for loop generates moves for the bishop in the southeast
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1;
                xIdxMod < 8 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx < 7) {
            /* This for loop generates moves for the bishop in the southwest
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1;
                xIdxMod >= 0 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7 && yIdx > 0) {
            /* This for loop generates moves for the bishop in the northeast
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1;
                xIdxMod < 8 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx > 0) {
            /* This for loop generates moves for the bishop in the northwest
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1;
                xIdxMod >= 0 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the knight, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the knight.
     * @param yIdx            The y index of the location of the knight.
     * @param colorsTurnItIs  The color of the knight.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     */
    public static int generateKnightsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                    ) throws IllegalArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int knightPieceInt;
        int moveIdx = moveIdxArg;

        knightPieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a knight of the matching color. If
           not, an exception is thrown. */
        if (knightPieceInt != (colorsTurnItIs | KNIGHT | LEFT) && knightPieceInt != (colorsTurnItIs | KNIGHT | RIGHT)) {
            throw new IllegalArgumentException(
                          "generateKnightsMoves() called with coordinates that point to cell on board that's not a "
                          + "knight or not the color whose turn it is");
        }

        /* These nested for loops generate a value to add to the xIdx between -2
           and +2, and for each a value to add to the yIdx between -2 and +2.... */
        for (int xIdxDelta = -2; xIdxDelta <= 2; xIdxDelta++) {
            for (int yIdxDelta = -2; yIdxDelta <= 2; yIdxDelta++) {
                /* If the deltas are both zero, or if |x delta| == |y delta|,
                   the loop is continued. */
                if (Math.abs(xIdxDelta) == Math.abs(yIdxDelta) || xIdxDelta == 0 || yIdxDelta == 0) {
                    continue;
                }

                /* The remaining deltas are both nonzero, one is ±1, and the
                   other is ±2. There's 8 combinations, and when applied to
                   xIdx and yIdx in turn they generate one of the eight knight's
                   moves. */
                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;

                /* If either of the adjusted values falls outside the board,
                   or if the square at that point on the board contains a piece
                   on our side, the loop is continued. */
                if (xIdxMod > 7 || xIdxMod < 0 || yIdxMod > 7 || yIdxMod < 0
                    || (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) != 0
                    || (boardArray[xIdxMod][yIdxMod] ^ otherColor) == KING) {
                    continue;
                }

                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }

                /* The move is saved to the movesArray. */
                setMoveToMovesArray(movesArray, moveIdx, knightPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                moveIdx++;
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the queen, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the queen.
     * @param yIdx            The y index of the location of the queen.
     * @param colorsTurnItIs  The color of the queen.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     */
    public static int generateQueensMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                   final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                   ) throws IllegalArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int queenPieceInt;
        int moveIdx = moveIdxArg;

        queenPieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a queen of the matching color. If
           not, an exception is thrown. */
        if (queenPieceInt != (colorsTurnItIs | QUEEN)) {
            throw new IllegalArgumentException("generateQueensMoves() called with coordinates that point to cell "
                                                    + "on board that's not a queen or not the color whose turn it is");
        }

        if (xIdx < 7) {
            /* This for loop generates moves for the queen in the south
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx + 1;
                xIdxMod < 8 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                xIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdx, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdx] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                        boardArray[xIdxMod][yIdx]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdx] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                /* This for loop generates moves for the queen in the southeast
                   direction. It stops if it reaches a square occupied by a
                   friendly piece. */
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1;
                     xIdxMod < 8 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod++, yIdxMod++, moveIdx++) {
                    /* If the move would put this side's king in check (or fail to
                       get it out of check), it's discarded. */
                    if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                        continue;
                    }
                    /* The move is saved if it wouldn't capture a king. */
                    if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                        setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                            boardArray[xIdxMod][yIdxMod]);
                    }
                    /* If the last move captured a piece, the loop breaks. */
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                /* This for loop generates moves for the queen in the northeast
                   direction. It stops if it reaches a square occupied by a
                   friendly piece. */
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0
                     && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod++, yIdxMod--, moveIdx++) {
                    /* If the move would put this side's king in check (or fail to
                       get it out of check), it's discarded. */
                    if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                        continue;
                    }
                    /* The move is saved if it wouldn't capture a king. */
                    if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                        setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                            boardArray[xIdxMod][yIdxMod]);
                    }
                    /* If the last move captured a piece, the loop breaks. */
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }
        if (yIdx < 7) {
            /* This for loop generates moves for the queen in the south
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int yIdxMod = yIdx + 1;
                 yIdxMod < 8 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod++, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdx, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdx][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                        boardArray[xIdx][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdx][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            /* This for loop generates moves for the queen in the east
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int yIdxMod = yIdx - 1;
                 yIdxMod >= 0 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdx, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdx][yIdxMod] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                        boardArray[xIdx][yIdxMod]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdx][yIdxMod] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            /* This for loop generates moves for the queen in the west
               direction. It stops if it reaches a square occupied by a friendly
               piece. */
            for (int xIdxMod = xIdx - 1;
                 xIdxMod >= 0 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod--, moveIdx++) {
                /* If the move would put this side's king in check (or fail to
                   get it out of check), it's discarded. */
                if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdx, colorsTurnItIs, colorOnTop)) {
                    continue;
                }
                /* The move is saved if it wouldn't capture a king. */
                if ((boardArray[xIdxMod][yIdx] ^ otherColor) != KING) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                        boardArray[xIdxMod][yIdx]);
                }
                /* If the last move captured a piece, the loop breaks. */
                if (boardArray[xIdxMod][yIdx] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                /* This for loop generates moves for the queen in the southwest
                   direction. It stops if it reaches a square occupied by a
                   friendly piece. */
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1;
                     xIdxMod >= 0 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod--, yIdxMod++, moveIdx++) {
                    /* If the move would put this side's king in check (or fail
                       to get it out of check), it's discarded. */
                    if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                        continue;
                    }
                    /* The move is saved if it wouldn't capture a king. */
                    if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                        setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                            boardArray[xIdxMod][yIdxMod]);
                    }
                    /* If the last move captured a piece, the loop breaks. */
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                /* This for loop generates moves for the queen in the northwest
                   direction. It stops if it reaches a square occupied by a
                   friendly piece. */
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1;
                     xIdxMod >= 0 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod--, yIdxMod--, moveIdx++) {
                    /* If the move would put this side's king in check (or fail
                       to get it out of check), it's discarded. */
                    if (wouldKingBeInCheck(boardArray, xIdx, yIdx, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                        continue;
                    }
                    /* The move is saved if it wouldn't capture a king. */
                    if ((boardArray[xIdxMod][yIdxMod] ^ otherColor) != KING) {
                        setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                            boardArray[xIdxMod][yIdxMod]);
                    }
                    /* If the last move captured a piece, the loop breaks. */
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /**
     * This method is used to generate possible moves for the king, and save
     * them to the movesArray it's given as an argument. It returns the index
     * of the first empty array in movesArray after it has filled one or more
     * arrays with moves. It checks each possible move for if it would put the
     * king in check. Of note, if it doesn't find any possible moves for the
     * king, then de facto the king is in checkmate.
     *
     * @param boardArray      An int[8][8] array that is the chessboard
     *                        representation used explicitly by methods in this
     *                        static class, and internally by the Chessboard object.
     * @param movesArray      The working int[][7] array that is populated by
     *                        generate*sMoves() methods. It may already contain
     *                        moves for other pieces. It is assumed to be long
     *                        enough to contain all new moves without checking
     *                        for overflow.
     * @param moveIdxArg      The index of the first empty array in movesArray.
     *                        This value is incremented each time a new move is
     *                        saved to movesArray, and the final value (still
     *                        pointing to the
     * @param xIdx            The x index of the location of the king.
     * @param yIdx            The y index of the location of the king.
     * @param colorsTurnItIs  The color of the king.
     * @param colorOnTop      The color playing from the top of the board.
     * @return                The new value for the index of the first empty
     *                        array in the int[][7] movesArray.
     * @see isKingInCheck
     */
    public static int generateKingsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                         final int xIdx, final int yIdx, final int colorsTurnItIs, final int colorOnTop
                                         ) throws IllegalArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pieceInt;
        int moveIdx = moveIdxArg;

        pieceInt = boardArray[xIdx][yIdx];

        /* A check to confirm that the coordinates the method was called and the
           color it was called with point to a king of the matching color. If
           not, an exception is thrown. */
        if (pieceInt != (colorsTurnItIs | KING)) {
            throw new IllegalArgumentException(
                          "generateKingsMoves() called with coordinates that point to cell on board that's not a king "
                          + "or not the color whose turn it is");
        }

        /* This pair of loops iterates over an x delta value in [-1,+1] and a y
           delta value [-1,+1].... */
        for (int xIdxDelta = -1; xIdxDelta <= 1; xIdxDelta++) {
            for (int yIdxDelta = -1; yIdxDelta <= 1; yIdxDelta++) {
                /* If both deltas are zero, then they would point to the king's
                   current square, not a move. The loop skips to the next
                   iteration. */
                if (xIdxDelta == 0 && yIdxDelta == 0) {
                    continue;
                }

                /* The modified x and y values are computed; if they point to a
                   location off the board, or to a square occupied by a piece
                   on our side, or to the other side's king (which may not be
                   captured), the loop skips to the next iteration. */
                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;
                if (xIdxMod < 0 || xIdxMod > 7 || yIdxMod < 0 || yIdxMod > 7
                    || (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) != 0
                    || (boardArray[xIdxMod][yIdxMod] ^ otherColor) == KING) {
                    continue;
                }

                if (wouldKingBeInCheck(boardArray, xIdxMod, yIdxMod, colorsTurnItIs, colorOnTop)) {
                    continue;
                }

                /* If the method was called with a movesArray, the possible move
                   is set to the current index on that array. */
                if (movesArray != null) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                }
                moveIdx++;
            }
        }

        /*
        * This set of conditionals checks whether the king can castle with
        * either rook. (The normal prerequisite of castling, that the king and
        * the rook must not have moved yet in the game, cannot be tested for
        * here but will be tested for by Chessboard.movePiece() if this move is
        * attempted, since the Chessboard class does track those booleans.)
        */
        if ((yIdx == 0 || yIdx == 7) && xIdx == 3) {
            /* If the king is at its starter position, then it might be able to
               castle in either direction. The first conditional checks whether
               kingside castling is possible. It checks if the rook is in
               position and the intervening squares are empty. */
            if (boardArray[2][yIdx] == 0 && boardArray[1][yIdx] == 0
                && (boardArray[0][yIdx] == (colorsTurnItIs | ROOK))) {

                /* If the king wouldn't be in check, then the castling move is
                   saved to the movesArray. It's signalled by saving a move to
                   the corner the rook is in and listing the rook as a capture
                   (although the rook is the same color and can't be captured). */
                if (!wouldKingBeInCheck(boardArray, 2, yIdx, colorsTurnItIs, colorOnTop)) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, 0, yIdx, boardArray[0][yIdx]);
                    moveIdx++;
                }
            }
            /* The second conditional checks whether queenside castling
               is possible. It checks if the rook is in position and the
               intervening squares are empty. */
            if (boardArray[4][yIdx] == 0 && boardArray[5][yIdx] == 0 && boardArray[6][yIdx] == 0
                       && (boardArray[7][yIdx] == (colorsTurnItIs | ROOK))) {

                /* If the king wouldn't be in check, then the castling move is
                   saved to the movesArray. It's signalled by saving a move to
                   the corner the rook is in and listing the rook as a capture
                   (although the rook is the same color and can't be captured). */
                if (!wouldKingBeInCheck(boardArray, 6, yIdx, colorsTurnItIs, colorOnTop)) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, 7, yIdx, boardArray[7][yIdx]);
                    moveIdx++;
                }
            }
        }

        /* The new value for moveIdx is returned. It's been incremented after
           every assignment to movesArray, so it's still the index of the first
           empty array in the int[][7] movesArray. */
        return moveIdx;
    }

    /*
     * An alternative signature of setMoveToMovesArray(int[][], int, int, int,
     * int, int, int, int, int) that omits the rarely used final argument,
     * promotedToPieceInt, since in the vast majority of cases the move isn't a
     * pawn promotion. See the other signature of setMoveToMovesArray() for the
     * full docs.
     */
    private static void setMoveToMovesArray(final int[][] movesArray, final int moveIdx, final int pieceInt,
                                     final int fromXIdx, final int fromYIdx, final int toXIdx, final int toYIdx,
                                     final int capturedPiece) throws IllegalArgumentException {
        setMoveToMovesArray(movesArray, moveIdx, pieceInt, fromXIdx, fromYIdx, toXIdx, toYIdx, capturedPiece, 0);
    }

    /*
     * A utility method used by generate*sMoves() methods to save a set of
     * values to a specific index on the int[][] movesArray the method is
     * populating. Just a shorthand for a series of array assignments.
     *
     * @param movesArray         The int[][7] array that the calling method is
     *                           iterating over.
     * @param moveIdx            The index of the nest empty int[7] array in
     *                           movesArray that the caller wishes to populate
     *                           with the remaining arguments.
     * @param pieceInt           The int value denoting a piece, to be set at
     *                           array index 0.
     * @param fromXIdx           The int value of the x coordinate the piece is
     *                           moving from, to be set at array index 1.
     * @param fromYIdx           The int value of the y coordinate the piece is
     *                           moving from, to be set at array index 2.
     * @param toXIdx             The int value of the x coordinate the piece is
     *                           moving to, to be set at array index 3.
     * @param toYIdx             The int value of the y coordinate the piece is
     *                           moving to, to be set at array index 4.
     * @param capturedPiece      If there is a piece at (toXIdx, toYIdx), this
     *                           is the int value denoting that piece, to be set
     *                           at array index 5.
     * @param promotedToPieceInt If the piece moving is a pawn, and its
     *                           destination is the opposite end of the board
     *                           from where its color started, then this is the
     *                           piece it promotes into.
     * @return                   Void.
     * @see generatePawnsMoves
     * @see generateRooksMoves
     * @see generateBishopsMoves
     * @see generateKnightsMoves
     * @see generateQueensMoves
     * @see generateKingsMoves
     */
    private static void setMoveToMovesArray(final int[][] movesArray, final int moveIdx, final int pieceInt,
                                     final int fromXIdx, final int fromYIdx, final int toXIdx, final int toYIdx,
                                     final int capturedPiece, final int promotedToPieceInt
                                     ) throws IllegalArgumentException {
        if (movesArray[moveIdx][0] != 0) {
            throw new IllegalArgumentException("setMoveToMovesArray() called with moveIdx arg pointing to "
                                                    + "non-zero entry in movesArray argument");
        }
        movesArray[moveIdx][0] = pieceInt;
        movesArray[moveIdx][1] = fromXIdx;
        movesArray[moveIdx][2] = fromYIdx;
        movesArray[moveIdx][3] = toXIdx;
        movesArray[moveIdx][4] = toYIdx;
        movesArray[moveIdx][5] = capturedPiece;
        movesArray[moveIdx][6] = promotedToPieceInt;
    }

    /**
     * This method tests whether the the king of the specified color is in check.
     *
     * @param boardArray     The int[8][8] array that represents the chessboard.
     * @param colorsTurnItIs The color of the king to be tested for if it's in
     *                       check.
     * @param colorOnTop     The color playing from the top of the board.
     * @return               A boolean, whether this color's king is in check.
     */
    public static boolean isKingInCheck(final int[][] boardArray, final int colorsTurnItIs, final int colorOnTop) {
        int[] kingCoords = findKing(boardArray, colorsTurnItIs);
        int kingXIdx = kingCoords[0];
        int kingYIdx = kingCoords[1];
        return wouldKingBeInCheck(boardArray, kingXIdx, kingYIdx, -1, -1, -1, -1, colorsTurnItIs, colorOnTop);
    }

    /**
     * This method tests whether the king of the specified color would be in
     * check if a friendly piece in the specified square moved to the specified
     * square.
     *
     * @param boardArray     The int[8][8] array that represents the chessboard.
     * @param fromXIdx       The x coordinate of the square a friendly piece is
     *                       moving from, to be treated as empty.
     * @param fromYIdx       The y coordinate of the square a friendly piece is
     *                       moving from, to be treated as empty.
     * @param toXIdx         The x coordinate of the square a friendly piece is
     *                       moving to, to be treated as occupied by a friendly
     *                       piece.
     * @param toYIdx         The x coordinate of the square a friendly piece is
     *                       moving to, to be treated as occupied by a friendly
     *                       piece.
     * @param colorsTurnItIs The color of the king to be tested for if it's in
     *                       check.
     * @param colorOnTop     The color playing from the top of the board.
     * @return               A boolean, whether any piece on this side has
     *                       the king in check.
     */
    public static boolean wouldKingBeInCheck(final int[][] boardArray, final int fromXIdx, final int fromYIdx,
                                             final int toXIdx, final int toYIdx, final int colorsTurnItIs,
                                             final int colorOnTop) {
        int[] kingCoords = findKing(boardArray, colorsTurnItIs);
        int kingXIdx = kingCoords[0];
        int kingYIdx = kingCoords[1];
        return wouldKingBeInCheck(boardArray, kingXIdx, kingYIdx, fromXIdx, fromYIdx, toXIdx, toYIdx,
                                  colorsTurnItIs, colorOnTop);
    }

    /**
     * This method tests whether the the king of the specified color would be in
     * check if it were located in the specified square.
     *
     * @param boardArray     The int[8][8] array that represents the chessboard.
     * @param colorsTurnItIs The color of the king to be tested for if it's in
     *                       check.
     * @param kingXIdx       The x coordinate of the square the king is to be
     *                       treated as being located in.
     * @param kingYIdx       The y coordinate of the square the king is to be
     *                       treated as being located in.
     * @param colorOnTop     The color playing from the top of the board.
     * @return               A boolean, whether this color's king is in check.
     */
    public static boolean wouldKingBeInCheck(final int[][] boardArray, final int kingXIdx, final int kingYIdx,
                                             final int colorsTurnItIs, final int colorOnTop) {
        return wouldKingBeInCheck(boardArray, kingXIdx, kingYIdx, -1, -1, -1, -1, colorsTurnItIs, colorOnTop);
    }

    /**
     * This method tests whether the king of the specified color would be in
     * check if it were located in the specified square and a friendly piece at
     * the specified square moved to the specified square.
     *
     * @param boardArray     The int[8][8] array that represents the chessboard.
     * @param kingXIdx       The x coordinate of the square the king is to be
     *                       treated as being located in.
     * @param kingYIdx       The y coordinate of the square the king is to be
     *                       treated as being located in.
     * @param fromXIdx       The x coordinate of the square a friendly piece is
     *                       moving from, to be treated as empty.
     * @param fromYIdx       The y coordinate of the square a friendly piece is
     *                       moving from, to be treated as empty.
     * @param toXIdx         The x coordinate of the square a friendly piece is
     *                       moving to, to be treated as occupied by a friendly
     *                       piece.
     * @param toYIdx         The x coordinate of the square a friendly piece is
     *                       moving to, to be treated as occupied by a friendly
     *                       piece.
     * @param colorsTurnItIs The color of the king to be tested for if it's in
     *                       check.
     * @param colorOnTop     The color playing from the top of the board.
     * @return               A boolean, whether this color's king is in check.
     */
    public static boolean wouldKingBeInCheck(final int[][] boardArray, final int kingXIdx, final int kingYIdx,
                                             final int fromXIdx, final int fromYIdx, final int toXIdx, final int toYIdx,
                                             final int colorsTurnItIs, final int colorOnTop) {
        int otherColor = colorsTurnItIs == WHITE ? BLACK : WHITE;
        int xIdx = kingXIdx;
        int yIdx = kingYIdx;
        int xIdxMod;
        int yIdxMod;

        /* This method is also available as wouldKingBeInCheck(boardArray,
           kingXIdx, kingYIdx, colorsTurnItIs, colorOnTop). It calls this method
           with fromXIdx = -1, fromYIdx = -1, toXIdx = -1, toYIdx = -1. That
           doesn't interfere with this method's execution; all it means is
           that !(xIdxMod == fromXIdx && yIdx == fromYIdx) and (xIdxMod ==
           toXIdx && yIdxMod == toYIdx) can never return true. The rest of the
           conditionals proceed as normal. */

        // Pawns
        if (colorOnTop == colorsTurnItIs) {
            yIdxMod = yIdx + 1;
        } else {
            yIdxMod = yIdx - 1;
        }

        /* Checking the two forward diagonals to see if either contains an
           opposing pawn. */
        if (0 <= yIdxMod && yIdxMod <= 7) {
            for (xIdxMod = xIdx - 1; xIdxMod <= xIdx + 1; xIdxMod += 2) {
                if (xIdxMod < 0 || xIdxMod > 7) {
                    continue;
                }
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | PAWN)) {
                    return true;
                }
            }
        }

        /* Checking for a king. */
        for (xIdxMod = xIdx - 1; xIdxMod <= xIdx + 1; xIdxMod++) {
            for (yIdxMod = yIdx - 1; yIdxMod <= yIdx + 1; yIdxMod++) {
                if (xIdxMod < 0 || xIdxMod > 7 || yIdxMod < 0 || yIdxMod > 7) {
                    break;
                }
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                }
            }
        }

        // Knights

        for (int xIdxDelta = -2; xIdxDelta <= +2; xIdxDelta++) {
            for (int yIdxDelta = -2; yIdxDelta <= +2; yIdxDelta++) {
                /* Skipping this iteration if both deltas are 0, or if
                   |xIdxDelta| == |yIdxDelta|. In the remaining 8 iterations,
                   one is ±1 and the other is ±2. When added to (xIdx, yIdx),
                   that produces the 8 knight's moves from the king's location.
                   They're tested to see if an opposing knight is in any one of
                   them. */
                if (xIdxDelta == 0 && yIdxDelta == 0 || Math.abs(xIdxDelta) == Math.abs(yIdxDelta)) {
                    continue;
                }

                xIdxMod = xIdx + xIdxDelta;
                yIdxMod = yIdx + yIdxDelta;

                if (xIdxMod < 0 || xIdxMod > 7 || yIdxMod < 0 || yIdxMod > 7) {
                    continue;
                }

                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KNIGHT | LEFT)
                    || boardArray[xIdxMod][yIdxMod] == (otherColor | KNIGHT | RIGHT)) {
                    return true;
                }
            }
        }

        // Bishops, Rooks, and Queens
        // Checking eastward on this rank for a queen or a rook
        for (xIdxMod = xIdx + 1; xIdxMod <= 7; xIdxMod++) {
            int pieceInt = boardArray[xIdxMod][yIdx];
            if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdx == fromYIdx)
                && pieceInt != (colorsTurnItIs | KING)) {
                break;
            } else if (pieceInt == (otherColor | ROOK) || pieceInt == (otherColor | QUEEN)) {
                return true;
            } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdx == toYIdx) {
                break;
            }
        }

        // Checking the southeast diagonal for a queen or a bishop
        for (xIdxMod = xIdx + 1; xIdxMod <= 7; xIdxMod++) {
            for (yIdxMod = yIdx + 1; yIdxMod <= 7; yIdxMod++) {
                int pieceInt = boardArray[xIdxMod][yIdxMod];
                if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdx == fromYIdx)
                    && pieceInt != (colorsTurnItIs | KING)) {
                    break;
                } else if (pieceInt == (otherColor | BISHOP) || pieceInt == (otherColor | QUEEN)) {
                    return true;
                } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdxMod == toYIdx) {
                    break;
                }
            }
        }

        // Checking southward on this file for a queen or a rook
        for (yIdxMod = yIdx + 1; yIdxMod <= 7; yIdxMod++) {
            int pieceInt = boardArray[xIdx][yIdxMod];
            if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdx == fromYIdx)
                && pieceInt != (colorsTurnItIs | KING)) {
                break;
            } else if (pieceInt == (otherColor | ROOK) || pieceInt == (otherColor | QUEEN)) {
                return true;
            } else if ((pieceInt & otherColor) != 0 || xIdx == toXIdx && yIdxMod == toYIdx) {
                break;
            }
        }

        // Checking the southwest diagonal for a queen or a bishop
        for (xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
            for (yIdxMod = yIdx + 1; yIdxMod <= 7; yIdxMod++) {
                int pieceInt = boardArray[xIdxMod][yIdxMod];
                if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdx == fromYIdx)
                    && pieceInt != (colorsTurnItIs | KING)) {
                    break;
                } else if (pieceInt == (otherColor | BISHOP) || pieceInt == (otherColor | QUEEN)) {
                    return true;
                } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdxMod == toYIdx) {
                    break;
                }
            }
        }

        // Checking westward on this rank for a queen or a rook
        for (xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
            int pieceInt = boardArray[xIdxMod][yIdx];
            if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdx == fromYIdx)
                && pieceInt != (colorsTurnItIs | KING)) {
                break;
            } else if (pieceInt == (otherColor | ROOK) || pieceInt == (otherColor | QUEEN)) {
                return true;
            } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdx == toYIdx) {
                break;
            }
        }

        // Checking the northwest diagonal for a queen or a bishop
        for (xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
            for (yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                int pieceInt = boardArray[xIdxMod][yIdxMod];
                if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdxMod == fromYIdx)
                    && pieceInt != (colorsTurnItIs | KING)) {
                    break;
                } else if (pieceInt == (otherColor | BISHOP) || pieceInt == (otherColor | QUEEN)) {
                    return true;
                } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdxMod == toYIdx) {
                    break;
                }
            }
        }

        // Checking northward on this file for a queen or a rook
        for (yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
            int pieceInt = boardArray[xIdx][yIdxMod];
            if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdxMod == fromYIdx)
                && pieceInt != (colorsTurnItIs | KING)) {
                break;
            } else if (pieceInt == (otherColor | ROOK) || pieceInt == (otherColor | QUEEN)) {
                return true;
            } else if ((pieceInt & otherColor) != 0 || xIdx == toXIdx && yIdxMod == toYIdx) {
                break;
            }
        }

        // Checking the northeast diagonal for a queen or a bishop
        for (xIdxMod = xIdx + 1; xIdxMod <= 7; xIdxMod++) {
            for (yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                int pieceInt = boardArray[xIdxMod][yIdxMod];
                if ((pieceInt & colorsTurnItIs) != 0 && !(xIdxMod == fromXIdx && yIdxMod == fromYIdx)
                    && pieceInt != (colorsTurnItIs | KING)) {
                    break;
                } else if (pieceInt == (otherColor | BISHOP) || pieceInt == (otherColor | QUEEN)) {
                    return true;
                } else if ((pieceInt & otherColor) != 0 || xIdxMod == toXIdx && yIdxMod == toYIdx) {
                    break;
                }
            }
        }

        /* If execution reaches this point in the method, than no possible
           avenue of attack contains a threatening piece. The king is not in
           check, and false is returned. */
        return false;
    }

    /**
     * A debugging method that prints the contents of a boardArray in a
     * pretty-printed format (reproduces the Java code needed to declare the
     * array). Used in some try/catch blocks to print the boardArray that was
     * involved in the exception being thrown, for reproducibility purposes.
     *
     * @param boardArray An int[8][8] array that is the chessboard
     *                   representation used explicitly by methods in this
     *                   static class, and internally by the Chessboard object.
     * @see com.kmfahey.jchessgame.Chessboard
     */
    public static void printBoard(final int[][] boardArray) {
        StringJoiner outerJoiner = new StringJoiner(",\n", "new int[][] {\n", "\n}\n");
        for (int outerIndex = 0; outerIndex < 8; outerIndex++) {
            StringJoiner innerJoiner = new StringJoiner(", ", "new int[] {", "}");
            for (int innerIndex = 0; innerIndex < 8; innerIndex++) {
                innerJoiner.add(String.valueOf(boardArray[outerIndex][innerIndex]));
            }
            outerJoiner.add(innerJoiner.toString());
        }
        System.out.println(outerJoiner.toString());
    }

    /**
     * This debugging method takes a fileName pointing to a CSV file, imports and
     * parses it, returning a int[][] that can be used as the first argument
     * to a Chessboard constructor or with Chessboard.setBoardArray(). The CSV
     * file must have no header, exactly 8 rows, exactly 8 columns, and contain
     * only integers. Every integer must be either a 0 or a valid piece-integer
     * value; see the piece constants defined at the top of BoardArrays.
     *
     * @param fileName The filename of the CSV file to import.
     * @return         An int[8][8] array representing a chessboard, suitable
     *                 for use by a Chessboard object.
     * @see com.kmfahey.jchessboard.Chessboard
     */
    public static int[][] fileNameToBoardArray(final String fileName)
                                                throws NullPointerException, BoardArrayFileParsingException, IOException {
        File boardFile;
        String fileContents;
        String[] fileLines;
        int[][] boardArray;

        /* The file indicated by fileName is opened and read. A test for length
           is applied */
        boardFile = new File(fileName);
        fileContents = Files.readString(boardFile.toPath());
        fileLines = fileContents.split("\n");
        if (fileLines.length != 8) {
            throw new BoardArrayFileParsingException("Board file " + fileName + " doesn't have exactly 8 lines.");
        }

        boardArray = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            String fileLine;
            String[] lineIntStr;

            /* Breaking the line into an array and checking it for length. */
            fileLine = fileLines[xIdx];
            lineIntStr = fileLine.split(", *");
            if (lineIntStr.length != 8) {
                throw new BoardArrayFileParsingException(
                              "Board file " + fileName + ", line " + (xIdx + 1) + " doesn't have exactly 8 "
                              + "comma-separated values. (Found " + Arrays.toString(lineIntStr) + " instead.)");
            }

            /* Iterates over the elements in this line, casting each one to an
               int and assigning it to the corresponding cell in the int[][]
               boardArray. Throws an error if an element doesn't cast to int
               or the elem as an int doesn't appear in the HashSet<Integer>
               VALID_PIECE_INTS. */
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt;

                try {
                    pieceInt = Integer.valueOf(lineIntStr[yIdx]);
                } catch (NumberFormatException exception) {
                    throw new BoardArrayFileParsingException(
                                  "Board file " + fileName + ", line " + (xIdx + 1) + ", item " + (yIdx + 1)
                                  + " doesn't eval as an integer. " + "(Found `" + lineIntStr[yIdx] + "` instead.)",
                                  exception);
                }
                if (pieceInt == 0) {
                    boardArray[xIdx][yIdx] = pieceInt;
                } else if (!VALID_PIECE_INTS.contains(pieceInt)) {
                    throw new BoardArrayFileParsingException(
                                  "Board file " + fileName + ", line " + (xIdx + 1) + ", item " + (yIdx + 1) + " is "
                                  + "an integer that's not a valid piece representation value. (Value is `"
                                  + pieceInt + "`.)");
                } else {
                    boardArray[xIdx][yIdx] = pieceInt;
                }
            }
        }

        /* Tests that the custom board contains a king of each color. Throws an
           error if one is missing, otherwise returns the completed board. */
        if (Objects.isNull(findKing(boardArray, WHITE))) {
            throw new BoardArrayFileParsingException(
                          "The imported board doesn't contain a White king. Board is invalid.");
        } else if (Objects.isNull(findKing(boardArray, BLACK))) {
            throw new BoardArrayFileParsingException(
                          "The imported board doesn't contain a Black king. Board is invalid.");
        } else {
            return boardArray;
        }
    }
}
