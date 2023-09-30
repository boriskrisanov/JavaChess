package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.piece.Piece;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveGenerationTest {
    private long generateMoves(Board board, int depth) {
        long positionsReached = 0;

        if (depth == 0) {
            return 1;
        }

        for (Move move : board.getAllLegalMovesForSide(Piece.Color.WHITE)) {
             board.makeMove(move);

            long result = generateMoves(board, depth - 1);
            positionsReached += result;

             board.unmakeMove(move);
        }

        return positionsReached;
    }

    @Test
    void testDepth1() {
        Board board = new Board();
        board.loadStartingPosition();

        long result = generateMoves(board, 1);

        assertEquals(20, result);
    }

    @Test
    @Disabled
    void testDepth2() {
        Board board = new Board();
        board.loadStartingPosition();

        long result = generateMoves(board, 2);

        assertEquals(400, result);
    }

    @Test
    @Disabled
    void testDepth3() {
        Board board = new Board();
        board.loadStartingPosition();

        long result = generateMoves(board, 3);

        assertEquals(8902, result);
    }
}
