package com.kmfahey.jchessgame;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Insets;

public class CoordinatesManager {

    public static final float TOTAL_BOARD_MEASUREMENT_100PCT = 2002F;
    public static final float OUTER_BLACK_BORDER_WIDTH_100PCT = 12F;
    public static final float BEIGE_MARGIN_WIDTH_100PCT = 47F;
    public static final float INNER_BLACK_BORDER_WIDTH_100PCT = 6F;
    public static final float SQUARE_MEASUREMENT_100PCT = 234F;

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


    private int totalBoardMeasurement;
    private int rightOuterBlackBorderWidth;
    private int leftOuterBlackBorderWidth;
    private int rightBeigeMarginWidth;
    private int leftBeigeMarginWidth;
    private int rightInnerBlackBorderWidth;
    private int leftInnerBlackBorderWidth;
    private int squareMeasurement;

    private Dimension totalBoardDimensions;
    private Insets beigeMarginInsets;
    private Dimension beigeMarginDimensions;
    private Insets innerBlackBorderInsets;
    private Dimension innerBlackBorderDimensions;
    private Insets boardSquareFieldInsets;
    private Dimension boardSquareFieldDimensions;
    private Dimension squareDimensions;
    private Point upperLeftCornerOfSquaresRegion;


    private double scalingProportion;

    public CoordinatesManager(final float scaleProportion) {
        scalingProportion = scaleProportion;

        totalBoardMeasurement = (int) Math.round(TOTAL_BOARD_MEASUREMENT_100PCT * scalingProportion);
        rightOuterBlackBorderWidth = (int) Math.round(OUTER_BLACK_BORDER_WIDTH_100PCT * scalingProportion);
        leftOuterBlackBorderWidth = (int) Math.round(OUTER_BLACK_BORDER_WIDTH_100PCT * scalingProportion);
        rightBeigeMarginWidth = (int) Math.round(BEIGE_MARGIN_WIDTH_100PCT * scalingProportion);
        leftBeigeMarginWidth = (int) Math.round(BEIGE_MARGIN_WIDTH_100PCT * scalingProportion);
        rightInnerBlackBorderWidth = (int) Math.round(INNER_BLACK_BORDER_WIDTH_100PCT * scalingProportion);
        leftInnerBlackBorderWidth = (int) Math.round(INNER_BLACK_BORDER_WIDTH_100PCT * scalingProportion);
        squareMeasurement = (int) Math.round(SQUARE_MEASUREMENT_100PCT * scalingProportion);

        int slopFactor = totalBoardMeasurement - squareMeasurement * 8
                         - rightOuterBlackBorderWidth - leftOuterBlackBorderWidth
                         - rightBeigeMarginWidth - leftBeigeMarginWidth
                         - rightInnerBlackBorderWidth - leftInnerBlackBorderWidth;

        /* Due to rounding issues, the total value of the 8 squares plus the
           left and right values of all 3 margins can sum up to a number that's
           up to +6 greater than the board's measurement, or as low as -6
           below it. (I ran detailed trials to establish the range of that
           number.) The switch statement below increments or decrements the
           inner measurements to reduce that difference to 0. */

        switch (slopFactor) {
            case -6:
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; rightBeigeMarginWidth -= 1;
                leftBeigeMarginWidth -= 1; rightOuterBlackBorderWidth -= 1; leftOuterBlackBorderWidth -= 1;
                break;
            case -5:
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; rightBeigeMarginWidth -= 1;
                leftBeigeMarginWidth -= 1; leftOuterBlackBorderWidth -= 1;
                break;
            case -4:
                rightInnerBlackBorderWidth -= 1; leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1;
                leftOuterBlackBorderWidth -= 1;
                break;
            case -3:
                leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1; leftOuterBlackBorderWidth -= 1;
                break;
            case -2:
                leftInnerBlackBorderWidth -= 1; leftBeigeMarginWidth -= 1;
                break;
            case -1:
                leftInnerBlackBorderWidth -= 1;
                break;
            case 1:
                leftOuterBlackBorderWidth += 1;
                break;
            case 2:
                leftBeigeMarginWidth += 1; leftOuterBlackBorderWidth += 1;
                break;
            case 3:
                leftInnerBlackBorderWidth += 1; leftBeigeMarginWidth += 1; leftOuterBlackBorderWidth += 1;
                break;
            case 4:
                leftInnerBlackBorderWidth += 1; leftBeigeMarginWidth += 1; rightOuterBlackBorderWidth += 1;
                leftOuterBlackBorderWidth += 1;
                break;
            case 5:
                leftInnerBlackBorderWidth += 1; rightBeigeMarginWidth += 1; leftBeigeMarginWidth += 1;
                rightOuterBlackBorderWidth += 1; leftOuterBlackBorderWidth += 1;
                break;
            case 6:
                rightInnerBlackBorderWidth += 1; leftInnerBlackBorderWidth += 1; rightBeigeMarginWidth += 1;
                leftBeigeMarginWidth += 1; rightOuterBlackBorderWidth += 1; leftOuterBlackBorderWidth += 1;
                break;
            default:
                break;
        }

        totalBoardDimensions = new Dimension(totalBoardMeasurement, totalBoardMeasurement);

        beigeMarginInsets = new Insets(rightOuterBlackBorderWidth, rightOuterBlackBorderWidth,
                                       leftOuterBlackBorderWidth, leftOuterBlackBorderWidth);

        int beigeMargin = totalBoardMeasurement - rightOuterBlackBorderWidth - leftOuterBlackBorderWidth;
        beigeMarginDimensions = new Dimension(beigeMargin, beigeMargin);

        int rightOuterBorderPlusMargin = rightOuterBlackBorderWidth + rightBeigeMarginWidth;
        int leftOuterBorderPlusMargin = leftOuterBlackBorderWidth + leftBeigeMarginWidth;
        innerBlackBorderInsets = new Insets(rightOuterBorderPlusMargin, rightOuterBorderPlusMargin,
                                            leftOuterBorderPlusMargin, leftOuterBorderPlusMargin);

        int innerSquaresPlusBorders = leftInnerBlackBorderWidth + rightInnerBlackBorderWidth + squareMeasurement * 8;
        innerBlackBorderDimensions = new Dimension(innerSquaresPlusBorders, innerSquaresPlusBorders);

        int rightBordersPlusMargins = rightOuterBlackBorderWidth + rightBeigeMarginWidth + rightInnerBlackBorderWidth;
        int leftBordersPlusMargins = leftOuterBlackBorderWidth + leftBeigeMarginWidth + leftInnerBlackBorderWidth;
        boardSquareFieldInsets = new Insets(rightBordersPlusMargins, rightBordersPlusMargins,
                                            leftBordersPlusMargins, leftBordersPlusMargins);

        boardSquareFieldDimensions = new Dimension(squareMeasurement * 8, squareMeasurement * 8);

        squareDimensions = new Dimension(squareMeasurement, squareMeasurement);

        upperLeftCornerOfSquaresRegion = new Point(boardSquareFieldInsets.left, boardSquareFieldInsets.top);
    }

    public Dimension getTotalBoardDimensions() {
        return totalBoardDimensions;
    }

    public Insets getBeigeMarginInsets() {
        return beigeMarginInsets;
    }

    public Dimension getBeigeMarginDimensions() {
        return beigeMarginDimensions;
    }

    public Insets getInnerBlackBorderInsets() {
        return innerBlackBorderInsets;
    }

    public Dimension getInnerBlackBorderDimensions() {
        return innerBlackBorderDimensions;
    }

    public Insets getBoardSquareFieldInsets() {
        return boardSquareFieldInsets;
    }

    public Dimension getBoardSquareFieldDimensions() {
        return boardSquareFieldDimensions;
    }

    public Dimension getSquareDimensions() {
        return squareDimensions;
    }

    public Point getSquareUpperLeftCorner(final int[] squareCoords) {
        return getSquareUpperLeftCorner(squareCoords[0], squareCoords[1]);
    }

    public Point getSquareUpperLeftCorner(final int xCoord, final int yCoord) {
        int upperLeftHorizPixel = upperLeftCornerOfSquaresRegion.x
                                  + (int) squareDimensions.getWidth() * xCoord;
        int upperLeftVertPixel = upperLeftCornerOfSquaresRegion.y
                                 + (int) squareDimensions.getHeight() * yCoord;
        return new Point(upperLeftHorizPixel, upperLeftVertPixel);
    }
}
