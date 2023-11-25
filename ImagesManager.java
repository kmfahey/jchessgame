package org.kmfahey.jchessgame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Manages an images store of chesspiece icon Image objects, which it loads from
 * an images directory. The Image objects are scaled to the correct size to be
 * used on the BoardView component where they'll fit the chessboard squares
 * there exactly.
 */
public class ImagesManager {

    private final HashMap<Integer, Image> piecesImagesScaled;

    /**
     * Initializes the ImagesManager object, loading the chesspiece icon files
     * from the specified directory (by default, ./images/). It scales each one
     * to the size of the square indicated by the squareDims argument.
     *
     * @param imageDirectory The relative or absolute path to the directory to
     *                       load chesspiece icon .png files from.
     * @param squareDims     A Dimension object from CoordinatesManager
     *                       describing the dimensions in pixels of a chessboard
     *                       square on the chessboard laid out in the GUI by
     *                       BoardView.
     * @throws IOException If there is an error reading from the imageDirectory.
     */
    public ImagesManager(final String imageDirectory, final Dimension squareDims)
                            throws IOException {
        piecesImagesScaled = new HashMap<>();

        /* This loop iterates over the .png files found in the directory
           indicates by the imageDirectory argument (by default, ./images/), */
        Path imageDirPath = Paths.get(imageDirectory);
        try (DirectoryStream<Path> imageDirStream = Files.newDirectoryStream(imageDirPath, "*.png")) {
            for (Path imagePath : imageDirStream) {
                /* loads each as a BufferedImage, */
                File imageFile = new File(imagePath.toString());
                BufferedImage imageData = ImageIO.read(imageFile);

                /* processes the filename into a string that can be looked up in
                   Chessboard.PIECE_STRS_TO_INTS to get a piece integer, */
                String pieceStr = getPieceStr(imagePath);
                int pieceInt = Chessboard.PIECE_STRS_TO_INTS.get(pieceStr);

                /* scales the image to the size indicated by the squareDims argument, */
                Image scaledImage = imageData.getScaledInstance((int) squareDims.getWidth(),
                                                                (int) squareDims.getHeight(),
                                                                BufferedImage.SCALE_SMOOTH);

                /* and saves each one to the piecesImagesScaled instance variable. */
                piecesImagesScaled.put(pieceInt, scaledImage);
            }
        }
    }

    private static String getPieceStr(Path imagePath) {
        String fileName = imagePath.getFileName().toString();
        String[] fileNamePieces = fileName.split("[ .-]");
        String pieceColor = fileNamePieces[0].toLowerCase();
        String pieceRole = fileNamePieces[1].toLowerCase();
        if (pieceRole.equals("knight")) {
            String chirality = fileNamePieces[2].toLowerCase();
            pieceRole = pieceRole + "-" + chirality;
        }
        return pieceColor + "-" + pieceRole;
    }

    /**
     * Accessor for the Image object associated with the piece integer argument.
     *
     * @param pieceInt The integer representation of the piece to retrieve the
     *                 corresponding chesspiece icon.
     * @return         An Image object containing the chesspiece icon
     *                 corresponding to the argument.
     */
    public Image getImageByPieceInt(final int pieceInt) {
        return piecesImagesScaled.get(pieceInt);
    }
}
