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
import java.util.Objects;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * This class subclasses JComponent and implements the chessboard area in the
 * GUI where the game is played. A chessboard is drawn using Graphics.fillRect()
 * calls, and chesspiece icons from ImagesManager are drawn on it using
 * Graphics.drawImage(). The player moves a piece by clicking on the piece
 * and then clicking on the square they'd like to move it to. If the move
 * is illegal, an error is printed to the MovesLog object whose textarea is
 * adjacent to BoardView's chessboard display in the GUI.
 */
public class BoardView extends JComponent implements MouseListener, ActionListener {

    private static final Color BEIGE = new Color(0.949019F, 0.901960F, 0.800000F);

    /* The state variables that store values between successive calls to
       mouseClicked() so the two-click process of selecting a piece and
       selecting a square for it to move to can be tracked. */
    private Chessboard.Piece clickEventClickedPiece = null;
    private int[] clickEventMovingFrom = new int[] {-1, -1};
    private Chessboard.Piece clickEventToCapturePiece = null;
    private int[] clickEventMovingTo = new int[] {-1, -1};

    /* The state variables that retain their values between the mouseClicked()
       method that spawns a PopupPawnPromotion(), the promotePawn() method that
       collects its captured info, and the actionPerformed() method that waits
       on that call. */
    private PopupPawnPromotion popupPawnPromotion = null;
    private int[] pawnToPromoteCoords = null;
    private boolean pawnHasntBeenPromotedYet = false;

    private final Chessboard chessboard;
    private final ChessGame chessGameFrame;
    private final CoordinatesManager coordinatesManager;
    private final MovesLog movesLog;

    /* The object that runs the minimax algorithm. */
    private final MinimaxRunner minimaxRunner;

    /* A rng used by actionPerformed to pick a random pawn promotion if the
       player declined to choose. */
    private final Random randomNumberGenerator = new Random();

    /* The Timer object and the time delay used by it. */
    private Timer opposingMoveDelayTimer;
    private final int timerDelayMlsec = 500;

    /* A turn count that's maintained by the move logic of both sides, and the
       has-moved booleans used to establish when it needs to be incremented. */
    private int turnCount;
    private boolean blackHasMoved;
    private boolean whiteHasMoved;

    /* The colors the player and the AI are playing. */
    private final int colorOfAI;
    private final int colorOfPlayer;

    /**
     * This constructor instances a BoardView object. It accepts quite a few
     * arguments since it needs almost every object that was instanced in the
     * ChessGame constructor that called it, so it can run the game.
     *
     * @param chessGame     A ChessGame object.
     * @param coordMgr      A CoordinatesManager object.
     * @param chessboardObj A Chessboard object.
     * @param movesLogObj   A MovesLog object, the textarea to the right of the
     *                      board that displays moves and error messages.
     * @param colorPlaying  An integer representing the color the player is
     *                      playing-- either BoardArrays.WHITE or
     *                      BoardArrays.BLACK.
     * @see ChessGame
     * @see Dimension
     * @see ImagesManager
     * @see CoordinatesManager
     * @see Chessboard
     * @see MovesLog
     */
    public BoardView(final ChessGame chessGame, final CoordinatesManager coordMgr, final Chessboard chessboardObj,
                     final MovesLog movesLogObj, final int colorPlaying) {

        chessGameFrame = chessGame;
        coordinatesManager = coordMgr;
        chessboard = chessboardObj;
        movesLog = movesLogObj;
        colorOfPlayer = colorPlaying;
        colorOfAI = (colorPlaying == BoardArrays.WHITE) ? BoardArrays.BLACK : BoardArrays.WHITE;
        minimaxRunner = new MinimaxRunner(chessboard, colorOfAI);
        turnCount = 0;
        whiteHasMoved = false;
        blackHasMoved = false;
        repaint();

        /* If the player chose to play Black, then the timer is set up and
           activated, so it can trigger actionPerformed() which contains the AI's
           move generation and execution logic. */
        if (colorOfAI == BoardArrays.WHITE) {
            opposingMoveDelayTimer = new Timer(timerDelayMlsec, this);
            opposingMoveDelayTimer.setActionCommand("move");
            opposingMoveDelayTimer.setRepeats(true);
            opposingMoveDelayTimer.start();
        }
    }

    /**
     * This method is called by ChessGame during a reinitialization after a
     * previous game has been cleared, if the player chose to play Black. It
     * sets the timer that triggers the actionPerformed() method which contains
     * the AI's move generation and execution logic.
     *
     * @see ChessGame
     */
    public void aiMovesFirst() {
        opposingMoveDelayTimer.start();
    }

    /**
     * This method is called by PopupPawnPromotion after a choice is made and
     * the "I've Chosen" button is clicked. It executes the pawn promotion and
     * sets a state variable so actionPerformed() (which is waiting on this
     * step) can run with the AI's move.
     *
     * @param xCoord   The x-coordinate of the pawn that's being promoted.
     * @param yCoord   The y-coordinate of the pawn that's being promoted.
     * @param newPiece The int value of the piece that the pawn is being
     *                 promoted to.
     * @see PopupPawnPromotion
     */
    public void promotePawn(final int xCoord, final int yCoord, final int newPiece) {
        chessboard.promotePawn(xCoord, yCoord, newPiece);
        pawnHasntBeenPromotedYet = false;
    }

    /**
     * This method is called by PopupGameOver() to clear the board of the
     * just-finished game. PopupColorChoice is called after that to repopulate
     * the board, but the board is on-screen during that choice (unlike when
     * PopupColorChoice runs at the initialization of the program) so the board
     * needs to be empty.
     *
     * @see PopupGameOver
     */
    public void blankBoard() {
        int[][] boardArray = chessboard.getBoardArray();

        for (int xIdx = 0; xIdx < 8; xIdx++) {
            for (int yIdx = 0; yIdx < 8; yIdx++) {
                boardArray[xIdx][yIdx] = 0;
            }
        }

        repaint();
    }

    /* This method redraws the board, using Graphics.fillRect() to create a
     * chessboard in the BoardView area, and (if not working from a blank
     * Chessboard object) using Graphics.drawImage() to place the piece icons in
     * the appropriate squares.
     *
     * @param graphics The Graphics object that gives the method access to
     *                 graphical methods.
     * @see java.awt.Graphics
     **/
    @Override
    protected void paintComponent(final Graphics graphics) {
        int piecesDrawn = 0;
        int[][] squareCoords;
        int[][] boardArray = chessboard.getBoardArray();

        super.paintComponent(graphics);

        /* The dimensions and insets needed to draw the board with rectangles
           are retrieved from CoordinatesManager. */
        Dimension totalBoardDimensions = coordinatesManager.getTotalBoardDimensions();
        Insets beigeMarginInsets = coordinatesManager.getBeigeMarginInsets();
        Dimension beigeMarginDimensions = coordinatesManager.getBeigeMarginDimensions();
        Insets innerBlackBorderInsets = coordinatesManager.getInnerBlackBorderInsets();
        Dimension innerBlackBorderDimensions = coordinatesManager.getInnerBlackBorderDimensions();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();

        /* The outer black border of the board is drawn as a full-board black
           rectangle. */
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, (int) totalBoardDimensions.getWidth(),
                                (int) totalBoardDimensions.getHeight());

        /* The outer biege margin of the board is drawn over that as a
           full-board beige rectangle. */
        graphics.setColor(BEIGE);
        graphics.fillRect(beigeMarginInsets.left, beigeMarginInsets.top,
                          (int) beigeMarginDimensions.getWidth(),
                          (int) beigeMarginDimensions.getHeight());

        /* The inner black border of the square field is drawn as a black
           rectangle over that. */
        graphics.setColor(Color.BLACK);
        graphics.fillRect(innerBlackBorderInsets.left, innerBlackBorderInsets.top,
                          (int) innerBlackBorderDimensions.getWidth(),
                          (int) innerBlackBorderDimensions.getHeight());

        /* The light-colored squares are drawn in position, one after another,
           defining the black squares by contrast using the black of the
           previously drawn big rectangle. */
        graphics.setColor(BEIGE);
        for (int[] lightColoredSquareCoords : CoordinatesManager.LIGHT_COLORED_SQUARES_COORDS) {
            int xCoord = lightColoredSquareCoords[0];
            int yCoord = lightColoredSquareCoords[1];
            Point squareUpperLeftCorner = coordinatesManager
                                          .getSquareUpperLeftCorner(xCoord, yCoord);
            graphics.fillRect(squareUpperLeftCorner.x, squareUpperLeftCorner.y,
                              (int) squareDimensions.getWidth(),
                              (int) squareDimensions.getHeight());
        }

        /* This method returns an int[][2] array of every square in the
           boardArray that is occupied by a piece of any kind. This can be
           zero-length if the board was blanked after a game over and the board
           hasn't been repopulated yet. */
        squareCoords = chessboard.occupiedSquareCoords();

        /* This for loop iterates across the squareCoords array. */
        for (int[] pieceCoords : squareCoords) {
            piecesDrawn++;

            /* The piece at those coordinates is fetched, and its icon is
               retrieved from the Chessboard.Piece object. */
            Chessboard.Piece piece = chessboard.getPieceAtCoords(pieceCoords[0], pieceCoords[1]);
            Image pieceIcon = piece.pieceImage();

            /* The exact coordinates to base the image at are retrieved
               from CoordinatesManager, and the image is drawn using
               Graphics.drawImage(). */
            Point pieceUpperLeftCorner = coordinatesManager
                    .getSquareUpperLeftCorner(piece.xCoord(), piece.yCoord());
            graphics.drawImage(pieceIcon, pieceUpperLeftCorner.x, pieceUpperLeftCorner.y, this);
        }

        /* piecesDrawn as nonzero is used to confirm that this isn't a repaint()
           that draws an empty board. This is actually the best place to put the
           Game Over checking logic, so that test runs if the board has pieces
           on it but not otherwise. */
        if (piecesDrawn != 0 && BoardArrays.isKingInCheckmate(boardArray, chessboard.getColorPlaying(),
                                                              chessboard.getColorOnTop())) {
            /* The player's color king is in checkmate. It's game over. */
            PopupGameOver popupGameOver = new PopupGameOver(chessGameFrame, this, PopupGameOver.PLAYER_LOST);
            turnCount = 0;
        }
    }

    /* This method translates between the pixel coordinates in a MouseEvent
       object and the square coordinates needed to find a piece on the board.
       If the coordinates don't point to a valid square, null is returned.
       Otherwise, an int[2] array of the coordinates is returned. */
    private int[] mouseClickedEventToCoords(final MouseEvent event) {
        int eventXCoord = event.getX();
        int eventYCoord = event.getY();

        /* Several relevant dimension and inset values are retrieved from
           CoordinatesManager. */
        Insets boardSquareFieldInsets = coordinatesManager.getBoardSquareFieldInsets();
        Dimension squareDimensions = coordinatesManager.getSquareDimensions();
        Dimension boardSquareFieldDimensions = coordinatesManager.getBoardSquareFieldDimensions();

        /* If the click falls outside the actual square field of the board (so
           it's in the margins around the square field but still on the board)
           state variables are reset, and it aborts. */
        if (eventXCoord <= boardSquareFieldInsets.left
            || eventXCoord > boardSquareFieldInsets.left + boardSquareFieldDimensions.getWidth()
            || eventYCoord <= boardSquareFieldInsets.top
            || eventYCoord > boardSquareFieldInsets.top + boardSquareFieldDimensions.getHeight()) {
            resetClickEventVars();
            return null;
        }

        int tempXCoord = eventXCoord;
        int tempYCoord = eventYCoord;
        int xCoord = 0;
        int yCoord = 0;

        /* The pixel coordinates are decremented by the size of a square
           repeatedly as the coordinate values are incremented in tandem. When
           the pixel value falls inside a square, the coordinate value will be
           the coordinate of the square. */
        while (tempXCoord > squareDimensions.getWidth()) {
            tempXCoord -= squareDimensions.getWidth();
            xCoord += 1;
        }
        while (tempYCoord > squareDimensions.getHeight()) {
            tempYCoord -= squareDimensions.getHeight();
            yCoord += 1;
        }

        /* If the click falls off the far end of the board, then reset the state
           variables and abort. */
        if (xCoord > 7 || yCoord > 7) {
            resetClickEventVars();
            return null;
        }

        return new int[] {xCoord, yCoord};
    }

    /* This method takes the clicked square coordinates-- if it's the 1st click,
       it's saved to state and null is returned. If it's the 2nd click, a
       Chessboard.Move is instanced and returned. */
    private Chessboard.Move mouseClickedCoordsToMoveObj(final int[] clickSquareCoord) {
        Chessboard.Move moveObj;

        /* If clickEventClickedPiece is null, then this is the first click, so
           it tries to define clickEventClickedPiece. */
        if (Objects.isNull(clickEventClickedPiece)) {
            clickEventMovingFrom = clickSquareCoord;
            clickEventClickedPiece = chessboard.getPieceAtCoords(clickEventMovingFrom);

            /* If that fails, the player clicked on an empty square. If it
               succeeds but the piece is of the opposing color, then the player
               clicked on a piece of the opposing color. Either way, the state
               variables are reset. */
            if (Objects.isNull(clickEventClickedPiece)
                || (clickEventClickedPiece.pieceInt() & chessboard.getColorPlaying()) == 0) {
                resetClickEventVars();
            }

            /* Either way, the 2nd click logic can't run now, so null is
               returned and mouseClicked() will abort. */
            return null;
        } else {
            clickEventMovingTo = clickSquareCoord;

            clickEventToCapturePiece = chessboard.getPieceAtCoords(clickEventMovingTo);

            /* These boolean assignments break down the logic that tests whether
               the inputs by the player signal that they intend to castle. */
            boolean firstPieceIsKing = (clickEventClickedPiece.pieceInt() & Chessboard.KING) != 0;
            boolean secondPieceIsRook = Objects.nonNull(clickEventToCapturePiece)
                                            && (clickEventToCapturePiece.pieceInt() & Chessboard.ROOK) != 0;
            boolean bothPiecesAreSameColor = Objects.nonNull(clickEventToCapturePiece)
                                                 && (clickEventClickedPiece.pieceInt() & Chessboard.WHITE)
                                                 == (clickEventToCapturePiece.pieceInt() & Chessboard.WHITE);
            boolean moveIsCastling = firstPieceIsKing && secondPieceIsRook && bothPiecesAreSameColor;
            boolean moveIsCastlingKingside = moveIsCastling && clickEventMovingTo[0] == 0;
            boolean moveIsCastlingQueenside = moveIsCastling && clickEventMovingTo[0] == 7;
            /* The booleans moveIsCastlingKingside and moveIsCastlingQueenside
               are used as arguments to the Chessboard.Move constructor. */

            /* A Chessboard.Move object is incepted with the mouseClicked()
               state variables and the castling booleans. */
            moveObj = new Chessboard.Move(clickEventClickedPiece, clickEventMovingFrom[0], clickEventMovingFrom[1],
                                          clickEventMovingTo[0], clickEventMovingTo[1],
                                          Objects.nonNull(clickEventToCapturePiece)
                                              ? clickEventToCapturePiece.pieceInt() : 0,
                                          moveIsCastlingKingside, moveIsCastlingQueenside, 0);
            return moveObj;
        }
    }

    /* This method applies four different tests to the moveobj to identify move
       errors: if the 2nd click was on a friendly piece (provided the 1st click
       wasn't on the king and the 2nd of a rook, which signals castling), if the
       king is in check and the move doesn't fix that, if the move would put the
       king in check, or if the move is not a valid move for the piece selected.

       If an error is found, it's communicated to the moveslog textarea, which
       displays the error message, and the method returns false. otherwise true
       is returned. */
    private boolean mouseClickedTestForMoveErrors(final Chessboard.Move moveObj) {
        int colorOnTop = chessboard.getColorOnTop();

        /* The time that both pieces are the same color, and it's a valid
           move is if the first one is a king and the second one is rook, bc
           that's how castling is signalled. */
        if (!(moveObj.isCastlingQueenside() || moveObj.isCastlingKingside())
            && Objects.nonNull(clickEventToCapturePiece)
            && (clickEventToCapturePiece.pieceInt() & chessboard.getColorPlaying()) != 0) {

            /* Both pieces are the same color (and it's not castling) so it's a no-op. */
            if (clickEventMovingFrom[0] != clickEventMovingTo[0] || clickEventMovingFrom[1] != clickEventMovingTo[1]) {
                /* Clicking on a piece and then clicking on it again isn't an
                   error, it's how you cancel a move. */
                movesLog.addError(moveObj, MovesLog.MoveError.IS_A_FRIENDLY_PIECE);
            }

            resetClickEventVars();
            return false;
        } else if ((clickEventClickedPiece.pieceInt() & Chessboard.KING) != 0
                   && BoardArrays.wouldKingBeInCheck(
                          chessboard.getBoardArray(), clickEventMovingTo[0], clickEventMovingTo[1],
                          colorOfPlayer, colorOnTop)) {
            /* The piece is a King, and either moving it to that square would put it
               in check, or it's already in check and that move wouldn't change
               that, so movement is cancelled. */
            if (BoardArrays.isKingInCheck(chessboard.getBoardArray(), colorOfPlayer, colorOnTop)) {
                movesLog.addError(moveObj, MovesLog.MoveError.IS_IN_CHECK);
            } else {
                movesLog.addError(moveObj, MovesLog.MoveError.WOULD_BE_IN_CHECK);
            }
            resetClickEventVars();
            return false;
        } else if ((clickEventClickedPiece.pieceInt() & Chessboard.KING) == 0
                   && BoardArrays.wouldKingBeInCheck(
                          chessboard.getBoardArray(), clickEventMovingFrom[0], clickEventMovingFrom[1],
                          clickEventMovingTo[0], clickEventMovingTo[1], colorOfPlayer, colorOnTop)) {
            /* The piece isn't a king, and that move would put the king in check
               or the king is in check and that move doesn't resolve that. */
            if (BoardArrays.isKingInCheck(chessboard.getBoardArray(), colorOfPlayer, colorOnTop)) {
                System.err.println("foo");
                movesLog.addError(moveObj, MovesLog.MoveError.IS_IN_CHECK);
            } else {
                movesLog.addError(moveObj, MovesLog.MoveError.WOULD_BE_IN_CHECK);
            }
            resetClickEventVars();
            return false;
        /* This method checks whether a piece of the given type at the moveObj's
           moving-from coordinates can move to a square at the moveObj's
           moving-to coordinates. */
        } else if (!chessboard.isMovePossible(moveObj)) {
            movesLog.addError(moveObj, MovesLog.MoveError.NOT_A_VALID_MOVE);
            resetClickEventVars();
            return false;
        }

        return true;
    }

    /* The moveObj is passed to Chessboard.movePiece() for execution, which may
       raise an exception. If one is raised, it returns false, otherwise true. */
    private boolean mouseClickedMovePieceAndCleanup(final Chessboard.Move moveObj) {
        int[][] boardArray = chessboard.getBoardArray();
        int colorOnTop = chessboard.getColorOnTop();

        /* An attempt to execute the moveObj using Chessboard.movePiece(). If an
           exception is thrown, this method returns false. */
        try {
            BoardArrays.printBoard(boardArray);
            System.err.println();
            chessboard.movePiece(moveObj);
        } catch (KingIsInCheckException exception) {
            movesLog.addError(moveObj, MovesLog.MoveError.IS_IN_CHECK);
            resetClickEventVars();
            return false;
        } catch (CastlingNotPossibleException exception) {
            movesLog.addError(moveObj, MovesLog.MoveError.CASTLING_NOT_POSSIBLE);
            resetClickEventVars();
            return false;
        }

        /* The move is logged with the MovesLog textarea. */
        movesLog.addMove(moveObj);

        /* Moves taken variables are tracked. */
        if (colorOfPlayer == BoardArrays.WHITE) {
            whiteHasMoved = true;
        } else {
            blackHasMoved = true;
        }

        /* If this is move leads to a pawn promotion, pawn promotion state
           variables are initialized, and a PopupPawnPromotion dialog box is
           spawned. */
        if ((moveObj.movingPiece().pieceInt() & Chessboard.PAWN) != 0) {
            int pieceColor = moveObj.movingPiece().pieceInt() ^ Chessboard.PAWN;
            if ((pieceColor == colorOnTop) ? (moveObj.toYCoord() == 7) : (moveObj.toYCoord() == 0)) {
                pawnHasntBeenPromotedYet = true;
                pawnToPromoteCoords = new int[] {moveObj.toXCoord(), moveObj.toYCoord()};
                popupPawnPromotion = new PopupPawnPromotion(this, moveObj.toXCoord(), moveObj.toYCoord());
            }
        }

        /* A turn count is kept so the AI algorithm can be called with it as an
           argument. If both sides have moved, the counter is incremented. */
        if (whiteHasMoved && blackHasMoved) {
            turnCount++;
            whiteHasMoved = false;
            blackHasMoved = false;
        }

        resetClickEventVars();

        repaint();

        return true;
    }

    /**
     * This method is called when the player clicks anywhere on BoardView's
     * chessboard display. It and its delegate methods handle all the logic
     * needed to process moving a chesspiece on the board. A player needs to
     * click first on the piece to move and then on the square to move it to.
     * This method keeps state between calls so, over the course of two clicks,
     * it can execute one piece move.
     */
    public void mouseClicked(final MouseEvent event) {
        int[] clickSquareCoord;
        boolean isMoveValid;
        boolean didMoveExecute;

        /* This method translates between the pixel coordinates in a MouseEvent
           object and the square coordinates needed to find a piece on the
           board. If the coordinates don't point to a valid square, null is
           returned. Otherwise, an int[2] array of the coordinates is returned. */
        clickSquareCoord = mouseClickedEventToCoords(event);

        if (Objects.isNull(clickSquareCoord)) {
            return;
        }

        Chessboard.Move moveObj;

        /* This method takes the clicked square coordinates-- if it's the 1st
           click, it's saved to state and null is returned. If it's the 2nd
           click, a Chessboard.Move is instanced and returned. */
        moveObj = mouseClickedCoordsToMoveObj(clickSquareCoord);

        if (Objects.isNull(moveObj)) {
            return;
        }

        /* This method applies four different tests to the moveobj to identify
           move errors: if the 2nd click was on a friendly piece (provided the
           1st click wasn't on the king and the 2nd of a rook, which signals
           castling), if the king is in check and the move doesn't fix that, if
           the move would put the king in check, or if the move is not a valid
           move for the piece selected.

           If an error is found, it's communicated to the moveslog textarea,
           which displays the error message, and the method returns false.
           otherwise true is returned. */
        isMoveValid = mouseClickedTestForMoveErrors(moveObj);

        if (!isMoveValid) {
            return;
        }

        /* The moveObj is passed to Chessboard.movePiece() for execution, which
           may raise an exception. If one is raised, it returns false, otherwise
           true. */
        didMoveExecute = mouseClickedMovePieceAndCleanup(moveObj);

        if (!didMoveExecute) {
            return;
        }

        /* This timer is used both to introduce a hangtime between the
           player's move and the opposing move, and to break the opposing
           move logic out of this mouseClicked() method and run it in its
           own execution by using an event to trigger another, unconnected
           method call.

           In the special case of pawn promotion, there's a PopupPawnPromotion()
           dialog box waiting for player interaction, and this timer repeatedly
           triggers actionPerformed, which aborts unless the pawn promotion
           dialog has finished and called promotePawn() with the result. */
        if (Objects.isNull(opposingMoveDelayTimer)) {
            opposingMoveDelayTimer = new Timer(timerDelayMlsec, this);
            opposingMoveDelayTimer.setActionCommand("move");
            opposingMoveDelayTimer.setRepeats(true);
        }
        opposingMoveDelayTimer.start();
    }

    /* This method is used by utility methods implementing mouseClicked()'s
       player move logic to clear the instance variables that carry move data
       between executions of mouseClicked. Two clicks are required to complete a
       move; if these are cleared, the last click is cleared and mouseClicked()
       resets to expecting the first click. */
    private void resetClickEventVars() {
        clickEventClickedPiece = null;
        clickEventMovingFrom = new int[] {-1, -1};
        clickEventToCapturePiece = null;
        clickEventMovingTo = new int[] {-1, -1};
    }

    /**
     * This method is triggered by a timer set at the end of mouseClicked()
     * so that the AI's move logic is separate from the player's move logic.
     * It consults the minimax algorithm implementation in MinimaxRunner to
     * generate a move for the AI, and executes it.
     *
     * @param event The event object sent by the timer.
     */
    public void actionPerformed(final ActionEvent event) {
        Chessboard.Move moveToMake;

        if (!event.getActionCommand().equals("move")) {
            return;
        /* If the player's pawn is being promoted, then a PopupPawnPromotion
           dialog box was presented to the player. If it hasn't been closed yet,
           then pawnHasntBeenPromotedYet will be true, so the method aborts. The
           timer will repeatedly trigger this method until the pawn has been
           promoted and the timer can be turned off. */
        } else if (pawnHasntBeenPromotedYet) {
            if (Objects.nonNull(popupPawnPromotion) && !popupPawnPromotion.isDisplayable()) {
                /* The player closed the pawn promotion dialog box via the X on
                   the top bar, so a promotion option is chosen at random. */
                int randIndex = randomNumberGenerator.nextInt(BoardArrays.PAWN_PROMOTION_PIECES.length);
                int pieceInt = BoardArrays.PAWN_PROMOTION_PIECES[randIndex];
                if (pieceInt == BoardArrays.KNIGHT) {
                    pieceInt = BoardArrays.KNIGHT | (randomNumberGenerator.nextInt(2) == 1
                                                    ? BoardArrays.LEFT : BoardArrays.RIGHT);
                }
                promotePawn(pawnToPromoteCoords[0], pawnToPromoteCoords[1], pieceInt);
            } else {
                return;
            }
        }

        /* The pawn promotion is done, or never was an issue, so the timer can
           be turned off and its state variables nulled. */
        opposingMoveDelayTimer.stop();
        popupPawnPromotion = null;
        pawnToPromoteCoords = null;

        /* Executing the minimax algorithm so the AI can generate a move, or
           handling the error that results if the algorithm chokes. */
        try {
            moveToMake = minimaxRunner.algorithmTopLevel(turnCount);
        } catch (CastlingNotPossibleException | IllegalArgumentException exception) {
            String exceptionClassName = exception.getClass().getName().split("^.*\\.")[1];
            JOptionPane.showMessageDialog(chessGameFrame, "Minimax algorithm experienced a " + exceptionClassName
                                                          + ":\n" + exception.getMessage());
            BoardArrays.printBoard(chessboard.getBoardArray());
            exception.printStackTrace();
            System.exit(1);
            return;
        }

        /* If the AI's move generation logic yielded an empty moves array, then
           it couldn't generate any moves that'd get its king out of check, and
           the player has checkmate. The AI concedes and the player gets a game
           over popup awarding the win to them. */
        if (Objects.isNull(moveToMake.movingPiece())) {
            PopupGameOver popupGameOver = new PopupGameOver(chessGameFrame, this, PopupGameOver.AI_LOST);
            turnCount = 0;
            repaint();
            return;
        }

        /* Moving the piece, or handling the error that arises because the move
           isn't executable. Hasn't had an error on an AI move yet. */
        try {
            int[][] boardArray = chessboard.getBoardArray();
            BoardArrays.printBoard(boardArray);
            System.err.println();
            chessboard.movePiece(moveToMake);
        } catch (KingIsInCheckException | CastlingNotPossibleException exception) {
            String exceptionClassName = exception.getClass().getName().split("^.*\\.")[1];
            JOptionPane.showMessageDialog(chessGameFrame, "Moving the AI's piece (" + moveToMake
                                                          + ") caused a " + exceptionClassName
                                                          + ":\n" + exception.getMessage());
            BoardArrays.printBoard(chessboard.getBoardArray());
            exception.printStackTrace();
            System.exit(1);
            return;
        }

        /* The move is logged. */
        movesLog.addMove(moveToMake);

        /* Turns are counted so that algorithmTopLevel can be called with a
           turnCount value (it handles its moves differently on the first turn).
           If both sides have moved then a turn has happened. */
        if (colorOfAI == BoardArrays.WHITE) {
            whiteHasMoved = true;
        } else {
            blackHasMoved = true;
        }

        if (whiteHasMoved && blackHasMoved) {
            turnCount++;
            whiteHasMoved = false;
            blackHasMoved = false;
        }

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
