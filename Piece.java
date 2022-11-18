package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.awt.Image;

public class Piece {

    private String pieceIdentity;
    private String boardLocation;

    private ImagesManager imagesManager;

    public Piece(final String identity, final ImagesManager imgMgr) {
        imagesManager = imgMgr;
        pieceIdentity = identity;
    }

    public String getBoardLocation() {
        return boardLocation;
    }

    public void setBoardLocation(String boardLocationStr) {
        boardLocation = boardLocationStr;
    }

    public String getIdentity() {
        return pieceIdentity;
    }

    public String getColor() {
        return pieceIdentity.split("-")[0];
    }

    public String getRole() {
        return pieceIdentity.split("-")[1];
    }

    public String getChirality() {
        return pieceIdentity.split("-")[2];
    }

    public Image getImage() {
        Image pieceImage = null;
        switch (pieceIdentity) {
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

