package com.kmfahey.jchessgame;

import java.util.Iterator;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.StringJoiner;
import java.util.Random;

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

    /* These are alternate versions of main arrays in the int-array-based board
       logic that are used by some methods. In order to avoid instantiating a
       new array each time one is called, a spare array is stored to an instance
       variable and reused each time that method is called. */
    private int[][] kingsMovesSpareBoardArray;    
    private int[][] tallyPawnsArray;              
    private int[][] colorMobilitySpareMovesArray; 

    private HashMap<String,Double> evaluateBoardMemoizeMap;

    private int colorOfAI;
    private int colorOfPlayer;
    private int colorOnTop;
    private int algorithmStartingDepth;
    private Function<Integer, String> indenter = (intval) ->   (intval == 5) ? "                    "
                                                             : (intval == 4) ? "                "
                                                             : (intval == 3) ? "            " 
                                                             : (intval == 2) ? "        " 
                                                             : "        ";

    public record Move(Piece movingPiece, String currentLocation, String moveToLocation) { };

    public MinimaxRunner(final String colorOfAIStr, final String colorOnTopStr) {
        colorOfAI = colorOfAIStr.equals("white") ? WHITE : BLACK;
        colorOfPlayer = colorOfAI == WHITE ? BLACK : WHITE;
        colorOnTop = colorOnTopStr.equals("white") ? WHITE : BLACK;
        kingsMovesSpareBoardArray = new int[8][8];
        tallyPawnsArray = new int[8][2];
        colorMobilitySpareMovesArray = new int[128][6];
        algorithmStartingDepth = 4;
        evaluateBoardMemoizeMap = new HashMap<String,Double>();
    }

    private boolean doesQueensMoveCheckKing(final int[][] boardArray, final int xIdx, final int yIdx,
                                                   final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;

        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8; xIdxMod++) {
                if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdx] != 0) {
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8; xIdxMod++, yIdxMod++) {
                    if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        return true;
                    } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0; xIdxMod++, yIdxMod--) {
                    if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        return true;
                    } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                        break;
                    }
                }
            }
        }
        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8; yIdxMod++) {
                if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdx][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdx][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
                if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdx] != 0) {
                    break;
                }
            }
            if (yIdx < 7) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1; xIdxMod >= 0 && yIdxMod < 8; xIdxMod--, yIdxMod++) {
                    if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        return true;
                    } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                        break;
                    }
                }
            }
            if (yIdx > 0) {
                for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1; xIdxMod >= 0 && yIdxMod >= 0; xIdxMod--, yIdxMod--) {
                    if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                        return true;
                    } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                        break;
                    }
                }
            }
        }

        return false;
    }

    private boolean doesBishopsMoveCheckKing(final int[][] boardArray, final int xIdx, final int yIdx,
                                                    final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;

        if (xIdx < 7 && yIdx < 7) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx + 1; xIdxMod < 8 && yIdxMod < 8; xIdxMod++, yIdxMod++) {
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx < 7) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx + 1; xIdxMod >= 0 && yIdxMod < 8; xIdxMod--, yIdxMod++) {
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (xIdx < 7 && yIdx > 0) {
            for (int xIdxMod = xIdx + 1, yIdxMod = yIdx - 1; xIdxMod < 8 && yIdxMod >= 0; xIdxMod++, yIdxMod--) {
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (xIdx > 0 && yIdx > 0) {
            for (int xIdxMod = xIdx - 1, yIdxMod = yIdx - 1; xIdxMod >= 0 && yIdxMod >= 0; xIdxMod--, yIdxMod--) {
                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                    break;
                }
            }
        }

        return false;
    }

    private boolean doesKnightsMoveCheckKing(final int[][] boardArray, final int xIdx, final int yIdx,
                                                    final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;

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

                if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                    break;
                }
            }
        }

        return false;
    }

    private boolean doesRooksMoveCheckKing(final int[][] boardArray, final int xIdx, final int yIdx,
                                                  final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int[] kingFoundAry = null;

        if (yIdx < 7) {
            for (int yIdxMod = yIdx + 1; yIdxMod < 8; yIdxMod++) {
                if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdx][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (yIdx > 0) {
            for (int yIdxMod = yIdx - 1; yIdxMod >= 0; yIdxMod--) {
                if (boardArray[xIdx][yIdxMod] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdx][yIdxMod] != 0) {
                    break;
                }
            }
        }
        if (xIdx < 7) {
            for (int xIdxMod = xIdx + 1; xIdxMod < 8; xIdxMod++) {
                if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdx] != 0) {
                    break;
                }
            }
        }
        if (xIdx > 0) {
            for (int xIdxMod = xIdx - 1; xIdxMod >= 0; xIdxMod--) {
                if (boardArray[xIdxMod][yIdx] == (otherColor | KING)) {
                    return true;
                } else if (boardArray[xIdxMod][yIdx] != 0) {
                    break;
                }
            }
        }

        return false;
    }

    private boolean doesPawnsMoveCheckKing(final int[][] boardArray, final int xIdx,
                                           final int yIdx, final int colorsTurnItIs) {
        int otherColor = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int yIdxMod;

        if (yIdx < 7 && (colorOnTop == WHITE && colorsTurnItIs == WHITE
            || colorOnTop == BLACK && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx + 1;
        } else if (yIdx > 0 && (colorOnTop == BLACK && colorsTurnItIs == WHITE
            || colorOnTop == WHITE && colorsTurnItIs == BLACK)) {
            yIdxMod = yIdx - 1;
        } else {
            return false;
        }

        for (int xIdxMod = xIdx - 1; xIdxMod <= xIdx + 1; xIdxMod += 2) {
            if (xIdxMod < 0 || xIdxMod > 7) {
                continue;
            }

            if (boardArray[xIdxMod][yIdxMod] == (otherColor | KING)) {
                return true;
            } else if (boardArray[xIdxMod][yIdxMod] != 0) {
                break;
            }
        }

        return false;
    }

    private boolean isKingThreatened(final int[][] boardAry, final int colorThreatening) {
        int colorOfKing = (colorThreatening == WHITE) ? BLACK : WHITE;
        boolean kingIsThreatened = false;
        threatsch:
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt = boardAry[xIdx][yIdx];
                if (pieceInt == 0 || (pieceInt & colorOfKing) != 0) {
                    continue;
                }
                switch (pieceInt ^ colorThreatening) {
                    case PAWN:
                        kingIsThreatened = doesPawnsMoveCheckKing(boardAry, xIdx, yIdx, colorThreatening);
                        break;
                    case ROOK:
                        kingIsThreatened = doesRooksMoveCheckKing(boardAry, xIdx, yIdx, colorThreatening);
                        break;
                    case KNIGHT | LEFT: case KNIGHT | RIGHT:
                        kingIsThreatened = doesKnightsMoveCheckKing(boardAry, xIdx, yIdx, colorThreatening);
                        break;
                    case BISHOP:
                        kingIsThreatened = doesBishopsMoveCheckKing(boardAry, xIdx, yIdx, colorThreatening);
                        break;
                    case QUEEN:
                        kingIsThreatened = doesQueensMoveCheckKing(boardAry, xIdx, yIdx, colorThreatening);
                        break;
                    default:
                        break;
                }
                if (kingIsThreatened) {
                    break threatsch;
                }
            }
        }

        return kingIsThreatened;
    }

    private int generateKingsMoves(final int[][] boardAry, final int[][] movesArray, final int moveIdxArg,
                                   final int xIdx, final int yIdx, final int colorsTurnItIs
                                   ) throws AlgorithmBadArgumentException {
        int colorThreatening = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int pieceInt;
        int atLocPieceInt;
        int kingPieceInt;
        int moveIdx = moveIdxArg;
        int otherSidePiecesCount = 0;
        int[][] otherBoardArray = new int[8][8];
        int otherLocsIdx = 0;
        boolean kingIsThreatenedHere;

        pieceInt = boardAry[xIdx][yIdx];

        if (pieceInt != (colorsTurnItIs | KING)) {
            throw new AlgorithmBadArgumentException("generateKingsMoves() called with coordinates that point to cell "
                                                    + "on board that's not a king or not the color whose turn it is");
        }

        for (int otherXIdx = 0; otherXIdx < 8; otherXIdx++) {
            for (int otherYIdx = 0; otherYIdx < 8; otherYIdx++) {
                kingsMovesSpareBoardArray[otherXIdx][otherYIdx] = boardAry[otherXIdx][otherYIdx];
            }
        }

        kingPieceInt = kingsMovesSpareBoardArray[xIdx][yIdx];
        kingsMovesSpareBoardArray[xIdx][yIdx] = 0;

        for (int xIdxDelta = -1; xIdxDelta <= 1; xIdxDelta++) {
            for (int yIdxDelta = -1; yIdxDelta <= 1; yIdxDelta++) {
                if (xIdxDelta == 0 && yIdxDelta == 0) {
                    continue;
                }
                int xIdxMod = xIdx + xIdxDelta;
                int yIdxMod = yIdx + yIdxDelta;
                if (xIdxMod < 0 || xIdxMod > 7 || yIdxMod < 0 || yIdxMod > 7
                    || (boardAry[xIdxMod][yIdxMod] & colorsTurnItIs) != 0) {
                    continue;
                }

                atLocPieceInt = kingsMovesSpareBoardArray[xIdxMod][yIdxMod];
                kingsMovesSpareBoardArray[xIdxMod][yIdxMod] = kingPieceInt;
                kingIsThreatenedHere = isKingThreatened(kingsMovesSpareBoardArray, colorThreatening);
                kingsMovesSpareBoardArray[xIdxMod][yIdxMod] = atLocPieceInt;

                if (kingIsThreatenedHere) {
                    continue;
                }

                if (movesArray != null) {
                    setMoveToMovesArray(movesArray, moveIdx, pieceInt, xIdx, yIdx, xIdxMod, yIdxMod,
                                        boardAry[xIdxMod][yIdxMod]);
                }
                moveIdx++;
            }
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
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                    boardArray[xIdxMod][yIdx]);
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
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                    boardArray[xIdx][yIdxMod]);
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
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                    boardArray[xIdx][yIdxMod]);
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
                setMoveToMovesArray(movesArray, moveIdx, queenPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                    boardArray[xIdxMod][yIdx]);
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
                                                    + "cell on board that's not a bishop or not the color whose turn "
                                                    + "it is");
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
                                                    + "cell on board that's not a knight or not the color whose turn "
                                                    + "it is");
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
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                    boardArray[xIdx][yIdxMod]);
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
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdx, yIdxMod,
                                    boardArray[xIdx][yIdxMod]);
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
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                    boardArray[xIdxMod][yIdx]);
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
                setMoveToMovesArray(movesArray, moveIdx, rookPieceInt, xIdx, yIdx, xIdxMod, yIdx,
                                    boardArray[xIdxMod][yIdx]);
                if (movesArray[moveIdx][5] != 0) {
                    moveIdx++;
                    break;
                }
            }
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

    private int generatePossibleMoves(final int[][] boardArray, final int[][] movesArray, final int colorsTurnItIs
                                         ) throws AlgorithmBadArgumentException {
        int colorOpposing = (colorsTurnItIs == WHITE) ? BLACK : WHITE;
        int moveIdx = 0;
        boolean kingIsThreatened;

        kingIsThreatened = isKingThreatened(boardArray, colorOpposing);

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                int pieceInt = boardArray[xIdx][yIdx];
                if (pieceInt == 0 || (pieceInt & colorOpposing) != 0 || kingIsThreatened && (pieceInt ^ colorsTurnItIs) != KING) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardArray, movesArray, moveIdx, xIdx, yIdx, colorsTurnItIs);
            }
        }

        return moveIdx;
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

    private double colorMobility(final int[][] boardArray, final int colorsTurnItIs
                                ) throws AlgorithmBadArgumentException {
        int moveIdx = 0;
        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if ((boardArray[xIdx][yIdx] & colorsTurnItIs) == 0) {
                    continue;
                }
                moveIdx = generatePieceMoves(boardArray, colorMobilitySpareMovesArray, moveIdx, xIdx, yIdx,
                                             colorsTurnItIs);
            }
        }

        for (int newMoveIdx = 0; newMoveIdx < moveIdx; newMoveIdx++) {
            for (int moveArrayIdx = 0; moveArrayIdx < colorMobilitySpareMovesArray[newMoveIdx].length; moveArrayIdx++) {
                colorMobilitySpareMovesArray[newMoveIdx][moveArrayIdx] = 0;
            }
        }

        return (double) moveIdx;
    }

    private void printBoard(final int[][] boardArray) {
        StringJoiner outerJoiner = new StringJoiner(",\\n", "new int[][] {\\n", "\\n}\\n");
        for (int outerIndex = 0; outerIndex < 8; outerIndex++) {
            StringJoiner innerJoiner = new StringJoiner(", ", "new int[] {", "}");
            for (int innerIndex = 0; innerIndex < 8; innerIndex++) {
                innerJoiner.add(String.valueOf(boardArray[outerIndex][innerIndex]));
            }
            outerJoiner.add(innerJoiner.toString());
        }
    }

    private double[] tallySpecialPawns(final int[][] boardArray, final int colorsTurnItIs) {
        int colorOpposing = colorsTurnItIs == WHITE ? BLACK : WHITE;
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
                if ((boardArray[tallyPawnsArray[pawnIndex][0]]
                                               [tallyPawnsArray[pawnIndex][1] + 1] & colorOpposing) != 0) {
                    blockedPawns++;
                }
            } else if (colorsTurnItIs == colorOnTop && tallyPawnsArray[pawnIndex][1] - 1 > 0) {
                if ((boardArray[tallyPawnsArray[pawnIndex][0]]
                               [tallyPawnsArray[pawnIndex][1] - 1] & colorOpposing) != 0) {
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
                } else if (pawnIndex == maxPawnIndex
                           && tallyPawnsArray[pawnIndex][0] - tallyPawnsArray[pawnIndex - 1][0] > 1) {
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
            if (colorsTurnItIs == colorOnTop) {
                if (tallyPawnsArray[pawnIndex][0] == 0) {
                    continue;
                }
                if (pawnIndex < 7 && tallyPawnsArray[pawnIndex][1] == tallyPawnsArray[pawnIndex + 1][1]
                    && tallyPawnsArray[pawnIndex][0] == tallyPawnsArray[pawnIndex + 1][0]) {
                    doubledPawns++;
                } else if (boardArray[tallyPawnsArray[pawnIndex][0]][tallyPawnsArray[pawnIndex][0] - 1] != 0) {
                    blockedPawns++;
                }
            } else {
                if (tallyPawnsArray[pawnIndex][0] == 7) {
                    continue;
                }
                if (pawnIndex < 7 && tallyPawnsArray[pawnIndex][1] == tallyPawnsArray[pawnIndex + 1][1]
                    && tallyPawnsArray[pawnIndex][0] == tallyPawnsArray[pawnIndex + 1][0]) {
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

    private double evaluateBoard(final int[][] boardArray, final int colorsTurnItIs
                                ) throws AlgorithmBadArgumentException {
        String boardStr = String.format("%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o"
            + "%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o"
            + "%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o%03o"
            + "%03o%03o%03o%03o%03o%03o%03o%03o",
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
        int xIdx = 0;
        int yIdx = 0;

        int thisColorIndex = colorsTurnItIs == WHITE ? whiteIndex : blackIndex;
        int otherColorIndex = colorsTurnItIs == WHITE ? blackIndex : whiteIndex;

        double[][] piecesCounts = new double[2][6];

        int whiteKingNotInCheckBonus = isKingThreatened(boardArray, BLACK) ? 0 : 1;
        int blackKingNotInCheckBonus = isKingThreatened(boardArray, WHITE) ? 0 : 1;

        for (int[] boardRow : boardArray) {
            for (int pieceInt : boardRow) {
                switch (pieceInt) {
                    case 0: break;
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

        double thisMobility = colorMobility(boardArray, colorOfAI);
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
        evaluateBoardMemoizeMap.put(boardStr, totalScore);
        return totalScore;
    };

    private int[][] copyBoard(final int[][] boardArray) {
        int[][] newBoardArray = new int[8][8];

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                if (boardArray[xIdx][yIdx] == 0) {
                    continue;
                }
                newBoardArray[xIdx][yIdx] = boardArray[xIdx][yIdx];
            }
        }

        return newBoardArray;
    }

    private double algorithmCallExecutor(final int[][] boardArray, boolean maximize,
                                         final int[] moveArray, final int colorsTurnItIs,
                                         final int depth, final double alpha, final double beta
                                         ) throws AlgorithmBadArgumentException {
        int fromXIdx = moveArray[1];
        int fromYIdx = moveArray[2];
        int toXIdx = moveArray[3];
        int toYIdx = moveArray[4];
        int[][] newBoardArray;
        double retval;

        /* The same boardArray is passed down the call stack and reused by
           every step of the algorithm, to avoid having to clone it each time.
           That means I need to execute this moveArray's move on the board,
           execute the recursive call, and then undo the move so the board can
           be reused. savedPieceInt holds whatever was at the square the piece
           was moved to so it can be restored. */

        int savedPiece = boardArray[toXIdx][toYIdx];
        boardArray[toXIdx][toYIdx] = moveArray[0];
        boardArray[fromXIdx][fromYIdx] = 0;

        retval = algorithmLowerLevel(boardArray, maximize, depth - 1, colorsTurnItIs, alpha, beta);

        boardArray[fromXIdx][fromYIdx] = boardArray[toXIdx][toYIdx];
        boardArray[toXIdx][toYIdx] = savedPiece;

        return retval;
    }

    public Move algorithmTopLevel(final Chessboard chessboard
                                 ) throws AlgorithmBadArgumentException, AlgorithmInternalError {
        int[][] boardArray = new int[8][8];
        int[][] movesArray = new int[128][6];
        double bestScore;
        int[] bestMoveArray = null;
        int movesArrayUsedLength;
        double alpha;
        double beta;
        Move bestMoveObj;
        Iterator<Piece> boardIter = chessboard.iterator();
        Random randomSource = new Random();

        alpha = Double.NEGATIVE_INFINITY;
        beta = Double.POSITIVE_INFINITY;
        bestScore = Double.NEGATIVE_INFINITY;

        while (boardIter.hasNext()) {
            Piece nextPiece = boardIter.next();
            int[] pieceCoords = chessboard.algNotnLocToNumericIndexes(nextPiece.getLocation());
            int pieceNumber = pieceIdentitiesToInts.get(nextPiece.getIdentity());
            boardArray[pieceCoords[0]][pieceCoords[1]] = pieceNumber;
        }

        movesArrayUsedLength = generatePossibleMoves(boardArray, movesArray, colorOfAI);

        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            double thisScore = algorithmCallExecutor(boardArray, true, movesArray[moveIdx], colorOfAI,
                                                     algorithmStartingDepth, alpha, beta);
            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestMoveArray = movesArray[moveIdx];
            }
            if (thisScore > alpha) {
                alpha = thisScore;
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

    private double algorithmLowerLevel(final int[][] boardArray, boolean maximize, final int depth,
                                       final int colorsTurnItIs, final double alphaArg, final double betaArg
                                       ) throws AlgorithmBadArgumentException {
        int colorOpposing = colorsTurnItIs == WHITE ? BLACK : WHITE;
        double bestScore;
        double thisScore;
        double alpha = alphaArg;
        double beta = betaArg;
        int[][] movesArray = new int[128][6];
        int thisColorKingsMovesCount;
        int otherColorKingsMovesCount;
        int xIdx = 0;
        int yIdx = 0;
        int movesArrayUsedLength;
        
        if (depth == 0) {
            double score = evaluateBoard(boardArray, colorsTurnItIs);
            return score;
        } else if (algorithmStartingDepth - depth >= 2) {
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

            if (isKingThreatened(boardArray, colorOpposing)
                && generateKingsMoves(boardArray, null, 0, xIdx, yIdx, colorsTurnItIs) == 0) {
                if (colorOpposing == colorOfPlayer) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }

        bestScore = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        movesArrayUsedLength = generatePossibleMoves(boardArray, movesArray, colorOpposing);

        for (int moveIdx = 0; moveIdx < movesArrayUsedLength; moveIdx++) {
            thisScore = algorithmCallExecutor(boardArray, !maximize, movesArray[moveIdx], colorOpposing, depth, alpha, beta);
            if (maximize && thisScore == Double.POSITIVE_INFINITY || thisScore == Double.NEGATIVE_INFINITY) {
                return thisScore;
            }
            if (maximize && thisScore > alpha) {
                alpha = thisScore;
            } else if (!maximize && thisScore < beta) {
                beta = thisScore;
            }
            if (maximize && thisScore >= beta || thisScore <= alpha) {
                return thisScore;
            }
            if (maximize && thisScore > bestScore || thisScore < bestScore) {
                bestScore = thisScore;
            }
        }

        return bestScore;
    }
}
