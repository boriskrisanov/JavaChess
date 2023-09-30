package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueenTest {
    @Test
    void testGetLegalMoves() {
        Util.assertMoveListEquals(
                new String[]{
                        "a5", "b5", "c5", "e5", "f5", "g5", "h5", "d1", "d2", "d3", "d4", "d6", "d7", "d8",
                        "a2", "b3", "c4", "e6", "f7", "g8", "c6", "b7", "a8", "e4", "f3", "g2", "h1"
                },
                new Board("8/8/8/3Q4/8/8/8/8 w - - 0 1").getPieceOn("d5").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{
                        "a5", "b5", "c5", "e5", "f5", "g5", "h5", "d1", "d2", "d3", "d4", "d6", "d7", "d8",
                        "a2", "b3", "c4", "e6", "f7", "g8", "c6", "b7", "a8", "e4", "f3", "g2", "h1"
                },
                new Board("8/8/8/3q4/8/8/8/8 w - - 0 1").getPieceOn("d5").getLegalMoves()
        );
    }

    @Test
    void testGetChar() {
        Board board = new Board();
        Queen whiteQueen = new Queen(Piece.Color.WHITE, new Square("a1"), board);
        Queen blackQueen = new Queen(Piece.Color.BLACK, new Square("a2"), board);

        Assertions.assertEquals('Q', whiteQueen.getChar());
        Assertions.assertEquals('q', blackQueen.getChar());
    }
}
