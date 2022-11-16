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
import net.sf.repr.Repr;


public class ImageManager {

    private HashMap<String, HashMap<String, Image>> piecesImages;

    public ImageManager(final String imageDirectory) throws IOException, FileNotFoundException {

        piecesImages = new HashMap<>();

        Path imageDirPath = Paths.get(imageDirectory);
        try (DirectoryStream<Path> imageDirStream = Files.newDirectoryStream(imageDirPath, "*.png")) {
            piecesImages.put("chessboard", new HashMap<String, Image>());
            piecesImages.put("black", new HashMap<String, Image>());
            piecesImages.put("white", new HashMap<String, Image>());
            for (Path imagePath : imageDirStream) {
                File imageFile = new File(imagePath.toString());
                Image imageData = ImageIO.read(imageFile);
                String fileName = imagePath.getFileName().toString();
                String[] fileNamePieces = fileName.split("[ .-]");
                if (fileNamePieces[0].equals("Chessboard")) {
                    piecesImages.put("chessboard", new HashMap<String, Image>());
                    piecesImages.get("chessboard").put("chessboard", imageData);
                    continue;
                }
                String pieceColor = fileNamePieces[0].toLowerCase();
                String pieceRole = fileNamePieces[1].toLowerCase();
                if (pieceRole.equals("knight")) {
                    String handedness = fileNamePieces[2].toLowerCase();
                    pieceRole = handedness + pieceRole;
                }
                piecesImages.get(pieceColor).put(pieceRole, imageData);
            }
        }
    }

    public Image getChessboard(final Dimension imageDims) {
        return piecesImages.get("chessboard").get("chessboard").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackBishop(final Dimension imageDims) {
        return piecesImages.get("black").get("bishop").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackKing(final Dimension imageDims) {
        return piecesImages.get("black").get("king").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackKnightLeft(final Dimension imageDims) {
        return piecesImages.get("black").get("knightleft").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackKnightRight(final Dimension imageDims) {
        return piecesImages.get("black").get("knightright").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackPawn(final Dimension imageDims) {
        return piecesImages.get("black").get("pawn").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackQueen(final Dimension imageDims) {
        return piecesImages.get("black").get("queen").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getBlackRook(final Dimension imageDims) {
        return piecesImages.get("black").get("rook").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteBishop(final Dimension imageDims) {
        return piecesImages.get("white").get("bishop").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteKing(final Dimension imageDims) {
        return piecesImages.get("white").get("king").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteKnightLeft(final Dimension imageDims) {
        return piecesImages.get("white").get("knightleft").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteKnightRight(final Dimension imageDims) {
        return piecesImages.get("white").get("knightright").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhitePawn(final Dimension imageDims) {
        return piecesImages.get("white").get("pawn").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteQueen(final Dimension imageDims) {
        return piecesImages.get("white").get("queen").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }

    public Image getWhiteRook(final Dimension imageDims) {
        return piecesImages.get("white").get("rook").getScaledInstance((int) imageDims.getWidth(), (int) imageDims.getHeight(), Image.SCALE_FAST);
    }
}
