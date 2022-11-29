package com.kmfahey.jchessgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class PopupPawnPromotion extends JFrame {

    private BoardView callingBoardView;
    private ButtonGroup buttonGroup;
    private int pawnXCoord;
    private int pawnYCoord;
    private int newPiece;
    private Random randomNumberGenerator = new Random();

    public PopupPawnPromotion(final BoardView callingBoardViewObj, final int xCoordVal, final int yCoordVal) {
        super("Pawn Promotion");

        callingBoardView = callingBoardViewObj;
        pawnXCoord = xCoordVal;
        pawnYCoord = yCoordVal;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        GridBagLayout dboxLayout = new GridBagLayout();
        JPanel dboxPane = new JPanel(dboxLayout);
        setContentPane(dboxPane);
        setSize(400, 300);

        dboxLayout.columnWidths = new int[] {200, 200};
        dboxLayout.rowHeights = new int[] {75, 50, 50, 75};

        GridBagConstraints pawnPromLabConstraints = buildConstraints(0, 0, 1, 2);
        pawnPromLabConstraints.insets = new Insets(10, 20, 10, 20);
        JLabel pawnPromExplLabel = new JLabel(
            "<html><div style=\"text-align: center;\">"
                + "Your pawn is being promoted. Please choose<br>"
                + "the piece you want to exchange it for."
            + "</div></html>");
        pawnPromExplLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pawnPromExplLabel.setVerticalAlignment(SwingConstants.CENTER);
        dboxPane.add(pawnPromExplLabel, pawnPromLabConstraints);

        // creates radio button and set corresponding action
        // commands

        JRadioButton rBtnRook = new JRadioButton("Rook");
        rBtnRook.setActionCommand("Rook");

        JRadioButton rBtnKnight = new JRadioButton("Knight");
        rBtnKnight.setActionCommand("Knight");

        JRadioButton rBtnBishop = new JRadioButton("Bishop");
        rBtnBishop.setActionCommand("Bishop");

        JRadioButton rBtnQueen = new JRadioButton("Queen");
        rBtnQueen.setActionCommand("Queen");

        // add radio buttons to a ButtonGroup
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rBtnRook);
        buttonGroup.add(rBtnKnight);
        buttonGroup.add(rBtnBishop);
        buttonGroup.add(rBtnQueen);

        GridBagConstraints rBtnRookConstraints = buildConstraints(1, 0, 1, 1);
        rBtnRookConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnRook, rBtnRookConstraints);

        GridBagConstraints rBtnKnightConstraints = buildConstraints(1, 1, 1, 1);
        rBtnKnightConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnKnight, rBtnKnightConstraints);

        GridBagConstraints rBtnBishopConstraints = buildConstraints(2, 0, 1, 1);
        rBtnBishopConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnBishop, rBtnBishopConstraints);

        GridBagConstraints rBtnQueenConstraints = buildConstraints(2, 1, 1, 1);
        rBtnQueenConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnQueen, rBtnQueenConstraints);

        JButton iveChosenButton = buildIveChosenButton();
        GridBagConstraints iveChosenButtonConstraints = buildConstraints(3, 1, 1, 1);
        iveChosenButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(iveChosenButton, iveChosenButtonConstraints);

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

    private JButton buildIveChosenButton() {
        PopupPawnPromotion enclosingDbox = this;
        JButton button = new JButton("I've Chosen");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                newPiece = 0;
                ButtonModel selectedButtonMod = buttonGroup.getSelection();
                if (Objects.isNull(selectedButtonMod)) {
                    return;
                }
                switch (selectedButtonMod.getActionCommand()) {
                    case "Rook":
                        newPiece = BoardArrays.ROOK; break;
                    case "Knight":                       /* Since there's two Knight models, LEFT and RIGHT, this */
                        newPiece = BoardArrays.KNIGHT    /* expression chooses at random between them.            */
                                   | (randomNumberGenerator.nextInt(2) == 1 ? BoardArrays.LEFT : BoardArrays.RIGHT);
                        break;
                    case "Bishop":
                        newPiece = BoardArrays.BISHOP; break;
                    case "Queen":
                        newPiece = BoardArrays.QUEEN; break;
                    default:
                        break;
                }
                callingBoardView.promotePawn(pawnXCoord, pawnYCoord, newPiece);
                callingBoardView.repaint();
                enclosingDbox.dispose();
            }
        });
        return button;
    }
}
