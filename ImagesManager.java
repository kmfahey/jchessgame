package com.kmfahey.jchessgame;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.FileNotFoundException;

public class ImagesManager {

    private HashMap<Integer, BufferedImage> piecesImages;
    private HashMap<Integer, Image> piecesImagesScaled;
    private Dimension squareDimensions;

    public ImagesManager(final String imageDirectory, final Dimension squareDims)
                            throws IOException, FileNotFoundException {

        piecesImages = new HashMap<>();
        squareDimensions = squareDims;

        piecesImagesScaled = new HashMap<Integer, Image>();

        Path imageDirPath = Paths.get(imageDirectory);
        try (DirectoryStream<Path> imageDirStream = Files.newDirectoryStream(imageDirPath, "*.png")) {
            for (Path imagePath : imageDirStream) {
                File imageFile = new File(imagePath.toString());
                BufferedImage imageData = ImageIO.read(imageFile);
                String fileName = imagePath.getFileName().toString();
                String[] fileNamePieces = fileName.split("[ .-]");
                String pieceColor = fileNamePieces[0].toLowerCase();
                String pieceRole = fileNamePieces[1].toLowerCase();
                if (pieceRole.equals("knight")) {
                    String chirality = fileNamePieces[2].toLowerCase();
                    pieceRole = pieceRole + "-" + chirality;
                }
                String pieceStr = pieceColor + "-" + pieceRole;
                int pieceInt = Chessboard.pieceStrsToInts.get(pieceStr);
                piecesImages.put(pieceInt, imageData);
            }
        }

        for (Entry<Integer, BufferedImage> piecesEntry : piecesImages.entrySet()) {
            Image scaledImage = piecesEntry.getValue()
                                           .getScaledInstance((int) squareDimensions.getWidth(),
                                                              (int) squareDimensions.getHeight(),
                                                              BufferedImage.SCALE_SMOOTH);
            piecesImagesScaled.put(piecesEntry.getKey(), scaledImage);
        }
    }

    public Image getImageByPieceInt(final int pieceInt) {
        return piecesImagesScaled.get(pieceInt);
    }

    public Image getBlackBishop() {
        return piecesImagesScaled.get("black-bishop");
    }

    public Image getBlackKing() {
        return piecesImagesScaled.get("black-king");
    }

    public Image getBlackKnightLeft() {
        return piecesImagesScaled.get("black-knight-left");
    }

    public Image getBlackKnightRight() {
        return piecesImagesScaled.get("black-knight-right");
    }

    public Image getBlackPawn() {
        return piecesImagesScaled.get("black-pawn");
    }

    public Image getBlackQueen() {
        return piecesImagesScaled.get("black-queen");
    }

    public Image getBlackRook() {
        return piecesImagesScaled.get("black-rook");
    }

    public Image getWhiteBishop() {
        return piecesImagesScaled.get("white-bishop");
    }

    public Image getWhiteKing() {
        return piecesImagesScaled.get("white-king");
    }

    public Image getWhiteKnightLeft() {
        return piecesImagesScaled.get("white-knight-left");
    }

    public Image getWhiteKnightRight() {
        return piecesImagesScaled.get("white-knight-right");
    }

    public Image getWhitePawn() {
        return piecesImagesScaled.get("white-pawn");
    }

    public Image getWhiteQueen() {
        return piecesImagesScaled.get("white-queen");
    }

    public Image getWhiteRook() {
        return piecesImagesScaled.get("white-rook");
    }
}
