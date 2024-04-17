package io.github.boriskrisanov.javachess.engine;

import io.github.boriskrisanov.javachess.engine.board.*;
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
        assertEquals(4865609, runTest(5, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testStartingPosition() {
        assertEquals(119060324, runTest(6, Board.STARTING_POSITION_FEN));
    }

    @Test
    void testPosition1() {
        assertEquals(193690690, runTest(5, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 0"));
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

    @Test
    void realTestPosition1() {
        // https://lichess.org/QR5UbqUY#16
        assertEquals(108181315, runTest(5, "r1bqk2r/ppp2ppp/2n1pn2/8/QbBP4/2N2N2/PP3PPP/R1B2RK1 w kq - 4 9"));
    }

    @Test
    void realTestPosition2() {
        // https://lichess.org/INY3KINN#51
        assertEquals(406683732, runTest(6, "2rr2k1/5np1/1pp1pn1p/p4p2/P1PP4/3NP1P1/5PP1/2RRB1K1 b - - 0 26"));
    }

    @Test
    void realTestPosition3() {
        // https://lichess.org/INY3KINN#115
        assertEquals(85338565, runTest(6, "6k1/6p1/7p/2N3P1/PR6/5PK1/r5P1/6n1 b - - 2 58"));
    }

    @Test
    void realTestPosition4() {
        // https://lichess.org/751DRMPG#29
        assertEquals(63507755, runTest(5, "r2q1rk1/4bppp/1p2pn2/3pP3/2p2B2/4P2P/1PPNQPP1/R4RK1 b - - 0 15"));
    }

    @Test
    void realTestPosition5() {
        // https://lichess.org/751DRMPG#89
        assertEquals(509977948, runTest(6, "3Q4/5k1N/4q1p1/3pB3/8/5P2/r5P1/6K1 b - - 4 45"));
    }

    @Test
    void realTestPosition6() {
        // https://lichess.org/I5iGXY21#108
        assertEquals(234461080, runTest(7, "8/8/8/p6p/P3R1r1/2k5/4K3/8 w - - 1 55"));
    }

    @Test
    void realTestPosition7() {
        // Played in engine test game
        assertEquals(101255241, runTest(5, "r2q1rk1/ppp2p1p/1bn5/7R/1P1p2b1/N1P5/P4QP1/R1B1KBN1 b Q - 0 19"));
    }
}
