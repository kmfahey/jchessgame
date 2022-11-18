package com.kmfahey.jchessgame;

import java.awt.Color;
import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Point;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.Iterator;
import javax.swing.JComponent;
//import javax.swing.Timer;

public class BoardView extends JComponent /* implements ActionListener, MouseListener */ {

    private static final Color BEIGE = new Color(0.949019F, 0.901960F, 0.800000F);

    private Dimension boardDims;
    private ImagesManager imagesManager;
    private CoordinatesManager coordinatesManager;
    private PiecesManager piecesManager;

    public BoardView(final Dimension cmpntDims, final ImagesManager imgMgr, final CoordinatesManager coordMgr, final PiecesManager piecesMgr) {
        boardDims = cmpntDims;
        imagesManager = imgMgr;
        coordinatesManager = coordMgr;
        piecesManager = piecesMgr;
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        final String[] lightColoredSquaresLocs = new String[] {
            "a8", "c8", "e8", "g8", "b7", "d7", "f7", "h7",
            "a6", "c6", "e6", "g6", "b5", "d5", "f5", "h5",
            "a4", "c4", "e4", "g4", "b3", "d3", "f3", "h3",
            "a2", "c2", "e2", "g2", "b1", "d1", "f1", "h1"};

        Dimension totalBoardDimensions = coordinatesManager.getTotalBoardDimensions();
        Insets beigeMarginInsets = coordinatesManager.getBeigeMarginInsets();
        Dimension beigeMarginDimensions = coordinatesManager.getBeigeMarginDimensions();
        Insets innerBlackBorderInsets = coordinatesManager.getInnerBlackBorderInsets();
        Dimension innerBlackBorderDimensions = coordinatesManager.getInnerBlackBorderDimensions();
        Insets boardSquareFieldInsets = coordinatesManager.getBoardSquareFieldInsets();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();

        graphics.setColor(Color.BLACK);

        graphics.fillRect(0, 0, (int) totalBoardDimensions.getWidth(), (int) totalBoardDimensions.getHeight());

        graphics.setColor(BEIGE);

        graphics.fillRect(beigeMarginInsets.left, beigeMarginInsets.top,
                          (int) beigeMarginDimensions.getWidth(), (int) beigeMarginDimensions.getHeight());

        graphics.setColor(Color.BLACK);

        graphics.fillRect(innerBlackBorderInsets.left, innerBlackBorderInsets.top,
                          (int) innerBlackBorderDimensions.getWidth(), (int) innerBlackBorderDimensions.getHeight());

        graphics.setColor(BEIGE);

        for (String lightColoredSquareLoc : lightColoredSquaresLocs) {
            Point squareUpperLeftCorner = coordinatesManager.getSquareUpperLeftCorner(lightColoredSquareLoc);
            graphics.fillRect(squareUpperLeftCorner.x, squareUpperLeftCorner.y,
                              (int) squareDimensions.getWidth(), (int) squareDimensions.getHeight());
        }

        for (String pieceIdentity : piecesManager) {
            for (Piece piece : piecesManager.getPieces(pieceIdentity)) {
                Image pieceIcon = piece.getImage();
                Point pieceUpperLeftCorner = coordinatesManager.getSquareUpperLeftCorner(piece.getBoardLocation());
                graphics.drawImage(pieceIcon, pieceUpperLeftCorner.x, pieceUpperLeftCorner.y, this);
            }
        }
    }
}
