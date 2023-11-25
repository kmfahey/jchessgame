package org.kmfahey.jchessgame;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Computes and makes available (as Dimension and Insets objects) all the
 * measurements needed by BoardView for it to draw a chessboard in its
 * JComponent region using Graphics.fillRect() calls. Its computed values
 * for the dimensions of an individual square on the board are also used by
 * ImagesManager to scale the chesspiece icon Image objects it loads from
 * ./images/. It furnishes these values via accessors.
 */
public class CoordinatesManager {

    /* The primary measurements that other values are computed from. Crucially,
       these are all scaled such that if they were used unmodified, the board
       that resulted would be in proportion with the unmodified size of the
       chesspiece icons stored in the ./images/ directory; each one is 234px x
       234px. */

    /** Measurement of the chessboard proportionate to the unscaled piece icons. */
    public static final float TOTAL_BOARD_MEASUREMENT_100PCT = 2002F;

    /** Measurement of the outer black border proportionate to the unscaled piece icons. */
    public static final float OUTER_BLACK_BORDER_WIDTH_100PCT = 12F;

    /** Measurement of the outer beige margin proportionate to the unscaled piece icons. */
    public static final float BEIGE_MARGIN_WIDTH_100PCT = 47F;

    /** Measurement of the inner black border proportionate to the unscaled piece icons. */
    public static final float INNER_BLACK_BORDER_WIDTH_100PCT = 6F;

    /** Measurement of a chessboard square proportionate to the unscaled piece icons. */
    public static final float SQUARE_MEASUREMENT_100PCT = 234F;

    /** The locations, in squarefield coordinates, of the squares that should be
        light-colored for the chessboard to have its correct alternating light
        and dark square pattern. */
    public static final int[][] LIGHT_COLORED_SQUARES_COORDS = new int[][] {
        new int[] {0, 0}, new int[] {0, 2}, new int[] {0, 4}, new int[] {0, 6},
        new int[] {1, 1}, new int[] {1, 3}, new int[] {1, 5}, new int[] {1, 7},
        new int[] {2, 0}, new int[] {2, 2}, new int[] {2, 4}, new int[] {2, 6},
        new int[] {3, 1}, new int[] {3, 3}, new int[] {3, 5}, new int[] {3, 7},
        new int[] {4, 0}, new int[] {4, 2}, new int[] {4, 4}, new int[] {4, 6},
        new int[] {5, 1}, new int[] {5, 3}, new int[] {5, 5}, new int[] {5, 7},
        new int[] {6, 0}, new int[] {6, 2}, new int[] {6, 4}, new int[] {6, 6},
        new int[] {7, 1}, new int[] {7, 3}, new int[] {7, 5}, new int[] {7, 7}
    };

    /** Dimensions of the entire chessboard, scaled by the scaleProportion. */
    private final Dimension totalBoardDimensions;

    /** Insets of the board's outer beige margin, scaled by the scaleProportion. */
    private final Insets beigeMarginInsets;

    /** Dimensions of the board's outer beige margin, scaled by the scaleProportion. */
    private final Dimension beigeMarginDimensions;

    /** Insets of the board's inner black border, scaled by the scaleProportion. */
    private final Insets innerBlackBorderInsets;

    /** Dimensions of the board's inner black border, scaled by the scaleProportion. */
    private final Dimension innerBlackBorderDimensions;

    /** Insets of the board's squarefield, scaled by the scaleProportion. */
    private final Insets boardSquareFieldInsets;

    /** Dimensions of the board's squarefield, scaled by the scaleProportion. */
    private final Dimension boardSquareFieldDimensions;

    /** Dimensions of a single square on the board, scaled by the scaleProportion. */
    private final Dimension squareDimensions;

    /** Coordinates of the upper left corner of the squarefield. */
    private final Point upperLeftCornerOfSquaresRegion;

    /**
     * Initializes the CoordinatesManager object, completing all the
     * calculations and instancing all the Dimension and Insets objects needed
     * by BoardView to draw the chessboard and place chesspiece icons on it.
     *
     * @param scaleProportion A value greater than 0.0 and (on all but the
     *                        largest monitors) less than 1.0.
     *                        It's the ratio between the constant
     *                        TOTAL_BOARD_MEASUREMENT_100PCT and the side
     *                        measurement that the BoardView object was
     *                        initialized to. It's used to scale all the other
     *                        measurements the object computes.
     */
    public CoordinatesManager(final float scaleProportion) {
        /* The scaling proportion is a calculated value which expresses, as a
           decimal usually between 0.0 and 1.0, the ratio between the default
           board measurement TOTAL_BOARD_MEASUREMENT_100PCT, and the actual side
           measurement the BoardView object has been instanced as. Every default
           measurement is proportionate to TOTAL_BOARD_MEASUREMENT_100PCT, so
           this scaling value is applied to all of them to arrive at the actual
           values of the board dimensions that will fit in the board's actual
           size. */

        /* The proportion between TOTAL_BOARD_MEASUREMENT_100PCT and the actual side
           measurement the BoardView object was instanced to. */

        /* The scaled values of all the primary measurement values are computed.
           Left and right values are computed separately because it may be
           necessary to adjust them separately by +/- 1px. */
        /* The local measurements that are derived from the primary measurements
           by scaling them, assigning separate values for their left and right
           measures, and (if necessary) applying a +/- 1px adjustment to bring the
           measurements back into true. */
        int totalBoardMeasurement = (int) Math.round(TOTAL_BOARD_MEASUREMENT_100PCT * (double) scaleProportion);
        int rightOuterBlackBorderWidth = (int) Math.round(OUTER_BLACK_BORDER_WIDTH_100PCT * (double) scaleProportion);
        int leftOuterBlackBorderWidth = (int) Math.round(OUTER_BLACK_BORDER_WIDTH_100PCT * (double) scaleProportion);
        int rightBeigeMarginWidth = (int) Math.round(BEIGE_MARGIN_WIDTH_100PCT * (double) scaleProportion);
        int leftBeigeMarginWidth = (int) Math.round(BEIGE_MARGIN_WIDTH_100PCT * (double) scaleProportion);
        int rightInnerBlackBorderWidth = (int) Math.round(INNER_BLACK_BORDER_WIDTH_100PCT * (double) scaleProportion);
        int leftInnerBlackBorderWidth = (int) Math.round(INNER_BLACK_BORDER_WIDTH_100PCT * (double) scaleProportion);
        int squareMeasurement = (int) Math.round(SQUARE_MEASUREMENT_100PCT * (double) scaleProportion);

        /* Due to rounding issues, the total value of the 8 squares plus the
           left and right values of all 3 margins can sum up to a number that's
           up to +6 greater than the board's measurement, or as low as -6
           below it. (Detailed trials established those bounds.) The switch
           statement below increments or decrements the inner measurements to
           reduce that difference to 0. */

        int slopFactor = totalBoardMeasurement - squareMeasurement * 8
                         - rightOuterBlackBorderWidth - leftOuterBlackBorderWidth
                         - rightBeigeMarginWidth - leftBeigeMarginWidth
                         - rightInnerBlackBorderWidth - leftInnerBlackBorderWidth;

        switch (slopFactor) {
            case -6 -> {
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; rightBeigeMarginWidth -= 1;
                leftBeigeMarginWidth -= 1; rightOuterBlackBorderWidth -= 1; leftOuterBlackBorderWidth -= 1;
            }
            case -5 -> {
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; rightBeigeMarginWidth -= 1;
                leftBeigeMarginWidth -= 1; leftOuterBlackBorderWidth -= 1; }
            case -4 -> {
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1;
                leftOuterBlackBorderWidth -= 1;
            }
            case -3 -> {
                leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1; leftOuterBlackBorderWidth -= 1;
            }
            case -2 -> {
                leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1;
            }
            case -1 -> leftInnerBlackBorderWidth -= 1;
            case 1 -> leftOuterBlackBorderWidth += 1;
            case 2 -> {
                leftBeigeMarginWidth += 1; leftOuterBlackBorderWidth += 1;
            }
            case 3 -> {
                leftInnerBlackBorderWidth += 1; leftBeigeMarginWidth += 1; leftOuterBlackBorderWidth += 1;
            }
            case 4 -> {
                leftInnerBlackBorderWidth += 1; leftBeigeMarginWidth += 1; rightOuterBlackBorderWidth += 1;
                leftOuterBlackBorderWidth += 1;
            }
            case 5 -> {
                leftInnerBlackBorderWidth += 1; rightBeigeMarginWidth += 1; leftBeigeMarginWidth += 1;
                rightOuterBlackBorderWidth += 1; leftOuterBlackBorderWidth += 1;
            }
            case 6 -> {
                rightInnerBlackBorderWidth += 1; leftInnerBlackBorderWidth += 1; rightBeigeMarginWidth += 1;
                leftBeigeMarginWidth += 1; rightOuterBlackBorderWidth += 1; leftOuterBlackBorderWidth += 1;
            }
            default -> { }
        }

        /* Now that all the primary measurements have been scaled, and if
           necessary adjusted by +/- 1px each so all the measurements of the
           board line up to the pixel, the Dimension and Inset objects that are
           used to communicate the board's measurements can be instanced. */

        /* The total measurements of the board, used to draw the first black
           rectangle that will make up the outer black border. */
        totalBoardDimensions = new Dimension(totalBoardMeasurement, totalBoardMeasurement);

        /* The insets for the inner beige square that's drawn over it, providing
           the wider beige outer margin to the board. */
        beigeMarginInsets = new Insets(rightOuterBlackBorderWidth, rightOuterBlackBorderWidth,
                leftOuterBlackBorderWidth, leftOuterBlackBorderWidth);

        /* The dimensions of the beige rectangle the makes up the wider beige
           outer margin of the board. */
        int beigeMargin = totalBoardMeasurement - rightOuterBlackBorderWidth - leftOuterBlackBorderWidth;
        beigeMarginDimensions = new Dimension(beigeMargin, beigeMargin);

        /* The insets of the black rectangle that makes up the black inner
           margin of the board, and provides the black of the black squares. */
        int rightOuterBorderPlusMargin = rightOuterBlackBorderWidth + rightBeigeMarginWidth;
        int leftOuterBorderPlusMargin = leftOuterBlackBorderWidth + leftBeigeMarginWidth;
        innerBlackBorderInsets = new Insets(rightOuterBorderPlusMargin, rightOuterBorderPlusMargin,
                                            leftOuterBorderPlusMargin, leftOuterBorderPlusMargin);

        /* The dimensions of the black rectangle that makes up the black inner
           margin of the board, and provides the black of the black squares. */
        int innerSquaresPlusBorders = leftInnerBlackBorderWidth + rightInnerBlackBorderWidth + squareMeasurement * 8;
        innerBlackBorderDimensions = new Dimension(innerSquaresPlusBorders, innerSquaresPlusBorders);

        /* The insets of the squarefield of the board. */
        int rightBordersPlusMargins = rightOuterBlackBorderWidth + rightBeigeMarginWidth + rightInnerBlackBorderWidth;
        int leftBordersPlusMargins = leftOuterBlackBorderWidth + leftBeigeMarginWidth + leftInnerBlackBorderWidth;
        boardSquareFieldInsets = new Insets(rightBordersPlusMargins, rightBordersPlusMargins,
                                            leftBordersPlusMargins, leftBordersPlusMargins);

        /* The dimensions of the squarefield of the board. */
        boardSquareFieldDimensions = new Dimension(squareMeasurement * 8, squareMeasurement * 8);

        /* The dimensions of an individual square on the board. */
        squareDimensions = new Dimension(squareMeasurement, squareMeasurement);

        /* This Point saves the location of the upper left corner of the
           squarefield,  so it's easily accessible. */
        upperLeftCornerOfSquaresRegion = new Point(boardSquareFieldInsets.left, boardSquareFieldInsets.top);
    }

    /**
     * Accessor for the totalBoardDimensions instance variable.
     *
     * @return A Dimension object, the value of totalBoardDimensions.
     */
    public Dimension getTotalBoardDimensions() {
        return totalBoardDimensions;
    }

    /**
     * Accessor for the beigeMarginInsets instance variable.
     *
     * @return An Insets object, the value of beigeMarginInsets.
     */
    public Insets getBeigeMarginInsets() {
        return beigeMarginInsets;
    }

    /**
     * Accessor for the beigeMarginDimensions instance variable.
     *
     * @return A Dimension object, the value of beigeMarginDimensions.
     */
    public Dimension getBeigeMarginDimensions() {
        return beigeMarginDimensions;
    }

    /**
     * Accessor for the innerBlackBorderInsets instance variable.
     *
     * @return An Insets object, the value of innerBlackBorderInsets.
     */
    public Insets getInnerBlackBorderInsets() {
        return innerBlackBorderInsets;
    }

    /**
     * Accessor for the innerBlackBorderDimensions instance variable.
     *
     * @return A Dimension object, the value of innerBlackBorderDimensions.
     */
    public Dimension getInnerBlackBorderDimensions() {
        return innerBlackBorderDimensions;
    }

    /**
     * Accessor for the boardSquareFieldInsets instance variable.
     *
     * @return An Insets object, the value of boardSquareFieldInsets.
     */
    public Insets getBoardSquareFieldInsets() {
        return boardSquareFieldInsets;
    }

    /**
     * Accessor for the boardSquareFieldDimensions instance variable.
     *
     * @return A Dimension object, the value of boardSquareFieldDimensions.
     */
    public Dimension getBoardSquareFieldDimensions() {
        return boardSquareFieldDimensions;
    }

    /**
     * Accessor for the squareDimensions instance variable.
     *
     * @return A Dimension object, the value of squareDimensions.
     */
    public Dimension getSquareDimensions() {
        return squareDimensions;
    }

    /**
     * Compute the x and y coordinates, in pixels, of the upper left corner of a
     * chessboard square, from the x and y coordinates of it in squares.
     *
     * @param xCoord The x coordinate, in squares, of the chessboard square to
     *               plot.
     * @param yCoord The y coordinate, in squares, of the chessboard square to
     *               plot.
     * @return       A Point object that comprises the x and y coordinates, in
     *               pixels, of the square on the chessboard corresponding to
     *               the arguments.
     */
    public Point getSquareUpperLeftCorner(final int xCoord, final int yCoord) {
        int upperLeftHorizPixel = upperLeftCornerOfSquaresRegion.x
                                  + (int) squareDimensions.getWidth() * xCoord;
        int upperLeftVertPixel = upperLeftCornerOfSquaresRegion.y
                                 + (int) squareDimensions.getHeight() * yCoord;
        return new Point(upperLeftHorizPixel, upperLeftVertPixel);
    }
}
