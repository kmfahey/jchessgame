package com.kmfahey.jchessgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.Timer;


public class BoardView extends JComponent implements MouseListener, ActionListener {

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
    private Chessboard chessboard;

    private Piece clickEventClickedPiece = null;
    private String clickEventMovingFrom = "";
    private Piece clickEventToCapturePiece = null;
    private String clickEventMovingTo = "";

    private Piece lastPieceMovedByPlayer;

    private String colorPlaying;
    private final int timerDelayMlsec = 500;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public BoardView(final Dimension cmpntDims, final ImagesManager imgMgr,
                     final CoordinatesManager coordMgr, final Chessboard chessBoard,
                     final String playingColor) {
        boardDims = cmpntDims;
        imagesManager = imgMgr;
        coordinatesManager = coordMgr;
        chessboard = chessBoard;
        colorPlaying = playingColor;
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

        graphics.fillRect(0, 0, (int) totalBoardDimensions.getWidth(),
                                (int) totalBoardDimensions.getHeight());

        graphics.setColor(BEIGE);

        graphics.fillRect(beigeMarginInsets.left, beigeMarginInsets.top,
                          (int) beigeMarginDimensions.getWidth(),
                          (int) beigeMarginDimensions.getHeight());

        graphics.setColor(Color.BLACK);

        graphics.fillRect(innerBlackBorderInsets.left, innerBlackBorderInsets.top,
                          (int) innerBlackBorderDimensions.getWidth(),
                          (int) innerBlackBorderDimensions.getHeight());

        graphics.setColor(BEIGE);

        for (String lightColoredSquareLoc : LIGHT_COLORED_SQUARES_LOCS) {
            Point squareUpperLeftCorner = coordinatesManager
                                          .getSquareUpperLeftCorner(lightColoredSquareLoc);
            graphics.fillRect(squareUpperLeftCorner.x, squareUpperLeftCorner.y,
                              (int) squareDimensions.getWidth(),
                              (int) squareDimensions.getHeight());
        }

        Iterator<Piece> pieceIterator = chessboard.iterator();
        while (pieceIterator.hasNext()) {
            Piece piece = pieceIterator.next();
            Image pieceIcon = piece.getImage();
            Point pieceUpperLeftCorner = coordinatesManager
                                         .getSquareUpperLeftCorner(piece.getLocation());
            graphics.drawImage(pieceIcon, pieceUpperLeftCorner.x, pieceUpperLeftCorner.y, this);
        }
    }

    public void mouseClicked(final MouseEvent event) {
        Timer opposingMoveDelayTimer;
        int horizCoord = event.getX();
        int vertCoord = event.getY();

        /* FIXME: this code needs to handle check and checkmate cases. */

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

        String clickSquareLoc = chessboard.numericIndexesToAlgNotnLoc(horizIndex, vertIndex);

        if (Objects.isNull(clickEventClickedPiece)) {
            clickEventMovingFrom = clickSquareLoc;
            clickEventClickedPiece = chessboard.getPieceAtLocation(clickEventMovingFrom);

            if (Objects.isNull(clickEventClickedPiece) ||
                    !clickEventClickedPiece.getColor().equals(chessboard.getColorPlaying())) {
                resetClickEventVars();
                return;
            }
        } else {
            clickEventMovingTo = clickSquareLoc;

            if (!chessboard.getValidMoveSet(clickEventMovingFrom).contains(clickEventMovingTo)) {
                resetClickEventVars();
                return;
            }

            clickEventToCapturePiece = chessboard.getPieceAtLocation(clickEventMovingTo);

            if (!Objects.isNull(clickEventToCapturePiece)
                    && clickEventToCapturePiece.getColor()
                                               .equals(chessboard.getColorPlaying())) {
                resetClickEventVars();
                return;
            } else if (clickEventClickedPiece.getRole().equals("king")
                    && chessboard.isSquareThreatened(clickEventMovingTo,
                                                     clickEventClickedPiece.getColor().equals("white")
                                                     ? "black" : "white")) {
                resetClickEventVars();
                return;
            }

            /* This method returns a king Piece if a king has been put in check
               or checkmate by the move. FIXME need to check for those cases
               and handle them in the game logic. Also if it's check then the
               possible moves need to be limited to moves that can end the
               check. */
            chessboard.movePiece(clickEventClickedPiece, clickEventMovingFrom, clickEventMovingTo);

            resetClickEventVars();

            repaint();

            /* This timer is used both to introduce a hangtime between the
               player's move and the opposing move, and to break the opposing
               move logic out of this mouseClicked() method and run it in its
               own execution by using an event to trigger another, unconnected
               method call. */
            opposingMoveDelayTimer = new Timer(timerDelayMlsec, this);
            opposingMoveDelayTimer.setActionCommand("move");
            opposingMoveDelayTimer.setRepeats(false);
            opposingMoveDelayTimer.start();
        }
    }

    private void resetClickEventVars() {
        clickEventClickedPiece = null;
        clickEventMovingFrom = "";
        clickEventToCapturePiece = null;
        clickEventMovingTo = "";
    }

    public void actionPerformed(final ActionEvent event) {
        ScoredMove optimalMove;
        Chessboard clonedBoard;
        Piece clonedPiece;
        MinimaxTreeNode treeNode;
        Move moveToMake;
        LocalDateTime timeRightNow;
        String opposingColor = colorPlaying.equals("white") ? "black" : "white";

        timeRightNow = LocalDateTime.now();
        System.out.println(dateTimeFormatter.format(timeRightNow) + " - starting algorithm");

        if (!event.getActionCommand().equals("move")) {
            return;
        }

        lastPieceMovedByPlayer = chessboard.getLastMovedPiece();
        clonedBoard = chessboard.clone();
        clonedPiece = chessboard.getPieceAtLocation(lastPieceMovedByPlayer.getLocation());
        treeNode = new MinimaxTreeNode(clonedBoard);

        try {
            moveToMake = treeNode.minimaxAlgorithmTopLevel(opposingColor);
        } catch (AlgorithmNoResultException exception) {
            System.out.println(exception.toString());
            return;
        }

        chessboard.movePiece(moveToMake.getMovingPiece(), moveToMake.getCurrentLocation(), moveToMake.getMoveToLocation());

        timeRightNow = LocalDateTime.now();
        System.out.println(dateTimeFormatter.format(timeRightNow) + " - algorithm finished");

        repaint();
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
