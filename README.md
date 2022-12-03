### JChessGame

A simple graphical chessgame that implements a standard icon-based graphical
chessboard interface and the minimax chess-playing algorithm with the alpha/beta
optimization. This program was written as a self-teaching exercise. It turned
out once the algorithm was completed that, without other optimizations
unfamiliar to me, it turns out to be a pretty dumb AI.

It plays aggressively, moving quickly across the board to capture pieces on the
other side, but it isn't attentive to when it's placing its own pieces at risk
of capture. It's pretty easy to pick off its important pieces over time, leaving
its king undefended.

### The Algorithm

#### Credit

The algorithm is implemented in MinimaxRunner. The minimax algorithm is
implemented using alpha/beta pruning. The minimax algorithm is elucidated
[at chessprogramming.org](https://www.chessprogramming.org/Minimax);
alpha/beta pruning is also [explained on that
site](https://www.chessprogramming.org/Alpha-Beta). The evaluation
algorithm used was originally devised by Claude Shannon, a
mathematician, and published in his 1949 paper _Programming
a Computer for playing Chess_. This is also [explained on
chessprogramming.org](https://www.chessprogramming.org/Evaluation).

#### Implementation Details

##### Int-Based Board Array

The game relies on an integer-array-based model of the chessboard, using an 8x8
array of int arrays. Integers are used to represent the chess pieces, in the
following manner. In the class BoardArrays, the following integer flags are
defined:

    public static final int BLACK =     0b1000000000;
    public static final int WHITE =     0b0100000000;

    public static final int KING =      0b0010000000;
    public static final int QUEEN =     0b0001000000;
    public static final int BISHOP =    0b0000100000;
    public static final int KNIGHT =    0b0000010000;
    public static final int ROOK =      0b0000001000;
    public static final int PAWN =      0b0000000100;

    public static final int RIGHT =     0b0000000010;
    public static final int LEFT =      0b0000000001;

To derive an integer that represents a chesspiece, these flags are or'd
together. One of `BLACK` or `WHITE` is or'd with one of `KING`, `QUEEN`,
`BISHOP`, `KNIGHT`, `ROOK`, or `PAWN`. In the case of a knight, one of `LEFT` or
`RIGHT` is or'd into the int value as well. (There are two knight icons in the
icon set the package uses, one facing left and one facing right. Only the knight
piece has chirality.)

For example, to build an integer representing a black pawn, one would combine
the black and pawn flags: `BLACK | PAWN` (equal to 516). A white queen would
be built using the white and queen flags: `WHITE | QUEEN` (equal to 320). In
the case of knights, one would combine the white and knight flags, and one of
either the left flag or the right flag: `WHITE | KNIGHT | LEFT` (equal to 273)
or `WHITE | KNIGHT | RIGHT` (equal to 274).

##### Algorithm Optimization through Strictly-Primitives Design

Almost all the chessboard logic is implemented strictly in terms of ints and int
arrays. This strict-primitives model was the second design for the board, after
the naive OOP model proved to run the minimax algorithm terribly slowly.

With an OOP board (which needed to be cloned for each step of the algorithm,
along with clones of all its piece objects), the minimax algorithm took ~63sec
to run. When it was replaced with a strictly-primitives chessboard model, a
dramatic speedup was achieved: the algorithm now ran in roughly 0.5sec.

### Images Credit

These chesspiece images are used as icons on the chessboard display in the
game's GUI.

 * [./images/Black Bishop.png](./images/Black Bishop.png)
 * [./images/Black King.png](./images/Black King.png)
 * [./images/Black Knight Left-Facing.png](./images/Black Knight Left-Facing.png)
 * [./images/Black Knight Right-Facing.png](./images/Black Knight Right-Facing.png)
 * [./images/Black Pawn.png](./images/Black Pawn.png)
 * [./images/Black Queen.png](./images/Black Queen.png)
 * [./images/Black Rook.png](./images/Black Rook.png)
 * [./images/White Bishop.png](./images/White Bishop.png)
 * [./images/White King.png](./images/White King.png)
 * [./images/White Knight Left-Facing.png](./images/White Knight Left-Facing.png)
 * [./images/White Knight Right-Facing.png](./images/White Knight Right-Facing.png)
 * [./images/White Pawn.png](./images/White Pawn.png)
 * [./images/White Queen.png](./images/White Queen.png)
 * [./images/White Rook.png](./images/White Rook.png)

They were individually cropped out of a public domain SVG image [retrieved from
CreativeCommons.org](https://freesvg.org/chess-pieces-vector).
