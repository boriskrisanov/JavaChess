package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveGenerationTest {
    private long generateMoves(Board board, int depth, boolean rootNode, TreeMap<String, Long> positionsReachedMap) {
        long positionsReached = 0;

        if (depth == 0) {
            return 1;
        }

        for (Move move : board.getAllLegalMovesForSide(Piece.Color.WHITE)) {
            board.makeMove(move);

            System.out.println("========== " + move.toUciString());
            System.out.println(board);

            long result = generateMoves(board, depth - 1, false, positionsReachedMap);
            positionsReached += result;

            if (rootNode) {
                positionsReachedMap.putIfAbsent(move.toUciString(), positionsReached);
                positionsReachedMap.computeIfPresent(move.toUciString(), (k, v) -> result);
            }

            board.unmakeMove(move);
        }

        return positionsReached;
    }

    private long runTest(int depth) {
        System.out.println("Running move generation test with depth " + depth);

        Board board = new Board();
        board.loadStartingPosition();

        TreeMap<String, Long> positionsReached = new TreeMap<>();

        long result = generateMoves(board, depth, true, positionsReached);

        positionsReached.forEach((k, v) -> System.out.println(k + ": " + v));

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
    @Disabled
    void testDepth3() {
//        assertEquals(20, runTest(3));
    }
}
