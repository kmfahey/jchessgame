package com.kmfahey.jchessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class PopupColorChoice extends JFrame {

    private BoardView callingBoardView;
    private ButtonGroup buttonGroup;
    private int pawnXCoord;
    private int pawnYCoord;
    private int newPiece;
    private Random randomNumberGenerator = new Random();
    private ChessGame callingChessGame;
    private boolean isLaterGame;

    public PopupColorChoice(final ChessGame callingChessGameObj) {
        super("Color Choice");

        callingChessGame = callingChessGameObj;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        GridBagLayout dboxLayout = new GridBagLayout();
        JPanel dboxPane = new JPanel(dboxLayout);
        setContentPane(dboxPane);
        setSize(300, 150);

        dboxLayout.columnWidths = new int[] {150, 150};
        dboxLayout.rowHeights = new int[] {75, 75};

        GridBagConstraints colorChoiceLabConstraints = buildConstraints(0, 0, 1, 2);
        colorChoiceLabConstraints.insets = new Insets(10, 20, 10, 20);
        JLabel pawnPromExplLabel = new JLabel(
            "<html><div style=\"text-align: center;\">"
                + "Welcome to the game. Do you wish to<br>"
                + "play White, or Black?"
            + "</div></html>");
        pawnPromExplLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pawnPromExplLabel.setVerticalAlignment(SwingConstants.CENTER);
        dboxPane.add(pawnPromExplLabel, colorChoiceLabConstraints);

        JButton playWhiteButton = buildPlayWhiteButton();
        GridBagConstraints playWhiteButtonConstraints = buildConstraints(1, 0, 1, 1);
        playWhiteButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(playWhiteButton, playWhiteButtonConstraints);

        JButton playBlackButton = buildPlayBlackButton();
        GridBagConstraints playBlackButtonConstraints = buildConstraints(1, 1, 1, 1);
        playBlackButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(playBlackButton, playBlackButtonConstraints);

        setLocationRelativeTo(null);
        setVisible(true);

        validate();
        pack();
    }

    private GridBagConstraints buildConstraints(final int row, final int col, final int rowspan, final int colspan) {
        GridBagConstraints elemConstraints = new GridBagConstraints();
        elemConstraints.fill = GridBagConstraints.BOTH;
        elemConstraints.gridy = row;
        elemConstraints.gridx = col;
        elemConstraints.gridheight = rowspan;
        elemConstraints.gridwidth = colspan;
        return elemConstraints;
    }

    private JButton buildPlayWhiteButton() {
        PopupColorChoice enclosingDbox = this;
        JButton button = new JButton("Play White");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                callingChessGame.setColorPlaying(BoardArrays.WHITE);
                enclosingDbox.dispose();
            }
        });
        return button;
    }

    private JButton buildPlayBlackButton() {
        PopupColorChoice enclosingDbox = this;
        JButton button = new JButton("Play Black");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                callingChessGame.setColorPlaying(BoardArrays.BLACK);
                enclosingDbox.dispose();
            }
        });
        return button;
    }

}
