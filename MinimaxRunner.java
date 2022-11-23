package com.kmfahey.jchessgame;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.BiFunction;

public class MinimaxRunner {

    public static final int WHITE = 0100;
    public static final int BLACK = 0200;

    public static final int PAWN = 010;
    public static final int ROOK = 020;
    public static final int KNIGHT = 030;
    public static final int BISHOP = 040;
    public static final int QUEEN = 050;
    public static final int KING = 060;

    public static final int LEFT = 01;
    public static final int RIGHT = 02;

    public static final int DOUBLED = 0;
    public static final int ISOLATED = 1;
    public static final int BLOCKED = 2;

    private final HashMap<String, Integer> pieceIdentitiesToInts = new HashMap<>() {{
        this.put("white-king", (WHITE | KING));
        this.put("white-queen", (WHITE | QUEEN));
        this.put("white-rook", (WHITE | ROOK));
        this.put("white-bishop", (WHITE | BISHOP));
        this.put("white-knight-right", (WHITE | KNIGHT | RIGHT));
        this.put("white-knight-left", (WHITE | KNIGHT | LEFT));
        this.put("white-pawn", (WHITE | PAWN));
        this.put("black-king", (BLACK | KING));
        this.put("black-queen", (BLACK | QUEEN));
        this.put("black-rook", (BLACK | ROOK));
        this.put("black-bishop", (BLACK | BISHOP));
        this.put("black-knight-right", (BLACK | KNIGHT | RIGHT));
        this.put("black-knight-left", (BLACK | KNIGHT | LEFT));
        this.put("black-pawn", (BLACK | PAWN));
    }};

    private final HashMap<Integer, String> pieceIntsToIdentities = new HashMap<>() {{
        this.put((WHITE | KING), "white-king");
        this.put((WHITE | QUEEN), "white-queen");
        this.put((WHITE | ROOK), "white-rook");
        this.put((WHITE | BISHOP), "white-bishop");
        this.put((WHITE | KNIGHT | RIGHT), "white-knight-right");
        this.put((WHITE | KNIGHT | LEFT), "white-knight-left");
        this.put((WHITE | PAWN), "white-pawn");
        this.put((BLACK | KING), "black-king");
        this.put((BLACK | QUEEN), "black-queen");
        this.put((BLACK | ROOK), "black-rook");
        this.put((BLACK | BISHOP), "black-bishop");
        this.put((BLACK | KNIGHT | RIGHT), "black-knight-right");
        this.put((BLACK | KNIGHT | LEFT), "black-knight-left");
        this.put((BLACK | PAWN), "black-pawn");
    }};

    private int[][] kingsMovesSpareBoardArray;    /* These are alternate versions of main arrays in the                 */
    private int[][] kingsMovesSpareMovesArray;    /* int-array-based board logic that are used by some methods. In      */
    private int[][] tallyPawnsArray;              /* order to avoid instantiating a new array each time one is          */
    private int[][] colorMobilitySpareMovesArray; /* called, a spare array is stored to an instance variable and reused */
    private int[][] algorithmSpareMovesArray;     /* each time that method is called.                                   */

    private int colorPlaying;
    private int colorOpposing;
    private int colorOnTop;
    private int algorithmStartingDepth;

    public record Move(Piece movingPiece, String currentLocation, String moveToLocation) { };

    public MinimaxRunner(final String colorPlayingStr, final String colorOnTopStr) {
        colorPlaying = colorPlayingStr.equals("white") ? WHITE : BLACK;
        colorOpposing = colorPlaying == WHITE ? BLACK : WHITE;
        colorOnTop = colorOnTopStr.equals("white") ? WHITE : BLACK;
        kingsMovesSpareBoardArray = new int[8][8];
        kingsMovesSpareMovesArray = new int[16][10];
        tallyPawnsArray = new int[8][2];
        colorMobilitySpareMovesArray = new int[128][10];
        algorithmSpareMovesArray = new int[8][10];
        algorithmStartingDepth = 4;
    }

    private void surveyKingsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                  final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7 && (boardArray[xIdx + 1][yIdx] & otherColor) != 0) {
            threatenedCount++;
            if (yIdx < 7 && (boardArray[xIdx + 1][yIdx + 1] & otherColor) != 0) {
                threatenedCount++;
            }
            if (yIdx > 0 && (boardArray[xIdx + 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
        }
        if (yIdx < 7 && (boardArray[xIdx][yIdx + 1] & otherColor) != 0) {
            threatenedCount++;
        }
        if (yIdx > 0 && (boardArray[xIdx + 1][yIdx - 1] & otherColor) != 0) {
            threatenedCount++;
        }
        if (xIdx > 0 && (boardArray[xIdx - 1][yIdx] & otherColor) != 0) {
            threatenedCount++;
            if (yIdx < 7 && (boardArray[xIdx + 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
            if (yIdx > 0 && (boardArray[xIdx - 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyQueensMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                   final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyQueensMoveThreatenedSquares(boardArray, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyQueensMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                   final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig,
                                                   final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8; xIdxMod++) {
                if (boardArray[xIdxMod][yIdx] != 0) {
                    if ((boardArray[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdx], xIdxMod, yIdx);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdx == yIdxOrig)) {
                        break;
                    }
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8; xIdxMod++, yIdxMod++) {
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                                if (checkOnly) {
                                    return;
                                }
                            }
                            threatenedCount++;
                        }
                        if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                            break;
                        }
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0; xIdxMod++, yIdxMod--) {
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                                if (checkOnly) {
                                    return;
                                }
                            }
                            threatenedCount++;
                        }
                        if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                            break;
                        }
                    }
                }
            }
        }
        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8; yIdxMod++) {
                if (boardArray[xIdx][yIdxMod] != 0) {
                    if ((boardArray[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdx][yIdxMod], xIdx, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdx == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                if (boardArray[xIdx][yIdxMod] != 0) {
                    if ((boardArray[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdx][yIdxMod], xIdx, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdx == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
                if (boardArray[xIdxMod][yIdx] != 0) {
                    if ((boardArray[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdx], xIdxMod, yIdx);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdx == yIdxOrig)) {
                        break;
                    }
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1; xIdxMod >= 0 && yIdxMod < 8; xIdxMod--, yIdxMod++) {
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                                if (checkOnly) {
                                    return;
                                }
                            }
                            threatenedCount++;
                        }
                        if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                            break;
                        }
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1; xIdxMod >= 0 && yIdxMod >= 0; xIdxMod--, yIdxMod--) {
                    if (boardArray[xIdxMod][yIdxMod] != 0) {
                        if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                                if (checkOnly) {
                                    return;
                                }
                            }
                            threatenedCount++;
                        }
                        if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                            break;
                        }
                    }
                }
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyBishopsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                    final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyBishopsMoveThreatenedSquares(boardArray, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyBishopsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                    final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig,
                                                    final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7 && yIdx < 7) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8; xIdxMod++, yIdxMod++) {
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx > 0 && yIdx < 7) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1; xIdxMod >= 0 && yIdxMod < 8; xIdxMod--, yIdxMod++) {
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx < 7 && yIdx > 0) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0; xIdxMod++, yIdxMod--) {
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx > 0 && yIdx > 0) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1; xIdxMod >= 0 && yIdxMod >= 0; xIdxMod--, yIdxMod--) {
                if (boardArray[xIdxMod][yIdxMod] != 0) {
                    if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyKnightsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                    final int colorsTurnItIs) {
        surveyKnightsMoveThreatenedSquares(boardArray, moveArray, colorsTurnItIs, false);
    }

    private void surveyKnightsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                    final int colorsTurnItIs, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        for (int xIdxDelta = -2; xIdxDelta <= 2; xIdxDelta++) {
            for (int yIdxDelta = -2; yIdxDelta <= 2; yIdxDelta++) {
                if (Math.abs(xIdxDelta) == Math.abs(yIdxDelta) || xIdxDelta == 0 || yIdxDelta == 0) {
                    continue;
                }

                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;

                if (xIdxMod > 7 || xIdxMod < 0 || yIdxMod > 7 || yIdxMod < 0
                    || (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }

                if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                    if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                        if (checkOnly) {
                            return;
                        }
                    }
                    threatenedCount++;
                }
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyRooksMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                  final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyRooksMoveThreatenedSquares(boardArray, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyRooksMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray,
                                                  final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig,
                                                  final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8; yIdxMod++) {
                if (boardArray[xIdx][yIdxMod] != 0) {
                    if ((boardArray[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdx][yIdxMod], xIdx, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdx == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                if (boardArray[xIdx][yIdxMod] != 0) {
                    if ((boardArray[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdx][yIdxMod], xIdx, yIdxMod);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdx == xIdxOrig && yIdxMod == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8; xIdxMod++) {
                if (boardArray[xIdxMod][yIdx] != 0) {
                    if ((boardArray[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdx], xIdxMod, yIdx);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdx == yIdxOrig)) {
                        break;
                    }
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
                if (boardArray[xIdxMod][yIdx] != 0) {
                    if ((boardArray[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdx], xIdxMod, yIdx);
                            if (checkOnly) {
                                return;
                            }
                        }
                        threatenedCount++;
                    }
                    if (!(xIdxMod == xIdxOrig && yIdx == yIdxOrig)) {
                        break;
                    }
                }
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyPawnsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray, final int colorsTurnItIs) {
        surveyPawnsMoveThreatenedSquares(boardArray, moveArray, colorsTurnItIs, false);
    }

    private void surveyPawnsMoveThreatenedSquares(final int[][] boardArray, final int[] moveArray, final int colorsTurnItIs,
                                                  final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundArray = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int yIdxMod;
        int threatenedCount = 0;

        if (yIdx < 7 && (colorOnTop == WHITE && colorsTurnItIs == WHITE
            || colorOnTop == BLACK && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && (colorOnTop == BLACK && colorsTurnItIs == WHITE
            || colorOnTop == WHITE && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx - 1;
        } else {
            return;
        }

        for (int xIdxMod = xIdx - 1; xIdxMod <= xIdx + 1; xIdxMod += 2) {
            if (xIdxMod < 0 || xIdxMod > 7) {
                continue;
            }

            if ((boardArray[xIdxMod][yIdxMod] & otherColor) != 0) {
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    setMoveArrayKingInCheck(moveArray, boardArray[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
                    if (checkOnly) {
                        return;
                    }
                }
                threatenedCount++;
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void setMoveArrayKingInCheck(final int[] moveArray, final int pieceInt, final int pieceXIdx, final int pieceYIdx) {
        moveArray[6] = pieceInt;
        moveArray[7] = pieceXIdx;
        moveArray[8] = pieceYIdx;
    }

    private boolean isKingThreatened(final int[][] boardAry, final int[][] moveArray, final int kingPieceInt, final int xIdx, final int yIdx, final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        boolean kingIsThreatened = false;
        boardAry[xIdx][yIdx] = kingPieceInt;
        int moveIdx = 0;
        for (int otherXIdx = 0; otherXIdx < 8; otherXIdx++) {
            for (int otherYIdx = 0; otherYIdx < 8; otherYIdx++) {
                int otherPieceInt = boardAry[otherXIdx][otherYIdx];
                if (otherPieceInt == 0 || (otherPieceInt & otherColor) != 0) {
                    continue;
                }
                moveArray[moveIdx][0] = otherPieceInt;
                moveArray[moveIdx][3] = otherXIdx;
                moveArray[moveIdx][4] = otherYIdx;
                switch (otherPieceInt ^ colorsTurnItIs) {
                    case PAWN:
                        surveyPawnsMoveThreatenedSquares(boardAry, moveArray[moveIdx], colorsTurnItIs);
                        break;
                    case ROOK:
                        surveyRooksMoveThreatenedSquares(boardAry, moveArray[moveIdx], colorsTurnItIs, otherXIdx, otherYIdx);
                        break;
                    case KNIGHT | LEFT: case KNIGHT | RIGHT:
                        surveyKnightsMoveThreatenedSquares(boardAry, moveArray[moveIdx], colorsTurnItIs);
                        break;
                    case BISHOP:
                        surveyBishopsMoveThreatenedSquares(boardAry, moveArray[moveIdx], colorsTurnItIs, otherXIdx, otherYIdx);
                        break;
                    case QUEEN:
                        surveyQueensMoveThreatenedSquares(boardAry, moveArray[moveIdx], colorsTurnItIs, otherXIdx, otherYIdx);
                        break;
                    default:
                        break;
                }
                if (moveArray[moveIdx][6] == kingPieceInt) {
                    kingIsThreatened = true;
                    moveArray[moveIdx][6] = 0;
                    moveArray[moveIdx][7] = 0;
                    moveArray[moveIdx][8] = 0;
                    break;
                }
                moveIdx++;
            }
        }
        boardAry[xIdx][yIdx] = 0;
        for (int newMoveIdx = 0; newMoveIdx < moveIdx; newMoveIdx++) {
            moveArray[newMoveIdx][0] = 0;
            moveArray[newMoveIdx][3] = 0;
            moveArray[newMoveIdx][4] = 0;
        }
        return kingIsThreatened;
    }

    private int generateKingsMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                      final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pieceInt;
        int moveIdx = moveIdxArg;
        int otherSidePiecesCount = 0;
        int[][] otherBoardArray = new int[8][8];
        int[][] otherMoveArray = new int[16][10];
        int otherLocsIdx = 0;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | KING)) {
            throw new AlgorithmBadArgumentException("generateKingsMoves() called with coordinates that point to cell on board that's not a king or not the color whose turn it is");
        }

        for (int otherXIdx = 0; otherXIdx < 8; otherXIdx++) {
            for (int otherYIdx = 0; otherYIdx < 8; otherYIdx++) {
                if ((boardAry[otherXIdx][otherYIdx] & otherColor) != 0) {
                    otherMoveArray[otherLocsIdx][0] = boardAry[otherXIdx][otherYIdx];
                    otherMoveArray[otherLocsIdx][3] = otherXIdx;
                    otherMoveArray[otherLocsIdx][4] = otherXIdx;
                }
            }
        }

        for (int otherXIdx = 0; otherXIdx < 8; otherXIdx++) {
            for (int otherYIdx = 0; otherYIdx < 8; otherYIdx++) {
                otherBoardArray[otherXIdx][otherYIdx] = boardAry[otherXIdx][otherYIdx];
            }
        }

        otherBoardArray[xIdx][yIdx] = 0;

        for (int xIdxDelta = -1; xIdxDelta <= 1; xIdxDelta++) {
            for (int yIdxDelta = -1; yIdxDelta <= 1; yIdxDelta++) {
                if (xIdxDelta == 0 && yIdxDelta == 0) {
                    continue;
                }
                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;
                if (xIdxMod < 0 || xIdxMod > 7 || yIdxMod < 0 || yIdxMod > 7 || (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }
                if (isKingThreatened(otherBoardArray, otherMoveArray, pieceInt, xIdxMod, yIdxMod, otherColor)) {
                    continue;
                }
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                moveIdx++;
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyKingsMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs);
        }

        return moveIdx;
    }

    private int generateQueensMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                   final int xIdx, final int yIdx, final int colorsTurnItIs
                                   ) throws AlgorithmBadArgumentException {
        int queenPieceInt;
        int moveIdx = moveIdxArg;

        queenPieceInt = boardArray[xIdx][yIdx];

        if (queenPieceInt != (colorsTurnItIs | QUEEN)) {
            throw new AlgorithmBadArgumentException("generateQueensMoves() called with coordinates that point to cell "
                                                    + "on board that's not a queen or not the color whose turn it is");
        }

        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1;
                xIdxMod < 8 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                xIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx, boardArray[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1;
                     xIdxMod < 8 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod++, yIdxMod++, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0
                     && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod++, yIdxMod--, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }
        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1;
                 yIdxMod < 8 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod, boardArray[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1;
                 yIdxMod >= 0 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod, boardArray[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1;
                 xIdxMod >= 0 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx, boardArray[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1;
                     xIdxMod >= 0 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod--, yIdxMod++, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1;
                     xIdxMod >= 0 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                     xIdxMod--, yIdxMod--, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardArray[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyQueensMoveThreatenedSquares(boardArray, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generateBishopsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs
                                    ) throws AlgorithmBadArgumentException {
        int bishopPieceInt;
        int moveIdx = moveIdxArg;

        bishopPieceInt = boardArray[xIdx][yIdx];

        if (bishopPieceInt != (colorsTurnItIs | BISHOP)) {
            throw new AlgorithmBadArgumentException("generateBishopsMoves() called with coordinates that point to "
                                                    + "cell on board that's not a bishop or not the color whose turn it is");
        }

        if (xIdx < 7 && yIdx < 7) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1;
                xIdxMod < 8 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx < 7) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1;
                xIdxMod >= 0 && yIdxMod < 8 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7 && yIdx > 0) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1;
                xIdxMod < 8 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx > 0) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1;
                xIdxMod >= 0 && yIdxMod >= 0 && (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, bishopPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyBishopsMoveThreatenedSquares(boardArray, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generateKnightsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs
                                    ) throws AlgorithmBadArgumentException {
        int knightPieceInt;
        int moveIdx = moveIdxArg;

        knightPieceInt = boardArray[xIdx][yIdx];

        if (knightPieceInt != (colorsTurnItIs | KNIGHT | LEFT) && knightPieceInt != (colorsTurnItIs | KNIGHT | RIGHT)) {
            throw new AlgorithmBadArgumentException("generateKnightsMoves() called with coordinates that point to "
                                                    + "cell on board that's not a knight or not the color whose turn it is");
        }

        for (int xIdxDelta = -2; xIdxDelta <= 2; xIdxDelta++) {
            for (int yIdxDelta = -2; yIdxDelta <= 2; yIdxDelta++) {
                if (Math.abs(xIdxDelta) == Math.abs(yIdxDelta) || xIdxDelta == 0 || yIdxDelta == 0) {
                    continue;
                }

                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;

                if (xIdxMod > 7 || xIdxMod < 0 || yIdxMod > 7 || yIdxMod < 0
                    || (boardArray[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }

                setMoveToMovesArray(movesArray, moveIdx, knightPieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                    boardArray[xIdxMod][yIdxMod]);
                moveIdx++;
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyKnightsMoveThreatenedSquares(boardArray, movesArray[newMoveIdx], colorsTurnItIs);
        }

        return moveIdx;
    }

    private int generateRooksMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs
                                  ) throws AlgorithmBadArgumentException {
        int rookPieceInt;
        int moveIdx = moveIdxArg;

        rookPieceInt = boardArray[xIdx][yIdx];

        if (rookPieceInt != (colorsTurnItIs | ROOK)) {
            throw new AlgorithmBadArgumentException("generateRooksMoves() called with coordinates that point to cell "
                                                    + "on board that's not a rook or not the color whose turn it is");
        }

        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1;
                 yIdxMod < 8 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod, boardArray[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1;
                 yIdxMod >= 0 && (boardArray[xIdx][yIdxMod] & colorsTurnItIs) == 0;
                 yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod, boardArray[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1;
                 xIdxMod < 8 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx, boardArray[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1;
                 xIdxMod >= 0 && (boardArray[xIdxMod][yIdx] & colorsTurnItIs) == 0;
                 xIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx, boardArray[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyRooksMoveThreatenedSquares(boardArray, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generatePawnsMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs
                                  ) throws AlgorithmBadArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pawnPieceInt;
        int yIdxMod;
        int moveIdx = moveIdxArg;

        pawnPieceInt = boardArray[xIdx][yIdx];

        if (pawnPieceInt != (colorsTurnItIs | PAWN)) {
            throw new AlgorithmBadArgumentException("generatePawnsMoves() called with coordinates that point to cell "
                                                    + "on board that's not a pawn or not the color whose turn it is");
        }

        if (yIdx < 7 && (colorOnTop == WHITE && colorsTurnItIs == WHITE
                         || colorOnTop == BLACK && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && (colorOnTop == WHITE && colorsTurnItIs == BLACK
                                || colorOnTop == BLACK && colorsTurnItIs == WHITE)) {
            yIdxMod = yIdx - 1;
        } else {
            return moveIdx;
        }

        if (boardArray[xIdx][yIdxMod] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx, yIdxMod, 0);
            moveIdx++;
        }
        if (xIdx < 7 && (boardArray[xIdx + 1][yIdxMod] & otherColor) != 0) {
            setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx + 1, yIdxMod,
                                boardArray[xIdx + 1][yIdxMod]);
            moveIdx++;
        }
        if (xIdx > 0 && (boardArray[xIdx - 1][yIdxMod] & otherColor) != 0) {
            setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx - 1, yIdxMod,
                                boardArray[xIdx - 1][yIdxMod]);
            moveIdx++;
        }

        if (yIdx == 6 && boardArray[xIdx][yIdx - 2] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx, yIdx - 2, 0);
            moveIdx++;
        } else if (yIdx == 1 && boardArray[xIdx][yIdx + 2] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pawnPieceInt, xIdx, yIdx, xIdx, yIdx + 2, 0);
            moveIdx++;
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyPawnsMoveThreatenedSquares(boardArray, movesArray[newMoveIdx], colorsTurnItIs);
        }

        return moveIdx;
    }

    private void setMoveToMovesArray(final int[][] movesArray, final int moveIdx, final int pieceInt,
                                     final int startXIdx, final int startYIdx, final int endXIdx, final int endYIdx,
                                     final int capturedPiece) throws AlgorithmBadArgumentException {
        if (movesArray[moveIdx][0] != 0 || movesArray[moveIdx][1] != 0 || movesArray[moveIdx][2] != 0
            || movesArray[moveIdx][3] != 0 || movesArray[moveIdx][4] != 0 || movesArray[moveIdx][5] != 0) {
            throw new AlgorithmBadArgumentException("setMoveToMovesArray() called with moveIdx arg pointing to "
                                                    + "non-zero entry in movesArray argument");
        }
        movesArray[moveIdx][0] = pieceInt;
        movesArray[moveIdx][1] = startXIdx;
        movesArray[moveIdx][2] = startYIdx;
        movesArray[moveIdx][3] = endXIdx;
        movesArray[moveIdx][4] = endYIdx;
        movesArray[moveIdx][5] = capturedPiece;
    }

    private int generatePieceMoves(final int[][] boardArray, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs
                                  ) throws AlgorithmBadArgumentException {
        int pieceInt = boardArray[xIdx][yIdx];
        int moveIdx = moveIdxArg;

        switch (pieceInt ^ colorsTurnItIs) {
            case PAWN:
                moveIdx = generatePawnsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case ROOK:
                moveIdx = generateRooksMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case KNIGHT | LEFT: case KNIGHT | RIGHT:
                moveIdx = generateKnightsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case BISHOP:
                moveIdx = generateBishopsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case QUEEN:
                moveIdx = generateQueensMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case KING:
                moveIdx = generateKingsMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            default:
                break;
        }

        return moveIdx;
    }

    private int[][] generatePossibleMoves(final int[][] boardArray, final int colorsTurnItIs
                                         ) throws AlgorithmBadArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[][] movesArray = new int[128][10];
        int moveIdx = 0;

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt = boardArray[xIdx][yIdx];
                if (pieceInt == 0 || (pieceInt & otherColor) != 0) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
            }
        }

        return movesArray;
    }

    private int movesArrayFindUsedLength(final int[][] movesArray) {
        int lowerB = 0;
        int upperB = movesArray.length - 1;

        if (movesArray[0][0] == 0) {
            return 0;
        } else if (movesArray[upperB][0] != 0) {
            return movesArray.length;
        }

        while (lowerB <= upperB) {
            int guessIdx = (lowerB + upperB) / 2;
            if (movesArray[guessIdx - 1][0] == 0) {
                upperB = guessIdx - 1;
                continue;
            } else if (movesArray[guessIdx][0] != 0) {
                lowerB = guessIdx + 1;
                continue;
            } else {
                return guessIdx;
            }
        }

        return -1;
    }

    private double colorMobility(final int[][] boardArray, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int moveIdx = 0;
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if ((boardArray[xIdx][yIdx] & colorsTurnItIs) == 0) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardArray, colorMobilitySpareMovesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
            }
        }

        for (int newMoveIdx = 0; newMoveIdx < moveIdx; newMoveIdx++) {
            for (int moveArrayIdx = 0; moveArrayIdx < colorMobilitySpareMovesArray[newMoveIdx].length; moveArrayIdx++) {
                colorMobilitySpareMovesArray[newMoveIdx][moveArrayIdx] = 0;
            }
        }

        return (double) moveIdx;
    }

    private double[] tallySpecialPawns(final int[][] boardArray, final int colorsTurnItIs) {
        int otherColor = colorsTurnItIs == WHITE ? BLACK : WHITE;
        double[] retval = new double[3];
        double doubledPawns = 0;
        double isolatedPawns = 0;
        double blockedPawns = 0;
        int pawnIndex = 0;

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (colorsTurnItIs | PAWN)) {
                    tallyPawnsArray[pawnIndex][0] = xIdx;
                    tallyPawnsArray[pawnIndex][1] = yIdx;
                    pawnIndex++;
                }
            }
        }

        for (pawnIndex = 0; pawnIndex < 8; pawnIndex++) {
            if (colorsTurnItIs != colorOnTop && tallyPawnsArray[pawnIndex][1] + 1 < 8) {
                if ((boardArray[tallyPawnsArray[pawnIndex][0]][tallyPawnsArray[pawnIndex][1] + 1] & otherColor) != 0) {
                    blockedPawns++;
                }
            } else if (colorsTurnItIs == colorOnTop && tallyPawnsArray[pawnIndex][1] - 1 > 0) {
                if ((boardArray[tallyPawnsArray[pawnIndex][0]][tallyPawnsArray[pawnIndex][1] - 1] & otherColor) != 0) {
                    blockedPawns++;
                }
            }
        }

        /* Normally an isolated pawn is one where its neighboring pawns are each
           more than one file away from them. But if there's only 1 pawn left
           it is by default isolated. */
        if (pawnIndex == 1) {
            isolatedPawns++;
        /* If there's two left, they're both isolated if they're more than 1
           file apart. */
        } else if (pawnIndex == 2 && tallyPawnsArray[1][0] - tallyPawnsArray[1][0] > 1) {
            isolatedPawns += 2;
        } else {
            int maxPawnIndex = pawnIndex - 1;
            for (pawnIndex = 0; pawnIndex < 8; pawnIndex++) {
                /* If a pawn is the leftmost or rightmost on the board, it's
                /* isolated if its inward neighbor is more than 1 file away from
                /* it. */
                if (pawnIndex == 0 && tallyPawnsArray[pawnIndex + 1][0] - tallyPawnsArray[pawnIndex][0] > 1) {
                    isolatedPawns++;
                } else if (pawnIndex == maxPawnIndex && tallyPawnsArray[pawnIndex][0] - tallyPawnsArray[pawnIndex - 1][0] > 1) {
                    isolatedPawns++;
                } else if (pawnIndex != 0 && pawnIndex != maxPawnIndex) {
                    if (tallyPawnsArray[pawnIndex][0] > tallyPawnsArray[pawnIndex - 1][0]
                        && tallyPawnsArray[pawnIndex + 1][0] > tallyPawnsArray[pawnIndex][0]) {
                        isolatedPawns++;
                    }
                }
            }
        }
        for (pawnIndex = 0; pawnIndex < 8; pawnIndex++) {
            if (colorPlaying == colorOnTop) {
                if (tallyPawnsArray[pawnIndex][0] == 0) {
                    continue;
                }
                if (tallyPawnsArray[pawnIndex][1] == tallyPawnsArray[pawnIndex + 1][1]
                    && tallyPawnsArray[pawnIndex][0] - 1 == tallyPawnsArray[pawnIndex + 1][0]) {
                    doubledPawns++;
                } else if (boardArray[tallyPawnsArray[pawnIndex][0]][tallyPawnsArray[pawnIndex][0] - 1] != 0) {
                    blockedPawns++;
                }
            } else {
                if (tallyPawnsArray[pawnIndex][0] == 7) {
                    continue;
                }
                if (tallyPawnsArray[pawnIndex][1] == tallyPawnsArray[pawnIndex + 1][1]
                    && tallyPawnsArray[pawnIndex][0] + 1 == tallyPawnsArray[pawnIndex + 1][0]) {
                    doubledPawns++;
                } else if (boardArray[tallyPawnsArray[pawnIndex][0]][tallyPawnsArray[pawnIndex][0] + 1] != 0) {
                    blockedPawns++;
                }
            }
        }

        retval[DOUBLED] = doubledPawns;
        retval[BLOCKED] = blockedPawns;
        retval[ISOLATED] = isolatedPawns;
        return retval;
    }

    private double evaluateBoard(final int[][] boardArray, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int otherColor = colorsTurnItIs == WHITE ? BLACK : WHITE;
        int whiteIndex = 0;
        int blackIndex = 1;
        int kingIndex = 0;
        int queenIndex = 1;
        int rookIndex = 2;
        int bishopIndex = 3;
        int knightIndex = 4;
        int pawnIndex = 5;
        int xIdx = 0;
        int yIdx = 0;

        int thisColorIndex = colorsTurnItIs == WHITE ? whiteIndex : blackIndex;
        int otherColorIndex = colorsTurnItIs == WHITE ? blackIndex : whiteIndex;

        double[][] piecesCounts = new double[2][6];

        kingsch:
        for (xIdx = 0; xIdx < 8; xIdx++) {
            for (yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (WHITE | KING)) {
                    break kingsch;
                }
            }
        }
        int whiteKingNotInCheckBonus = isKingThreatened(boardArray, kingsMovesSpareMovesArray, (WHITE | KING), xIdx, yIdx, BLACK) ? 0 : 1;

        kingsch:
        for (xIdx = 0; xIdx < 8; xIdx++) {
            for (yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (BLACK | KING)) {
                    break kingsch;
                }
            }
        }
        int blackKingNotInCheckBonus = isKingThreatened(boardArray, kingsMovesSpareMovesArray, (BLACK | KING), xIdx, yIdx, WHITE) ? 0 : 1;

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
                }
            }
        }

        double[] colorPlayingSpecialPawnsTallies = tallySpecialPawns(boardArray, colorPlaying);
        double[] otherColorSpecialPawnsTallies = tallySpecialPawns(boardArray, otherColor);
        double isolatedPawnDifference = colorPlayingSpecialPawnsTallies[ISOLATED]
                                       - otherColorSpecialPawnsTallies[ISOLATED];
        double blockedPawnDifference = colorPlayingSpecialPawnsTallies[BLOCKED]
                                      - otherColorSpecialPawnsTallies[BLOCKED];
        double doubledPawnDifference = colorPlayingSpecialPawnsTallies[DOUBLED]
                                      - otherColorSpecialPawnsTallies[DOUBLED];
        double specialPawnScore = 0.5F * (isolatedPawnDifference + blockedPawnDifference
                                         + doubledPawnDifference);

        double thisMobility = colorMobility(boardArray, colorPlaying);
        double otherMobility = colorMobility(boardArray, otherColor);
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

        return totalScore;
    };

    public Move algorithmTopLevel(final Chessboard chessboard) throws AlgorithmBadArgumentException, AlgorithmInternalError {
        int[][] boardArray = new int[8][8];
        int[][] movesArray;
        double bestScore;
        int[] bestMoveArray = null;
        int movesArrayUsedLength;
        Move bestMoveObj;
        Iterator<Piece> boardIter = chessboard.iterator();

        bestScore = Double.NEGATIVE_INFINITY;

        while (boardIter.hasNext()) {
            Piece nextPiece = boardIter.next();
            int[] pieceCoords = chessboard.algNotnLocToNumericIndexes(nextPiece.getLocation());
            int pieceNumber = pieceIdentitiesToInts.get(nextPiece.getIdentity());
            boardArray[pieceCoords[0]][pieceCoords[1]] = pieceNumber;
        }

        movesArray = generatePossibleMoves(boardArray, colorPlaying);

        movesArrayUsedLength = movesArrayFindUsedLength(movesArray);

        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            /* The same boardArray is passed down the call stack and reused by
               every step of the algorithm, to avoid having to clone it each
               time. That means I need to execute this moveArray's move on the
               board, execute the recursive call, and then undo the move so the
               board can be reused. savedPieceInt holds whatever was at the
               square the piece was moved to so it can be restored. */
            int[] moveArray = movesArray[moveIdx];
            int fromXIdx = moveArray[1];
            int fromYIdx = moveArray[2];
            int toXIdx = moveArray[1];
            int toYIdx = moveArray[2];
            int savedPieceInt = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[0];

            double thisScore = algorithmLowerLevel(boardArray, algorithmStartingDepth - 1, colorOpposing);

            boardArray[fromXIdx][fromYIdx] = moveArray[0];
            boardArray[toXIdx][toYIdx] = savedPieceInt;

            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestMoveArray = movesArray[moveIdx];
            }
        }

        if (Objects.isNull(bestMoveArray)) {
            throw new AlgorithmInternalError("algorithm top-level execution failed to find best move");
        }

        String bestMoveFromLoc = chessboard.numericIndexesToAlgNotnLoc(bestMoveArray[1], bestMoveArray[2]);
        String bestMoveToLoc = chessboard.numericIndexesToAlgNotnLoc(bestMoveArray[3], bestMoveArray[4]);
        Piece bestMovePiece = chessboard.getPieceAtLocation(bestMoveFromLoc);

        bestMoveObj = new Move(bestMovePiece, bestMoveFromLoc, bestMoveToLoc);

        return bestMoveObj;
    }

    private double algorithmLowerLevel(int[][] boardArray, int depth, int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int otherColor = colorsTurnItIs == WHITE ? BLACK : WHITE;
        double bestScore;
        int thisColorKingsMovesCount;
        int otherColorKingsMovesCount;
        int xIdx = 0;
        int yIdx = 0;
        BiFunction<Double, Double, Boolean> comparator;

        if (depth == 0) {
            return evaluateBoard(boardArray, colorsTurnItIs);
        }

        if (colorPlaying == colorsTurnItIs) {
            bestScore = Double.NEGATIVE_INFINITY;
            comparator = (score, bestScoreVal) -> (score > bestScoreVal);
        } else {
            bestScore = Double.POSITIVE_INFINITY;
            comparator = (score, bestScoreVal) -> (score < bestScoreVal);
        }

        /* This is just to locate the coordinates of this side's King on the
           board so I can call generateKingsMoves() with them. */
        kingsch:
        for (xIdx = 0; xIdx < 8; xIdx++) {
            for (yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (colorsTurnItIs | KING)) {
                    break kingsch;
                }
            }
        }

        if (isKingThreatened(boardArray, kingsMovesSpareMovesArray, (colorsTurnItIs | KING), xIdx, yIdx, otherColor)
            && generateKingsMoves(boardArray, algorithmSpareMovesArray, 0, xIdx, yIdx, colorPlaying) == 0) {
            return Double.NEGATIVE_INFINITY;
        }

        /* Locating the coordinates of the other side's King on the
           board so I can call generateKingsMoves() with them. */
        kingsch:
        for (xIdx = 0; xIdx < 8; xIdx++) {
            for (yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == (otherColor | KING)) {
                    break kingsch;
                }
            }
        }

        if (isKingThreatened(boardArray, kingsMovesSpareMovesArray, (otherColor | KING), xIdx, yIdx, colorsTurnItIs)
            && generateKingsMoves(boardArray, algorithmSpareMovesArray, 0, xIdx, yIdx, colorOpposing) == 0) {
            return Double.POSITIVE_INFINITY;
        }

        for (int[] moveArray : generatePossibleMoves(boardArray, otherColor)) {
            /* The same boardArray is passed down the call stack and reused by
               every step of the algorithm, to avoid having to clone it each
               time. That means I need to execute this moveArray's move on the
               board, execute the recursive call, and then undo the move so the
               board can be reused. savedPieceInt holds whatever was at the
               square the piece was moved to so it can be restored. */
            int fromXIdx = moveArray[1];
            int fromYIdx = moveArray[2];
            int toXIdx = moveArray[1];
            int toYIdx = moveArray[2];
            int savedPieceInt = boardArray[toXIdx][toYIdx];
            boardArray[toXIdx][toYIdx] = moveArray[0];

            double thisScore = algorithmLowerLevel(boardArray, depth - 1, otherColor);

            boardArray[fromXIdx][fromYIdx] = moveArray[0];
            boardArray[toXIdx][toYIdx] = savedPieceInt;

            if (comparator.apply(thisScore, bestScore)) {
                bestScore = thisScore;
            }
        }

        return bestScore;
    }
}
