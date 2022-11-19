package com.kmfahey.jchessgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Point;
import javax.swing.JComponent;
import java.util.Objects;
import java.util.Arrays;

public class BoardView extends JComponent implements MouseListener {

    private static final String[][] BOARD_ALG_NOTN_LOCS = new String[][] {
        new String[] {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"},
        new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"},
        new String[] {"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"},
        new String[] {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
        new String[] {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"},
        new String[] {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"},
        new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
        new String[] {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"}
    };

    private static final String[] LIGHT_COLORED_SQUARES_LOCS = new String[] {
        "a2", "a4", "a6", "a8", "b1", "b3", "b5", "b7",
        "c2", "c4", "c6", "c8", "d1", "d3", "d5", "d7",
        "e2", "e4", "e6", "e8", "f1", "f3", "f5", "f7",
        "g2", "g4", "g6", "g8", "h1", "h3", "h5", "h7"
    };

    private static final Color BEIGE = new Color(0.949019F, 0.901960F, 0.800000F);

    private Dimension boardDims;
    private ImagesManager imagesManager;
    private CoordinatesManager coordinatesManager;
    private PiecesManager piecesManager;

    private Piece clickEventClickedPiece = null;
    private String clickEventMovingFrom = "";
    private Piece clickEventToCapturePiece = null;
    private String clickEventMovingTo = "";

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

        for (String lightColoredSquareLoc : LIGHT_COLORED_SQUARES_LOCS) {
            Point squareUpperLeftCorner = coordinatesManager.getSquareUpperLeftCorner(lightColoredSquareLoc);
            graphics.fillRect(squareUpperLeftCorner.x, squareUpperLeftCorner.y,
                              (int) squareDimensions.getWidth(), (int) squareDimensions.getHeight());
        }

        for (String piecesIdentity : piecesManager.getPiecesIdentities()) {
            for (Piece piece : piecesManager.getPiecesByIdentity(piecesIdentity)) {
                Image pieceIcon = piece.getImage();
                Point pieceUpperLeftCorner = coordinatesManager.getSquareUpperLeftCorner(piece.getBoardLocation());
                graphics.drawImage(pieceIcon, pieceUpperLeftCorner.x, pieceUpperLeftCorner.y, this);
            }
        }
    }

    public void mouseClicked(final MouseEvent event) {
        int horizCoord = event.getX();
        int vertCoord = event.getY();

        Piece capturedPiece;
        
        Insets boardSquareFieldInsets = coordinatesManager.getBoardSquareFieldInsets();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();
        Dimension boardSquareFieldDimensions = coordinatesManager.getBoardSquareFieldDimensions();

        if (horizCoord <= boardSquareFieldInsets.left
            || horizCoord > boardSquareFieldInsets.left + boardSquareFieldDimensions.getWidth()
            || vertCoord <= boardSquareFieldInsets.top
            || vertCoord > boardSquareFieldInsets.top + boardSquareFieldDimensions.getHeight()) {
            return;
        }

        int tempHorizCoord = horizCoord;
        int tempVertCoord = vertCoord;
        int horizIndex = 0;
        int vertIndex = 0;

        while (tempHorizCoord > squareDimensions.getWidth()) {
            tempHorizCoord -= squareDimensions.getWidth();
            horizIndex += 1;
        }
        while (tempVertCoord > squareDimensions.getHeight()) {
            tempVertCoord -= squareDimensions.getHeight();
            vertIndex += 1;
        }

        String clickSquareLoc = piecesManager.numericIndexesToAlgNotnLoc(horizIndex, vertIndex);

        if (Objects.isNull(clickEventClickedPiece)) {
            clickEventMovingFrom = clickSquareLoc;
            clickEventClickedPiece = piecesManager.getPiece(clickEventMovingFrom);

            if (Objects.isNull(clickEventClickedPiece) || !clickEventClickedPiece.getColor().equals(piecesManager.getColorPlaying())) {
                resetClickEventVars();
                return;
            }
        } else {
            clickEventMovingTo = clickSquareLoc;

            if (!piecesManager.getValidMoveSet(clickEventMovingFrom).contains(clickEventMovingTo)) {
                resetClickEventVars();
                return;
            }

            clickEventToCapturePiece = piecesManager.getPiece(clickEventMovingTo);
            
            if (!Objects.isNull(clickEventToCapturePiece) && clickEventToCapturePiece.getColor().equals(piecesManager.getColorPlaying())) {
                resetClickEventVars();
                return;
            } else if (clickEventClickedPiece.getRole().equals(PiecesManager.KING) && piecesManager.isSquareThreatened(clickEventMovingTo)) {
                resetClickEventVars();
                return;
            } 

            piecesManager.movePiece(clickEventMovingFrom, clickEventMovingTo);

            resetClickEventVars();

            repaint();

        }
    }

    private void resetClickEventVars() {
        clickEventClickedPiece = null;
        clickEventMovingFrom = "";
        clickEventToCapturePiece = null;
        clickEventMovingTo = "";
    }

    /**
     * An implementation of MouseListener.mouseEntered, required because I
     * implement that interface. I ignore the event, so 'assert true' is used as
     * a filler line.
     *
     * @param event The event to be processed.
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(final MouseEvent event) {
        assert true;
    }

    /**
     * An implementation of MouseListener.mouseExited, required because I
     * implement that interface. I ignore the event, so 'assert true' is used as
     * a filler line.
     *
     * @param event The event to be processed.
     * @see java.awt.event.MouseListener
     */
    public void mouseExited(final MouseEvent event) {
        assert true;
    }

    /**
     * An implementation of MouseListener.mousePressed, required because I
     * implement that interface. I ignore the event, so 'assert true' is used as
     * a filler line.
     *
     * @param event The event to be processed.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(final MouseEvent event) {
        assert true;
    }

    /**
     * An implementation of MouseListener.mouseReleased, required because I
     * implement that interface. I ignore the event, so 'assert true' is used as
     * a filler line.
     *
     * @param event The event to be processed.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(final MouseEvent event) {
        assert true;
    }
}
