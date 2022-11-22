package com.kmfahey.jchessgame.junit;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.StringJoiner;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.kmfahey.jchessgame.MinimaxRunner;
import com.kmfahey.jchessgame.LogFormatter;
import com.kmfahey.jchessgame.LogFormatter;
import com.kmfahey.jchessgame.AlgorithmBadArgumentException;

public final class TestMinimaxRunner extends TestCase {

    public static final int WHITE = MinimaxRunner.WHITE;
    public static final int BLACK = MinimaxRunner.BLACK;

    public static final int PAWN = MinimaxRunner.PAWN;
    public static final int ROOK = MinimaxRunner.ROOK;
    public static final int KNIGHT = MinimaxRunner.KNIGHT;
    public static final int BISHOP = MinimaxRunner.BISHOP;
    public static final int QUEEN = MinimaxRunner.QUEEN;
    public static final int KING = MinimaxRunner.KING;

    public static final int LEFT = MinimaxRunner.LEFT;
    public static final int RIGHT = MinimaxRunner.RIGHT;

    private static Logger logger;
    private static FileHandler logHandler;
    private MinimaxRunner runner;

    private int[][] movesArray;

    public TestMinimaxRunner() {
        super();
    
        LogManager.getLogManager().reset();
        logger = Logger.getLogger("scraper_main");
        logger.setLevel(Level.INFO);

        try {
            logHandler = new FileHandler("./testMinimaxRunner.log");
            logHandler.setFormatter(new LogFormatter());
            logger.addHandler(logHandler);
            logger.info("test");
        } catch (SecurityException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private Method getSurveyKingsMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyKingsMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSurveyQueensMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyQueensMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class, int.class, int.class, boolean.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSurveyBishopsMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyBishopsMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class, int.class, int.class, boolean.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSurveyKnightsMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyKnightsMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class, boolean.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSurveyRooksMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyRooksMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class, int.class, int.class, boolean.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSurveyPawnsMoveThreatenedSquares() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("surveyPawnsMoveThreatenedSquares", new Class[] {int[][].class, int[].class, int.class, boolean.class});
        method.setAccessible(true);
        return method;
    }

    private Method getIsKingThreatened() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("isKingThreatened", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGenerateKingsMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generateKingsMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGenerateQueensMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generateQueensMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGenerateBishopsMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generateBishopsMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGenerateKnightsMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generateKnightsMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGenerateRooksMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generateRooksMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getGeneratePawnsMoves() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("generatePawnsMoves", new Class[] {int[][].class, int[][].class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    private Method getSetMoveToMovesArray() throws NoSuchMethodException, SecurityException {
        Method method = MinimaxRunner.class.getDeclaredMethod("setMoveToMovesArray", new Class[] {int[][].class, int.class, int.class, int.class, int.class, int.class, int.class, int.class});
        method.setAccessible(true);
        return method;
    }

    public void setUp() {
        runner = new MinimaxRunner("white", "white");
        movesArray = new int[32][10];
    }

 /* public void tearDown() throws
SQLException
 {
        assert true;
    } */

    private boolean arraysUnorderedEquality(final int runIdx, final int[][] lhsArray,
                                            final int[][] rhsArray, final int rhsLength) {
        ArrayList<Integer> expectedRhsIndexes = new ArrayList<Integer>(
            IntStream.range(0, lhsArray.length).boxed().collect(Collectors.toList()));
        ArrayList<Integer> testedEqualRhsIndexes = new ArrayList<>();
        boolean arraysEqual;

        for (int lhsIndex = 0; lhsIndex < lhsArray.length; lhsIndex++) {
            for (int rhsIndex = 0; rhsIndex < rhsLength; rhsIndex++) {
                if (testedEqualRhsIndexes.contains(rhsIndex)) {
                    continue;
                } else if (Arrays.equals(lhsArray[lhsIndex], rhsArray[rhsIndex])) {
                    testedEqualRhsIndexes.add(rhsIndex);
                    break;
                }
            }
        }

        Collections.sort(testedEqualRhsIndexes);
        arraysEqual = expectedRhsIndexes.equals(testedEqualRhsIndexes);

        StringJoiner outerJoiner = new StringJoiner(",\\n", "new int[] {\\n", "\\n}\\n");
        for (int outerIndex = 0; outerIndex < rhsArray.length; outerIndex++) {
            if (rhsArray[outerIndex][0] == 0) {
                break;
            }
            StringJoiner innerJoiner = new StringJoiner(", ", "new int[] {", "}");
            for (int innerIndex = 0; innerIndex < rhsArray[outerIndex].length; innerIndex++) {
                innerJoiner.add(String.valueOf(rhsArray[outerIndex][innerIndex]));
            }
            outerJoiner.add(innerJoiner.toString());
        }
        logger.info("run #" + runIdx + ": " + outerJoiner.toString());

        return arraysEqual;
    }

    public void testGeneratePawnsMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  wp, 0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wp, 1, 1, 1, 2, 0, 0, 0, 0, 0},
            new int[] {wp, 1, 1, 1, 3, 0, 0, 0, 0, 0},
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 1, WHITE);

        assertTrue("run #1", arraysUnorderedEquality(1, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  wp, 0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wp, 1, 2, 1, 3, 0, 0, 0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 2, WHITE);

        assertTrue("run #2", arraysUnorderedEquality(2, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,  wp, 0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wp, 1, 2, 0, 3, bp, 0, 0, 0, 0},
            new int[] {wp, 1, 2, 1, 3, 0,  0, 0, 0, 0},
            new int[] {wp, 1, 2, 2, 3, bp, 0, 0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 2, WHITE);

        assertTrue("run #3", arraysUnorderedEquality(3, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_4() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  wp, wp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 2, WHITE);

        assertTrue("run #4", arraysUnorderedEquality(4, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_5() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {136, 1, 6, 1, 5, 0, 0, 0, 0, 0},
            new int[] {136, 1, 6, 1, 4, 0, 0, 0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 6, BLACK);

        assertTrue("run #5", arraysUnorderedEquality(5, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_6() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  bp, 0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {bp, 1, 5, 1, 4, 0, 0, 0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 5, BLACK);

        assertTrue("run #6", arraysUnorderedEquality(6, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_7() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  bp, 0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {bp, 1, 5, 1, 4, 0, 0, 0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 5, BLACK);

        assertTrue("run #7", arraysUnorderedEquality(7, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_8() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int bk = BLACK | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  bk, 0,  0,  0},
            new int[] {0,  0,  wp, 0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wp, 1, 2, 1, 3, 0, bk, 0, 4, 1}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 2, WHITE);

        assertTrue("run #8", arraysUnorderedEquality(8, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_9() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int wk = WHITE | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  wk, 0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {bp, 1, 6, 1, 5, 0, wk, 0, 4, 1},
            new int[] {bp, 1, 6, 1, 4, 0, 0,  0, 0, 0}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 6, BLACK);

        assertTrue("run #9", arraysUnorderedEquality(9, expectedMoves, movesArray, moveIdx));
    }

    public void testGeneratePawnsMoves_10() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  wp, 0,  0},
            new int[] {0,  0,  0,  0,  wp, 0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  wp, 0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {bp, 1, 6, 1, 5, 0, 0, 0, 0, 0},
            new int[] {bp, 1, 6, 2, 5, wp, 0, 0, 0, 1},
            new int[] {bp, 1, 6, 0, 5, wp, 0, 0, 0, 1}
        };

        method = getGeneratePawnsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 6, BLACK);

        assertTrue("run #10", arraysUnorderedEquality(10, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {wr, 0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 2, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 4, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 5, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 6, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 0, 7, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 2, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 3, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 4, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 5, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 6, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 0, 7, 0, 0, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 0, WHITE);

        assertTrue("run #11", arraysUnorderedEquality(11, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  wr, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 3, 0, 0, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 1, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 2, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 4, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 5, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 6, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 7, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 1, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 2, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 3, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 4, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 5, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 6, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 7, 3, 0, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 3, WHITE);

        assertTrue("run #12", arraysUnorderedEquality(12, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wp = WHITE | PAWN;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  wp, 0,  wr, 0,  0,  wp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  wp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 3, 0, 2, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 4, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 0, 5, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 1, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 2, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 3, 3, 0, 0, 0, 0, 0},
            new int[] {wr, 0, 3, 4, 3, 0, 0, 0, 0, 0},
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 3, WHITE);

        assertTrue("run #13", arraysUnorderedEquality(13, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_4() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  bp, 0,  wr, 0,  0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 3, 0, 1, bp, 0, 0, 0, 1},
            new int[] {wr, 0, 3, 0, 2, 0,  0, 0, 0, 2},
            new int[] {wr, 0, 3, 0, 4, 0,  0, 0, 0, 2},
            new int[] {wr, 0, 3, 0, 5, 0,  0, 0, 0, 2},
            new int[] {wr, 0, 3, 0, 6, bp, 0, 0, 0, 1},
            new int[] {wr, 0, 3, 1, 3, 0,  0, 0, 0, 1},
            new int[] {wr, 0, 3, 2, 3, 0,  0, 0, 0, 1},
            new int[] {wr, 0, 3, 3, 3, 0,  0, 0, 0, 1},
            new int[] {wr, 0, 3, 4, 3, 0,  0, 0, 0, 1},
            new int[] {wr, 0, 3, 5, 3, bp, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 3, WHITE);

        assertTrue("run #14", arraysUnorderedEquality(14, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_5() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int bk = BLACK | KING;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {bk, bp, 0,  wr, 0,  0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 3, 0, 1, bp, bk, 0, 0, 2},
            new int[] {wr, 0, 3, 0, 2, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 4, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 5, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 6, bp, 0,  0, 0, 1},
            new int[] {wr, 0, 3, 1, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 2, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 3, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 4, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 5, 3, bp, 0,  0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 3, WHITE);

        assertTrue("run #15", arraysUnorderedEquality(15, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_6() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int bk = BLACK | KING;
        int wr = WHITE | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {bk, bp, 0,  wr, 0,  0,  bp, 0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wr, 0, 3, 0, 1, bp, bk, 0, 0, 2},
            new int[] {wr, 0, 3, 0, 2, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 4, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 5, 0,  0,  0, 0, 2},
            new int[] {wr, 0, 3, 0, 6, bp, 0,  0, 0, 1},
            new int[] {wr, 0, 3, 1, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 2, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 3, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 4, 3, 0,  0,  0, 0, 1},
            new int[] {wr, 0, 3, 5, 3, bp, 0,  0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 3, WHITE);

        assertTrue("run #16", arraysUnorderedEquality(16, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_7() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int br = BLACK | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0, br},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {br, 0, 7, 0, 0, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 1, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 2, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 3, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 4, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 5, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 0, 6, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 1, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 2, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 3, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 4, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 5, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 6, 7, 0, 0, 0, 0, 0},
            new int[] {br, 0, 7, 7, 7, 0, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 7, BLACK);

        assertTrue("run #17", arraysUnorderedEquality(17, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_8() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int br = BLACK | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0, br,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {br, 7, 4, 7, 5, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 6, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 7, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 3, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 2, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 1, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 0, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 6, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 5, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 4, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 3, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 2, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 1, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 0, 4, 0, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 7, 4, BLACK);

        assertTrue("run #18", arraysUnorderedEquality(18, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_9() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int bp = BLACK | PAWN;
        int br = BLACK | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  bp, 0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  bp, 0,  0,  br, 0,  bp, 0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {br, 7, 4, 7, 5, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 3, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 7, 2, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 6, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 5, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 4, 4, 0, 0, 0, 0, 0},
            new int[] {br, 7, 4, 3, 4, 0, 0, 0, 0, 0}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 7, 4, BLACK);

        assertTrue("run #19", arraysUnorderedEquality(19, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_10() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KING;
        int wp = WHITE | PAWN;
        int br = BLACK | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  wk, 0,  0,  0},
            new int[] {0,  0,  0,  0,  wp, 0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  wp, 0,  0,  br, 0,  wp, 0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {br, 7, 4, 2, 4, wp, wk, 1, 4, 1},
            new int[] {br, 7, 4, 3, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 4, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 5, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 6, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 7, 1, wp, 0,  0, 0, 1},
            new int[] {br, 7, 4, 7, 2, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 3, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 5, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 6, wp, 0,  0, 0, 1}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 7, 4, BLACK);

        assertTrue("run #20", arraysUnorderedEquality(20, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateRooksMoves_11() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KING;
        int wp = WHITE | PAWN;
        int br = BLACK | ROOK;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  wk, 0,  0,  0},
            new int[] {0,  0,  0,  0,  wp, 0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  wp, 0,  0,  br, 0,  wp, 0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {br, 7, 4, 2, 4, wp, wk, 1, 4, 1},
            new int[] {br, 7, 4, 3, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 4, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 5, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 6, 4, 0,  0,  0, 0, 1},
            new int[] {br, 7, 4, 7, 1, wp, 0,  0, 0, 1},
            new int[] {br, 7, 4, 7, 2, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 3, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 5, 0,  0,  0, 0, 2},
            new int[] {br, 7, 4, 7, 6, wp, 0,  0, 0, 1}
        };

        method = getGenerateRooksMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 7, 4, BLACK);

        assertTrue("run #21", arraysUnorderedEquality(21, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKnightsMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KNIGHT | LEFT;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  wk, 0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wk, 3, 4, 1, 3, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 1, 5, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 2, 2, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 2, 6, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 4, 2, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 4, 6, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 5, 3, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 4, 5, 5, 0, 0, 0, 0, 0}
        };

        method = getGenerateKnightsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 4, WHITE);

        assertTrue("run #22", arraysUnorderedEquality(22, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKnightsMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KNIGHT | LEFT;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  wk, 0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {89, 1, 1, 0, 3, 0, 0, 0, 0, 0},
            new int[] {89, 1, 1, 2, 3, 0, 0, 0, 0, 0},
            new int[] {89, 1, 1, 3, 0, 0, 0, 0, 0, 0},
            new int[] {89, 1, 1, 3, 2, 0, 0, 0, 0, 0}
        };

        method = getGenerateKnightsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 1, WHITE);

        assertTrue("run #23", arraysUnorderedEquality(23, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKnightsMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wkn = WHITE | KNIGHT | LEFT;
        int bkg = BLACK | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  wkn, 0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  bkg,0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wkn, 1, 1, 0, 3, 0, 0,   0, 0, 0},
            new int[] {wkn, 1, 1, 2, 3, 0, 0,   0, 0, 0},
            new int[] {wkn, 1, 1, 3, 0, 0, 0,   0, 0, 0},
            new int[] {wkn, 1, 1, 3, 2, 0, bkg, 5, 3, 1}
        };

        method = getGenerateKnightsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 1, WHITE);

        assertTrue("run #24", arraysUnorderedEquality(24, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKnightsMoves_4() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wkn = WHITE | KNIGHT | LEFT;
        int wp = WHITE | PAWN;
        int bp = BLACK | PAWN;
        int bkg = BLACK | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  wp, 0,  0,  0,  0},
            new int[] {0,  wkn, 0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  bp, 0,  0,  0,  0},
            new int[] {wp, 0,   bp, 0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  bkg,0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wkn, 1, 1, 2, 3, bp, 0,   0, 0, 0},
            new int[] {wkn, 1, 1, 3, 2, bp, bkg, 5, 3, 1}
        };

        method = getGenerateKnightsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 1, 1, WHITE);

        assertTrue("run #25", arraysUnorderedEquality(25, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateBishopsMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wb = WHITE | BISHOP;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {wb, 0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wb, 0, 0, 1, 1, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 2, 2, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 3, 3, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 4, 4, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 5, 5, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 6, 6, 0, 0, 0, 0, 0},
            new int[] {wb, 0, 0, 7, 7, 0, 0, 0, 0, 0},
        };

        method = getGenerateBishopsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 0, 0, WHITE);

        assertTrue("run #26", arraysUnorderedEquality(26, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateBishopsMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wb = WHITE | BISHOP;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  wb, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wb, 3, 3, 0, 0, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 0, 6, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 1, 1, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 1, 5, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 2, 2, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 2, 4, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 4, 2, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 4, 4, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 5, 1, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 5, 5, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 6, 0, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 6, 6, 0, 0, 0, 0, 0},
            new int[] {wb, 3, 3, 7, 7, 0, 0, 0, 0, 0}
        };

        method = getGenerateBishopsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 3, WHITE);

        assertTrue("run #27", arraysUnorderedEquality(27, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateBishopsMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wb = WHITE | BISHOP;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  bp,  0,  0,  0,  bp, 0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  wb, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  bp,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  bp, 0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wb, 3, 3, 1, 1, bp, 0, 0, 0, 1},
            new int[] {wb, 3, 3, 1, 5, bp, 0, 0, 0, 1},
            new int[] {wb, 3, 3, 2, 2, 0,  0, 0, 0, 2},
            new int[] {wb, 3, 3, 2, 4, 0,  0, 0, 0, 2},
            new int[] {wb, 3, 3, 4, 2, 0,  0, 0, 0, 2},
            new int[] {wb, 3, 3, 4, 4, 0,  0, 0, 0, 2},
            new int[] {wb, 3, 3, 5, 1, bp, 0, 0, 0, 1},
            new int[] {wb, 3, 3, 5, 5, 0,  0, 0, 0, 2},
            new int[] {wb, 3, 3, 6, 6, bp, 0, 0, 0, 1}
        };

        method = getGenerateBishopsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 3, WHITE);

        assertTrue("run #28", arraysUnorderedEquality(28, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateBishopsMoves_4() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wb = WHITE | BISHOP;
        int bp = BLACK | PAWN;
        int bk = BLACK | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  bk, 0},
            new int[] {0,  bp,  0,  0,  0,  bp, 0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  wb, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  bp,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  bp, 0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wb, 3, 3, 1, 1, bp, 0,  0, 0, 1},
            new int[] {wb, 3, 3, 1, 5, bp, bk, 0, 6, 2},
            new int[] {wb, 3, 3, 2, 2, 0,  0,  0, 0, 2},
            new int[] {wb, 3, 3, 2, 4, 0,  0,  0, 0, 2},
            new int[] {wb, 3, 3, 4, 2, 0,  0,  0, 0, 2},
            new int[] {wb, 3, 3, 4, 4, 0,  0,  0, 0, 2},
            new int[] {wb, 3, 3, 5, 1, bp, 0,  0, 0, 1},
            new int[] {wb, 3, 3, 5, 5, 0,  0,  0, 0, 2},
            new int[] {wb, 3, 3, 6, 6, bp, 0,  0, 0, 1}
        };

        method = getGenerateBishopsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 3, WHITE);

        assertTrue("run #29", arraysUnorderedEquality(29, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateBishopsMoves_5() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wb = WHITE | BISHOP;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  wp,  0,  0,  0,  wp, 0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  wb, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  wp,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  wp, 0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wb, 3, 3, 2, 2, 0,  0, 0, 0, 0},
            new int[] {wb, 3, 3, 2, 4, 0,  0, 0, 0, 0},
            new int[] {wb, 3, 3, 4, 2, 0,  0, 0, 0, 0},
            new int[] {wb, 3, 3, 4, 4, 0,  0, 0, 0, 0},
            new int[] {wb, 3, 3, 5, 5, 0,  0, 0, 0, 0},
        };

        method = getGenerateBishopsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 3, WHITE);

        assertTrue("run #30", arraysUnorderedEquality(30, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateQueensMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wq = WHITE | QUEEN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wq, 0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wq, 3, 0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 1, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 2, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 2, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 1, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 0, 3, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 3, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 4, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 5, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 6, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 7, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 4, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 5, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 6, 3, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 7, 4, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 4, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 5, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 6, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 7, 0, 0, 0, 0, 0, 0}
        };

        method = getGenerateQueensMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #31", arraysUnorderedEquality(31, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateQueensMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wq = WHITE | QUEEN;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {wp, 0,   0,  wp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wq, 0,   0,  wp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wp, 0,   0,  wp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wq, 3, 0, 2, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 1, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 2, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 1, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 3, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 4, 1, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 5, 2, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 4, 0, 0, 0, 0, 0, 0},
            new int[] {wq, 3, 0, 5, 0, 0, 0, 0, 0, 0}
        };

        method = getGenerateQueensMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #32", arraysUnorderedEquality(32, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateQueensMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wq = WHITE | QUEEN;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {bp, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wq, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {bp, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wq, 3, 0, 2, 0, 0,  0, 0, 0, 2},
            new int[] {wq, 3, 0, 1, 0, 0,  0, 0, 0, 2},
            new int[] {wq, 3, 0, 0, 0, bp, 0, 0, 0, 3},
            new int[] {wq, 3, 0, 2, 1, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 1, 2, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 0, 3, bp, 0, 0, 0, 2},
            new int[] {wq, 3, 0, 3, 1, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 3, 2, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 3, 3, bp, 0, 0, 0, 4},
            new int[] {wq, 3, 0, 4, 1, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 5, 2, 0,  0, 0, 0, 1},
            new int[] {wq, 3, 0, 6, 3, bp, 0, 0, 0, 2},
            new int[] {wq, 3, 0, 4, 0, 0,  0, 0, 0, 2},
            new int[] {wq, 3, 0, 5, 0, 0,  0, 0, 0, 2},
            new int[] {wq, 3, 0, 6, 0, bp, 0, 0, 0, 3}
        };

        method = getGenerateQueensMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #33", arraysUnorderedEquality(33, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateQueensMoves_4() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wq = WHITE | QUEEN;
        int bp = BLACK | PAWN;
        int bk = BLACK | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {bp, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wq, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {bp, 0,   0,  bp, 0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  bk, 0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wq, 3, 0, 2, 0, 0,  0,  0, 0, 2},
            new int[] {wq, 3, 0, 1, 0, 0,  0,  0, 0, 2},
            new int[] {wq, 3, 0, 0, 0, bp, 0,  0, 0, 3},
            new int[] {wq, 3, 0, 2, 1, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 1, 2, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 0, 3, bp, 0,  0, 0, 2},
            new int[] {wq, 3, 0, 3, 1, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 3, 2, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 3, 3, bp, 0,  0, 0, 4},
            new int[] {wq, 3, 0, 4, 1, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 5, 2, 0,  0,  0, 0, 1},
            new int[] {wq, 3, 0, 6, 3, bp, bk, 7, 4, 3},
            new int[] {wq, 3, 0, 4, 0, 0,  0,  0, 0, 2},
            new int[] {wq, 3, 0, 5, 0, 0,  0,  0, 0, 2},
            new int[] {wq, 3, 0, 6, 0, bp, 0,  0, 0, 3}
        };

        method = getGenerateQueensMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #34", arraysUnorderedEquality(34, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKingsMoves_1() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KING;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {wk, 0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wk, 3, 0, 2, 0, 0, 0,  0, 0, 0},
            new int[] {wk, 3, 0, 2, 1, 0, 0,  0, 0, 0},
            new int[] {wk, 3, 0, 3, 1, 0, 0,  0, 0, 0},
            new int[] {wk, 3, 0, 4, 0, 0, 0,  0, 0, 0},
            new int[] {wk, 3, 0, 4, 1, 0, 0,  0, 0, 0}
        };

        method = getGenerateKingsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #35", arraysUnorderedEquality(35, expectedMoves, movesArray, moveIdx));
    }

    public void testGenerateKingsMoves_2() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Method method;
        int wk = WHITE | KING;
        int wp = WHITE | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  wp,  0,  0,  0,  0,  0,  0},
            new int[] {wk, 0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  wp,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0},
            new int[] {0,  0,   0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wk, 3, 0, 2, 0, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 0, 3, 1, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 0, 4, 0, 0, 0, 0, 0, 0},
        };

        method = getGenerateKingsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #36", arraysUnorderedEquality(36, expectedMoves, movesArray, moveIdx));
    }

    /* public void testGenerateKingsMoves_3() throws AlgorithmBadArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        int wk = WHITE | KING;
        int bp = BLACK | PAWN;
        int moveIdx = 0;
        int[][] testBoard = new int[][] {
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {wk, 0,  bp, 0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0},
            new int[] {0,  0,  0,  0,  0,  0,  0,  0}
        };
        int[][] expectedMoves = new int[][] {
            new int[] {wk, 3, 0, 2, 0, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 0, 3, 1, 0, 0, 0, 0, 0},
            new int[] {wk, 3, 0, 4, 0, 0, 0, 0, 0, 0},
        };

        method = getGenerateKingsMoves();
        moveIdx = (int) method.invoke(runner, testBoard, movesArray, moveIdx, 3, 0, WHITE);

        assertTrue("run #37", arraysUnorderedEquality(37, expectedMoves, movesArray, moveIdx));
    } */

}
