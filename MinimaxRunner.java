package com.kmfahey.jchessgame;

import java.util.Iterator;
import java.util.Arrays;
import java.util.HashMap;

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

    private int colorPlaying;
    private int colorOpposing;
    private int colorOnTop;

    public record Move(Piece movingPiece, String currentLocation, String moveToLocation) { };

    public MinimaxRunner(final String colorPlayingStr, final String colorOnTopStr) {
        colorPlaying = colorPlayingStr.equals("white") ? WHITE : BLACK;
        colorOpposing = colorPlaying == WHITE ? BLACK : WHITE;
        colorOnTop = colorOnTopStr.equals("white") ? WHITE : BLACK;
    }

    private void surveyKingsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7 && (boardAry[xIdx + 1][yIdx] & otherColor) != 0) {
            threatenedCount++;
            if (yIdx < 7 && (boardAry[xIdx + 1][yIdx + 1] & otherColor) != 0) {
                threatenedCount++;
            }
            if (yIdx > 0 && (boardAry[xIdx + 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
        }
        if (yIdx < 7 && (boardAry[xIdx][yIdx + 1] & otherColor) != 0) {
            threatenedCount++;
        }
        if (yIdx > 0 && (boardAry[xIdx + 1][yIdx - 1] & otherColor) != 0) {
            threatenedCount++;
        }
        if (xIdx > 0 && (boardAry[xIdx - 1][yIdx] & otherColor) != 0) {
            threatenedCount++;
            if (yIdx < 7 && (boardAry[xIdx + 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
            if (yIdx > 0 && (boardAry[xIdx - 1][yIdx - 1] & otherColor) != 0) {
                threatenedCount++;
            }
        }

        moveArray[9] = threatenedCount;
    }

    private void surveyQueensMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyQueensMoveThreatenedSquares(boardAry, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyQueensMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8; xIdxMod++) {
                if (boardAry[xIdxMod][yIdx] != 0) {
                    if ((boardAry[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdx], xIdxMod, yIdx);
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
                    if (boardAry[xIdxMod][yIdxMod] != 0) {
                        if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                    if (boardAry[xIdxMod][yIdxMod] != 0) {
                        if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                if (boardAry[xIdx][yIdxMod] != 0) {
                    if ((boardAry[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdx][yIdxMod], xIdx, yIdxMod);
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
                if (boardAry[xIdx][yIdxMod] != 0) {
                    if ((boardAry[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdx][yIdxMod], xIdx, yIdxMod);
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
                if (boardAry[xIdxMod][yIdx] != 0) {
                    if ((boardAry[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdx], xIdxMod, yIdx);
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
                    if (boardAry[xIdxMod][yIdxMod] != 0) {
                        if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                    if (boardAry[xIdxMod][yIdxMod] != 0) {
                        if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                            if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                                setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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

    private void surveyBishopsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyBishopsMoveThreatenedSquares(boardAry, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyBishopsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (xIdx < 7 && yIdx < 7) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8; xIdxMod++, yIdxMod++) {
                if (boardAry[xIdxMod][yIdxMod] != 0) {
                    if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                if (boardAry[xIdxMod][yIdxMod] != 0) {
                    if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                if (boardAry[xIdxMod][yIdxMod] != 0) {
                    if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
                if (boardAry[xIdxMod][yIdxMod] != 0) {
                    if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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

    private void surveyKnightsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs) {
        surveyKnightsMoveThreatenedSquares(boardAry, moveArray, colorsTurnItIs, false);
    }

    private void surveyKnightsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
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
                    || (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }

                if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                    if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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
    
    private void surveyRooksMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig) {
        surveyRooksMoveThreatenedSquares(boardAry, moveArray, colorsTurnItIs, xIdxOrig, yIdxOrig, false);
    }

    private void surveyRooksMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final int xIdxOrig, final int yIdxOrig, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int threatenedCount = 0;

        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8; yIdxMod++) {
                if (boardAry[xIdx][yIdxMod] != 0) {
                    if ((boardAry[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdx][yIdxMod], xIdx, yIdxMod);
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
                if (boardAry[xIdx][yIdxMod] != 0) {
                    if ((boardAry[xIdx][yIdxMod] & otherColor) != 0) {
                        if (boardAry[xIdx][yIdxMod] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdx][yIdxMod], xIdx, yIdxMod);
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
                if (boardAry[xIdxMod][yIdx] != 0) {
                    if ((boardAry[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdx], xIdxMod, yIdx);
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
                if (boardAry[xIdxMod][yIdx] != 0) {
                    if ((boardAry[xIdxMod][yIdx] & otherColor) != 0) {
                        if (boardAry[xIdxMod][yIdx] == (otherColor | KING)) {
                            setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdx], xIdxMod, yIdx);
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

    private void surveyPawnsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs) {
        surveyPawnsMoveThreatenedSquares(boardAry, moveArray, colorsTurnItIs, false);
    }

    private void surveyPawnsMoveThreatenedSquares(final int[][] boardAry, final int[] moveArray, final int colorsTurnItIs, final boolean checkOnly) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;
        int xIdx = moveArray[3];
        int yIdx = moveArray[4];
        int yIdxMod;
        int threatenedCount = 0;

        if (yIdx < 7 && (colorOnTop == WHITE && colorsTurnItIs == WHITE || colorOnTop == BLACK && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && (colorOnTop == BLACK && colorsTurnItIs == WHITE || colorOnTop == WHITE && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx - 1;
        } else {
            return;
        }

        for (int xIdxMod = xIdx - 1; xIdxMod <= xIdx + 1; xIdxMod += 2) {
            if (xIdxMod < 0 || xIdxMod > 7) {
                continue;
            }

            if ((boardAry[xIdxMod][yIdxMod] & otherColor) != 0) {
                if (boardAry[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    setMoveArrayKingInCheck(moveArray, boardAry[xIdxMod][yIdxMod], xIdxMod, yIdxMod);
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

    private int generateQueensMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                   final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int pieceInt;
        int moveIdx = moveIdxArg;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | QUEEN)) {
            throw new AlgorithmBadArgumentException("generateQueensMoves() called with coordinates that point to cell on board that's not a queen or not the color whose turn it is");
        }

        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8 && (boardAry[xIdxMod][yIdx] & colorsTurnItIs) == 0; xIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdx, boardAry[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod++, yIdxMod++, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod++, yIdxMod--, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }
        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8 && (boardAry[xIdx][yIdxMod] & colorsTurnItIs) == 0; yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdxMod, boardAry[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0 && (boardAry[xIdx][yIdxMod] & colorsTurnItIs) == 0; yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdxMod, boardAry[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0 && (boardAry[xIdxMod][yIdx] & colorsTurnItIs) == 0; xIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdx, boardAry[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1; xIdxMod >= 0 && yIdxMod < 8 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod--, yIdxMod++, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1; xIdxMod >= 0 && yIdxMod >= 0 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0; xIdxMod--, yIdxMod--, moveIdx++) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                    if (movesArray[moveIdx][5] != 0) {
                        moveIdx++;
                        break;
                    }
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyQueensMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generateBishopsMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int pieceInt;
        int moveIdx = moveIdxArg;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | BISHOP)) {
            throw new AlgorithmBadArgumentException("generateBishopsMoves() called with coordinates that point to cell on board that's not a bishop or not the color whose turn it is");
        }

        if (xIdx < 7 && yIdx < 7) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1;
                xIdxMod < 8 && yIdxMod < 8 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx < 7) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1;
                xIdxMod >= 0 && yIdxMod < 8 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7 && yIdx > 0) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1;
                xIdxMod < 8 && yIdxMod >= 0 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod++, yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx > 0) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1;
                xIdxMod >= 0 && yIdxMod >= 0 && (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) == 0;
                xIdxMod--, yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyBishopsMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generateKnightsMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                    final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int pieceInt;
        int moveIdx = moveIdxArg;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | KNIGHT | LEFT) && pieceInt != (colorsTurnItIs | KNIGHT | RIGHT)) {
            throw new AlgorithmBadArgumentException("generateKnightsMoves() called with coordinates that point to cell on board that's not a knight or not the color whose turn it is");
        }

        for (int xIdxDelta = -2; xIdxDelta <= 2; xIdxDelta++) {
            for (int yIdxDelta = -2; yIdxDelta <= 2; yIdxDelta++) {
                if (Math.abs(xIdxDelta) == Math.abs(yIdxDelta) || xIdxDelta == 0 || yIdxDelta == 0) {
                    continue;
                }

                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;

                if (xIdxMod > 7 || xIdxMod < 0 || yIdxMod > 7 || yIdxMod < 0
                    || (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }

                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod, boardAry[xIdxMod][yIdxMod]);
                moveIdx++;
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyKnightsMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs);
        }

        return moveIdx;
    }

    private int generateRooksMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int pieceInt;
        int moveIdx = moveIdxArg;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | ROOK)) {
            throw new AlgorithmBadArgumentException("generateRooksMoves() called with coordinates that point to cell on board that's not a rook or not the color whose turn it is");
        }

        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8 && (boardAry[xIdx][yIdxMod] & colorsTurnItIs) == 0; yIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdxMod, boardAry[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0 && (boardAry[xIdx][yIdxMod] & colorsTurnItIs) == 0; yIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdxMod, boardAry[xIdx][yIdxMod]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8 && (boardAry[xIdxMod][yIdx] & colorsTurnItIs) == 0; xIdxMod++, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdx, boardAry[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0 && (boardAry[xIdxMod][yIdx] & colorsTurnItIs) == 0; xIdxMod--, moveIdx++) {
                setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdx, boardAry[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyRooksMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs, xIdx, yIdx);
        }

        return moveIdx;
    }

    private int generatePawnsMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pieceInt;
        int yIdxMod;
        int moveIdx = moveIdxArg;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | PAWN)) {
            throw new AlgorithmBadArgumentException("generatePawnsMoves() called with coordinates that point to cell on board that's not a pawn or not the color whose turn it is");
        }

        if (yIdx < 7 && (colorOnTop == WHITE && colorsTurnItIs == WHITE || colorOnTop == BLACK && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && (colorOnTop == WHITE && colorsTurnItIs == BLACK || colorOnTop == BLACK && colorsTurnItIs == WHITE)) {
            yIdxMod = yIdx - 1;
        } else {
            return moveIdx;
        }

        if (boardAry[xIdx][yIdxMod] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdxMod, 0);
            moveIdx++;
        }
        if (xIdx < 7 && (boardAry[xIdx + 1][yIdxMod] & otherColor) != 0) {
            setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx + 1, yIdxMod, boardAry[xIdx + 1][yIdxMod]);
            moveIdx++;
        }
        if (xIdx > 0 && (boardAry[xIdx - 1][yIdxMod] & otherColor) != 0) {
            setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx - 1, yIdxMod, boardAry[xIdx - 1][yIdxMod]);
            moveIdx++;
        }

        if (yIdx == 6 && boardAry[xIdx][yIdx - 2] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdx - 2, 0);
            moveIdx++;
        } else if (yIdx == 1 && boardAry[xIdx][yIdx + 2] == 0) {
            setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdx, yIdx + 2, 0);
            moveIdx++;
        }

        for (int newMoveIdx = moveIdxArg; newMoveIdx < moveIdx; newMoveIdx++) {
            surveyPawnsMoveThreatenedSquares(boardAry, movesArray[newMoveIdx], colorsTurnItIs);
        }

        return moveIdx;
    }

    private void setMoveToMovesArray(final int[][] movesArray, final int moveIdx, final int pieceInt, final int startXIdx,
                                   final int startYIdx, final int endXIdx, final int endYIdx, final int capturedPiece) throws AlgorithmBadArgumentException {
        if (movesArray[moveIdx][0] != 0 || movesArray[moveIdx][1] != 0 || movesArray[moveIdx][2] != 0
            || movesArray[moveIdx][3] != 0 || movesArray[moveIdx][4] != 0 || movesArray[moveIdx][5] != 0) {
            throw new AlgorithmBadArgumentException("setMoveToMovesArray() called with moveIdx arg pointing to non-zero entry in movesArray argument");
        }
        movesArray[moveIdx][0] = pieceInt;
        movesArray[moveIdx][1] = startXIdx;
        movesArray[moveIdx][2] = startYIdx;
        movesArray[moveIdx][3] = endXIdx;
        movesArray[moveIdx][4] = endYIdx;
        movesArray[moveIdx][5] = capturedPiece;
    }

    private int[][] copyBoard(final int[][] boardAry) {
        int[][] newBoardAry = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                newBoardAry[xIdx][yIdx] = boardAry[xIdx][yIdx];
            }
        }

        return newBoardAry;
    }

    private int generatePieceMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                  final int xIdx, final int yIdx, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int pieceInt = boardAry[xIdx][yIdx];
        int moveIdx = moveIdxArg;

        switch (pieceInt ^ colorsTurnItIs) {
            case PAWN:
                moveIdx = generatePawnsMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case ROOK:
                moveIdx = generateRooksMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case KNIGHT | LEFT: case KNIGHT | RIGHT:
                moveIdx = generateKnightsMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case BISHOP:
                moveIdx = generateBishopsMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case QUEEN:
                moveIdx = generateQueensMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            case KING:
                moveIdx = generateKingsMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
                break;
            default:
                break;
        }

        return moveIdx;
    }

    private int[][] generatePossibleMoves(final int[][] boardAry, final int colorsTurnItIs) throws AlgorithmBadArgumentException {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[][] movesArray = new int[128][10];
        int moveIdx = 0;

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt = boardAry[xIdx][yIdx];
                if (pieceInt == 0 || (pieceInt & otherColor) != 0) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardAry, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
            }
        }

        return movesArray;
    }

    private int movesArrayFind1stZeroArray(final int[][] movesArray) {
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

    public void runAlgorithm(final Chessboard chessboard) {
        int[][] boardAry = new int[8][8];
        Iterator<Piece> boardIter = chessboard.iterator();

        while (boardIter.hasNext()) {
            Piece nextPiece = boardIter.next();
            int[] pieceCoords = chessboard.algNotnLocToNumericIndexes(nextPiece.getLocation());
            int pieceNumber = pieceIdentitiesToInts.get(nextPiece.getIdentity());
            boardAry[pieceCoords[0]][pieceCoords[1]] = pieceNumber;
        }
    }
}
