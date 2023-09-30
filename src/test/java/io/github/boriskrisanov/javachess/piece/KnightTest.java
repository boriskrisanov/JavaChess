package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KnightTest {
    @Test
    void testGetLegalMoves() {
        Util.assertMoveListEquals(
                new String[]{"b3", "c2"},
                new Board("7k/8/8/8/8/8/8/N6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b3", "c2"},
                new Board("7k/8/8/8/8/8/n7/Nn5K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b3", "c2"},
                new Board("7k/8/8/8/8/1n6/2n5/N6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c2"},
                new Board("7k/8/8/8/8/1N6/2n5/N6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("7k/8/8/8/8/1N6/2N5/N6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b6", "c7"},
                new Board("N6k/8/8/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b6", "c7"},
                new Board("N6k/2n5/1n6/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b6", "c7"},
                new Board("Nr5k/r1n5/1n6/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("Nr5k/r1N5/1N6/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c7"},
                new Board("Nr5k/r1n5/1N6/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"g6", "f5", "f3", "g2"},
                new Board("7k/8/8/8/7N/8/8/7K w - - 0 1").getPieceOn("h4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b7", "c6", "e6", "f7"},
                new Board("3N3k/8/8/8/8/8/8/7K w - - 0 1").getPieceOn("d8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b2", "c3", "e3", "f2"},
                new Board("7k/8/8/8/8/8/8/3N3K w - - 0 1").getPieceOn("d1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b6", "c7", "e7", "f6", "f4", "e3", "c3", "b4"},
                new Board("7k/8/8/3N4/8/8/8/7K w - - 0 1").getPieceOn("d5").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b6", "c7", "e7", "f6", "f4", "e3", "c3", "b4"},
                new Board("7k/8/8/3n4/8/8/8/7K w - - 0 1").getPieceOn("d5").getLegalMoves()
        );
    }

    @Test
    void testGetChar() {
        Board board = new Board();
        Knight whiteKnight = new Knight(Piece.Color.WHITE, new Square("a1"), board);
        Knight blackKnight = new Knight(Piece.Color.BLACK, new Square("a2"), board);

        Assertions.assertEquals('N', whiteKnight.getChar());
        Assertions.assertEquals('n', blackKnight.getChar());
    }
}
