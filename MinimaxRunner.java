package com.kmfahey.jchessgame;

import java.util.HashMap;

/**
 * Implements the minimax algorithm with alpha/beta pruning. The frontend method
 * to the algorithm is algorithmTopLevel(), Its constructor accepts a Chessboard
 * object, and that object's boardArray is what the algorithm uses to calculate
 * its moves.
 *
 * @see MinimaxRunner#algorithmTopLevel
 * @see #algorithmTopLevel
 */
public class MinimaxRunner {

    /* These statements copy the piece int constants from BoardArrays to this
       class for convenience. */

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

    /* These three indexes are stored as a convenience so the return value
       from tallySpecialPawns() is readable in tallySpecialPawns() and
       evaluateBoard(). */
    private static final int DOUBLED = 0;
    private static final int ISOLATED = 1;
    private static final int BLOCKED = 2;

    /* This mapping is used to memoize results of evaluateBoard(). */
    private final HashMap<String, Double> evaluateBoardMemoizeMap;

    /* These three ints store relevant colors that decide how the algorithm
       picks sides and processes moves. */
    private final int colorOfAI;
    private final int colorOfPlayer;
    private final int colorOnTop;

    /** The default depth value used by the algorithm. */
    private final int algorithmStartingDepth;

    /** Object whose boardArray instance variable the algorithm calculates its
        moves on. */
    private final Chessboard chessboard;

    /**
     * Initializes the MinimaxRunner object, which hosts the minimax algorithm
     * implemented with a frontend at the algorithmTopLevel() method.
     *
     * @param chessboardObj The Chessboard object modelling the game that the
     *                      minimax algorithm is needed to generate moves for.
     * @param aiColor       The color the AI is playing, which moves will be
     *                      generated for. One of either BoardArrays.WHITE or
     *                      BoardArrays.BLACK.
     */
    public MinimaxRunner(final Chessboard chessboardObj, final int aiColor) {
        chessboard = chessboardObj;
        colorOfAI = aiColor;
        colorOfPlayer = colorOfAI == WHITE ? BLACK : WHITE;
        colorOnTop = chessboard.getColorOnTop();
        algorithmStartingDepth = 4;
        evaluateBoardMemoizeMap = new HashMap<>();
    }

    /**
     * Implements the minimax algorithm with the alpha/beta optimization. The
     * default recursion depth is 4 calls.
     *
     * @param turnCount The number of the turn it is, counting from 0.
     * @return          A Chessboard.Move object describing the move that the
     *                  algorithm has selected.
     */
    public Chessboard.Move algorithmTopLevel(final int turnCount) {
        Chessboard.Move bestMoveObj;
        int[][] movesArray = new int[128][7];
        int[][] boardArray;
        int[] bestMoveArray = null;
        boolean isCastlingKingside;
        boolean isCastlingQueenside;
        int capturedPieceInt;
        int fromXIdx;
        int fromYIdx;
        int movedPieceInt;
        int movesArrayUsedLength;
        int promotedToPieceInt;
        int toXIdx;
        int toYIdx;
        int useableMovesCount;
        double alpha = Double.NEGATIVE_INFINITY;
        double bestScore = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double thisScore;

        boardArray = chessboard.getBoardArray();

        movesArrayUsedLength = BoardArrays.generatePossibleMoves(boardArray, movesArray, colorOfAI, colorOnTop);
        useableMovesCount = movesArrayUsedLength;

        if (movesArrayUsedLength == 0) {
            /* BoardArrays.generatePossibleMoves() found zero moves. That only
               happens if the king is in checkmate. So the game is over and
               the AI concedes defeat. The algorithm returns a Chessboard.Move
               object with a null movedPiece attribute as a signal value. */
            return new Chessboard.Move(null, 0, 0, 0, 0, 0, false, false, 0);
        }

        if (turnCount == 0) {
            /* Introduces some nondeterminism into the algorithm in case the
               best score is tied between multiple moves. This is done at the AI's
               move in the first turn to prevent the AI from using the same opening
               move each time. */
            BoardArrays.shuffleMovesArray(movesArray, movesArrayUsedLength);
        }

        /* The main loop of the top-level phase of the minimax algorithm.
           algorithmExecutor() implements the given move on the board,
           calls algorithmLowerLevel(), reverses the move, and returns the
           score returned by algorithmLowerLevel(). During this for loop
           highest-scoring move is found and that's the one the algorithm will
           indicate as the AI's move this turn. */
        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            thisScore = algorithmCallExecutor(boardArray, true, movesArray[moveIdx],
                                              (colorOfAI == WHITE ? BLACK : WHITE),
                                              algorithmStartingDepth - 1, alpha, beta);
            if (thisScore >= bestScore) {
                bestScore = thisScore;
                bestMoveArray = movesArray[moveIdx];
            }
            if (thisScore > alpha) {
                alpha = thisScore;
            }
        }

        /* If the AI has no usable moves, that means it needs to concede. A null
           Move object is returned as a signal value. */
        if (useableMovesCount == 0) {
            return new Chessboard.Move(null, 0, 0, 0, 0, 0, false, false, 0);
        }

        /* The values of the bestMoveArray are broken out into named variables,
           for clarity. */
        assert bestMoveArray != null;
        movedPieceInt = bestMoveArray[0];
        fromXIdx = bestMoveArray[1];
        fromYIdx = bestMoveArray[2];
        toXIdx = bestMoveArray[3];
        toYIdx = bestMoveArray[4];
        capturedPieceInt = bestMoveArray[5];
        promotedToPieceInt = bestMoveArray[6];
        isCastlingKingside = false;
        isCastlingQueenside = false;

        /* If the moving piece is a king, the captured piece is a rook, and
           they're both the same color, then it's a castling move, so one of the
           castling booleans is set to true. */
        if ((capturedPieceInt & ROOK) != 0 && (movedPieceInt & KING) != 0
            && (movedPieceInt & WHITE) == (capturedPieceInt & WHITE)) {

            if (toXIdx == 0) {
                isCastlingKingside = true;
            } else {
                isCastlingQueenside = true;
            }
        }

        /* The best move found is built into a Chessbpard.Move object. */
        bestMoveObj = new Chessboard.Move(chessboard.getPieceAtCoords(fromXIdx, fromYIdx), fromXIdx, fromYIdx,
                                          toXIdx, toYIdx, capturedPieceInt, isCastlingKingside, isCastlingQueenside,
                                          promotedToPieceInt);

        return bestMoveObj;
    }

    /*
     * This method implements the levels of the minimax algorithm after the 1st
     * call. Alpha/beta pruning is done.
     *
     * @param boardArray     The int[8][8] array that represents the chessboard.
     * @param maximize       A boolean, true if this level of the algorithm is
     *                       a maximizing step, false if it's a minimizing step.
     * @param depth          The depth counter, which is decremented with each
     *                       successive recursive call. When it reaches zero,
     *                       this method returns the result of applying the
     *                       evaluateBoard() method to boardArray instead of
     *                       its normal logic.
     * @param colorsTurnItIs An integer, indicating which side of the game this
     *                       level of the algorithm is calculating for. Either
     *                       BoardArrays.BLACK or BoardArrays.WHITE.
     * @param alphaArg       The value for alpha.
     * @param betaArg        The value for beta.
     */
    private double algorithmLowerLevel(final int[][] boardArray, final boolean maximize, final int depth,
                                       final int colorsTurnItIs, final double alphaArg, final double betaArg) {
        double bestScore;
        double thisScore;
        double alpha = alphaArg;
        double beta = betaArg;
        int[][] movesArray = new int[128][7];
        int movesArrayUsedLength;

        /* If the depth counter has decreased to 0, the value of evaluateBoard()
           is returned rather than recursing any further. */
        if (depth == 0) {
            return evaluateBoard(boardArray, colorsTurnItIs);
        }

        /* bestScore is initialized to the worst possible score for the
           maximize/minimize mode the algorithm is in. */
        bestScore = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        /* Moves are calculated and saved to movesArray. */
        movesArrayUsedLength = BoardArrays.generatePossibleMoves(boardArray, movesArray, colorsTurnItIs, colorOnTop);

        /* BoardArrays.generatePossibleMoves() only returns a 0 if the king is
           in checkmate. That's the worst possible outcome if it was generating
           moves for the AI, or the best possible outcome if it was generating
           moves for the player. */
        if (movesArrayUsedLength == 0 && colorsTurnItIs == colorOfAI) {
            return Double.NEGATIVE_INFINITY;
        } else if (movesArrayUsedLength == 0 && colorsTurnItIs == colorOfPlayer) {
            return Double.POSITIVE_INFINITY;
        }

        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            /* The score is returned from the lower level call. */
            thisScore = algorithmCallExecutor(boardArray, !maximize, movesArray[moveIdx],
                                              (colorsTurnItIs == WHITE ? BLACK : WHITE), depth - 1, alpha, beta);
            /* If the score returned is the best possible score, further calls
               are skipped, and it's returned directly. */
            if (maximize ? thisScore == Double.POSITIVE_INFINITY : thisScore == Double.NEGATIVE_INFINITY) {
                return thisScore;
            }
            /* If a better value for alpha is found, it's set. */
            if (maximize && thisScore > alpha) {
                alpha = thisScore;
            /* If a better value for beta is found, it's set. */
            } else if (!maximize && thisScore < beta) {
                beta = thisScore;
            }
            /* If this score bests alpha or beta, further calls are skipped, and
               it's returned directly. */
            if (maximize ? thisScore >= beta : thisScore <= alpha) {
                return thisScore;
            }
            /* If this score bests the current best score, bestScore is set to
               thisScore. */
            if (maximize ? thisScore > bestScore : thisScore < bestScore) {
                bestScore = thisScore;
            }
        }

        /* The best score found is returned. */
        return bestScore;
    }

    /*
     * This method performs the actual call to algorithmLowerLevel(),
     * which has the same logic when algorithmTopLevel() does it and when
     * algorithmLowerLevel() does it, so it's refactored into its own method.
     *
     * @param boardArray     The int[8][8] array used to model the chessboard.
     * @param maximize       A boolean, whether this step of the algorithm is
     *                       maximizing the score (if true) or minimizing it (if
     *                       false).
     * @param moveArray      The array representing the individual move to execute.
     * @param colorsTurnItIs The color whose turn is being modelled in the call
     *                       of algorithmLowerLevel().
     * @param depth          A number that decreases by 1 each successive
     *                       recursive call. When algorithmLowerLevel() is
     *                       called with depth=0, it returns the result of
     *                       evaluateBoard() rather than conducting its normal
     *                       logic.
     * @param alpha          The value for alpha.
     * @param beta           The value for beta.
     */
    private double algorithmCallExecutor(final int[][] boardArray, final boolean maximize,
                                         final int[] moveArray, final int colorsTurnItIs,
                                         final int depth, final double alpha, final double beta) {
        int movedPieceInt = moveArray[0];
        int fromXIdx = moveArray[1];
        int fromYIdx = moveArray[2];
        int toXIdx = moveArray[3];
        int toYIdx = moveArray[4];
        int capturedPieceInt = moveArray[5];
        int savedPieceNo1;
        int savedPieceNo2;
        boolean isCastlingKingside = false;
        boolean isCastlingQueenside = false;
        double retval;
        Chessboard.Move moveObj;

        /* The same boardArray is passed down the call stack and reused by
           every step of the algorithm, to avoid having to clone it each time.
           That means I need to execute this moveArray's move on the board,
           execute the recursive call, and then undo the move so the board can
           be reused. savedPiece holds whatever was at the square the piece
           was moved to, so it can be restored. */

        if ((movedPieceInt & KING) != 0 && (capturedPieceInt & ROOK) != 0
            && (movedPieceInt & WHITE) == (capturedPieceInt & WHITE)) {
            /* If the moving piece is a King, and the "captured" piece is a
               rook, and they're both of the same color, then this is actually a
               castling move. */
            if (toXIdx != 0 && toXIdx !=  7) {
                throw new IllegalArgumentException("algorithmCallExecutor() called with a moveArray that indicated "
                                                   + "castling but the Rook isn't in position");
            }
            if (toXIdx == 0) {
                isCastlingKingside = true;
                savedPieceNo1 = boardArray[2][toYIdx];
                savedPieceNo2 = boardArray[1][fromYIdx];
            } else {
                isCastlingQueenside = true;
                savedPieceNo1 = boardArray[5][toYIdx];
                savedPieceNo2 = boardArray[6][fromYIdx];
            }

            moveObj = new Chessboard.Move(chessboard.getPieceAtCoords(fromXIdx, fromYIdx),
                                          fromXIdx, fromYIdx, toXIdx, toYIdx,
                                          capturedPieceInt, isCastlingKingside, isCastlingQueenside, 0);

            /* The boardArray being used is the same one this chessboard
               object manipulates internally when movePiece() is called. Here
               movePiece() is used because castling depends on state information
               (whether the king or rook has moved so far, which makes castling
               impossible) that's tracked internally by the Chessboard object. */
            try {
                chessboard.movePiece(moveObj);
            } catch (CastlingNotPossibleException | KingIsInCheckException exception) {
                return maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }

            /* The actual call to algorithmLowerLevel(). */
            retval = algorithmLowerLevel(boardArray, maximize, depth, colorsTurnItIs, alpha, beta);

            /* The castling is reversed and the board is returned to the state
               it was in when this method was called. */
            if (isCastlingKingside) {
                boardArray[3][toYIdx] = movedPieceInt;
                boardArray[0][toYIdx] = capturedPieceInt;
                boardArray[3][toYIdx] = savedPieceNo1;
                boardArray[2][fromYIdx] = savedPieceNo2;
            } else {
                boardArray[7][toYIdx] = movedPieceInt;
                boardArray[0][toYIdx] = capturedPieceInt;
                boardArray[5][toYIdx] = savedPieceNo1;
                boardArray[6][fromYIdx] = savedPieceNo2;
            }
        } else if (moveArray[6] != 0) {
            /* The 7th element in a moveArray is only nonzero if the move is a
               pawn being promoted, so this is a pawn promotion. */

            /* A check for if this move would put the king in check. Those
               aren't explored any further, and the worst possible value is
               returned. */
            if (BoardArrays.wouldKingBeInCheck(boardArray, fromXIdx, fromYIdx, toXIdx, toYIdx,
                                               colorsTurnItIs, colorOnTop)) {
                return maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }

            /* The move is executed, with the destination square set to the new
               piece. The original piece is saved. */
            int promotedFromPieceInt = boardArray[fromXIdx][fromYIdx];
            savedPieceNo1 = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[6];
            boardArray[fromXIdx][fromYIdx] = 0;

            /* The actual call to algorithmLowerLevel(). */
            retval = algorithmLowerLevel(boardArray, maximize, depth, colorsTurnItIs, alpha, beta);

            /* The move is reversed and the board is returned to the state it
               was in when this method was called. */
            boardArray[fromXIdx][fromYIdx] = promotedFromPieceInt;
            boardArray[toXIdx][toYIdx] = savedPieceNo1;
        } else {
            /* This is a normal move. */

            /* A check for if this move would put the king in check. Those
               aren't explored any further, and the worst possible value is
               returned. */
            if (BoardArrays.wouldKingBeInCheck(boardArray, fromXIdx, fromYIdx, toXIdx, toYIdx, colorsTurnItIs,
                                               colorOnTop)) {
                return maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }

            /* The move is executed, and the original piece at the destination
               square is saved. */
            savedPieceNo1 = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[0];
            boardArray[fromXIdx][fromYIdx] = 0;

            /* The actual call to algorithmLowerLevel(). */
            retval = algorithmLowerLevel(boardArray, maximize, depth, colorsTurnItIs, alpha, beta);

            /* The move is reversed and the board is returned to the state it
               was in when this method was called. */
            boardArray[fromXIdx][fromYIdx] = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = savedPieceNo1;
        }

        /* The score returned by algorithmLowerLevel() is passed up. */
        return retval;
    }

    /*
     * This method (and its delegate methods) implements an algorithm to
     * evaluate the desirability of a board that was authored by early computer
     * programmer Claude Shannon in 1949, in his paper _Programming a Computer
     * for playing Chess_.
     *
     * @param boardArray     The int[8][8] array used to represent the chessboard.
     * @param colorsTurnItIs An integer indicating which color the AI is
     *                       playing (either BoardArrays.WHITE or BoardArrays.BLACK).
     */
    private double evaluateBoard(final int[][] boardArray, final int colorsTurnItIs) {
        /* This statement derives from the boardArray a string value that is
           guaranteed to be unique for that board configuration, so that this
           method's memoization HashMap evaluateBoardMemoizeMap can store
           the board's score with that key. The format() statement creates a
           200-character hexadecimal string that comprises each element of
           boardArray, in order, in hex.

           Doing it as one big call to String.format avoids the overhead of
           having to instance a StringJoiner object, and use 2 for loops with 64
           calls to StringJoiner.add() to populate it. It's ugly but definitely
           faster.  */
        String boardStr = String.format("%03x%03x%03x%03x%03x%03x%03x%03x\n%03x%03x%03x%03x%03x%03x%03x%03x\n%03x%03x%03x"
            + "%03x%03x%03x%03x%03x\n%03x%03x%03x%03x%03x%03x%03x%03x\n%03x%03x%03x%03x%03x%03x%03x%03x\n%03x%03x%03x%03x"
            + "%03x%03x%03x%03x\n%03x%03x%03x%03x%03x%03x%03x%03x\n%03x%03x%03x%03x%03x%03x%03x%03x",
            boardArray[0][0], boardArray[0][1], boardArray[0][2], boardArray[0][3], boardArray[0][4], boardArray[0][5],
            boardArray[0][6], boardArray[0][7], boardArray[1][0], boardArray[1][1], boardArray[1][2], boardArray[1][3],
            boardArray[1][4], boardArray[1][5], boardArray[1][6], boardArray[1][7], boardArray[2][0], boardArray[2][1],
            boardArray[2][2], boardArray[2][3], boardArray[2][4], boardArray[2][5], boardArray[2][6], boardArray[2][7],
            boardArray[3][0], boardArray[3][1], boardArray[3][2], boardArray[3][3], boardArray[3][4], boardArray[3][5],
            boardArray[3][6], boardArray[3][7], boardArray[4][0], boardArray[4][1], boardArray[4][2], boardArray[4][3],
            boardArray[4][4], boardArray[4][5], boardArray[4][6], boardArray[4][7], boardArray[5][0], boardArray[5][1],
            boardArray[5][2], boardArray[5][3], boardArray[5][4], boardArray[5][5], boardArray[5][6], boardArray[5][7],
            boardArray[6][0], boardArray[6][1], boardArray[6][2], boardArray[6][3], boardArray[6][4], boardArray[6][5],
            boardArray[6][6], boardArray[6][7], boardArray[7][0], boardArray[7][1], boardArray[7][2], boardArray[7][3],
            boardArray[7][4], boardArray[7][5], boardArray[7][6], boardArray[7][7]);

        /* The boardStr value is used to memoize the return values of this
           method to evaluateBoardMemoizeMap. */
        if (evaluateBoardMemoizeMap.containsKey(boardStr)) {
            return evaluateBoardMemoizeMap.get(boardStr);
        }

        int otherColor = colorsTurnItIs == WHITE ? BLACK : WHITE;
        int whiteIndex = 0;
        int blackIndex = 1;
        int kingIndex = 0;
        int queenIndex = 1;
        int rookIndex = 2;
        int bishopIndex = 3;
        int knightIndex = 4;
        int pawnIndex = 5;

        int thisColorIndex = colorsTurnItIs == WHITE ? whiteIndex : blackIndex;
        int otherColorIndex = colorsTurnItIs == WHITE ? blackIndex : whiteIndex;

        double[][] piecesCounts = new double[2][6];

        /* A highly-valued component of the calculation is whether a side's king
           is in check or not, so that's computed for both sides. */
        int whiteKingNotInCheckBonus = BoardArrays.isKingInCheck(boardArray, WHITE, colorOnTop) ? 0 : 1;
        int blackKingNotInCheckBonus = BoardArrays.isKingInCheck(boardArray, BLACK, colorOnTop) ? 0 : 1;

        /* Switch statement's case values must be constants at compile time, so
           the piece integer values are used, and they're all stored to an array
           by key indexes, where they can be recovered from later. */
        for (int[] boardRow : boardArray) {
            for (int pieceInt : boardRow) {
                switch (pieceInt) {
                    case WHITE | KING:           piecesCounts[whiteIndex][kingIndex] = whiteKingNotInCheckBonus;
                    case WHITE | QUEEN:          piecesCounts[whiteIndex][queenIndex]++; break;
                    case WHITE | ROOK:           piecesCounts[whiteIndex][rookIndex]++; break;
                    case WHITE | BISHOP:         piecesCounts[whiteIndex][bishopIndex]++; break;
                    case WHITE | KNIGHT | RIGHT: case WHITE | KNIGHT | LEFT:
                                                 piecesCounts[whiteIndex][knightIndex]++; break;
                    case WHITE | PAWN:           piecesCounts[whiteIndex][pawnIndex]++; break;
                    case BLACK | KING:           piecesCounts[blackIndex][kingIndex] = blackKingNotInCheckBonus;
                    case BLACK | QUEEN:          piecesCounts[blackIndex][queenIndex]++; break;
                    case BLACK | ROOK:           piecesCounts[blackIndex][rookIndex]++; break;
                    case BLACK | BISHOP:         piecesCounts[blackIndex][bishopIndex]++; break;
                    case BLACK | KNIGHT | RIGHT: case BLACK | KNIGHT | LEFT:
                                                 piecesCounts[blackIndex][knightIndex]++; break;
                    case BLACK | PAWN:           piecesCounts[blackIndex][pawnIndex]++; break;
                    default: break;
                }
            }
        }

        /* Three special values are calculated for the pawns in play (see
           tallySpecialPawns() for more info) by this method and returned as a
           double[3] array. */
        double[] thisColorSpecialPawnsTallies = tallySpecialPawns(boardArray, colorsTurnItIs);
        double[] otherColorSpecialPawnsTallies = tallySpecialPawns(boardArray, otherColor);

        double thisColorSpecialPawnScore = (-thisColorSpecialPawnsTallies[ISOLATED]
                                            - thisColorSpecialPawnsTallies[BLOCKED]
                                            - thisColorSpecialPawnsTallies[DOUBLED]);
        double otherColorSpecialPawnScore = (-otherColorSpecialPawnsTallies[ISOLATED]
                                             - otherColorSpecialPawnsTallies[BLOCKED]
                                             - otherColorSpecialPawnsTallies[DOUBLED]);

        /* Since the special pawn score is a penalty-- a negative number--
           then the following computation will work out to a positive
           increment to the score if abs(otherColorSpecialPawnScore) >
           abs(thisColorSpecialPawnScore). */
        double specialPawnScore = 0.5F * (thisColorSpecialPawnScore - otherColorSpecialPawnScore);

        /* Mobility is the total number of moves available to that color. */
        double thisMobility = totalColorMobility(boardArray, colorsTurnItIs);
        double otherMobility = totalColorMobility(boardArray, otherColor);
        double mobilityScore = 0.1F * (thisMobility - otherMobility);

        /* The weighting assigned to whether one side's king is in check
           outshines every other value in this calculation by a wide margin. If
           a move would put the player's side's king in check, that move will be
           weighted far above every other possible move. */
        double kingScore = 200F * (piecesCounts[thisColorIndex][kingIndex]
                                   - piecesCounts[otherColorIndex][kingIndex]);

        /* These weighted scores are calculated from the difference between the
           number of pieces in play for each color. */
        double queenScore = 9F * (piecesCounts[thisColorIndex][queenIndex]
                                  - piecesCounts[otherColorIndex][queenIndex]);
        double rookScore = 5F * (piecesCounts[thisColorIndex][rookIndex]
                                 - piecesCounts[otherColorIndex][rookIndex]);
        double bishopScore = 3F * (piecesCounts[thisColorIndex][bishopIndex]
                                   - piecesCounts[otherColorIndex][bishopIndex]);
        double knightScore = 3F * (piecesCounts[thisColorIndex][knightIndex]
                                   - piecesCounts[otherColorIndex][knightIndex]);
        double generalPawnScore = (piecesCounts[thisColorIndex][pawnIndex]
                                   - piecesCounts[otherColorIndex][pawnIndex]);

        double totalScore = (kingScore + queenScore + rookScore + bishopScore
                            + knightScore + generalPawnScore + specialPawnScore
                            + mobilityScore);
        evaluateBoardMemoizeMap.put(boardStr, totalScore);
        return totalScore;
    }

    /*
     * This is a utility method that handles some logic needed by
     * evaluateBoard(). It reviews the positions of all the friendly pawns on the
     * board, noting pawns which are blocked (a piece of either side that isn't
     * a friendly pawn occupies the square ahead of them), doubled (two pawns in
     * a row), or isolated (no pawns in the files to either side). It returns
     * those values in a double[3] array.
     *
     * @param boardArray      The int[8][8] board representation to count pawns in.
     * @param colorInQuestion The color of pawns to count.
     * @param colorOnTop      Which color is playing from the top the board (the
     *                        lowest y values).
     * @return                A double[3] array of doubledPawnsCount,
     *                        blockedPawnsCount, and isolatedPawnsCount.
     * @see #evaluateBoard
     */
    private double[] tallySpecialPawns(final int[][] boardArray, final int colorInQuestion) {
        int[][] tallyPawnsCoords = new int[8][2];
        int[][] doubledPawnsCoords = new int[8][2];
        double[] retval = new double[3];
        double blockedPawnsCount = 0;
        double doubledPawnsCount = 0;
        double isolatedPawnsCount = 0;
        int dblpIdx = 0;
        int pawnsCount = 0;
        int maxPawnIndex;

        /* These loops traverse the int[8][8] boardArray detecting pawns of this
           color and saving their coordinates to int[8][2] so that the rest
           of the loops in this method can just iterate over the saved pawns
           coordinates. */
        for (int xIdx = 0, pawnIndex = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (colorInQuestion | PAWN)) {
                    tallyPawnsCoords[pawnIndex][0] = xIdx;
                    tallyPawnsCoords[pawnIndex][1] = yIdx;
                    pawnIndex++;
                    pawnsCount++;
                }
            }
        }

        maxPawnIndex = pawnsCount - 1;

        /* Normally an isolated pawn is one where its neighboring pawns are each
           more than one file away from them. */
        switch (pawnsCount) {
            /* But if there's only 1 pawn left it is by default isolated. */
            case 1 -> isolatedPawnsCount++;
            case 2 -> {
                /* If there's two left, they're both isolated if their X indexes
                   differ by more than 1. */
                if (tallyPawnsCoords[1][0] - tallyPawnsCoords[0][0] > 1) {
                    isolatedPawnsCount += 2;
                }
            }
            default -> {
                for (int pawnIndex = 0; pawnIndex < pawnsCount; pawnIndex++) {
                    int thisPawnXIdx = tallyPawnsCoords[pawnIndex][0];

                    /* If a pawn is the leftmost on the board, it's isolated
                       if the difference between its X index and its right
                       neighbor's X index is more than 1. */
                    if (pawnIndex == 0) {
                        int nextPawnXIdx = tallyPawnsCoords[pawnIndex + 1][0];
                        if (nextPawnXIdx - thisPawnXIdx > 1) {
                            isolatedPawnsCount++;
                        }
                    /* Vice versa, if it's rightmost on the board, it's isolated
                       if its left neighbor's X index exceeds its X index by
                       more than 1. */
                    } else if (pawnIndex == maxPawnIndex) {
                        int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][0];
                        if (prevPawnXIdx - thisPawnXIdx > 1) {
                            isolatedPawnsCount++;
                        }
                    /* Otherwise, it's isolated if the difference between its x
                       index and the previous pawn's x index, and the difference
                       between the next pawn's x index and its x index, are both
                       more than 1. */
                    } else { // 0 < pawnIndex && pawnIndex < maxPawnIndex
                        int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][0];
                        int nextPawnXIdx = tallyPawnsCoords[pawnIndex + 1][0];
                        if (nextPawnXIdx - thisPawnXIdx > 1 && thisPawnXIdx - prevPawnXIdx > 1) {
                            isolatedPawnsCount++;
                        }
                    }
                }
            }
        }

        /* This loop counts doubled pawns and blocked pawns. It tracks pawns
           that are doubled in doubledPawnsCoords, so it can avoid
           double-counting any. */
        for (int pawnIndex = 0; pawnIndex < pawnsCount; pawnIndex++) {
            int thisPawnXIdx = tallyPawnsCoords[pawnIndex][0];
            int thisPawnYIdx = tallyPawnsCoords[pawnIndex][1];
            if (pawnIndex > 0) {
                int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][0];
                int prevPawnYIdx = tallyPawnsCoords[pawnIndex - 1][1];

                /* The y values in this loop's calculations run two different
                   ways depending on whether colorInQuestion is playing from
                   the top of the board (== instance var colorOnTop) or from
                   the bottom (!= colorOnTop). If on top, yDiff is set to -1,
                   otherwise +1. A successive piece's y index is calculated by
                   adding yDiff to the earlier piece's y index. */
                int yDiff = colorInQuestion == colorOnTop ? -1 : +1;

                /* Two successive pawns are doubled if their x indexes are equal
                   and the difference between the first y index and the second
                   equals yDiff (-1 if playing from the top of the board, +1 if
                   from the bottom). */
                if (thisPawnXIdx == prevPawnXIdx && thisPawnYIdx - prevPawnYIdx == yDiff) {

                    /* Tripled pawns are a possibility. If two pawns'
                       coordinates have already been stored to doubledPawnsCoord
                       (ie. dblpIdx > 1), its last coords are checked for
                       equality with the first doubled pawn. If they match, only
                       the second doubled pawn is counted and stored. Otherwise,
                       both are counted and stored. */
                    if (dblpIdx <= 1 || doubledPawnsCoords[dblpIdx - 1][0] != prevPawnXIdx
                            || doubledPawnsCoords[dblpIdx - 1][1] != prevPawnYIdx) {
                        doubledPawnsCoords[dblpIdx][0] = prevPawnXIdx;
                        doubledPawnsCoords[dblpIdx][1] = prevPawnYIdx;
                        dblpIdx++;
                        doubledPawnsCount++;

                    }
                    doubledPawnsCoords[dblpIdx][0] = thisPawnXIdx;
                    doubledPawnsCoords[dblpIdx][1] = thisPawnYIdx;
                    dblpIdx++;
                    doubledPawnsCount++;
                }

                int nextSquareXIdx = thisPawnXIdx;
                int nextSquareYIdx = thisPawnYIdx + yDiff;
                int nextSquarePieceInt = boardArray[nextSquareXIdx][nextSquareYIdx];
                /* If the square ahead of this square is occupied, and it's
                   not a pawn on this side, then this pawn is blocked and
                   blockedPawnsCount is incremented. */
                if (nextSquarePieceInt != 0 && (nextSquarePieceInt ^ colorInQuestion) != PAWN) {
                    blockedPawnsCount++;
                }
            }
        }

        /* This method has 3 doubles to return, so they're packed into a double[3]
           array and that's the return value. */
        retval[DOUBLED] = doubledPawnsCount;
        retval[BLOCKED] = blockedPawnsCount;
        retval[ISOLATED] = isolatedPawnsCount;
        return retval;
    }

    /*
     * This method is subordinate to evaluateBoard. It calculates the number
     * of moves possible for every friendly piece on the board and returns the
     * total number of moves (which is equal to the index of the first empty
     * array in an int[][7] movesArray).
     *
     * @param boardArray     The board array to calculate moves on.
     * @param colorsTurnItIs An integer representing the color to calculate
     *                       moves for (either BoardArrays.WHITE or
     *                       BoardArrays.BLACK).
     * @return               A double, the total number of moves possible.
     * @see #evaluateBoard
     */
    private double totalColorMobility(final int[][] boardArray, final int colorsTurnItIs) {
        int[][] colorMobilitySpareMovesArray = new int[128][7];
        int moveIdx = 0;

        /* Iterating across the board, stopping when a friendly
           piece is encountered. For each friendly piece,
           BoardArrays.generatePieceMoves() is called, and the new value for
           moveIdx is set to its return value. */
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if ((boardArray[xIdx][yIdx] & colorsTurnItIs) == 0) {
                    continue;
                }
                moveIdx = BoardArrays.generatePieceMoves(boardArray, colorMobilitySpareMovesArray, moveIdx, xIdx, yIdx,
                                                         colorsTurnItIs, colorOnTop);
            }
        }

        /* The index of the first empty array in the int[][7] movesArray is also
           the length of the used arrays, so that is returned. It's returned as
           a double because the calculations in evaluateBoard() are all done in
           doubles. */
        return moveIdx;
    }
}
