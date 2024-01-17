package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
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
//        assertEquals(4865609, runTest(1, Board.STARTING_POSITION_FEN, "a2a3 a7a5 b2b4 a5b4"));
        assertEquals(4865609, runTest(5, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testStartingPosition() {
        assertEquals(119060324, runTest(6, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testPosition1() {
        assertEquals(4085603, runTest(2, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0", "a1b1 h3g2"));
    }

    @Test
    void testPosition2() {
        assertEquals(11030083, runTest(6, "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 0"));
    }

    @Test
    void testPosition3() {
        assertEquals(15833292, runTest(5, "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"));
    }

    @Test
    void testPosition4() {
        assertEquals(89941194, runTest(5, "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"));
    }

    @Test
    void testPosition5() {
        assertEquals(164075551, runTest(5, "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 "));
    }
}
