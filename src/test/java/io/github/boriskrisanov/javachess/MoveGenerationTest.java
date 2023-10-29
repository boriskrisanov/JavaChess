package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MoveGenerationTest {
    private long generateMoves(Board board, int depth, TreeMap<String, Long> moveCounts, boolean rootNode) {
        long positionsReached = 0;

        if (depth == 0) {
            return 1;
        }

        for (Move move : board.getLegalMovesForSideToMove()) {
            board.makeMove(move);

            long result = generateMoves(board, depth - 1, moveCounts, false);
            positionsReached += result;

            if (rootNode) {
                moveCounts.putIfAbsent(move.toUciString(), positionsReached);
                moveCounts.computeIfPresent(move.toUciString(), (k, v) -> result);
            }

            board.unmakeMove();
        }

        return positionsReached;
    }

    private long runTest(int depth, String fen, String moveSequence) {
        Board board = new Board(fen);

        if (moveSequence.length() == 4) {
            board.makeMove(moveSequence);
        } else if (!moveSequence.isEmpty()) {
            for (String move : moveSequence.split(" ")) {
                board.makeMove(move);
            }
        }

        var moveCounts = new TreeMap<String, Long>();

        long result = generateMoves(board, depth, moveCounts, true);

        moveCounts.forEach((move, count) -> System.out.println(move + ": " + count));

        System.out.println("Positions reached: " + result);

        return result;
    }

    private long runTest(int depth, String fen) {
        return runTest(depth, fen, "");
    }

    @Test
    void testStartingPositionDepth5() {
        assertEquals(4865609, runTest(5, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testDepth6() {
        assertEquals(119060324, runTest(6, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testDepth7() {
        assertEquals(3195901860L, runTest(7, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testDepth8() {
        assertEquals(84998978956L, runTest(8, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testPosition1() {
//        runTest(5, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0");
//        runTest(3, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0", "d5e6 a6b7");
        assertEquals(4085603, runTest(4, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0"));
    }

    @Test
    void testPosition3() {
        assertEquals(674624, runTest(5, "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0"));
    }
}
