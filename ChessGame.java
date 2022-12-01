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


public class ChessGame extends JFrame implements ActionListener {

    private final int timerDelayMlsec = 500;
    private float scalingProportion;
    private int colorOnTop;
    private int colorPlaying = -1;
    private CoordinatesManager coordinatesManager;
    private Dimension windowDims;
    private Dimension boardDims;
    private Dimension boardSectionDims;
    private Dimension movesLogSectionDims;
    private GridBagLayout gameLayout;
    private ImagesManager imagesManager;
    private JPanel gamePanel;
    private PopupColorChoice popupColorChoice;
    private String fileName = null;
    private Timer colorChoicePopupDelayTimer;
    private Chessboard chessboard = null;
    private BoardView boardView;
    private MovesLog movesLog;

    public ChessGame() throws IOException, FileNotFoundException {
        this(null);
    }

    public ChessGame(final String fileNameStr) throws IOException, FileNotFoundException {
        super("Chess Game");

        popupColorChoice = null;

        if (Objects.nonNull(fileNameStr)) {
            fileName = fileNameStr;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
        boardSectionDims = new Dimension((int) Math.floor(0.8D * (double) screenDims.getHeight()),
                                         (int) Math.floor(0.8D * (double) screenDims.getHeight()));
        movesLogSectionDims = new Dimension((int) Math.floor(0.2D * (double) screenDims.getHeight()),
                                            (int) Math.floor(0.8D * (double) screenDims.getHeight()));

        gameLayout = new GridBagLayout();
        gamePanel = new JPanel(gameLayout);
        setContentPane(gamePanel);
        setSize(boardSectionDims);

        boardDims = new Dimension((int) boardSectionDims.getWidth() - 40, (int) boardSectionDims.getHeight() - 40);

        scalingProportion = (float) boardDims.getWidth()
                                    / CoordinatesManager.TOTAL_BOARD_MEASUREMENT_100PCT;
        coordinatesManager = new CoordinatesManager(scalingProportion);

        imagesManager = new ImagesManager("./images/", coordinatesManager.getSquareDimensions());

        chooseColor();
    }

    public void chooseColor() {
        colorPlaying = -1;

        popupColorChoice = new PopupColorChoice(this);

        colorChoicePopupDelayTimer = new Timer(timerDelayMlsec, this);
        colorChoicePopupDelayTimer.setActionCommand("resume");
        colorChoicePopupDelayTimer.setRepeats(true);
        colorChoicePopupDelayTimer.start();
    }

    public void setColorPlaying(final int colorToPlay) {
        colorPlaying = colorToPlay;
        colorOnTop = colorPlaying == BoardArrays.WHITE ? BoardArrays.BLACK : BoardArrays.WHITE;
    }

    public void actionPerformed(final ActionEvent event) {
        int[][] boardArray = null;

        if (!event.getActionCommand().equals("resume")) {
            return;
        } else if (colorPlaying == -1) {
            if (!popupColorChoice.isDisplayable()) {
                System.exit(0);
            }
            return;
        }

        colorChoicePopupDelayTimer.stop();

        if (Objects.nonNull(fileName)) {
            try {
                boardArray = BoardArrays.fileNameToBoardArray(fileName);
            } catch (NullPointerException | BoardArrayFileParsingException | IOException exception) {
                String exceptionClassName = exception.getClass().getName().split("^.*\\.")[1];
                JOptionPane.showMessageDialog(this, "Loading a board file caused a " + exceptionClassName + ":\n"
                                                    + exception.getMessage());
                exception.printStackTrace();
                System.exit(1);
            }
            if (Objects.nonNull(chessboard)) {
                chessboard.setColors(colorPlaying, colorOnTop);
                chessboard.setBoardArray(boardArray);
                chessboard.layOutPieces();
            } else {
                chessboard = new Chessboard(boardArray, imagesManager, colorPlaying, colorOnTop);
            }
        } else {
            if (Objects.nonNull(chessboard)) {;
                chessboard.setColors(colorPlaying, colorOnTop);
                chessboard.layOutPieces();
            } else {
                chessboard = new Chessboard(imagesManager, colorPlaying, colorOnTop);
                colorOnTop = (colorPlaying == BoardArrays.WHITE) ? BoardArrays.BLACK : BoardArrays.WHITE;
            }
        }

        gameLayout.columnWidths = new int[] {(int) boardSectionDims.getWidth(), (int) movesLogSectionDims.getWidth()};
        gameLayout.rowHeights = new int[] {(int) boardSectionDims.getHeight()};

        if (Objects.nonNull(movesLog)) {
            movesLog.clear();
        } else {
            movesLog = new MovesLog();
            JScrollPane scrollableMovesLog = new JScrollPane(movesLog);

            GridBagConstraints movesLogConstraints = new GridBagConstraints();
            movesLogConstraints.fill = GridBagConstraints.BOTH;
            movesLogConstraints.gridy = 0;
            movesLogConstraints.gridx = 1;
            movesLogConstraints.gridheight = 1;
            movesLogConstraints.gridwidth = 1;
            movesLogConstraints.insets = new Insets(20, 20, 20, 20);

            gamePanel.add(scrollableMovesLog, movesLogConstraints);
        }

        if (Objects.nonNull(boardView)) {
            if (colorPlaying == BoardArrays.BLACK) {
                boardView.aiMovesFirst();
            } else {
                boardView.repaint();
            }
        } else {
            GridBagConstraints boardConstraints = new GridBagConstraints();
            boardConstraints.fill = GridBagConstraints.BOTH;
            boardConstraints.gridy = 0;
            boardConstraints.gridx = 0;
            boardConstraints.gridheight = 1;
            boardConstraints.gridwidth = 1;
            boardConstraints.insets = new Insets(20, 20, 20, 20);

            boardView = new BoardView(this, boardDims, imagesManager, coordinatesManager, chessboard, colorPlaying);
            gamePanel.add(boardView, boardConstraints);
            boardView.addMouseListener(boardView);
        }

        validate();
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

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
