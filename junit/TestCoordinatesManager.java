package com.kmfahey.jchessgame.junit;

import junit.framework.TestCase;

import java.awt.Insets;
import java.awt.Dimension;

import com.kmfahey.jchessgame.CoordinatesManager;

public final class TestCoordinatesManager extends TestCase {

 /* public void setUp() {
        assert true;
    } */

 /* public void tearDown() throws SQLException {
        assert true;
    } */

    public void testCoordinates() {
        CoordinatesManager coordinatesManager = new CoordinatesManager(0.5F);

        Dimension totalBoardDimensions = coordinatesManager.getTotalBoardDimensions();
        Insets beigeMarginInsets = coordinatesManager.getBeigeMarginInsets();
        Dimension beigeMarginDimensions = coordinatesManager.getBeigeMarginDimensions();
        Insets innerBlackBorderInsets = coordinatesManager.getInnerBlackBorderInsets();
        Dimension innerBlackBorderDimensions = coordinatesManager.getInnerBlackBorderDimensions();
        Insets boardSquareFieldInsets = coordinatesManager.getBoardSquareFieldInsets();
        Dimension boardSquareFieldDimensions = coordinatesManager.getBoardSquareFieldDimensions();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();

        assertEquals(1001, (int) totalBoardDimensions.getWidth());
        assertEquals(6, (int) beigeMarginInsets.left);
        assertEquals(989, (int) beigeMarginDimensions.getWidth());
        assertEquals(30, (int) innerBlackBorderInsets.left);
        assertEquals(941, (int) innerBlackBorderDimensions.getWidth());
        assertEquals(33, (int) boardSquareFieldInsets.left);
        assertEquals(936, (int) boardSquareFieldDimensions.getWidth());
        assertEquals(117, (int) squareDimensions.getWidth());
    }
}
