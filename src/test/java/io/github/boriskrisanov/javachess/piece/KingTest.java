package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KingTest {
    @Test
    void testBasicMovement() {
        Util.assertMoveListEquals(
                new String[]{"a2", "b2", "b1"},
                new Board("7k/8/8/8/8/8/8/K7 w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"h7", "g7", "g8"},
                new Board("7k/8/8/8/8/8/8/K7 w - - 0 1").getPieceOn("h8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a7", "b7", "b8"},
                new Board("k7/8/8/8/8/8/8/7K w - - 0 1").getPieceOn("a8").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"g1", "g2", "h2"},
                new Board("k7/8/8/8/8/8/8/7K w - - 0 1").getPieceOn("h1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a8", "b8", "c8", "a7", "c7", "a6", "b6", "c6"},
                new Board("8/1k6/8/8/8/8/6K1/8 w - - 0 1").getPieceOn("b7").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"f3", "g3", "h3", "f2", "h2", "f1", "g1", "h1"},
                new Board("8/1k6/8/8/8/8/6K1/8 w - - 0 1").getPieceOn("g2").getLegalMoves()
        );
    }

    @Test
    void testKingCannotMoveIntoCheck() {
        Util.assertMoveListEquals(
                new String[]{"b5", "c5", "b4", "b3", "c3"},
                new Board("8/8/8/8/2k1K3/8/8/8 w - - 0 1").getPieceOn("c4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"e5", "f5", "f4", "f3", "e3"},
                new Board("8/8/8/8/2k1K3/8/8/8 w - - 0 1").getPieceOn("e4").getLegalMoves()
        );
    }

    @Test
    void testKingMustMoveOutOfCheck() {

    }

    @Test
    void testWhiteKingCanShortCastle() {

    }

    @Test
    void testWhiteKingCanLongCastle() {

    }

    @Test
    void testBlackKingCanShortCastle() {

    }

    @Test
    void testBlackKingCanLongCastle() {

    }

    @Test
    void testGetChar() {
        Board board = new Board();
        King whiteKing = new King(Piece.Color.WHITE, new Square("a1"), board);
        King blackKing = new King(Piece.Color.BLACK, new Square("h8"), board);

        Assertions.assertEquals('K', whiteKing.getChar());
        Assertions.assertEquals('k', blackKing.getChar());
    }
}
