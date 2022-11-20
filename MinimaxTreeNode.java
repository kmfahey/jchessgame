package com.kmfahey.jchessgame;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.function.BiFunction;

public class MinimaxTreeNode {

    private MinimaxTreeNode parentNode;
    private Chessboard chessboard;
    private ArrayList<MinimaxTreeNode> childNodes;
    private String playingColor;
    private String colorsTurnItIs;

    public MinimaxTreeNode(final Chessboard clonedChessboard) {
        chessboard = clonedChessboard;
        childNodes = new ArrayList<MinimaxTreeNode>();
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public boolean playingColorKingInCheckmate(final String playingColor) {
        return !Objects.isNull(chessboard.checkIfKingIsInCheckmate(playingColor));
    }

    public boolean otherColorKingInCheckmate(final String playingColor) {
        return !Objects.isNull(chessboard.checkIfKingIsInCheckmate(playingColor.equals("white") ? "black" : "white"));
    }

    public Move minimaxAlgorithmTopLevel(final String playingColor) throws AlgorithmNoResultException {
        double bestScoreSeen = Double.NEGATIVE_INFINITY;
        Move retval = null;
        Piece bestScoredMovedPiece = null;
        Iterator<Piece> piecesIterator;
        HashMap<String, Piece> piecesMap = new HashMap<>();

        piecesIterator = chessboard.iterator();

        while (piecesIterator.hasNext()) {
            Piece piece = piecesIterator.next();
            if (!piece.getColor().equals(playingColor)) {
                continue;
            }
            piecesMap.put(piece.getIdentity(), piece);
            String pieceLocation = piece.getLocation();
            for (String possibleMove : chessboard.getValidMoveSet(piece)) {
                Chessboard clonedBoard = chessboard.clone();
                Piece clonedPiece = clonedBoard.getPieceAtLocation(pieceLocation);
                clonedBoard.movePiece(clonedPiece, clonedPiece.getLocation(), possibleMove);
                childNodes.add(new MinimaxTreeNode(clonedBoard));
            }
        }

        Collections.shuffle(childNodes);

        for (MinimaxTreeNode childNode : childNodes) {
            double moveScore = childNode.minimaxAlgorithmLowerLevel(3, playingColor, playingColor.equals("black") ? "white" : "black");
            if (moveScore > bestScoreSeen) {
                bestScoreSeen = moveScore;
                bestScoredMovedPiece = childNode.getChessboard().getLastMovedPiece();
            }
        }

        for (Entry<String, Piece> identityToPiece : piecesMap.entrySet()) {
            String identity = identityToPiece.getKey();
            Piece piece = identityToPiece.getValue();
            if (!bestScoredMovedPiece.getIdentity().equals(identity) || !bestScoredMovedPiece.getLastLocation().equals(piece.getLocation())) {
                continue;
            }
            retval = new Move(piece, piece.getLocation(), bestScoredMovedPiece.getLocation());

        }

        if (Objects.isNull(retval)) {
            throw new AlgorithmNoResultException("minimax algorithm yielded no results");
        }

        return retval;
    }

    public double minimaxAlgorithmLowerLevel(final int callDepth, final String playingColor, final String colorsTurnItIs) {
        double bestScoreSeen;
        double moveScore;
        BiFunction<Double, Double, Boolean> comparator;
        Iterator<Piece> piecesIterator;

        if (callDepth == 0) {
            return chessboard.evaluateBoard();
        } else if (playingColorKingInCheckmate(playingColor)) {
            return Double.POSITIVE_INFINITY;
        } else if (otherColorKingInCheckmate(playingColor)) {
            return Double.NEGATIVE_INFINITY;
        }
        
        if (playingColor.equals(colorsTurnItIs)) {
            bestScoreSeen = Double.NEGATIVE_INFINITY;
            comparator = (score, bestScore) -> (score > bestScore);
        } else {
            bestScoreSeen = Double.POSITIVE_INFINITY;
            comparator = (score, bestScore) -> (score < bestScore);
        }

        piecesIterator = chessboard.iterator();

        while (piecesIterator.hasNext()) {
            Piece piece = piecesIterator.next();
            if (!piece.getColor().equals(colorsTurnItIs)) {
                continue;
            }
            String pieceLocation = piece.getLocation();
            for (String possibleMove : chessboard.getValidMoveSet(piece)) {
                Chessboard clonedBoard = chessboard.clone();
                Piece clonedPiece = clonedBoard.getPieceAtLocation(pieceLocation);
                clonedBoard.movePiece(clonedPiece, clonedPiece.getLocation(), possibleMove);
                childNodes.add(new MinimaxTreeNode(clonedBoard));
            }
        }

        Collections.shuffle(childNodes);

        for (MinimaxTreeNode childNode : childNodes) {
            moveScore = childNode.minimaxAlgorithmLowerLevel(callDepth - 1, playingColor, colorsTurnItIs.equals("black") ? "white" : "black");
            if (comparator.apply(moveScore, bestScoreSeen)) {
                bestScoreSeen = moveScore;
            }
        }

        return bestScoreSeen;
    }
}
