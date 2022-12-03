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

/**
 * This class subclasses JFrame, implementing a dialog box that informs the
 * player that their pawn is being promoted and giving them a choice, via radio
 * buttons, of what piece to promote it to. Once that choice is made and the
 * [I've Chosen] button is clicked, the choice is conveyed back to the BoardView
 * object using BoardView.promotePawn(); then BoardView.repaint() is called to
 * redraw the board with the new piece displayed.
 *
 * @see BoardView.blankBoard
 * @see ChessGame.chooseColor
 * @see PopupColorChoice
 * @see ChessGame.actionPerformed
 */
public class PopupPawnPromotion extends JFrame {

    private BoardView callingBoardView;
    private ButtonGroup buttonGroup;
    private int pawnXCoord;
    private int pawnYCoord;
    private int newPiece;
    private Random randomNumberGenerator = new Random();

    /**
     * This constructor initializes the PopupPawnPromotion object and displays the
     * dialog box it comprises.
     *
     * @param callingBoardViewObj The BoardView object that the calling
     *                            ChessGame object is using.
     * @param xCoordVal           The x coordinate of the pawn on the chess
     *                            board.
     * @param yCoordVal           The y coordinate of the pawn on the chess
     *                            board.
     */
    public PopupPawnPromotion(final BoardView callingBoardViewObj, final int xCoordVal, final int yCoordVal) {
        super("Pawn Promotion");

        callingBoardView = callingBoardViewObj;
        pawnXCoord = xCoordVal;
        pawnYCoord = yCoordVal;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        /* Instancing the GridBagLayout object and the JPanel object and
           associating them. */
        GridBagLayout dboxLayout = new GridBagLayout();
        JPanel dboxPane = new JPanel(dboxLayout);
        setContentPane(dboxPane);

        /* Setting the size of this JFrame subclass object, and the columnWidths
           and the rowHeights on the GridBagLayout object. The dialog box
           operates on a 2x2 grid. */
        setSize(400, 300);
        dboxLayout.columnWidths = new int[] {200, 200};
        dboxLayout.rowHeights = new int[] {75, 50, 50, 75};

        /* Builds the JLabel that communicates the pawn promotion situation to
           the player, instances GridBagConstraints, and attaches it with its
           constraints to the JPanel object dboxPane. */
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

        /* This builds the (o) Rook radio button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JRadioButton rBtnRook = new JRadioButton("Rook");
        rBtnRook.setActionCommand("Rook");
        GridBagConstraints rBtnRookConstraints = buildConstraints(1, 0, 1, 1);
        rBtnRookConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnRook, rBtnRookConstraints);

        /* This builds the (o) Knight radio button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JRadioButton rBtnKnight = new JRadioButton("Knight");
        rBtnKnight.setActionCommand("Knight");
        GridBagConstraints rBtnKnightConstraints = buildConstraints(1, 1, 1, 1);
        rBtnKnightConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnKnight, rBtnKnightConstraints);

        /* This builds the (o) Bishop radio button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JRadioButton rBtnBishop = new JRadioButton("Bishop");
        rBtnBishop.setActionCommand("Bishop");
        GridBagConstraints rBtnBishopConstraints = buildConstraints(2, 0, 1, 1);
        rBtnBishopConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnBishop, rBtnBishopConstraints);

        /* This builds the (o) Queen radio button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JRadioButton rBtnQueen = new JRadioButton("Queen");
        rBtnQueen.setActionCommand("Queen");
        GridBagConstraints rBtnQueenConstraints = buildConstraints(2, 1, 1, 1);
        rBtnQueenConstraints.insets = new Insets(10, 40, 10, 40);
        dboxPane.add(rBtnQueen, rBtnQueenConstraints);

        /* This builds the [I've Chosen] button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JButton iveChosenButton = buildIveChosenButton();
        GridBagConstraints iveChosenButtonConstraints = buildConstraints(3, 1, 1, 1);
        iveChosenButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(iveChosenButton, iveChosenButtonConstraints);

        /* The four radio buttons are added to a ButtonGroup object so that the
           exclusiveness of radio buttons can run correctly, and the choice can
           be read using ButtonGroup.getSelection(). */
        buttonGroup = new ButtonGroup();
        buttonGroup.add(rBtnRook);
        buttonGroup.add(rBtnKnight);
        buttonGroup.add(rBtnBishop);
        buttonGroup.add(rBtnQueen);

        setLocationRelativeTo(null);
        setVisible(true);

        validate();
        pack();
    }

    /*
     * This utility function instances a GridBagConstraints object and sets a
     * series of instance variables on it according to the arguments it was
     * called with.
     *
     * @param row      The value for GridBagConstraints.gridy.
     * @param col      The value for GridBagConstraints.gridx.
     * @param rowspawn The value for GridBagConstraints.gridheight.
     * @param colspawn The value for GridBagConstraints.gridwidth.
     */
    private GridBagConstraints buildConstraints(final int row, final int col, final int rowspan, final int colspan) {
        GridBagConstraints elemConstraints = new GridBagConstraints();
        elemConstraints.fill = GridBagConstraints.BOTH;
        elemConstraints.gridy = row;
        elemConstraints.gridx = col;
        elemConstraints.gridheight = rowspan;
        elemConstraints.gridwidth = colspan;
        return elemConstraints;
    }

    /*
     * This method builds a custom JButton that is clicked after a radio
     * Thbutton is selected. (If no radio button has been selected, the button
     * Thdoes nothing.) e correct integer piece value is derived (choosing
     * Thrandomly between KNIGHT | LEFT or KNIGHT | RIGHT if knight was chosen),
     * Thand communicated to the BoardView object that spawned this popup
     * Thusing BoardView.promotePawn() and BoardView.repaint(). Then it uses
     * Ththis.dispose() to close the dialog box.
     * 
     * @see BoardView.promotePawn()
     * @see BoardView.repaint()
     */
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
                if (newPiece != 0) {
                    callingBoardView.promotePawn(pawnXCoord, pawnYCoord, newPiece);
                    callingBoardView.repaint();
                    enclosingDbox.dispose();
                }
            }
        });
        return button;
    }
}
