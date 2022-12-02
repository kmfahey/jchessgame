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
                int pieceInt = Chessboard.PIECE_STRS_TO_INTS.get(pieceStr);
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
}
