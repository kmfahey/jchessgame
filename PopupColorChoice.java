package com.kmfahey.jchessgame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * This class subclasses JFrame, implementing a dialog box that pops up
 * on instantiation and says, "Welcome to the game. Do you wish to play
 * White, or Black?" The choice is implemented with two buttons, each of
 * which conveys the chosen color back to the calling ChessGame object with
 * ChessGame.setColorPlaying() and closes the dialog box.
 *
 * @see ChessGame
 * @see ChessGame#setColorPlaying
 */
public class PopupColorChoice extends JFrame {

    /**
     * The ChessGame object that instanced this dialog box; stored so its
     * mutator ChessGame.setColorPlaying() can be used to convey the choice the
     * player makes.
     */
    private final ChessGame callingChessGame;

    /**
     * This constructor instances and displays a popup dialog box that reads,
     * "Welcome to the game. Do you wish to play White, or Black?". It features
     * two buttons, [Play White] and [Play Black]. When a button is clicked,
     * that color is set with the ChessGame object that spawned this dialog box
     * using a mutator, and this dialog box closes.
     *
     * @param callingChessGameObj The ChessGame object that spawned this dialog
     *                            box, included so the button objects can set
     *                            the color to play on it with a mutator.
     * @see ChessGame#setColorPlaying
     */
    public PopupColorChoice(final ChessGame callingChessGameObj) {
        super("Color Choice");

        callingChessGame = callingChessGameObj;

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
        setSize(300, 150);
        dboxLayout.columnWidths = new int[] {150, 150};
        dboxLayout.rowHeights = new int[] {75, 75};

        /* This builds the JLabel that displays the text of the dialog box
           to the user, instances the GridBagConstraints object for it, and
           attaches the JLabel and its constraints to the JPanel dboxPane. */
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

        /* This builds the [Play Black] button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JButton playWhiteButton = buildPlayWhiteButton();
        GridBagConstraints playWhiteButtonConstraints = buildConstraints(1, 0, 1, 1);
        playWhiteButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(playWhiteButton, playWhiteButtonConstraints);

        /* This builds the [Play Black] button, instances the GridBagConstraints
           object for it, and sets it with its constraints object to the JPanel
           dboxPane. */
        JButton playBlackButton = buildPlayBlackButton();
        GridBagConstraints playBlackButtonConstraints = buildConstraints(1, 1, 1, 1);
        playBlackButtonConstraints.insets = new Insets(20, 40, 20, 40);
        dboxPane.add(playBlackButton, playBlackButtonConstraints);

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
     * This method builds a custom JButton representing the choice to play
     * White, with a private ActionListener class that sends the color choice to
     * the ChessGame that spawned this popup using ChessGame.setColorPlaying()
     * and closing this window with JFrame.dispose().
     */
    private JButton buildPlayWhiteButton() {
        PopupColorChoice enclosingDbox = this;
        JButton button = new JButton("Play White");
        button.addActionListener(event -> {
            callingChessGame.setColorPlaying(BoardArrays.WHITE);
            enclosingDbox.dispose();
        });
        return button;
    }

    /*
     * This method builds a custom JButton representing the choice to play
     * Black, with a private ActionListener class that sends the color choice to
     * the ChessGame that spawned this popup using ChessGame.setColorPlaying()
     * and closing this window with JFrame.dispose().
     */
    private JButton buildPlayBlackButton() {
        PopupColorChoice enclosingDbox = this;
        JButton button = new JButton("Play Black");
        button.addActionListener(event -> {
            callingChessGame.setColorPlaying(BoardArrays.BLACK);
            enclosingDbox.dispose();
        });
        return button;
    }
}
