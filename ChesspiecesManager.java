package com.kmfahey.jchessgame;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;

public class ChesspiecesManager implements Iterable<String> {

    private final HashMap<String, String[]> piecesStartingLocsWhiteBelow = new HashMap<>() {{
        this.put("black-rook", new String[] {"a8", "h8"});
        this.put("black-bishop", new String[] {"c8", "f8"});
        this.put("black-knight-right", new String[] {"g8"});
        this.put("black-king", new String[] {"d8"});
        this.put("black-queen", new String[] {"e8"});
        this.put("black-knight-left", new String[]  {"b8"});
        this.put("black-pawn", new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"});
        this.put("white-rook", new String[] {"a1", "h1"});
        this.put("white-bishop", new String[] {"c1", "f1"});
        this.put("white-knight-right", new String[] {"g1"});
        this.put("white-king", new String[] {"d1"});
        this.put("white-queen", new String[] {"e1"});
        this.put("white-knight-left", new String[]  {"b1"});
        this.put("white-pawn", new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"});
    }};

    private final HashMap<String, String[]> piecesStartingLocsBlackBelow = new HashMap<>() {{
        this.put("white-rook", new String[] {"a8", "h8"});
        this.put("white-bishop", new String[] {"c8", "f8"});
        this.put("white-knight-right", new String[] {"g8"});
        this.put("white-king", new String[] {"d8"});
        this.put("white-queen", new String[] {"e8"});
        this.put("white-knight-left", new String[]  {"b8"});
        this.put("white-pawn", new String[] {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"});
        this.put("black-rook", new String[] {"a1", "h1"});
        this.put("black-bishop", new String[] {"c1", "f1"});
        this.put("black-knight-right", new String[] {"g1"});
        this.put("black-king", new String[] {"d1"});
        this.put("black-queen", new String[] {"e1"});
        this.put("black-knight-left", new String[]  {"b1"});
        this.put("black-pawn", new String[] {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"});
    }};

    private HashMap<Character, HashMap<Character, Chesspiece>> piecesLocations = new HashMap<>() {{
        this.put('a', new HashMap<Character, Chesspiece>()); this.put('b', new HashMap<Character, Chesspiece>());
        this.put('c', new HashMap<Character, Chesspiece>()); this.put('d', new HashMap<Character, Chesspiece>());
        this.put('e', new HashMap<Character, Chesspiece>()); this.put('f', new HashMap<Character, Chesspiece>());
        this.put('g', new HashMap<Character, Chesspiece>()); this.put('h', new HashMap<Character, Chesspiece>());
        this.get('a').put('1', null); this.get('a').put('2', null); this.get('a').put('3', null); this.get('a').put('4', null);
        this.get('a').put('5', null); this.get('a').put('6', null); this.get('a').put('7', null); this.get('a').put('8', null);
        this.get('b').put('1', null); this.get('b').put('2', null); this.get('b').put('3', null); this.get('b').put('4', null);
        this.get('b').put('5', null); this.get('b').put('6', null); this.get('b').put('7', null); this.get('b').put('8', null);
        this.get('c').put('1', null); this.get('c').put('2', null); this.get('c').put('3', null); this.get('c').put('4', null);
        this.get('c').put('5', null); this.get('c').put('6', null); this.get('c').put('7', null); this.get('c').put('8', null);
        this.get('d').put('1', null); this.get('d').put('2', null); this.get('d').put('3', null); this.get('d').put('4', null);
        this.get('d').put('5', null); this.get('d').put('6', null); this.get('d').put('7', null); this.get('d').put('8', null);
        this.get('e').put('1', null); this.get('e').put('2', null); this.get('e').put('3', null); this.get('e').put('4', null);
        this.get('e').put('5', null); this.get('e').put('6', null); this.get('e').put('7', null); this.get('e').put('8', null);
        this.get('f').put('1', null); this.get('f').put('2', null); this.get('f').put('3', null); this.get('f').put('4', null);
        this.get('f').put('5', null); this.get('f').put('6', null); this.get('f').put('7', null); this.get('f').put('8', null);
        this.get('g').put('1', null); this.get('g').put('2', null); this.get('g').put('3', null); this.get('g').put('4', null);
        this.get('g').put('5', null); this.get('g').put('6', null); this.get('g').put('7', null); this.get('g').put('8', null);
        this.get('h').put('1', null); this.get('h').put('2', null); this.get('h').put('3', null); this.get('h').put('4', null);
        this.get('h').put('5', null); this.get('h').put('6', null); this.get('h').put('7', null); this.get('h').put('8', null);
    }};

    private ImagesManager imagesManager;

    private HashMap<String, Chesspiece[]> chesspiecesMap;

    private String playingColor;

    public ChesspiecesManager(final ImagesManager imgMgr, String colorPlaying) {

        imagesManager = imgMgr;
        playingColor = colorPlaying;
        chesspiecesMap = new HashMap<String, Chesspiece[]>();

        HashMap<String, String[]> piecesStartingLocs = (colorPlaying == "white")
                                                       ? piecesStartingLocsWhiteBelow
                                                       : piecesStartingLocsBlackBelow;

        for (Entry<String, String[]> pieceStartingLocs : piecesStartingLocs.entrySet()) {
            Chesspiece[] chesspieces = new Chesspiece[pieceStartingLocs.getValue().length];
            String[] startingLocs = pieceStartingLocs.getValue();
            for (int index = 0; index < startingLocs.length; index++) {
                Chesspiece chesspiece = new Chesspiece(pieceStartingLocs.getKey(), imagesManager);
                chesspiece.setChessboardLocation(startingLocs[index]);
                chesspieces[index] = chesspiece;
            }
            chesspiecesMap.put(pieceStartingLocs.getKey(), chesspieces);
        }

        for (Entry<String, Chesspiece[]> identityToChesspiece : chesspiecesMap.entrySet()) {
            for (Chesspiece chesspiece : identityToChesspiece.getValue()) {
                String pieceLocAlgNotn = chesspiece.getChessboardLocation();
                char algNotnAlpha = pieceLocAlgNotn.charAt(0);
                char algNotnNum = pieceLocAlgNotn.charAt(1);
                piecesLocations.get(algNotnAlpha).put(algNotnNum, chesspiece);
            }
        }
    }

    public Chesspiece[] getChesspieces(String chesspieceIdentity) {
        return chesspiecesMap.get(chesspieceIdentity);
    }

    @Override
    public Iterator<String> iterator() {
        Iterator<String> iterator = new Iterator<String>() {
            private Object[] piecesFlags = chesspiecesMap.keySet().toArray();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < piecesFlags.length;
            }

            @Override
            public String next() {
                String retval = (String) piecesFlags[index];
                index += 1;
                return retval;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return iterator;
    }
}
