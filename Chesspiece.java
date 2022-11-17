package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.awt.Image;

public class Chesspiece {
    public final int ROOK = 0;
    public final int KNIGHT = 1;
    public final int BISHOP = 2;
    public final int QUEEN = 3;
    public final int KING = 4;
    public final int PAWN = 5;

    public final int WHITE = 0;
    public final int BLACK = 1;

    public final int LEFT = 0;
    public final int RIGHT = 1;

    private int roleFlag;
    private int colorFlag;
    private int chiralityFlag;

    private String chessboardLocation;

    private CoordinatesManager coordinatesManager;

    public Chesspiece(final int pieceRoleFlag, final int pieceColorFlag, final int pieceChiralityFlag,
                      final ImagesManager imgMgr, final CoordinatesManager coordMgr) {
        imagesManager = imgMgr;
        coordinatesManager = coordMgr;
        roleFlag = pieceRoleFlag;
        colorFlag = pieceColorFlag;
        chiralityFlag = pieceChiralityFlag;
    }

    public Image getImage() {
        Image pieceImage = null;
        switch (colorFlag) {
            case WHITE:
                switch (roleFlag) {
                    case ROOK: pieceImage = imagesManager.getWhiteRook(); break;
                    case KNIGHT:
                        switch (chiralityFlag) {
                            case LEFT: pieceImage = imagesManager.getWhiteKnightLeft(); break;
                            case RIGHT: pieceImage = imagesManager.getWhiteKnightRight(); break;
                        }
                    case BISHOP: pieceImage = imagesManager.getWhiteBishop(); break;
                    case QUEEN: pieceImage = imagesManager.getWhiteQueen(); break;
                    case KING: pieceImage = imagesManager.getWhiteKing(); break;
                    case PAWN: pieceImage = imagesManager.getWhitePawn(); break;
                }
            case BLACK:
                switch (roleFlag) {
                    case ROOK: pieceImage = imagesManager.getBlackRook(); break;
                    case KNIGHT:
                        switch (chiralityFlag) {
                            case LEFT: pieceImage = imagesManager.getBlackKnightLeft(); break;
                            case RIGHT: pieceImage = imagesManager.getBlackKnightRight(); break;
                        }
                    case BISHOP: pieceImage = imagesManager.getBlackBishop(); break;
                    case QUEEN: pieceImage = imagesManager.getBlackQueen(); break;
                    case KING: pieceImage = imagesManager.getBlackKing(); break;
                    case PAWN: pieceImage = imagesManager.getBlackPawn(); break;
                }
        }
        return pieceImage;
    }
}

