package com.kmfahey.jchessgame;

//import java.awt.Color;
import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.awt.Graphics;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.Iterator;
import javax.swing.JComponent;
//import javax.swing.Timer;

public class ChessboardView extends JComponent /* implements ActionListener, MouseListener */ {

    private Dimension chessboardDims;
    private ImageManager imageManager;

    public ChessboardView(final Dimension componentDims, final ImageManager imageManagerObj) {
        chessboardDims = componentDims;
        imageManager = imageManagerObj;
        repaint();
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        graphics.drawImage(imageManager.getChessboard(chessboardDims), 0, 0, this);
    }
}
