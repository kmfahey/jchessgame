package com.kmfahey.jchessgame;

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
import java.io.IOException;
import java.io.FileNotFoundException;


public class ChessGame extends JFrame {

    private ChesspiecesManager chesspiecesManager;
    private CoordinatesManager coordinatesManager;
    private ImagesManager imagesManager;
    private float scalingProportion;

    public ChessGame() throws IOException, FileNotFoundException {
        super("Chess");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowDims = new Dimension((int) Math.floor(0.8D * (double) screenDims.getHeight()),
                                             (int) Math.floor(0.8D * (double) screenDims.getHeight()));

        GridBagLayout gameLayout = new GridBagLayout();
        JPanel gamePanel = new JPanel(gameLayout);
        setContentPane(gamePanel);
        setSize(windowDims);

        GridBagConstraints chessboardConstraints = new GridBagConstraints();
        chessboardConstraints.fill = GridBagConstraints.BOTH;
        chessboardConstraints.gridy = 0;
        chessboardConstraints.gridx = 0;
        chessboardConstraints.gridheight = 1;
        chessboardConstraints.gridwidth = 1;
        chessboardConstraints.insets = new Insets(20, 20, 20, 20);

        Dimension chessboardDims = new Dimension((int) windowDims.getWidth() - 40, (int) windowDims.getHeight() - 40);

        scalingProportion = (float) chessboardDims.getWidth() / CoordinatesManager.TOTAL_BOARD_MEASUREMENT_100PCT;
        coordinatesManager = new CoordinatesManager(scalingProportion);

        imagesManager = new ImagesManager("./images/", coordinatesManager.getSquareDimensions());

        chesspiecesManager = new ChesspiecesManager(imagesManager, "black");

        gameLayout.columnWidths = new int[] {(int) windowDims.getWidth()};
        gameLayout.rowHeights = new int[] {(int) windowDims.getHeight()};

        ChessboardView chessboardView = new ChessboardView(chessboardDims, imagesManager, coordinatesManager, chesspiecesManager);
        gamePanel.add(chessboardView, chessboardConstraints);

        validate();
        pack();
    }

    public static void main(String[] args) throws IOException, FileNotFoundException {
        ChessGame chessGame = new ChessGame();
        chessGame.setVisible(true);
        chessGame.setLocationRelativeTo(null);
    }
}
