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
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.Timer;


public class BoardView extends JComponent implements MouseListener, ActionListener {

    private static final int[][] LIGHT_COLORED_SQUARES_COORDS = new int[][] {
        new int[] {0, 0}, new int[] {0, 2}, new int[] {0, 4}, new int[] {0, 6},
        new int[] {1, 1}, new int[] {1, 3}, new int[] {1, 5}, new int[] {1, 7},
        new int[] {2, 0}, new int[] {2, 2}, new int[] {2, 4}, new int[] {2, 6},
        new int[] {3, 1}, new int[] {3, 3}, new int[] {3, 5}, new int[] {3, 7},
        new int[] {4, 0}, new int[] {4, 2}, new int[] {4, 4}, new int[] {4, 6},
        new int[] {5, 1}, new int[] {5, 3}, new int[] {5, 5}, new int[] {5, 7},
        new int[] {6, 0}, new int[] {6, 2}, new int[] {6, 4}, new int[] {6, 6},
        new int[] {7, 1}, new int[] {7, 3}, new int[] {7, 5}, new int[] {7, 7}
    };

    private static final Color BEIGE = new Color(0.949019F, 0.901960F, 0.800000F);

    private Dimension boardDims;
    private ImagesManager imagesManager;
    private CoordinatesManager coordinatesManager;
    private Chessboard chessboard;

    private Piece clickEventClickedPiece = null;
    private int[] clickEventMovingFrom = new int[] {-1, -1};
    private Piece clickEventToCapturePiece = null;
    private int[] clickEventMovingTo = new int[] {-1, -1};

    private Piece lastPieceMovedByPlayer;

    private String colorPlaying;
    private final int timerDelayMlsec = 500;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");

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
        int[][] squareCoords;
        int[][] boardArray;
        super.paintComponent(graphics);

        Dimension totalBoardDimensions = coordinatesManager.getTotalBoardDimensions();
        Insets beigeMarginInsets = coordinatesManager.getBeigeMarginInsets();
        Dimension beigeMarginDimensions = coordinatesManager.getBeigeMarginDimensions();
        Insets innerBlackBorderInsets = coordinatesManager.getInnerBlackBorderInsets();
        Dimension innerBlackBorderDimensions = coordinatesManager.getInnerBlackBorderDimensions();
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

        for (int[] lightColoredSquareCoords : LIGHT_COLORED_SQUARES_COORDS) {
            int xCoord = lightColoredSquareCoords[0];
            int yCoord = lightColoredSquareCoords[1];
            Point squareUpperLeftCorner = coordinatesManager
                                          .getSquareUpperLeftCorner(xCoord, yCoord);
            graphics.fillRect(squareUpperLeftCorner.x, squareUpperLeftCorner.y,
                              (int) squareDimensions.getWidth(),
                              (int) squareDimensions.getHeight());
        }

        boardArray = chessboard.getBoardArray();

        squareCoords = chessboard.occupiedSquareCoords();

        for (int coordsIdx = 0; coordsIdx < squareCoords.length; coordsIdx++) {
            int[] pieceCoords = squareCoords[coordsIdx];
            Piece piece = chessboard.getPieceAtCoords(pieceCoords[0], pieceCoords[1]);
            Image pieceIcon = piece.getImage();
            Point pieceUpperLeftCorner = coordinatesManager
                                         .getSquareUpperLeftCorner(piece.getCoords());
            graphics.drawImage(pieceIcon, pieceUpperLeftCorner.x, pieceUpperLeftCorner.y, this);
        }
    }

    public void mouseClicked(final MouseEvent event) {
        Timer opposingMoveDelayTimer;
        int eventXCoord = event.getX();
        int eventYCoord = event.getY();

        /* FIXME: this code needs to handle check and checkmate cases. */

        Insets boardSquareFieldInsets = coordinatesManager.getBoardSquareFieldInsets();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();
        Dimension boardSquareFieldDimensions = coordinatesManager.getBoardSquareFieldDimensions();

        if (eventXCoord <= boardSquareFieldInsets.left
            || eventXCoord > boardSquareFieldInsets.left + boardSquareFieldDimensions.getWidth()
            || eventYCoord <= boardSquareFieldInsets.top
            || eventYCoord > boardSquareFieldInsets.top + boardSquareFieldDimensions.getHeight()) {
            return;
        }
        int[] clickSquareCoord;
        int tempXCoord = eventXCoord;
        int tempYCoord = eventYCoord;
        int xCoord = 0;
        int yCoord = 0;

        while (tempXCoord > squareDimensions.getWidth()) {
            tempXCoord -= squareDimensions.getWidth();
            xCoord += 1;
        }
        while (tempYCoord > squareDimensions.getHeight()) {
            tempYCoord -= squareDimensions.getHeight();
            yCoord += 1;
        }

        clickSquareCoord = new int[] {xCoord, yCoord};

        if (Objects.isNull(clickEventClickedPiece)) {
            clickEventMovingFrom = clickSquareCoord;
            clickEventClickedPiece = chessboard.getPieceAtCoords(clickEventMovingFrom);

            if (Objects.isNull(clickEventClickedPiece)
                || !clickEventClickedPiece.getColor().equals(chessboard.getColorPlaying())) {
                resetClickEventVars();
                return;
            }
        } else {
            clickEventMovingTo = clickSquareCoord;

            System.err.println("(" + clickSquareCoord[0] + ", " + clickSquareCoord[1] + ")");
            try {
                int[][] movesCoords = chessboard.getValidMoveCoordsArray(clickEventMovingFrom);
                if (!BoardArrays.arrayOfCoordsContainsCoord(movesCoords, clickEventMovingTo)) {
                    resetClickEventVars();
                    return;
                }
            } catch (AlgorithmBadArgumentException exception) {
                exception.printStackTrace();
                System.exit(1);
            }

            clickEventToCapturePiece = chessboard.getPieceAtCoords(clickEventMovingTo);

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
            try {
                chessboard.movePiece(clickEventClickedPiece, clickEventMovingFrom, clickEventMovingTo);
            } catch (KingIsInCheckError exception) {
                resetClickEventVars();
                return;
            }

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
        clickEventMovingFrom = new int[] {-1, -1};
        clickEventToCapturePiece = null;
        clickEventMovingTo = new int[] {-1, -1};
    }

    public void actionPerformed(final ActionEvent event) {
        MinimaxRunner minimaxRunner;
        Chessboard.Move moveToMake;
        LocalDateTime timeRightNow;

        if (!event.getActionCommand().equals("move")) {
            return;
        }

        String opposingColor = colorPlaying.equals("white") ? "black" : "white";

        timeRightNow = LocalDateTime.now();
        System.out.println(dateTimeFormatter.format(timeRightNow) + " - starting algorithm");

        minimaxRunner = new MinimaxRunner(opposingColor, opposingColor);

        try {
            moveToMake = minimaxRunner.algorithmTopLevel(chessboard);
        } catch (AlgorithmBadArgumentException | AlgorithmInternalError exception) {
            exception.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            chessboard.movePiece(moveToMake);
        } catch (KingIsInCheckError exception) {
            return;
        }

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
