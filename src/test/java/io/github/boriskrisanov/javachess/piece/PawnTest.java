package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PawnTest {
    @Test
    void testGetLegalMoves() {
        Util.assertMoveListEquals(
                new String[]{"a3", "a4"},
                new Board("8/8/8/8/8/8/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a4"},
                new Board("8/8/8/8/8/P7/8/8 w - - 0 1").getPieceOn("a3").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("8/8/8/8/8/P7/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a3"},
                new Board("8/8/8/8/P7/8/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("8/8/8/8/8/p7/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a3"},
                new Board("8/8/8/8/p7/8/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a3", "a4"},
                new Board("8/8/8/p7/8/8/P7/8 w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c3", "d3", "d4"},
                new Board("8/8/8/8/8/2p5/3P4/8 w - - 0 1").getPieceOn("d2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c3", "d3", "d4", "e3"},
                new Board("8/8/8/8/8/2p1p3/3P4/8 w - - 0 1").getPieceOn("d2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a6", "a5"},
                new Board("8/p7/8/8/8/8/8/8 w - - 0 1").getPieceOn("a7").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a5"},
                new Board("8/8/p7/8/8/8/8/8 w - - 0 1").getPieceOn("a6").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("8/p7/P7/8/8/8/8/8 w - - 0 1").getPieceOn("a7").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a6"},
                new Board("8/p7/8/P7/8/8/8/8 w - - 0 1").getPieceOn("a7").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a6", "b6", "b5", "c6"},
                new Board("8/1p6/P1P5/8/8/8/8/8 w - - 0 1").getPieceOn("b7").getLegalMoves()
        );
    }

    @Test
    void testEnPassant() {
        Util.assertMoveListEquals(
                new String[]{"b3", "a3"},
                new Board("8/8/8/8/Pp6/8/8/8 w - a3 0 1").getPieceOn("b4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c3"},
                new Board("8/6bb/8/8/R1pP2k1/4P3/P7/K7 b - d3 0 1").getPieceOn("c4").getLegalMoves()
        );
    }

    @Test
    void testGetChar() {
        Board board = new Board();
        Pawn whitePawn = new Pawn(Piece.Color.WHITE, new Square("a1"), board);
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Square("a2"), board);

        Assertions.assertEquals('P', whitePawn.getChar());
        Assertions.assertEquals('p', blackPawn.getChar());
    }

}
