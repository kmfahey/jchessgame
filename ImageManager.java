package com.kmfahey.jchessgame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.imageio.stream.ImageInputStream;


public class ImageManager {

    HashMap<String,HashMap<String,BufferedImage>> piecesImages = new HashMap<>();

    public ImageManager(String imageDirectory) throws IOException, FileNotFoundException {
        
        Path imageDirPath = Paths.get(imageDirectory);
        try (DirectoryStream<Path> imageDirStream = Files.newDirectoryStream(imageDirPath, "*.png")) {
            piecesImages.put("chessboard", new HashMap<String,BufferedImage>());
            piecesImages.put("black", new HashMap<String,BufferedImage>());
            piecesImages.put("white", new HashMap<String,BufferedImage>());
            for (Path imagePath : imageDirStream) {
                File imageFile = new File(imagePath.toString());
                BufferedImage imageData = ImageIO.read(imageFile);
                String fileName = imagePath.getFileName().toString();
                String[] fileNamePieces = fileName.split("[ .-]");
                if (fileNamePieces[0] == "Chessboard") {
                    piecesImages.put("chessboard", new HashMap<String,BufferedImage>());
                    piecesImages.get("chessboard").put("chessboard", imageData);
                    continue;
                }
                String pieceColor = fileNamePieces[0].toLowerCase();
                String pieceRole = fileNamePieces[1].toLowerCase();
                String handedness = "";
                if (pieceRole.equals("knight")) {
                    handedness = fileNamePieces[2].toLowerCase();
                    pieceRole = handedness + pieceRole;
                }
                piecesImages.get(pieceColor).put(pieceRole, imageData);
            }
            for (Entry<String,HashMap<String,BufferedImage>> hashCatgEntry : piecesImages.entrySet()) {
                String catKey = hashCatgEntry.getKey();
                for (Entry<String,BufferedImage> imgHashEntry : hashCatgEntry.getValue().entrySet()) {
                    String imgKey = imgHashEntry.getKey();
                    if (catKey == "chessboard") {
                        System.out.println(catKey);
                    } else {
                        System.out.println(catKey + " " + imgKey);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, FileNotFoundException {
        ImageManager imgMgr = new ImageManager("/home/kmfahey/Workspace/jchessgame/images/");
    }
}
