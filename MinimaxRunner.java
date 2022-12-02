package com.kmfahey.jchessgame;

import java.util.Arrays;
import java.util.HashMap;

public class MinimaxRunner {

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

    public static final int DOUBLED = 0;
    public static final int ISOLATED = 1;
    public static final int BLOCKED = 2;

    /* These are alternate versions of main arrays in the int-array-based board
       logic that are used by some methods. In order to avoid instantiating a
       new array each time one is called, a spare array is stored to an instance
       variable and reused each time that method is called. */
    private int[][] kingsMovesSpareBoardArray;
    private int[][] tallyPawnsCoords;
    private int[][] colorMobilitySpareMovesArray;

    private HashMap<String, Double> evaluateBoardMemoizeMap;

    private int colorOfAI;
    private int colorOfPlayer;
    private int colorOnTop;
    private int algorithmStartingDepth;

    private boolean noAlphaBeta = true;

    private Chessboard chessboard;

    public MinimaxRunner(final Chessboard chessboardObj, final int aiColor) {
        chessboard = chessboardObj;
        colorOfAI = aiColor;
        colorOfPlayer = colorOfAI == WHITE ? BLACK : WHITE;
        colorOnTop = chessboard.getColorOnTop();
        kingsMovesSpareBoardArray = new int[8][8];
        tallyPawnsCoords = new int[8][2];
        colorMobilitySpareMovesArray = new int[128][7];
        algorithmStartingDepth = 4;
        evaluateBoardMemoizeMap = new HashMap<String, Double>();
    }

    public Chessboard.Move algorithmTopLevel(final int turnCount
                                            ) throws CastlingNotPossibleException, IllegalArgumentException {
        int[] bestMoveArray = null;
        int[][] boardArray = new int[8][8];
        int capturedPieceInt;
        int promotedToPieceInt;
        int fromXIdx;
        int fromYIdx;
        int movedPieceInt;
        int[][] movesArray = new int[128][7];
        int movesArrayUsedLength;
        int toXIdx;
        int toYIdx;
        int useableMovesCount;
        double thisScore;
        Chessboard.Move bestMoveObj;
        boolean isCastlingKingside;
        boolean isCastlingQueenside;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double bestScore = Double.NEGATIVE_INFINITY;

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

        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            try {
                thisScore = algorithmCallExecutor(boardArray, true, movesArray[moveIdx], (colorOfAI == WHITE ? BLACK : WHITE),
                                                  algorithmStartingDepth, alpha, beta);
            } catch (KingIsInCheckException exception) {
                useableMovesCount--;
                continue;
            }
            if (thisScore >= bestScore) {
                bestScore = thisScore;
                bestMoveArray = movesArray[moveIdx];
            }
            if (thisScore > alpha) {
                alpha = thisScore;
            }
        }

        if (useableMovesCount == 0) {
            throw new IllegalStateException("out of " + useableMovesCount + " generated moves, all were eliminated due to KingIsInCheckException exceptions");
        }

        movedPieceInt = bestMoveArray[0];
        fromXIdx = bestMoveArray[1];
        fromYIdx = bestMoveArray[2];
        toXIdx = bestMoveArray[3];
        toYIdx = bestMoveArray[4];
        isCastlingKingside = false;
        isCastlingQueenside = false;
        capturedPieceInt = bestMoveArray[5];
        promotedToPieceInt = bestMoveArray[6];

        if ((movedPieceInt & ROOK) != 0 && (capturedPieceInt & KING) != 0
            && (movedPieceInt & WHITE) == (capturedPieceInt & WHITE)) {

            if (toXIdx == 0) {
                isCastlingKingside = true;
            } else {
                isCastlingQueenside = true;
            }
        }

        bestMoveObj = new Chessboard.Move(chessboard.getPieceAtCoords(fromXIdx, fromYIdx), fromXIdx, fromYIdx,
                                          toXIdx, toYIdx, capturedPieceInt, isCastlingKingside, isCastlingQueenside,
                                          promotedToPieceInt);

        return bestMoveObj;
    }

    private double algorithmLowerLevel(final int[][] boardArray, final boolean maximize, final int depth,
                                       final int colorsTurnItIs, final double alphaArg, final double betaArg
                                       ) throws CastlingNotPossibleException, IllegalArgumentException {
        double bestScore;
        double thisScore;
        double alpha = alphaArg;
        double beta = betaArg;
        int[][] movesArray = new int[128][7];
        int movesArrayUsedLength;

        if (depth == 0) {
            double score = evaluateBoard(boardArray, colorsTurnItIs);
            return score;
        }

        bestScore = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

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
            try {
                thisScore = algorithmCallExecutor(boardArray, !maximize, movesArray[moveIdx],
                                                  (colorsTurnItIs == WHITE ? BLACK : WHITE), depth, alpha, beta);
            } catch (KingIsInCheckException exception) {
                continue;
            }
            if (maximize && thisScore == Double.POSITIVE_INFINITY || !maximize && thisScore == Double.NEGATIVE_INFINITY) {
                return thisScore;
            }
            if (maximize && thisScore > alpha) {
                alpha = thisScore;
            } else if (!maximize && thisScore < beta) {
                beta = thisScore;
            }
            if (maximize && thisScore >= beta || !maximize && thisScore <= alpha) {
                return thisScore;
            }
            if (maximize && thisScore > bestScore || !maximize && thisScore < bestScore) {
                bestScore = thisScore;
            }
        }

        return bestScore;
    }

    private double algorithmCallExecutor(final int[][] boardArray, final boolean maximize,
                                         final int[] moveArray, final int colorsTurnItIs,
                                         final int depth, final double alpha, final double beta
                                         ) throws IllegalArgumentException, KingIsInCheckException,
                                                  CastlingNotPossibleException {
        int colorOpposing = (colorsTurnItIs == WHITE ? BLACK : WHITE);
        int movedPieceInt = moveArray[0];
        int fromXIdx = moveArray[1];
        int fromYIdx = moveArray[2];
        int toXIdx = moveArray[3];
        int toYIdx = moveArray[4];
        int capturedPieceInt = moveArray[5];
        int savedPieceNo1 = 0;
        int savedPieceNo2 = 0;
        boolean isCastlingKingside = false;
        boolean isCastlingQueenside = false;
        double retval;
        String thisColorStr = colorsTurnItIs == WHITE ? "black" : "white";
        Chessboard.Move moveObj;

        /* The same boardArray is passed down the call stack and reused by
           every step of the algorithm, to avoid having to clone it each time.
           That means I need to execute this moveArray's move on the board,
           execute the recursive call, and then undo the move so the board can
           be reused. savedPiece holds whatever was at the square the piece
           was moved to so it can be restored. */

        if ((movedPieceInt & ROOK) != 0 && (capturedPieceInt & KING) != 0
            && (movedPieceInt & WHITE) == (capturedPieceInt & WHITE)) {
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
            chessboard.movePiece(moveObj);

            retval = algorithmLowerLevel(boardArray, maximize, depth - 1, colorsTurnItIs, alpha, beta);

            if (isCastlingKingside) {
                movedPieceInt = boardArray[3][toYIdx];
                capturedPieceInt = boardArray[2][fromYIdx];
                boardArray[3][toYIdx] = savedPieceNo1;
                boardArray[2][fromYIdx] = savedPieceNo2;
            } else {
                movedPieceInt = boardArray[5][toYIdx];
                capturedPieceInt = boardArray[6][fromYIdx];
                boardArray[5][toYIdx] = savedPieceNo1;
                boardArray[6][fromYIdx] = savedPieceNo2;
            }
        } else if (moveArray[6] != 0) {
            /* The 7th element in a moveArray is only nonzero if the move is a
               pawn being promoted. */
            if (BoardArrays.wouldKingBeInCheck(boardArray, fromXIdx, fromYIdx, toXIdx, toYIdx, colorsTurnItIs, colorOnTop)) {
                throw new KingIsInCheckException("Move would place " + thisColorStr + "'s king in check or "
                                                 + thisColorStr + "'s King is in check and this move doesn't fix that. "
                                                 + "Move can't be made.");
            }

            int promotedFromPieceInt = boardArray[fromXIdx][fromYIdx];
            savedPieceNo1 = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[6];
            boardArray[fromXIdx][fromYIdx] = 0;

            retval = algorithmLowerLevel(boardArray, maximize, depth - 1, colorsTurnItIs, alpha, beta);

            boardArray[fromXIdx][fromYIdx] = promotedFromPieceInt;
            boardArray[toXIdx][toYIdx] = savedPieceNo1;
        } else {
            if (BoardArrays.wouldKingBeInCheck(boardArray, fromXIdx, fromYIdx, toXIdx, toYIdx, colorsTurnItIs, colorOnTop)) {
                throw new KingIsInCheckException("Move would place " + thisColorStr + "'s king in check or "
                                                 + thisColorStr + "'s King is in check and this move doesn't fix that. "
                                                 + "Move can't be made.");
            }

            savedPieceNo1 = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[0];
            boardArray[fromXIdx][fromYIdx] = 0;

            retval = algorithmLowerLevel(boardArray, maximize, depth - 1, colorsTurnItIs, alpha, beta);

            boardArray[fromXIdx][fromYIdx] = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = savedPieceNo1;
        }

        return retval;
    }

    private void setMoveToMovesArray(final int[][] movesArray, final int moveIdx, final int pieceInt,
                                     final int startXIdx, final int startYIdx, final int endXIdx, final int endYIdx,
                                     final int capturedPiece) throws IllegalArgumentException {
        if (movesArray[moveIdx][0] != 0 || movesArray[moveIdx][1] != 0 || movesArray[moveIdx][2] != 0
            || movesArray[moveIdx][3] != 0 || movesArray[moveIdx][4] != 0 || movesArray[moveIdx][5] != 0) {
            throw new IllegalArgumentException("setMoveToMovesArray() called with moveIdx arg pointing to "
                                                    + "non-zero entry in movesArray argument");
        }
        movesArray[moveIdx][0] = pieceInt;
        movesArray[moveIdx][1] = startXIdx;
        movesArray[moveIdx][2] = startYIdx;
        movesArray[moveIdx][3] = endXIdx;
        movesArray[moveIdx][4] = endYIdx;
        movesArray[moveIdx][5] = capturedPiece;
    }

    private double evaluateBoard(final int[][] boardArray, final int colorsTurnItIs
                                ) throws IllegalArgumentException {
        /* This statement derives from the boardArray a string value that is
           guaranteed to be unique for that board configuraion, so that this
           method's memoization HashMap evaluateBoardMemoizeMap can store
           the board's score with that key. The format() statement creates a
           128-character hexedecimal string that comprises each element of
           boardArray, in order, in hex. The highest value that occurs in
           boardArray is a black king at 0260 (176 in decimal, 0xb0 in hex), so
           only 2 digits are needed for each element.

           Doing it as one big call to String.format avoids the overhead of
           having to instance a StringJoiner object, and use 2 for loops with 64
           calls to StringJoiner.add() to populate it. It's ugly but definitely
           faster, and evaluateBoard get loops and the method calls. It's valid
           Java and undoubtedly faster, so why not. */
        String boardStr = String.format("%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x"
            + "%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x"
            + "%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x%03x"
            + "%03x%03x%03x%03x%03x%03x%03x%03x",
            boardArray[0][0], boardArray[0][1], boardArray[0][2], boardArray[0][3],
            boardArray[0][4], boardArray[0][5], boardArray[0][6], boardArray[0][7],
            boardArray[1][0], boardArray[1][1], boardArray[1][2], boardArray[1][3],
            boardArray[1][4], boardArray[1][5], boardArray[1][6], boardArray[1][7],
            boardArray[2][0], boardArray[2][1], boardArray[2][2], boardArray[2][3],
            boardArray[2][4], boardArray[2][5], boardArray[2][6], boardArray[2][7],
            boardArray[3][0], boardArray[3][1], boardArray[3][2], boardArray[3][3],
            boardArray[3][4], boardArray[3][5], boardArray[3][6], boardArray[3][7],
            boardArray[4][0], boardArray[4][1], boardArray[4][2], boardArray[4][3],
            boardArray[4][4], boardArray[4][5], boardArray[4][6], boardArray[4][7],
            boardArray[5][0], boardArray[5][1], boardArray[5][2], boardArray[5][3],
            boardArray[5][4], boardArray[5][5], boardArray[5][6], boardArray[5][7],
            boardArray[6][0], boardArray[6][1], boardArray[6][2], boardArray[6][3],
            boardArray[6][4], boardArray[6][5], boardArray[6][6], boardArray[6][7],
            boardArray[7][0], boardArray[7][1], boardArray[7][2], boardArray[7][3],
            boardArray[7][4], boardArray[7][5], boardArray[7][6], boardArray[7][7]);
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

        int whiteKingNotInCheckBonus = BoardArrays.isKingInCheck(boardArray, WHITE, colorOnTop) ? 0 : 1;
        int blackKingNotInCheckBonus = BoardArrays.isKingInCheck(boardArray, BLACK, colorOnTop) ? 0 : 1;

        for (int[] boardRow : boardArray) {
            for (int pieceInt : boardRow) {
                switch (pieceInt) {
                    case WHITE | KING:           piecesCounts[whiteIndex][kingIndex] = whiteKingNotInCheckBonus;
                    case WHITE | QUEEN:          piecesCounts[whiteIndex][queenIndex]++; break;
                    case WHITE | ROOK:           piecesCounts[whiteIndex][rookIndex]++; break;
                    case WHITE | BISHOP:         piecesCounts[whiteIndex][bishopIndex]++; break;
                    case WHITE | KNIGHT | RIGHT: piecesCounts[whiteIndex][knightIndex]++; break;
                    case WHITE | KNIGHT | LEFT:  piecesCounts[whiteIndex][knightIndex]++; break;
                    case WHITE | PAWN:           piecesCounts[whiteIndex][pawnIndex]++; break;
                    case BLACK | KING:           piecesCounts[blackIndex][kingIndex] = blackKingNotInCheckBonus;
                    case BLACK | QUEEN:          piecesCounts[blackIndex][queenIndex]++; break;
                    case BLACK | ROOK:           piecesCounts[blackIndex][rookIndex]++; break;
                    case BLACK | BISHOP:         piecesCounts[blackIndex][bishopIndex]++; break;
                    case BLACK | KNIGHT | RIGHT: piecesCounts[blackIndex][knightIndex]++; break;
                    case BLACK | KNIGHT | LEFT:  piecesCounts[blackIndex][knightIndex]++; break;
                    case BLACK | PAWN:           piecesCounts[blackIndex][pawnIndex]++; break;
                    default: break;
                }
            }
        }

        double[] colorOfAISpecialPawnsTallies = tallySpecialPawns(boardArray, colorOfAI);
        double[] otherColorSpecialPawnsTallies = tallySpecialPawns(boardArray, otherColor);
        double isolatedPawnDifference = colorOfAISpecialPawnsTallies[ISOLATED]
                                       - otherColorSpecialPawnsTallies[ISOLATED];
        double blockedPawnDifference = colorOfAISpecialPawnsTallies[BLOCKED]
                                      - otherColorSpecialPawnsTallies[BLOCKED];
        double doubledPawnDifference = colorOfAISpecialPawnsTallies[DOUBLED]
                                      - otherColorSpecialPawnsTallies[DOUBLED];
        double specialPawnScore = 0.5F * (isolatedPawnDifference + blockedPawnDifference
                                         + doubledPawnDifference);

        double thisMobility = totalColorMobility(boardArray, colorOfAI);
        double otherMobility = totalColorMobility(boardArray, otherColor);
        double mobilityScore = 0.1F * (thisMobility - otherMobility);

        double kingScore = 200F * (piecesCounts[thisColorIndex][kingIndex] - piecesCounts[otherColorIndex][kingIndex]);
        double queenScore = 9F * (piecesCounts[thisColorIndex][queenIndex] - piecesCounts[otherColorIndex][queenIndex]);
        double rookScore = 5F * (piecesCounts[thisColorIndex][rookIndex] - piecesCounts[otherColorIndex][rookIndex]);
        double bishopAndKnightScore = 3F * ((piecesCounts[thisColorIndex][bishopIndex]
                                               - piecesCounts[otherColorIndex][bishopIndex])
                                           + (piecesCounts[thisColorIndex][knightIndex]
                                               - piecesCounts[otherColorIndex][knightIndex]));
        double generalPawnScore = (piecesCounts[thisColorIndex][pawnIndex] - piecesCounts[otherColorIndex][pawnIndex]);

        double totalScore = (kingScore + queenScore + rookScore + bishopAndKnightScore
                            + generalPawnScore + specialPawnScore + mobilityScore);
        evaluateBoardMemoizeMap.put(boardStr, totalScore);
        return totalScore;
    };

    /* This is a utility method that handles some of the logic needed by
       evaluateBoard(). It reviews the positions of all the friendly pawns on the
       board, noting pawns which are blocked (a piece of either side that isn't
       a friendly pawn occupies the square ahead of them), doubled (two pawns in
       a row), or isolated (no pawns in the files to either side). It returns
       those values in a double[3] array.

       @param boardArray      The int[8][8] board representation to count pawns in.
       @param colorInQuestion The color of pawns to count.
       @param colorOnTop      Which color is playing from the top the board (the
                              lowest y values).
       @return                A double[3] array of doubledPawnsCount,
                              blockedPawnsCount, and isolatedPawnsCount.
       @see evaluateBoard()
       */
    private double[] tallySpecialPawns(final int[][] boardArray, final int colorInQuestion) {
        int[][] doubledPawnsCoords = new int[8][2];
        double[] retval = new double[3];
        double blockedPawnsCount = 0;
        double doubledPawnsCount = 0;
        double isolatedPawnsCount = 0;
        int dblpIdx = 0;
        int pawnsCount = 0;
        int X_IDX = 0;
        int Y_IDX = 1;
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
            case 1 -> {
                /* But if there's only 1 pawn left it is by default isolated. */
                isolatedPawnsCount++;
            }
            case 2 -> {
                /* If there's two left, they're both isolated if their X indexes
                   differ by more than 1. */
                if (tallyPawnsCoords[1][X_IDX] - tallyPawnsCoords[0][X_IDX] > 1) {
                    isolatedPawnsCount += 2;
                }
            }
            default -> {
                for (int pawnIndex = 0; pawnIndex < pawnsCount; pawnIndex++) {
                    int thisPawnXIdx = tallyPawnsCoords[pawnIndex][X_IDX];

                    /* If a pawn is the leftmost on the board, it's isolated
                       if the difference between its X index and its right
                       neighbor's X index is more than 1. */
                    if (pawnIndex == 0) {
                        int nextPawnXIdx = tallyPawnsCoords[pawnIndex + 1][X_IDX];
                        if (nextPawnXIdx - thisPawnXIdx > 1) {
                            isolatedPawnsCount++;
                        }
                    /* Vice versa, if it's rightmost on the board, it's isolated
                       if its left neighbor's X index exceeds its X index by
                       more than 1. */
                    } else if (pawnIndex == maxPawnIndex) {
                        int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][X_IDX];
                        if (prevPawnXIdx - thisPawnXIdx > 1) {
                            isolatedPawnsCount++;
                        }
                    /* Otherwise, it's isolated if the difference between its x
                       index and the previous pawn's x index, and the difference
                       between the next pawn's x index and its x index, are both
                       more than 1. */
                    } else { // 0 < pawnIndex && pawnIndex < maxPawnIndex
                        int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][X_IDX];
                        int nextPawnXIdx = tallyPawnsCoords[pawnIndex + 1][X_IDX];
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
            int thisPawnXIdx = tallyPawnsCoords[pawnIndex][X_IDX];
            int thisPawnYIdx = tallyPawnsCoords[pawnIndex][Y_IDX];
            if (pawnIndex > 0) {
                int prevPawnXIdx = tallyPawnsCoords[pawnIndex - 1][X_IDX];
                int prevPawnYIdx = tallyPawnsCoords[pawnIndex - 1][Y_IDX];

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
                    if (dblpIdx > 1 && doubledPawnsCoords[dblpIdx - 1][X_IDX] == prevPawnXIdx
                                    && doubledPawnsCoords[dblpIdx - 1][Y_IDX] == prevPawnYIdx) {
                        doubledPawnsCoords[dblpIdx][X_IDX] = thisPawnXIdx;
                        doubledPawnsCoords[dblpIdx][Y_IDX] = thisPawnYIdx;
                        dblpIdx++;
                        doubledPawnsCount++;
                    } else {
                        doubledPawnsCoords[dblpIdx][X_IDX] = prevPawnXIdx;
                        doubledPawnsCoords[dblpIdx][Y_IDX] = prevPawnYIdx;
                        dblpIdx++;
                        doubledPawnsCount++;

                        doubledPawnsCoords[dblpIdx][X_IDX] = thisPawnXIdx;
                        doubledPawnsCoords[dblpIdx][Y_IDX] = thisPawnYIdx;
                        dblpIdx++;
                        doubledPawnsCount++;
                    }
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

    private double totalColorMobility(final int[][] boardArray, final int colorsTurnItIs
                                ) throws IllegalArgumentException {
        int moveIdx = 0;
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if ((boardArray[xIdx][yIdx] & colorsTurnItIs) == 0) {
                    continue;
                }
                moveIdx = BoardArrays.generatePieceMoves(boardArray, colorMobilitySpareMovesArray, moveIdx, xIdx, yIdx,
                                             colorsTurnItIs, colorOnTop);
            }
        }

        for (int newMoveIdx = 0; newMoveIdx < moveIdx; newMoveIdx++) {
            for (int moveArrayIdx = 0; moveArrayIdx < colorMobilitySpareMovesArray[newMoveIdx].length; moveArrayIdx++) {
                colorMobilitySpareMovesArray[newMoveIdx][moveArrayIdx] = 0;
            }
        }

        return (double) moveIdx;
    }
}
