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
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.FileNotFoundException;

public class ImagesManager {

    private HashMap<String, BufferedImage> piecesImages;
    private Dimension squareDimensions;

    public ImagesManager(final String imageDirectory, final Dimension squareDims) throws IOException, FileNotFoundException {

        piecesImages = new HashMap<>();
        squareDimensions = squareDims;

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
                    pieceRole = pieceRole + " " + chirality;
                }
                String pieceKey = pieceColor + "-" + pieceRole;
                piecesImages.put(pieceKey, imageData);
            }
        }
    }

    public Image getBlackBishop() {
        return piecesImages.get("black-bishop")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackKing() {
        return piecesImages.get("black-king")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackKnightLeft() {
        return piecesImages.get("black-knight-left")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackKnightRight() {
        return piecesImages.get("black-knight-right")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackPawn() {
        return piecesImages.get("black-pawn")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackQueen() {
        return piecesImages.get("black-queen")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getBlackRook() {
        return piecesImages.get("black-rook")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteBishop() {
        return piecesImages.get("black-bishop")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteKing() {
        return piecesImages.get("black-king")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteKnightLeft() {
        return piecesImages.get("black-knight-left")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteKnightRight() {
        return piecesImages.get("black-knight-right")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhitePawn() {
        return piecesImages.get("black-pawn")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteQueen() {
        return piecesImages.get("black-queen")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

    public Image getWhiteRook() {
        return piecesImages.get("black-rook")
                           .getScaledInstance((int) squareDimensions.getWidth(),
                                              (int) squareDimensions.getHeight(),
                                              BufferedImage.SCALE_FAST);
    }

}
