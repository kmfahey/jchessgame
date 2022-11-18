package com.kmfahey.jchessgame.junit;

import java.util.HashSet;
import java.util.Collections;
import java.io.IOException;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import com.kmfahey.jchessgame.CoordinatesManager;
import com.kmfahey.jchessgame.ImagesManager;
import com.kmfahey.jchessgame.PiecesManager;

public final class TestPiecesManager extends TestCase {

    CoordinatesManager coordinatesManager;
    ImagesManager imagesManager;
    PiecesManager piecesManager;

    public void setUp() throws IOException, FileNotFoundException {
        coordinatesManager = new CoordinatesManager(0.5F);
        imagesManager = new ImagesManager("../images/", coordinatesManager.getSquareDimensions());
        piecesManager = new PiecesManager(imagesManager, PiecesManager.WHITE);
    }

 /* public void tearDown() throws SQLException {
        assert true;
    } */

    public void testPawns() {
        HashSet<String> lhTestingVar, rhTestingVar;

        lhTestingVar = piecesManager.getValidMoveSet("a2");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"a3", "a4"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));

        piecesManager.movePiece("b7", "b3");

        lhTestingVar = piecesManager.getValidMoveSet("a2");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"b3", "a3", "a4"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));

        piecesManager.movePiece("a2", "a3");

        lhTestingVar = piecesManager.getValidMoveSet("a3");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"a4"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }

    public void testRooks() {
        HashSet<String> lhTestingVar, rhTestingVar;

        piecesManager.movePiece("a2", "b6");

        lhTestingVar = piecesManager.getValidMoveSet("a1");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"a2", "a3", "a4", "a5", "a6", "a7"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));

        piecesManager.movePiece("a1", "a3");

        lhTestingVar = piecesManager.getValidMoveSet("a3");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"a1", "a2", "a4", "a5", "a6", "a7", "b3", "c3", "d3", "e3", "f3", "g3", "h3"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }

    public void testKnights() {
        HashSet<String> lhTestingVar, rhTestingVar;

        lhTestingVar = piecesManager.getValidMoveSet("b1");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"a3", "c3"});

        piecesManager.movePiece("b1", "c3");

        lhTestingVar = piecesManager.getValidMoveSet("c3");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"b1", "a4", "b5", "d5", "e4"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }

    public void testBishops() {
        HashSet<String> lhTestingVar, rhTestingVar;

        piecesManager.movePiece("b2", "b4");
        piecesManager.movePiece("d2", "d4");

        lhTestingVar = piecesManager.getValidMoveSet("c1");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"b2", "a3", "d2", "e3", "f4", "g5", "h6"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }

    public void testKing() {
        HashSet<String> lhTestingVar, rhTestingVar;

        piecesManager.movePiece("c1", "c4");
        piecesManager.movePiece("c2", "c5");
        piecesManager.movePiece("d2", "d5");
        piecesManager.movePiece("e1", "e4");
        piecesManager.movePiece("e2", "e5");

        lhTestingVar = piecesManager.getValidMoveSet("d1");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"c1", "c2", "d2", "e1", "e2"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }

    public void testQueen() {
        HashSet<String> lhTestingVar, rhTestingVar;

        piecesManager.movePiece("d1", "d4");
        piecesManager.movePiece("d2", "d5");
        piecesManager.movePiece("e2", "a6");
        piecesManager.movePiece("f1", "f4");
        piecesManager.movePiece("f2", "f5");

        lhTestingVar = piecesManager.getValidMoveSet("e1");
        rhTestingVar = new HashSet<String>();
        Collections.addAll(rhTestingVar, new String[] {"d1", "d2", "c3", "b4", "a5", "e2", "e3", "e4",
                                                       "e5", "e6", "e7", "f2", "g3", "h4", "f1"});

        assertTrue(String.valueOf(lhTestingVar), lhTestingVar.equals(rhTestingVar));
    }
}
