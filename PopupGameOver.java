package org.magentatobe.jchessgame;

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
 * Implements a dialog box that informs the player who won the chess game
 * that just completed; JChessGame spawns it as soon as checkmate is detected
 * for either side. It has two buttons, [Okay] and [Play Again]. If [Play
 * Again] is chosen, BoardView.blankBoard() is called to clear the board, and
 * JChessGame.chooseColor() is called to spawn the PopupColorChoice dialog box,
 * and prime JChessGame.actionPerformed() to be called to re-intialize the
 * JChessGame object.
 *
 * @see BoardView#blankBoard
 * @see JChessGame#chooseColor
 * @see PopupColorChoice
 * @see JChessGame#actionPerformed
 */
public class PopupGameOver extends JFrame {

    /** Flag, the player lost the game. */
    public static final int PLAYER_LOST = 0;

    /** Flag, the AI lost the game. */
    public static final int AI_LOST = 1;

    /**
     * The JChessGame object that instanced this dialog box. Stored so its method
     * JChessGame.chooseColor() can be called, spawning the PopupColorChoice
     * dialog box and priming JChessGame.actionPerformed() to be run once it
     * closes.
     */
    private final JChessGame chessgame;

    /**
     * The BoardView object that the program is using. Stored so
     * BoardView.blankBoard() can be called in the event
     * the player wants to play again.
     */
    private final BoardView boardview;

        /**
         * Initializes the PopupGameOver object and displays the dialog box it
         * comprises.
         *
         * @param chessGameObj The JChessGame object that spawned this dialog box.
         * @param boardViewObj The BoardView object that the calling JChessGame
         *                     object is using.
         * @param losingSide   An integer flag, one of PopupGameOver.AI_LOST or
         *                     PopupGameOver.PLAYER_LOST.
         */
        public PopupGameOver(final JChessGame chessGameObj, final BoardView boardViewObj, final int losingSide) {
            super("Game Over");

            JLabel pawnPromExplLabel;

            chessgame = chessGameObj;
            boardview = boardViewObj;

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
            dboxLayout.columnWidths = new int[] {150, 150};
            dboxLayout.rowHeights = new int[] {75, 75};

            /* This builds the JLabel that displays the text of the dialog box
               to the user-- communicating either a game won or a game lost
               to them depending on the value of losingSide-- instances the
               GridBagConstraints object for it, and attaches the JLabel and its
               constraints to the JPanel dboxPane. */
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

            /* This builds the [Play Again] button, instances the GridBagConstraints
               object for it, and sets it with its constraints object to the JPanel
               dboxPane. */
            JButton playAgainButton = buildPlayAgainButton();
            GridBagConstraints playAgainButtonConstraints = buildConstraints(1, 0, 1, 1);
            playAgainButtonConstraints.insets = new Insets(20, 40, 20, 40);
            dboxPane.add(playAgainButton, playAgainButtonConstraints);

            /* This builds the [Okay] button, instances the GridBagConstraints
               object for it, and sets it with its constraints object to the JPanel
               dboxPane. */
            JButton okayButton = buildOkayButton();
            GridBagConstraints okayButtonConstraints = buildConstraints(1, 1, 1, 1);
            okayButtonConstraints.insets = new Insets(20, 40, 20, 40);
            dboxPane.add(okayButton, okayButtonConstraints);

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
         * This method builds a custom JButton representing the response "Okay"
         * which accepts the end of the game and doesn't start another. Its private
         * ActionListener class calls this.dispose() to close this dialog box.
         */
        private JButton buildOkayButton() {
            PopupGameOver enclosingDbox = this;
            JButton button = new JButton("Okay");
            button.addActionListener(event -> enclosingDbox.dispose());
            return button;
        }

        /*
         * This method builds a custom JButton representing the choice to play
         * another game, with a private ActionListener class that triggers that
         * process by calling BoardView.blankBoard() to clear the BoardView
         * object's chessboard, calling JChessGame.chooseColor() to spawn the
         * PopupColorChoice object and initiate the process which will run
         * JChessGame.actionPerformed(), and this.dispose() to close this dialog box.
         *
         * @see BoardView#blankBoard
         * @see JChessGame#chooseColor
         * @see PopupColorChoice
         * @see JChessGame#actionPerformed
         */
        private JButton buildPlayAgainButton() {
            PopupGameOver enclosingDbox = this;
            JButton button = new JButton("Play Again");
            button.addActionListener(event -> {
                boardview.blankBoard();
                chessgame.chooseColor();
                enclosingDbox.dispose();
            });
            return button;
        }
}
