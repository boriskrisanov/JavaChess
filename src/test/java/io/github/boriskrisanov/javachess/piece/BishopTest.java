package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BishopTest {
    @Test
    void testGetLegalMoves() {
        Util.assertMoveListEquals(
                new String[]{"b2", "c3", "d4", "e5", "f6", "g7", "h8"},
                new Board("k7/8/8/8/8/8/8/B6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a1", "b2", "c3", "e3", "f2", "g1", "e5", "f6", "g7", "h8", "c5", "b6", "a7"},
                new Board("k7/8/8/8/3B4/8/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a1", "b2", "c3", "e3", "f2", "g1", "c5", "b6", "a7", "e5"},
                new Board("k7/8/8/4b3/3B4/8/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c5", "e5", "c3", "e3"},
                new Board("k7/8/8/2b1b3/3B4/2b1b3/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("k7/8/8/2B1B3/3B4/2B1B3/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b7", "c6", "e4"},
                new Board("k7/8/8/3b4/4B3/8/8/7K w - - 0 1").getPieceOn("d5").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"d5", "f3", "g2"},
                new Board("k7/8/8/3b4/4B3/8/8/7K w - - 0 1").getPieceOn("e4").getLegalMoves()
        );
    }

    @Test
    void testGetChar() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop(Piece.Color.WHITE, new Square("a1"), board);
        Bishop blackBishop = new Bishop(Piece.Color.BLACK, new Square("a2"), board);

        Assertions.assertEquals('B', whiteBishop.getChar());
        Assertions.assertEquals('b', blackBishop.getChar());
    }
}
