package com.kmfahey.jchessgame;

import java.util.HashSet;

public class MinimaxTreeNode {

    private MinimaxTreeNode parentNode;
    private Piece movedPiece;
    private String movedFromLoc;
    private String movedToLoc;
    private Chessboard resultantBoard;
    private HashSet<MinimaxTreeNode> childNodes;
    private String colorsTurnItIs;

    public MinimaxTreeNode(final Piece pieceToMove, final String movedFromLocation,
                           final String movedToLocation, final Chessboard boardToUse) {
        
    }
}
