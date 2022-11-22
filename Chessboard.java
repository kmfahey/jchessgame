package com.kmfahey.jchessgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public class Chessboard implements Cloneable {

    public static final int DOUBLED = 0;
    public static final int ISOLATED = 1;
    public static final int BLOCKED = 2;

    public static final int NORTH = 0;
    public static final int NORTH_NORTH_EAST = 1;
    public static final int NORTH_EAST = 2;
    public static final int EAST_NORTH_EAST = 3;
    public static final int EAST = 4;
    public static final int EAST_SOUTH_EAST = 5;
    public static final int SOUTH_EAST = 6;
    public static final int SOUTH_SOUTH_EAST = 7;
    public static final int SOUTH = 8;
    public static final int SOUTH_SOUTH_WEST = 9;
    public static final int SOUTH_WEST = 10;
    public static final int WEST_SOUTH_WEST = 11;
    public static final int WEST = 12;
    public static final int WEST_NORTH_WEST = 13;
    public static final int NORTH_WEST = 14;
    public static final int NORTH_NORTH_WEST = 15;

    private static final String ALG_NOTN_ALPHA_CHARS = "abcdefgh";
    private static final String ALG_NOTN_NUM_CHARS = "87654321";

    private final HashMap<String, String[]> piecesStartingLocsWhiteBelow = new HashMap<>() {{
        this.put("black-king", new String[] {"d8"});
        this.put("black-queen", new String[] {"e8"});
        this.put("black-rook", new String[] {"a8", "h8"});
        this.put("black-bishop", new String[] {"c8", "f8"});
        this.put("black-knight-right", new String[] {"g8"});
        this.put("black-knight-left", new String[]  {"b8"});
        this.put("black-pawn", new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"});
        this.put("white-king", new String[] {"d1"});
        this.put("white-queen", new String[] {"e1"});
        this.put("white-rook", new String[] {"a1", "h1"});
        this.put("white-bishop", new String[] {"c1", "f1"});
        this.put("white-knight-right", new String[] {"g1"});
        this.put("white-knight-left", new String[]  {"b1"});
        this.put("white-pawn", new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"});
    }};

    private final HashMap<String, String[]> piecesStartingLocsBlackBelow = new HashMap<>() {{
        this.put("white-king", new String[] {"d8"});
        this.put("white-queen", new String[] {"e8"});
        this.put("white-rook", new String[] {"a8", "h8"});
        this.put("white-bishop", new String[] {"c8", "f8"});
        this.put("white-knight-right", new String[] {"g8"});
        this.put("white-knight-left", new String[]  {"b8"});
        this.put("white-pawn", new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"});
        this.put("black-king", new String[] {"d1"});
        this.put("black-queen", new String[] {"e1"});
        this.put("black-rook", new String[] {"a1", "h1"});
        this.put("black-bishop", new String[] {"c1", "f1"});
        this.put("black-knight-right", new String[] {"g1"});
        this.put("black-knight-left", new String[]  {"b1"});
        this.put("black-pawn", new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"});
    }};

    private String colorPlaying;
    private Piece lastMovedPiece = null;
    private HashMap<String, Piece> piecesLocations;

    public Chessboard(final ImagesManager imagesManager, final String playingColor) {

        /* We only need the ImagesManager object to instantiate Piece objects
           with the correct Image 2nd argument. It's not saved to an instance
           variable since it's never used again after this constructor. */
        colorPlaying = playingColor;

        piecesLocations = new HashMap<String, Piece>();

        HashMap<String, String[]> piecesStartingLocs = (colorPlaying == "white")
                                                       ? piecesStartingLocsWhiteBelow
                                                       : piecesStartingLocsBlackBelow;

        for (String algNotnAlphaChar : "abcdefgh".split("(?=.)")) {
            for (String algNotnNumChar : "87654321".split("(?=.)")) {
                piecesLocations.put(algNotnAlphaChar + algNotnNumChar, null);
            }
        }

        for (Entry<String, String[]> identityToLocations : piecesStartingLocs.entrySet()) {
            String[] startingLocs = identityToLocations.getValue();
            for (int index = 0; index < startingLocs.length; index++) {
                Piece piece = new Piece(identityToLocations.getKey(),
                                        imagesManager.getImageByIdentity(identityToLocations.getKey()));
                piece.setLocation(startingLocs[index]);
                piecesLocations.put(startingLocs[index], piece);
            }
        }
    }

    public Chessboard(final HashMap<String, Piece> piecesLocs, final String playingColor) {
        piecesLocations = piecesLocs;
        colorPlaying = playingColor;
    }

    @Override
    public Chessboard clone() {
        HashMap<String, Piece> piecesLocsCopy = new HashMap<String, Piece>();

        /* The Pawn objects have state-- they store their location and Kings
           store if they're in check-- so they have to be individually cloned. */
        for (Entry<String, Piece> locToPiece : piecesLocations.entrySet()) {
            String location = locToPiece.getKey();
            Piece piece = locToPiece.getValue();
            piecesLocsCopy.put(location, Objects.isNull(piece) ? null : piece.clone());
        }

        return new Chessboard(piecesLocsCopy, colorPlaying);
    }

    public Chessboard(final String playingColor, final HashMap<String, Piece> piecesLocationsMap) {
        colorPlaying = playingColor;

        piecesLocations = piecesLocationsMap;
    }

    public String[] getPiecesIdentities(final String substringToMatch) {
        return Arrays.asList(getPiecesIdentities())
                     .stream()
                     .filter(strval -> strval.contains(substringToMatch))
                     .collect(Collectors.toList())
                     .toArray(String[]::new);
    }

    public String[] getPiecesIdentities() {
        return piecesLocations.values()
                              .stream()
                              .filter(pc -> !Objects.isNull(pc))
                              .map(pc -> pc.getIdentity())
                              .collect(Collectors.toList())
                              .toArray(String[]::new);
    }

    public Piece[] getPieceByIdentity(final String identity) {
        return piecesLocations.values()
                              .stream()
                              .filter(pc -> (!Objects.isNull(pc) && identity.equals(pc.getIdentity())))
                              .collect(Collectors.toList())
                              .toArray(Piece[]::new);
    }

    public Piece getPieceAtLocation(final String algNotnLoc) {
        return piecesLocations.get(algNotnLoc);
    }

    public float evaluateBoard() {
        String otherColor = colorPlaying.equals("white") ? "black" : "white";

        Iterator<Piece> piecesIterator;
        int whiteIndex = 0;
        int blackIndex = 1;
        int kingIndex = 0;
        int queenIndex = 1;
        int rookIndex = 2;
        int bishopIndex = 3;
        int knightIndex = 4;
        int pawnIndex = 5;

        int thisColorIndex = colorPlaying.equals("white") ? whiteIndex : blackIndex;
        int otherColorIndex = colorPlaying.equals("white") ? blackIndex : whiteIndex;

        float[][] piecesCounts = new float[2][6];

        /* The case values in a switch statements must be constant at
           compile-time, so this switch sorts them into the array by white
           and black, not thisColor and otherColor. thisColorIndex and
           otherColorIndex are used to assort the values into us and them later
           in the method. */
        piecesIterator = iterator();
        while (piecesIterator.hasNext()) {
            Piece piece = piecesIterator.next();
            switch (piece.getIdentity()) {
                                           /* A king is never captured so this case checks whether the king is in check.
                                           A huge weight (200F) is attached to whether a side's king is in check. */
                case "white-king":         piecesCounts[whiteIndex][kingIndex] += piece.isInCheck() ? 0F : 1F; break;
                case "white-queen":        piecesCounts[whiteIndex][queenIndex]++; break;
                case "white-rook":         piecesCounts[whiteIndex][rookIndex]++; break;
                case "white-bishop":       piecesCounts[whiteIndex][bishopIndex]++; break;
                case "white-knight-right": piecesCounts[whiteIndex][knightIndex]++; break;
                case "white-knight-left":  piecesCounts[whiteIndex][knightIndex]++; break;
                case "white-pawn":         piecesCounts[whiteIndex][pawnIndex]++; break;
                case "black-king":         piecesCounts[blackIndex][kingIndex]++; break;
                case "black-queen":        piecesCounts[blackIndex][queenIndex]++; break;
                case "black-rook":         piecesCounts[blackIndex][rookIndex]++; break;
                case "black-bishop":       piecesCounts[blackIndex][bishopIndex]++; break;
                case "black-knight-right": piecesCounts[blackIndex][knightIndex]++; break;
                case "black-knight-left":  piecesCounts[blackIndex][knightIndex]++; break;
                case "black-pawn":         piecesCounts[blackIndex][pawnIndex]++; break;
                default: break;
            }
        }

        float[] colorPlayingSpecialPawnsTallies = tallySpecialPawns(colorPlaying);
        float[] otherColorSpecialPawnsTallies = tallySpecialPawns(otherColor);
        float thisMobility = colorMobility(colorPlaying);
        float otherMobility = colorMobility(otherColor);

        float isolatedPawnDifference = colorPlayingSpecialPawnsTallies[ISOLATED]
                                       - otherColorSpecialPawnsTallies[ISOLATED];
        float blockedPawnDifference = colorPlayingSpecialPawnsTallies[BLOCKED]
                                      - otherColorSpecialPawnsTallies[BLOCKED];
        float doubledPawnDifference = colorPlayingSpecialPawnsTallies[DOUBLED]
                                      - otherColorSpecialPawnsTallies[DOUBLED];
        float kingScore = 200F * (piecesCounts[thisColorIndex][kingIndex] - piecesCounts[otherColorIndex][kingIndex]);
        float queenScore = 9F * (piecesCounts[thisColorIndex][queenIndex] - piecesCounts[otherColorIndex][queenIndex]);
        float rookScore = 5F * (piecesCounts[thisColorIndex][rookIndex] - piecesCounts[otherColorIndex][rookIndex]);
        float bishopAndKnightScore = 3F * ((piecesCounts[thisColorIndex][bishopIndex]
                                               - piecesCounts[otherColorIndex][bishopIndex])
                                           + (piecesCounts[thisColorIndex][knightIndex]
                                               - piecesCounts[otherColorIndex][knightIndex]));
        float generalPawnScore = (piecesCounts[thisColorIndex][pawnIndex] - piecesCounts[otherColorIndex][pawnIndex]);
        float specialPawnScore = 0.5F * (isolatedPawnDifference + blockedPawnDifference
                                         + doubledPawnDifference);
        float mobilityScore = 0.1F * (thisMobility - otherMobility);
        float totalScore = (kingScore + queenScore + rookScore + bishopAndKnightScore
                            + generalPawnScore + specialPawnScore + mobilityScore);
        return totalScore;
    }

    private float colorMobility(final String theColor) {
        float possibleMovesCount = 0;
        for (String piecesIdentity : getPiecesIdentities(theColor)) {
            for (Piece piece : getPieceByIdentity(piecesIdentity)) {
                int validMoveCount = getValidMoveSet(piece).size();
                possibleMovesCount += validMoveCount;
            }
        }
        return possibleMovesCount;
    }

    private float[] tallySpecialPawns(final String theColor) {
        HashSet<Piece> doubledPawns = new HashSet<Piece>();
        String otherColor = theColor.equals("white") ? "black" : "white";
        float[] retval = new float[3];
        float doubledPawnsTally = 0;
        float isolatedPawnsTally = 0;
        float blockedPawnsTally = 0;

        for (Piece thisPawn : getPieceByIdentity(theColor + "-pawn")) {
            String thisPawnLoc = thisPawn.getLocation();
            boolean hasAdjacentPawn = false;
            for (Piece otherPawn : getPieceByIdentity(theColor + "-pawn")) {
                if (thisPawn == otherPawn) {
                    continue;
                }
                String otherPawnLoc = otherPawn.getLocation();
                if (thisPawnLoc.charAt(0) == otherPawnLoc.charAt(0)
                        && thisPawnLoc.charAt(1) + 1 == otherPawnLoc.charAt(1)) {
                    if (!doubledPawns.contains(thisPawn)) {
                        doubledPawnsTally += 1;
                        doubledPawns.add(thisPawn);
                    }
                    if (!doubledPawns.contains(otherPawn)) {
                        doubledPawnsTally += 1;
                        doubledPawns.add(otherPawn);
                    }
                }
                if (thisPawnLoc.charAt(0) - otherPawnLoc.charAt(0) == 1
                        || otherPawnLoc.charAt(0) - thisPawnLoc.charAt(0) == 1) {
                    hasAdjacentPawn = true;
                }
            }
            isolatedPawnsTally += (!hasAdjacentPawn) ? 1 : 0;
            if (thisPawnLoc.charAt(1) != '8' && thisPawnLoc.charAt(1) != '1') {
                char adjustedChar = (char) (thisPawn.getColor().equals(colorPlaying)
                                            ? thisPawnLoc.charAt(1) + 1
                                            : thisPawnLoc.charAt(1) - 1);
                Piece pieceInFront = piecesLocations.get(String.valueOf(thisPawnLoc.charAt(0))
                                                         + String.valueOf(adjustedChar));
                if (!Objects.isNull(pieceInFront)
                    /* checking that the blocking piece isn't one of this side's
                       pawns, since that would be 'doubled' instead */
                    && !pieceInFront.getColor().equals(theColor)) {
                    blockedPawnsTally += 1;
                }
            }
        }
        retval[DOUBLED] = doubledPawnsTally;
        retval[BLOCKED] = blockedPawnsTally;
        retval[ISOLATED] = isolatedPawnsTally;
        return retval;
    }

    private HashSet<String> moveMapToSet(final HashMap<Integer, String[]> moveMap) {
        HashSet<String> moveSet = new HashSet<>();
        for (String[] dirLocnArray : moveMap.values()) {
            for (int index = 0; index < dirLocnArray.length; index++) {
                moveSet.add(dirLocnArray[index]);
            }
        }
        return moveSet;
    }

    private String plotDirMove(final String location, final int moveDist, final int moveDir) {
        int[] indexesPair = algNotnLocToNumericIndexes(location);
        int alphaIndex = indexesPair[0];
        int numIndex = indexesPair[1];
        switch (moveDir) {
            case NORTH:                                         numIndex -= moveDist;   break;
            case NORTH_EAST:        alphaIndex += moveDist;     numIndex -= moveDist;   break;
            case EAST:              alphaIndex += moveDist;                             break;
            case SOUTH_EAST:        alphaIndex += moveDist;     numIndex += moveDist;   break;
            case SOUTH:                                         numIndex += moveDist;   break;
            case SOUTH_WEST:        alphaIndex -= moveDist;     numIndex += moveDist;   break;
            case WEST:              alphaIndex -= moveDist;                             break;
            case NORTH_WEST:        alphaIndex -= moveDist;     numIndex -= moveDist;   break;
            /* These directions are for knights' moves */
            case NORTH_NORTH_EAST:  alphaIndex += 1;            numIndex -= 2;          break;
            case EAST_NORTH_EAST:   alphaIndex += 2;            numIndex -= 1;          break;
            case EAST_SOUTH_EAST:   alphaIndex += 2;            numIndex += 1;          break;
            case SOUTH_SOUTH_EAST:  alphaIndex += 1;            numIndex += 2;          break;
            case SOUTH_SOUTH_WEST:  alphaIndex -= 1;            numIndex += 2;          break;
            case WEST_SOUTH_WEST:   alphaIndex -= 2;            numIndex += 1;          break;
            case WEST_NORTH_WEST:   alphaIndex -= 2;            numIndex -= 1;          break;
            case NORTH_NORTH_WEST:  alphaIndex -= 1;            numIndex -= 2;          break;
            default: break;
        }
        if (alphaIndex < 0 || alphaIndex >= ALG_NOTN_ALPHA_CHARS.length()
            || numIndex < 0 || numIndex >= ALG_NOTN_NUM_CHARS.length()) {
            return null;
        }
        return String.valueOf(ALG_NOTN_ALPHA_CHARS.charAt(alphaIndex))
               + String.valueOf(ALG_NOTN_NUM_CHARS.charAt(numIndex));
    }

    private void pruneMoveMap(final Piece piece, final HashMap<Integer, String[]> moveMap) {
        for (Object moveDirObj : moveMap.keySet().toArray()) {
            int moveDir = (int) moveDirObj;
            boolean moveCrossesOccupiedSquare = false;
            for (int index = 0; index < moveMap.get(moveDir).length; index++) {
                if (Objects.isNull(moveMap.get(moveDir)[index])) {
                    if (index == 0) {
                        moveMap.remove(moveDir);
                    } else {
                        moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index));
                    }
                    break;
                } else if (!Objects.isNull(getPieceAtLocation(moveMap.get(moveDir)[index]))) {
                    if (piecesLocations.get(moveMap.get(moveDir)[index])
                                       .getColor().equals(piece.getColor())) {
                        if (index == 0) {
                            moveMap.remove(moveDir);
                        } else {
                            moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index));
                        }
                        break;
                    } else if (!piecesLocations.get(moveMap.get(moveDir)[index])
                                               .getColor().equals(piece.getColor())) {
                        moveMap.put(moveDir, Arrays.copyOf(moveMap.get(moveDir), index + 1));
                        break;
                    }
                }
            }
        }
    }

    private HashSet<String> rooksMoves(final Piece piece) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, EAST, SOUTH, WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(piece.getLocation(), moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> knightsMoves(final Piece piece) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH_NORTH_EAST, EAST_NORTH_EAST, EAST_SOUTH_EAST, SOUTH_SOUTH_EAST,
                                          SOUTH_SOUTH_WEST, WEST_SOUTH_WEST, WEST_NORTH_WEST, NORTH_NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(piece.getLocation(), 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> bishopsMoves(final Piece piece) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(piece.getLocation(), moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> queensMoves(final Piece piece) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[7]);
            for (int moveDist : new int[] {1, 2, 3, 4, 5, 6, 7}) {
                moveRangesByDir.get(moveDir)[moveDist - 1] = plotDirMove(piece.getLocation(), moveDist, moveDir);
            }
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> kingsMoves(final Piece piece) {
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();
        for (int moveDir : new int[] {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(piece.getLocation(), 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        return moveMapToSet(moveRangesByDir);
    }

    private HashSet<String> pawnsMoves(final Piece piece) {
        String pieceLocation = piece.getLocation();
        int FORWARD_DIR = piece.getColor().equals(colorPlaying) ? NORTH : SOUTH;
        int LEFT_DIAG_DIR = piece.getColor().equals(colorPlaying) ? NORTH_WEST : SOUTH_EAST;
        int RIGHT_DIAG_DIR = piece.getColor().equals(colorPlaying) ? NORTH_EAST : SOUTH_WEST;
        HashMap<Integer, String[]> moveRangesByDir = new HashMap<>();

        if (pieceLocation.charAt(1) == '2' || pieceLocation.charAt(1) == '7') {
            moveRangesByDir.put(FORWARD_DIR, new String[] {plotDirMove(pieceLocation, 1, FORWARD_DIR),
                                                     plotDirMove(pieceLocation, 2, FORWARD_DIR)});
        } else {
            moveRangesByDir.put(FORWARD_DIR, new String[] {plotDirMove(pieceLocation, 1, FORWARD_DIR)});
        }
        for (int moveDir : new int[] {RIGHT_DIAG_DIR, LEFT_DIAG_DIR}) {
            moveRangesByDir.put(moveDir, new String[] {plotDirMove(pieceLocation, 1, moveDir)});
        }
        pruneMoveMap(piece, moveRangesByDir);
        if (moveRangesByDir.containsKey(FORWARD_DIR)) {
            if (moveRangesByDir.get(FORWARD_DIR).length >= 1) {
                if (!Objects.isNull(getPieceAtLocation(moveRangesByDir.get(FORWARD_DIR)[0]))) {
                    moveRangesByDir.remove(FORWARD_DIR);
                } else if (moveRangesByDir.get(FORWARD_DIR).length == 2
                           && !Objects.isNull(getPieceAtLocation(moveRangesByDir.get(FORWARD_DIR)[1]))) {
                    moveRangesByDir.put(FORWARD_DIR, new String[] {moveRangesByDir.get(FORWARD_DIR)[0]});
                }
            }
        }
        for (int moveDir : new int[] {RIGHT_DIAG_DIR, LEFT_DIAG_DIR}) {
            if (moveRangesByDir.containsKey(moveDir)
                && (Objects.isNull(piecesLocations.get(moveRangesByDir.get(moveDir)[0]))
                    || piecesLocations.get(moveRangesByDir.get(moveDir)[0])
                                      .getColor().equals(piece.getColor()))) {
                moveRangesByDir.remove(moveDir);
            }
        }
        return moveMapToSet(moveRangesByDir);
    }

    public HashSet<String> getValidMoveSet(final Piece piece) {
        if (Objects.isNull(piece)) {
            return null;
        }
        HashSet<String> retval;
        switch (piece.getRole()) {
            case "rook":
                retval = rooksMoves(piece); break;
            case "knight":
                retval = knightsMoves(piece); break;
            case "bishop":
                retval = bishopsMoves(piece); break;
            case "queen":
                retval = queensMoves(piece); break;
            case "king":
                retval = kingsMoves(piece); break;
            case "pawn":
                retval = pawnsMoves(piece); break;
            default:
                retval = null;
        }
        return retval;
    }

    public HashSet<String> getValidMoveSet(final String pieceLocation) {
        Piece piece = piecesLocations.get(pieceLocation);
        return getValidMoveSet(piece);
    }

    public boolean isSquareThreatened(final String squareLoc, final String opposingColor) {
        for (String piecesIdentity : getPiecesIdentities(opposingColor)) {
            if (piecesIdentity.startsWith(colorPlaying)) {
                continue;
            }
            for (Piece piece : getPieceByIdentity(piecesIdentity)) {
                if (getValidMoveSet(piece).contains(squareLoc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Piece checkIfKingIsInCheck(final String kingColor) {
        HashSet<Piece> checkedPieces = new HashSet<Piece>();
        Piece kingPiece = getPieceByIdentity(kingColor + "-king")[0]; // FIXME: this threw a ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0 one time
        String kingLocation = kingPiece.getLocation();
        Piece retval = null;
        if (kingPiece.isInCheck()) {
            HashSet<String> kingInCheckByPcsLocs = kingPiece.getInCheckByPcsAtLocs();
            for (String threateningPieceLocation : kingInCheckByPcsLocs) {
                Piece threateningPiece = piecesLocations.get(threateningPieceLocation);
                if (!getValidMoveSet(threateningPiece).contains(kingLocation)) {
                    kingPiece.noLongerInCheckBy(threateningPiece);
                } else {
                    retval = kingPiece;
                }
                checkedPieces.add(threateningPiece);
            }
        }
        for (String otherSidePieceIdentity : getPiecesIdentities((kingColor.equals("white") ? "black" : "white"))) {
            for (Piece otherSidePiece : getPieceByIdentity(otherSidePieceIdentity)) {
                if (checkedPieces.contains(otherSidePiece)) {
                    continue;
                } else if (getValidMoveSet(otherSidePiece).contains(kingLocation)) {
                    kingPiece.inCheckByPiece(otherSidePiece);
                    retval = kingPiece;
                }
            }
        }
        return retval;
    }

    public Piece checkIfKingIsInCheckmate(final String kingColor) {
        if (Objects.isNull(checkIfKingIsInCheck(kingColor))) {
            return null;
        }
        String opposingColor = kingColor.equals("white") ? "black" : "white";
        Piece kingPiece = getPieceByIdentity(kingColor + "-king")[0];
        for (String possibleMove : kingsMoves(kingPiece)) {
            if (!isSquareThreatened(possibleMove, opposingColor)) {
                return null;
            }
        }
        return kingPiece;
    }

    public String getColorPlaying() {
        return colorPlaying;
    }

    public Piece getLastMovedPiece() {
        return lastMovedPiece;
    }

    public Piece movePiece(final Piece movingPiece, final String pieceCurrentLoc, final String movingToLoc) {
        Piece capturingPiece = null;
        HashSet<String> validMoves = null;
        Piece retval;

        lastMovedPiece = movingPiece;
        if (!Objects.isNull(piecesLocations.get(movingToLoc))) {
            capturingPiece = piecesLocations.get(movingToLoc);
            capturingPiece.setLocation(null);
        }
        Piece otherKing = getPieceByIdentity((colorPlaying.equals("white") ? "black" : "white") + "-king")[0];
        if (otherKing.getInCheckByPcsAtLocs().contains(pieceCurrentLoc)) {
            otherKing.noLongerInCheckBy(movingPiece);
        }
        piecesLocations.put(pieceCurrentLoc, null);
        movingPiece.setLocation(movingToLoc);
        piecesLocations.put(movingToLoc, movingPiece);

        /* Check possibilities:
           * Due to this move, this side's king is now in check (because this
             piece got out of the way).
           * Due to this move, this side's king is no longer in check (because
             this piece got in the way).
           * Due to this move, the other side's king is now in check by this
             piece.
           * Due to this move, the other side's king is now in check by another
             piece on this side.
           * Due to this move, the other side's king is no longer in check
             because this piece got in the way.

        There's a method checkIfKingIsInCheck() that's called by
        checkIfKingIsInCheckmate(); if one or more pieces are threatening the
        king, the king's piece has inCheckByPiece set with each threatening
        piece. In order to not call it twice, I call checkIfKingIsInCheckmate()
        instead so both cases are examined. It returns a king Piece if check or
        checkmate has happened. A king Piece has isInCheck() and isInCheckmate()
        boolean accessors which can be used by the calling code to discern
        whether it's check or checkmate. */

        retval = checkIfKingIsInCheckmate(movingPiece.getColor());
        if (!Objects.isNull(retval)) {
            return retval;
        }
        retval = checkIfKingIsInCheckmate(movingPiece.getColor().equals("white") ? "black" : "white");
        if (!Objects.isNull(retval)) {
            return retval;
        }

        return capturingPiece;
    }

    public String numericIndexesToAlgNotnLoc(final int horizIndex, final int vertIndex) {
        return String.valueOf(ALG_NOTN_ALPHA_CHARS.charAt(horizIndex))
               + String.valueOf(ALG_NOTN_NUM_CHARS.charAt(vertIndex));
    }

    public int[] algNotnLocToNumericIndexes(final String location) {
        int[] coordinates = new int[2];
        coordinates[0] = ALG_NOTN_ALPHA_CHARS.indexOf(String.valueOf(location.charAt(0)));
        coordinates[1] = ALG_NOTN_NUM_CHARS.indexOf(String.valueOf(location.charAt(1)));
        return coordinates;
    }

    public Iterator<Piece> iterator() {
        return new ChessboardIterator<Piece>(piecesLocations);
    }

    private class ChessboardIterator<Piece> implements Iterator<Piece> {

        Object[] pieceObjArray;
        private int index;


        ChessboardIterator(HashMap<String, Piece> piecesByLocations) {
            pieceObjArray = piecesByLocations.values()
                                             .stream()
                                             .filter(strval -> !Objects.isNull(strval))
                                             .collect(Collectors.toList())
                                             .toArray();
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < pieceObjArray.length;
        }

        @Override
        public Piece next() {
            @SuppressWarnings("unchecked")
            Piece retval = (Piece) pieceObjArray[index];
            index++;
            return retval;
        }
    }
}
