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

public class PopupGameOver extends JFrame {

    public static final int PLAYER_LOST = 0;
    public static final int AI_LOST = 1;

    private ButtonGroup buttonGroup;
    private int pawnXCoord;
    private int pawnYCoord;
    private int newPiece;
    private Random randomNumberGenerator = new Random();

    public PopupGameOver(final int losingSide) {
        super("Game Over");

        JLabel pawnPromExplLabel;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        GridBagLayout dboxLayout = new GridBagLayout();
        JPanel dboxPane = new JPanel(dboxLayout);
        setContentPane(dboxPane);
        setSize(400, 300);

        dboxLayout.columnWidths = new int[] {150, 150};
        dboxLayout.rowHeights = new int[] {75, 75};

        GridBagConstraints colorChoiceLabConstraints = buildConstraints(0, 0, 1, 2);
        colorChoiceLabConstraints.insets = new Insets(10, 20, 10, 20);
        if (losingSide == PLAYER_LOST) {
            pawnPromExplLabel = new JLabel(
                "<html><div style=\"text-align: center;\">"
                       + "Your king is in checkmate. You have lost."
                + "</div></html>");
        } else {
            pawnPromExplLabel = new JLabel(
                "<html><div style=\"text-align: center;\">"
                       + "The AI's king is in checkmate. You have won."
                + "</div></html>");
        }
        pawnPromExplLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pawnPromExplLabel.setVerticalAlignment(SwingConstants.CENTER);
        dboxPane.add(pawnPromExplLabel, colorChoiceLabConstraints);

        JButton okayButton = buildOkayButton();
        GridBagConstraints okayButtonConstraints = buildConstraints(1, 1, 1, 1);
        okayButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(okayButton, okayButtonConstraints);

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

    private JButton buildOkayButton() {
        PopupGameOver enclosingDbox = this;
        JButton button = new JButton("Okay");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                enclosingDbox.dispose();
            }
        });
        return button;
    }
}
