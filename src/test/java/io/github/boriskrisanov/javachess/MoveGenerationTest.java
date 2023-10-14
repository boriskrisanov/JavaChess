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

            board.unmakeMove(move);
        }

        return positionsReached;
    }

    private long runTest(int depth) {
        Board board = new Board();

        // board.makeMove(new Move(Square.fromString("b2"), Square.fromString("b3"), null));
        // board.makeMove(new Move(Square.fromString("a7"), Square.fromString("a6"), null));
        // board.makeMove(new Move(Square.fromString("c1"), Square.fromString("b2"), null));
        // board.makeMove(new Move(Square.fromString("a6"), Square.fromString("a5"), null));

        var moveCounts = new TreeMap<String, Long>();

        long result = generateMoves(board, depth, moveCounts, true);

        moveCounts.forEach((move, count) -> System.out.println(move + ": " + count));

        System.out.println("Positions reached: " + result);

        return result;
    }

    @Test
    void testDepth1() {
        assertEquals(20, runTest(1));
    }

    @Test
    void testDepth2() {
        assertEquals(400, runTest(2));
    }

    @Test
    void testDepth3() {
        assertEquals(8902, runTest(3));
    }

    @Test
    void testDepth4() {
        assertEquals(197281, runTest(4));
    }

    @Test
    void testDepth5() {
//        runTest(1);
        assertEquals(4865609, runTest(5));
    }

    @Test
    void testDepth6() {
        assertEquals(119060324, runTest(6));
    }

    @Test
    void testDepth7() {
        assertEquals(3195901860L, runTest(7));
    }

    @Test
    void testDepth8() {
        assertEquals(84998978956L, runTest(8));
    }
}
