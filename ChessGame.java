package com.kmfahey.jchessgame;

import java.util.Objects;
import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
//import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.FileNotFoundException;


public class ChessGame extends JFrame {

    private Chessboard chessboard;
    private CoordinatesManager coordinatesManager;
    private ImagesManager imagesManager;
    private float scalingProportion;
    private int colorPlaying;
    private int colorOnTop;

    public ChessGame() throws IOException, FileNotFoundException {
        this(null);
    }

    public ChessGame(String fileName) throws IOException, FileNotFoundException {
        super("Chess Game");

        int[][] boardArray = null;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowDims = new Dimension((int) Math.floor(0.8D * (double) screenDims.getHeight()),
                                             (int) Math.floor(0.8D * (double) screenDims.getHeight()));

        GridBagLayout gameLayout = new GridBagLayout();
        JPanel gamePanel = new JPanel(gameLayout);
        setContentPane(gamePanel);
        setSize(windowDims);

        GridBagConstraints boardConstraints = new GridBagConstraints();
        boardConstraints.fill = GridBagConstraints.BOTH;
        boardConstraints.gridy = 0;
        boardConstraints.gridx = 0;
        boardConstraints.gridheight = 1;
        boardConstraints.gridwidth = 1;
        boardConstraints.insets = new Insets(20, 20, 20, 20);

        Dimension boardDims = new Dimension((int) windowDims.getWidth() - 40,
                                            (int) windowDims.getHeight() - 40);

        scalingProportion = (float) boardDims.getWidth()
                                    / CoordinatesManager.TOTAL_BOARD_MEASUREMENT_100PCT;
        coordinatesManager = new CoordinatesManager(scalingProportion);

        imagesManager = new ImagesManager("./images/", coordinatesManager.getSquareDimensions());

        colorPlaying = BoardArrays.WHITE;
        colorOnTop = BoardArrays.BLACK;

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
            chessboard = new Chessboard(boardArray, imagesManager, colorPlaying, colorOnTop);
        } else {
            chessboard = new Chessboard(imagesManager, colorPlaying, colorOnTop);
        }

        if (Objects.isNull(boardArray)) {
            boardArray = chessboard.getBoardArray();
            BoardArrays.printBoard(boardArray);
        }

        gameLayout.columnWidths = new int[] {(int) windowDims.getWidth()};
        gameLayout.rowHeights = new int[] {(int) windowDims.getHeight()};

        BoardView boardView = new BoardView(this, boardDims, imagesManager,
                                            coordinatesManager, chessboard, colorPlaying);
        gamePanel.add(boardView, boardConstraints);

        boardView.addMouseListener(boardView);

        validate();
        pack();
    }

    public static void main(final String[] args) throws IOException, FileNotFoundException {
        ChessGame chessGame;
        if (args.length > 0) {
            String fileName = args[0];
            chessGame = new ChessGame(fileName);
        } else {
            chessGame = new ChessGame();
        }
        chessGame.setVisible(true);
        chessGame.setLocationRelativeTo(null);
    }
}
