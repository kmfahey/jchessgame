package com.kmfahey.jchessgame;

import java.util.Objects;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * This class is the frontend of the program, a JFrame subclass that presents
 * as the GUI to the chess program. It sizes itself to have a height 80% of
 * the screen height and a width equal to the screen height. It contains a
 * chessboard display on which the game is played by clicking on a chesspiece
 * icon and clicking on the square to move it to. It also features a scrolling
 * log that displays the moves in algebraic notation, and lists error messages
 * if an illegal move was attempted.
 * 
 * The opposing AI is implemented using the minimax algorithm with the
 * alpha/beta pruning optimization. The user has the option to play either color
 * (with White always going first), and once a game is over the board can be
 * reset and another game run (with a fresh color choice) if the player wishes.
 */
public class ChessGame extends JFrame implements ActionListener {

    /**
     * The BoardView class is a JComponent subclass that displays the chessboard
     * in the GUI and manages the movement of chesspiece icons on the board.
     */
    private BoardView boardView;

    /**
     * Chessboard is the class that manages the representation of the chessboard
     * in the game and has methods for manipulating it.
     */
    private Chessboard chessboard = null;

    /**
     * CoordinatesManager contains all the logic needed to compute the
     * dimensions and insets used by BoardView to build the board out of
     * carefully-measured beige and black rectangles drawn in the board region.
     */
    private CoordinatesManager coordinatesManager;

    /**
     * GridBagLayout manages the disposition of GUI elements in the window.
     */
    private GridBagLayout gameLayout;

    /**
     * JPanel is the panel that other GUI elements are attached to while building
     * the GUI.
     */
    private JPanel gamePanel;
    
    /**
     * ImagesManager manages the game's images directory and provides chess
     * piece icon Image objects that can be drawn to the board.
     */
    private ImagesManager imagesManager;

    /**
     * MovesLog is a JTextArea subclass that is placed alongside the chessboard
     * display, where it logs moves in algebraic notation, and (more relevantly)
     * also displays an error message if the user attempts an invalid move.
     */
    private MovesLog movesLog;

    /**
     * This is a dialog box that spawns when instanced, prompting the user to
     * pick a color to play. It spawns before the window does, and also spawns
     * after a game over if the user chooses to play another game.
     */
    private PopupColorChoice popupColorChoice;

    /**
     * If this class's main() method was called with a filename argument, it's
     * stored to this instance variable.
     */
    private String fileName = null;

    /**
     * This Timer object is used to repeatedly call actionPerformed() until the
     * PopupColorChoice dialog box calls setColorPlaying() and with a color set
     * actionPerformed() can complete execution.
     */
    private Timer colorChoicePopupDelayTimer;

    private Dimension boardDims;
    private Dimension boardSectionDims;
    private Dimension movesLogSectionDims;

    private int colorOnTop;
    private int colorPlaying = -1;
    private final int timerDelayMlsec = 500;

    /**
     * This constructor begins initializing the ChessGame object, before
     * triggering PopupColorChoice to prompt the user to pick a color to play,
     * setting up a Timer, and exitting. The Timer calls this.actionPerformed()
     * repeatedly until the color is set and it can execute, completing
     * ChessGame's initialization.
     */
    public ChessGame() throws IOException, FileNotFoundException {
        this(null);
    }

    /**
     * This constructor begins initializing the ChessGame object, before
     * triggering PopupColorChoice to prompt the user to pick a color to play,
     * setting up a Timer, and exitting. The Timer calls this.actionPerformed()
     * repeatedly until the color is set and it can execute, completing
     * ChessGame's initialization.
     *
     * @param fileName The filename of a board.csv file that specifies a
     *                 chessboard, which is loaded and used to set up the board
     *                 before play. The board must be a CSV file, with no
     *                 header, exactly 8 rows, exactly 8 columns, containing
     *                 only integers, and having each integer be a valid piece
     *                 integer value. (See the top of BoardArrays.java for
     *                 details.)
     */
    public ChessGame(final String fileNameStr) throws IOException, FileNotFoundException {
        super("Chess Game");
        
        Dimension windowDims;
        float scalingProportion;

        popupColorChoice = null;

        /* If this class, when executed as the frontend of the program, was
           passed a filename argument, this constructor was called with that
           String as an argument, so its value is set to fileName. The value
           won't be needed until the 2nd half of the constructor logic in
           actionPerformed(), so loading it into an instance variable is
           necessary for the value to still be available then. */
        if (Objects.nonNull(fileNameStr)) {
            fileName = fileNameStr;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        /* The dimensions of the board and the moves log are derived from the
           size of the screen, so they scale with the screen resolution. The
           height of the board (including insets) is 80% the height of the
           screen, and it has an equal width. The move log is the same height,
           and has a width equal to 20% of the height of the screen. Because the
           board and the moves log are side-by-side, the final window will have
           a width equal to the screen height. */
        Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
        boardSectionDims = new Dimension((int) Math.floor(0.8D * (double) screenDims.getHeight()),
                                         (int) Math.floor(0.8D * (double) screenDims.getHeight()));
        movesLogSectionDims = new Dimension((int) Math.floor(0.2D * (double) screenDims.getHeight()),
                                            (int) Math.floor(0.8D * (double) screenDims.getHeight()));
        windowDims = new Dimension((int) boardSectionDims.getWidth() + (int) movesLogSectionDims.getWidth(),
                                   (int) boardSectionDims.getHeight());

        gameLayout = new GridBagLayout();
        gamePanel = new JPanel(gameLayout);
        setContentPane(gamePanel);
        setSize(windowDims);

        /* A Dimension object is instanced with the size of the board, which is
           the size of its allotted space on the board minus the insets that
           will be set on it later (20px on all 4 sides. */
        boardDims = new Dimension((int) boardSectionDims.getWidth() - 40, (int) boardSectionDims.getHeight() - 40);

        /* The CoordinatesManager is instanced with a scaling proportion, which
           it uses to scale the icons it's loaded from their original resolution
           to the size of the squares on BoardView's chessboard display. */
        scalingProportion = (float) boardDims.getWidth()
                                    / CoordinatesManager.TOTAL_BOARD_MEASUREMENT_100PCT;
        coordinatesManager = new CoordinatesManager(scalingProportion);

        /* The ImagesManager object is instanced. Chessboard uses this to
           retrieve piece icons that BoardView will use to display the board's
           pieces with Graphics.drawImage(). */
        imagesManager = new ImagesManager("./images/", coordinatesManager.getSquareDimensions());

        chooseColor();
    }

    /**
     * This method sets colorPlaying to an out-of-band value (-1), triggers
     * the PopupColorChoice dialog box that asks the user what color they
     * wish to play, and sets a Timer object to send "construct" events to
     * this.actionPerformed(), which aborts when called until colorPlaying is
     * set. When PopupColorChoice has set the value for colorPlaying using
     * this.setColorPlaying(), the next call to this.actionPerformed() will not
     * abort, the timer is turned off, and the rest of the constructor logic in
     * actionPerformed() will be executed.
     *
     * This method is called at the end of this class's constructor (for the
     * first game), and it's also called from PopupGameOver when the player has
     * chosen to play again to restart the game setup process.
     */
    public void chooseColor() {
        colorPlaying = -1;

        popupColorChoice = new PopupColorChoice(this);

        if (Objects.isNull(colorChoicePopupDelayTimer)) {
            colorChoicePopupDelayTimer = new Timer(timerDelayMlsec, this);
            colorChoicePopupDelayTimer.setActionCommand("construct");
            colorChoicePopupDelayTimer.setRepeats(true);
        }
        colorChoicePopupDelayTimer.start();
    }

    /**
     * This mutator method sets the value for colorPlaying. It's used by
     * PopupColorChoice to communicate the color chosen to this object.
     *
     * @param colorPlayingVal The color the user has chosen to play, either
     *                        BoardArrays.WHITE or BoardArrays.BLACK.
     */
    public void setColorPlaying(final int colorPlayingVal) {
        colorPlaying = colorPlayingVal;
        colorOnTop = colorPlaying == BoardArrays.WHITE ? BoardArrays.BLACK : BoardArrays.WHITE;
    }

    /**
     * This method is used to execute the rest of the logic needed to complete
     * the constructor's initialization of the object. It's executed as an
     * actionPerformed() method so that a break in the execution can occur while
     * the user interacts with the PopupColorChoice dialog box and chooses a
     * color; also so that it can be repeated if a subsequent game is held. A
     * Timer is set at the end of the constructor that re-calls this method
     * every 1/2 second.
     *
     * This method aborts immediately after being called if colorPlaying hasn't
     * been set. The PopupColorChoice has two buttons, [Play White] and [Play
     * Black]; when one is clicked, the button's logic calls this class's
     * setColorPlaying() method with the value chosen, and closes the dialog
     * box.
     *
     * The Timer that calls this method keeps re-executing it, so the next time
     * it's called the initial test if the colorPlaying instance variable has
     * been set succeeds, the timer is stopped, and the remaining constructor
     * logic is executed.
     *
     * @param event The ActionEvent object generated by the Timer. Its command
     *              should be "construct".
     */
    public void actionPerformed(final ActionEvent event) {
        int[][] boardArray = null;

        if (!event.getActionCommand().equals("construct")) {
            return;
        } else if (colorPlaying == -1) {
            if (!popupColorChoice.isDisplayable()) {
                System.exit(0);
            }
            return;
        }

        colorChoicePopupDelayTimer.stop();

        if (Objects.nonNull(fileName)) {
            /* If the fileName string is set, then the constructor was passed
               a filename as an argument. The expected file is a CSV file
               representing a board, to instantiate the board with rather than
               starting from the normal starting configuration. */
            try {
                /* The utility method BoardArrays.fileNameToBoardArray() loads
                   the CSV file and interprets it to a boardArray, if it's
                   validly composed. (It must have no header, exactly 8 rows,
                   exactly 8 columns for each row, have all its values be ints,
                   and have the ints either be 0 or be a valid piece integer
                   representation of int flag constants (see the top of the
                   BoardArrays class) Or'd together. */
                boardArray = BoardArrays.fileNameToBoardArray(fileName);
            } catch (NullPointerException | BoardArrayFileParsingException | IOException exception) {
                /* The exception thrown has its name parsed out of the class,
                   and JOptionPane.showMessageDialog() is used to display a
                   popup to the user with the error message. The stack trace is
                   printed to console and the program exits. */
                String exceptionClassName = exception.getClass().getName().split("^.*\\.")[1];
                JOptionPane.showMessageDialog(this, "Loading a board file caused a " + exceptionClassName + ":\n"
                                                    + exception.getMessage());
                exception.printStackTrace();
                System.exit(1);
            }
            if (Objects.nonNull(chessboard)) {
                /* If chessboard is non-null then this is a subsequent game.
                   The new color values are set, a blank boardArray is set, and
                   Chessboard.layOutPieces() is used to array the pieces on the
                   board. */
                chessboard.setColors(colorPlaying, colorOnTop);
                chessboard.setBoardArray(new int[8][8]);
                chessboard.layOutPieces();
            } else {
                /* Otherwise this is a first run, so a new Chessboard object is
                   instanced, with the imported boardArray as its 1st argument. */
                chessboard = new Chessboard(boardArray, imagesManager, colorPlaying, colorOnTop);
            }
        } else {
            if (Objects.nonNull(chessboard)) {;
                /* If chessboard is non-null then this is a subsequent game.
                   The new color values are set, a blank boardArray is set, and
                   Chessboard.layOutPieces() is used to array the pieces on the
                   board. */
                chessboard.setColors(colorPlaying, colorOnTop);
                chessboard.setBoardArray(new int[8][8]);
                chessboard.layOutPieces();
            } else {
                /* Otherwise this is a first run, so a new Chessboard object is
                   instanced. */
                chessboard = new Chessboard(imagesManager, colorPlaying, colorOnTop);
            }
        }

        /* Setting the GridBagLayout object's columnWidths and rowHeights. The
           GUI consists of just two elements: the chessboard on the left side
           (Chessboard), and a textarea on the right that lists moves made and
           shows error messages if an invalid move is attempted. */
        gameLayout.columnWidths = new int[] {(int) boardSectionDims.getWidth(), (int) movesLogSectionDims.getWidth()};
        gameLayout.rowHeights = new int[] {(int) boardSectionDims.getHeight()};

        if (Objects.nonNull(movesLog)) {
            /* If the MovesLog object is non-null, then this is a subsequent
               game, and all it needs is to be cleared. */
            movesLog.clear();
        } else {
            /* Otherwise this is a first game, so GridBagConstraints are
               devised, the MovesLog object is instanced and it's added to the
               JPanel object with its constraints. */
            movesLog = new MovesLog();
            JScrollPane scrollableMovesLog = new JScrollPane(movesLog);

            GridBagConstraints movesLogConstraints = new GridBagConstraints();
            movesLogConstraints.fill = GridBagConstraints.BOTH;
            movesLogConstraints.gridy = 0;
            movesLogConstraints.gridx = 1;
            movesLogConstraints.gridheight = 1;
            movesLogConstraints.gridwidth = 1;
            movesLogConstraints.insets = new Insets(20, 0, 20, 20);

            gamePanel.add(scrollableMovesLog, movesLogConstraints);
        }

        if (Objects.nonNull(boardView)) {
            /* If the BoardView object is non-null, then this is a subsequent
               game. All it needs to know is whether the AI moves first (ie. is
               playing White) or not. */
            if (colorPlaying == BoardArrays.BLACK) {
                boardView.aiMovesFirst();
            } else {
                boardView.repaint();
            }
        } else {
            /* Otherwise this is a first game, so GridBagConstraints are
               devised, the BoardView object is instanced and it's added to the
               JPanel object with its constraints. */
            GridBagConstraints boardConstraints = new GridBagConstraints();
            boardConstraints.fill = GridBagConstraints.BOTH;
            boardConstraints.gridy = 0;
            boardConstraints.gridx = 0;
            boardConstraints.gridheight = 1;
            boardConstraints.gridwidth = 1;
            boardConstraints.insets = new Insets(20, 20, 20, 20);

            boardView = new BoardView(this, boardDims, imagesManager, coordinatesManager, chessboard, movesLog, colorPlaying);
            gamePanel.add(boardView, boardConstraints);
            boardView.addMouseListener(boardView);
        }

        validate();
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * The main method of this class, which instantiates an object of this class
     * when run. If an argument is passed on the commandline, it's assumed to be
     * a filename and used as an argument to this class's constructor.
     *
     * @param args Either a 0-length array, or a 1-length array comprised of a
     *             filename of a board.csv file to prime the board with.
     */
    public static void main(final String[] args) throws IOException, FileNotFoundException {
        ChessGame chessgame;
        if (args.length > 0) {
            String fileName = args[0];
            chessgame = new ChessGame(fileName);
        } else {
            chessgame = new ChessGame();
        }
    }
}
