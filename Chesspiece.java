package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.awt.Image;

public class Chesspiece {

    private String chesspieceIdentity;
    private String chessboardLocation;

    private ImagesManager imagesManager;

    public Chesspiece(final String pieceIdentity, final ImagesManager imgMgr) {
        imagesManager = imgMgr;
        chesspieceIdentity = pieceIdentity;
    }

    public String getChessboardLocation() {
        return chessboardLocation;
    }

    public void setChessboardLocation(String chessboardLocationStr) {
        chessboardLocation = chessboardLocationStr;
    }

    public String getChesspieceIdentity() {
        return chesspieceIdentity;
    }

    public Image getImage() {
        Image pieceImage = null;
        switch (chesspieceIdentity) {
            case "white-rook": pieceImage = imagesManager.getWhiteRook(); break;
            case "white-knight-left": pieceImage = imagesManager.getWhiteKnightLeft(); break;
            case "white-knight-right": pieceImage = imagesManager.getWhiteKnightRight(); break;
            case "white-bishop": pieceImage = imagesManager.getWhiteBishop(); break;
            case "white-queen": pieceImage = imagesManager.getWhiteQueen(); break;
            case "white-king": pieceImage = imagesManager.getWhiteKing(); break;
            case "white-pawn": pieceImage = imagesManager.getWhitePawn(); break;
            case "black-rook": pieceImage = imagesManager.getBlackRook(); break;
            case "black-knight-left": pieceImage = imagesManager.getBlackKnightLeft(); break;
            case "black-knight-right": pieceImage = imagesManager.getBlackKnightRight(); break;
            case "black-bishop": pieceImage = imagesManager.getBlackBishop(); break;
            case "black-queen": pieceImage = imagesManager.getBlackQueen(); break;
            case "black-king": pieceImage = imagesManager.getBlackKing(); break;
            case "black-pawn": pieceImage = imagesManager.getBlackPawn(); break;
        }
        return pieceImage;
    }
}

