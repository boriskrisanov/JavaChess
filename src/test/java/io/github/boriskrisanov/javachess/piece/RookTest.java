package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.Util;
import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Square;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RookTest {
    //    TestCase[] testCases = {
//            // Pawns
//            new TestCase("8/P7/8/8/8/8/8/8 w - - 0 1", "a7", new String[]{"a8"}),
//            new TestCase("8/8/8/8/8/8/1P6/8 w - - 0 1", "b2", new String[]{"b3", "b4"}),
//            new TestCase("8/8/8/8/8/1P6/8/8 w - - 0 1", "b3", new String[]{"b4"}),
//            new TestCase("8/1P6/8/8/8/8/8/8 w - - 0 1", "b7", new String[]{"b8"}),
//            new TestCase("8/8/8/8/8/P7/P7/8 w - - 0 1", "a2", new String[]{}),
//            new TestCase("8/8/8/8/P7/8/P7/8 w - - 0 1", "a2", new String[]{"a3"}),
//            new TestCase("8/8/8/P7/8/8/P7/8 w - - 0 1", "a2", new String[]{"a3", "a4"}),
//            new TestCase("8/8/8/8/8/1P6/P7/8 w - - 0 1", "a2", new String[]{"a3", "a4"}),
//            new TestCase("8/8/8/8/8/2P5/1P6/8 w - - 0 1", "b2", new String[]{"b3", "b4"}),
//            new TestCase("8/8/8/8/8/1p6/P7/8 w - - 0 1", "a2", new String[]{"a3", "a4", "b3"}),
//            new TestCase("8/8/8/8/8/Pp6/8/8 w - - 0 1", "a3", new String[]{"a4"}),
//            new TestCase("8/8/8/8/8/p7/1P6/8 w - - 0 1", "b2", new String[]{"b3", "b4", "a3"}),
//            new TestCase("8/8/8/8/p7/8/1P6/8 w - - 0 1", "b2", new String[]{"b3", "b4"}),
//            new TestCase("8/8/8/8/8/p7/P7/8 w - - 0 1", "a2", new String[]{}),
//            new TestCase("8/p7/8/8/8/8/8/8 w - - 0 1", "a7", new String[]{"a6", "a5"}),
//            new TestCase("8/p7/p7/8/8/8/8/8 w - - 0 1", "a7", new String[]{}),
//            new TestCase("8/p7/P7/8/8/8/8/8 w - - 0 1", "a7", new String[]{}),
//            new TestCase("8/p7/1P6/8/8/8/8/8 w - - 0 1", "a7", new String[]{"a6", "a5", "b6"}),
//
//            // En passant
//            // new TestCase("8/8/8/8/Pp6/8/8/8 w - a3 0 1", "b4", new String[]{"a3", "b3"}),
//            // new TestCase("8/8/8/8/Pp6/8/8/8 w - - 0 1", "b4", new String[]{"b3"}),
//            // new TestCase("8/6bb/8/8/R1pP2k1/4P3/P7/K7 b - d3 0 1", "c4", new String[]{"c3"}),
//
//            // Knights
//            new TestCase("8/8/8/8/8/8/8/1N6 w - - 0 1", "b1", new String[]{"a3", "c3", "d2"}),
//            new TestCase("8/8/8/3r4/8/2N5/8/8 w - - 0 1", "c3", new String[]{"b1", "a2", "a4", "b5", "d5", "e4", "e2", "d1"}),
//            new TestCase("8/8/3N4/8/8/8/8/8 w - - 0 1", "d6", new String[]{"c8", "b7", "b5", "c4", "e4", "f5", "f7", "e8"}),
//            new TestCase("8/8/8/8/8/8/8/3N4 w - - 0 1", "d1", new String[]{"b2", "c3", "e3", "f2"}),
//            new TestCase("8/8/8/8/8/8/8/7N w - - 0 1", "h1", new String[]{"g3", "f2"}),
//            new TestCase("8/8/8/8/7N/8/8/8 w - - 0 1", "h4", new String[]{"g2", "f3", "f5", "g6"}),
//            new TestCase("7N/8/8/8/8/8/8/8 w - - 0 1", "h8", new String[]{"g6", "f7"}),
//            new TestCase("4N3/8/8/8/8/8/8/8 w - - 0 1", "e8", new String[]{"c7", "d6", "f6", "g7"}),
//            new TestCase("8/8/8/8/N7/8/8/8 w - - 0 1", "a4", new String[]{"b2", "b6", "c3", "c5"}),
//            new TestCase("8/2N5/8/8/8/8/8/8 w - - 0 1", "c7", new String[]{"a8", "e8", "a6", "e6", "b5", "d5"}),
//            new TestCase("8/8/2R5/4N3/8/8/8/8 w - - 0 1", "e5", new String[]{"c4", "d3", "f3", "g4", "g6", "f7", "d7"}),
//            new TestCase("8/3R1R2/2R3R1/4N3/2R3R1/3R1R2/8/8 w - - 0 1", "e5", new String[]{}),
//            new TestCase("R2n3R/1N1R1R2/2R3R1/r7/2R3R1/3R1R2/R7/8 w - - 0 1", "b7", new String[]{"a5", "c5", "d6", "d8"}),
//            new TestCase("R2n3R/5R2/2R3R1/r4b1R/2R1RRRN/6p1/4p1n1/4R3 w - - 0 1", "h4", new String[]{"g2", "f3", "f5"}),
//
//            // Bishops
//            new TestCase("8/8/8/8/8/8/8/B7 w - - 0 1", "a1", new String[]{"b2", "c3", "d4", "e5", "f6", "g7", "h8"}),
//            new TestCase("8/8/8/8/8/8/1p6/B7 w - - 0 1", "a1", new String[]{"b2"}),
//            new TestCase("8/8/8/8/8/8/1P6/B7 w - - 0 1", "a1", new String[]{}),
//            new TestCase("8/8/8/8/8/8/8/7B w - - 0 1", "h1", new String[]{"g2", "f3", "e4", "d5", "c6", "b7", "a8"}),
//            new TestCase("8/8/8/8/8/8/6p1/7B w - - 0 1", "h1", new String[]{"g2"}),
//            new TestCase("8/8/8/8/8/8/6P1/7B w - - 0 1", "h1", new String[]{}),
//            new TestCase("8/8/8/8/8/5p2/6B1/8 w - - 0 1", "g2", new String[]{"h1", "f3", "f1", "h3"}),
//            new TestCase("8/8/8/8/8/5p2/6B1/7P w - - 0 1", "g2", new String[]{"f3", "h3", "f1"}),
//            new TestCase("7B/8/8/8/8/8/8/8 w - - 0 1", "h8", new String[]{"g7", "f6", "e5", "d4", "c3", "b2", "a1"}),
//            new TestCase("7B/6p1/8/8/8/8/8/8 w - - 0 1", "h8", new String[]{"g7"}),
//            new TestCase("7B/6P1/8/8/8/8/8/8 w - - 0 1", "h8", new String[]{}),
//            new TestCase("B7/8/8/8/8/8/8/8 w - - 0 1", "a8", new String[]{"b7", "c6", "d5", "e4", "f3", "g2", "h1"}),
//            new TestCase("B7/1p6/8/8/8/8/8/8 w - - 0 1", "a8", new String[]{"b7"}),
//            new TestCase("B7/1P6/8/8/8/8/8/8 w - - 0 1", "a8", new String[]{}),
//
//            // Rooks
//            new TestCase("8/8/8/8/8/8/8/R7 w - - 0 1", "a1", new String[]{"b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "a3", "a4", "a5", "a6", "a7", "a8"}),
//            new TestCase("8/8/8/8/8/8/p7/Rp6 w - - 0 1", "a1", new String[]{"b1", "a2"}),
//            new TestCase("8/8/8/8/8/8/p7/RP6 w - - 0 1", "a1", new String[]{"a2"}),
//            new TestCase("8/8/8/8/p7/8/8/R2P4 w - - 0 1", "a1", new String[]{"a2", "a3", "a4", "b1", "c1"}),
//            new TestCase("8/8/8/8/p2R4/8/8/3P4 w - - 0 1", "d4", new String[]{"c4", "b4", "a4", "d3", "d2", "e4", "f4", "g4", "h4", "d5", "d6", "d7", "d8"}),
//            new TestCase("7R/6p1/8/8/p6p/8/8/3P4 w - - 0 1", "h8", new String[]{"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h7", "h6", "h5", "h4"}),
//            new TestCase("8/6p1/8/5P2/p3PR1p/5P2/8/3P4 w - - 0 1", "f4", new String[]{"g4", "h4"}),
//            new TestCase("8/8/2r3P1/8/2r5/8/8/8 w - - 0 1", "c6", new String[]{"d6", "e6", "f6", "g6", "c7", "c8", "c5", "b6", "a6"}),
//            new TestCase("8/8/2r3P1/8/2r5/8/8/8 w - - 0 1", "c4", new String[]{"d4", "e4", "f4", "g4", "h4", "c3", "c2", "c1", "b4", "a4", "c5"}),
//
//            // Queen
//            new TestCase("8/8/8/8/8/8/8/Q7 w - - 0 1", "a1", new String[]{"a2", "a3", "a4", "a5", "a6", "a7", "a8", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "b2", "c3", "d4", "e5", "f6", "g7", "h8"}),
//            new TestCase("8/8/8/8/8/8/PP6/QP6 w - - 0 1", "a1", new String[]{}),
//            new TestCase("8/8/8/8/8/8/PP6/QP1p4 w - - 0 1", "a1", new String[]{}),
//            new TestCase("8/8/8/8/8/8/PP6/Q2p4 w - - 0 1", "a1", new String[]{"b1", "c1", "d1"}),
//            new TestCase("8/8/8/4P3/p2P4/8/8/Q2pP3 w - - 0 1", "a1", new String[]{"a2", "a3", "a4", "b2", "c3", "b1", "c1", "d1"}),
//            new TestCase("8/2p5/7p/4P3/p2PQ3/8/6q1/3pP3 w - - 0 1", "e4", new String[]{"e3", "e2", "f4", "g4", "h4", "f3", "g2", "d3", "c2", "b1", "d5", "c6", "b7", "a8", "f5", "g6", "h7"}),
//            new TestCase("Q3q2Q/6pP/5p1P/4p2P/3p3P/2p4P/1p5P/p6r w - - 0 1", "h8", new String[]{"g7", "g8", "f8", "e8"}),
//            new TestCase("2q4R/8/P7/5r2/8/8/8/8 w - - 0 1", "c8", new String[]{"d8", "e8", "f8", "g8", "h8", "d7", "e6", "c7", "c6", "c5", "c4", "c3", "c2", "c1", "b7", "a6", "b8", "a8"}),
//            new TestCase("8/8/8/q2RRr2/8/8/8/8 w - - 0 1", "a5", new String[]{"a6", "a7", "a8", "a4", "a3", "a2", "a1", "b5", "c5", "d5", "b6", "c7", "d8", "b4", "c3", "d2", "e1"}),
//
//            // King
//            new TestCase("7k/8/8/8/8/8/8/K7 w - - 0 1", "a1", new String[]{"a2", "b1", "b2"}),
//            new TestCase("7k/8/8/8/8/8/1K6/8 w - - 0 1", "b2", new String[]{"a1", "a2", "a3", "b1", "b3", "c1", "c2", "c3"}),
//            new TestCase("7k/8/8/8/8/q7/8/K7 w - - 0 1", "a1", new String[]{"b1"}),
//            new TestCase("7k/8/8/8/8/q1K5/8/8 w - - 0 1", "c3", new String[]{"c2", "c4", "d2", "d4"}),
//            new TestCase("7K/8/8/8/8/q1k5/8/8 w - - 0 1", "c3", new String[]{"b2", "b3", "b4", "c2", "d2", "d3", "d4", "c4"}),
//            new TestCase("2r4k/8/8/8/8/8/2p5/2K5 w - - 0 1", "c1", new String[]{"b2", "d2"}),
//            new TestCase("2R4k/8/8/8/8/8/2p5/2K5 w - - 0 1", "c1", new String[]{"b2", "c2", "d2"}),
//            new TestCase("8/8/8/8/8/k7/8/K7 w - - 0 1", "a1", new String[]{"b1"})
//    };
    @Test
    void testGetLegalMoves() {
        Util.assertMoveListEquals(
                new String[]{"a2", "a3", "a4", "a5", "a6", "a7", "a8", "b1", "c1", "d1", "e1", "f1", "g1"},
                new Board("7k/8/8/8/8/8/8/R6K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a2", "a3", "a4", "a5", "a6", "a7", "a8", "b1", "c1", "d1", "e1", "f1", "g1"},
                new Board("7K/8/8/8/8/8/8/r6k w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b1"},
                new Board("7k/8/8/8/8/8/r7/Rr5K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("7k/8/8/8/8/8/R7/RR5K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("a1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a3", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("a2").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a4", "b3", "a2"},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("a3").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a6", "a7", "a8", "a4", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("a5").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b2", "b3", "b4", "b5", "b6", "b7", "b8", "c1", "d1", "e1", "f1", "g1"},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("b1").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"b3", "c2", "c1", "d3", "e3", "f3", "g3", "h3", "c4", "c5", "c6", "c7", "c8"},
                new Board("7k/8/8/r7/8/r1r5/R7/RR5K w - - 0 1").getPieceOn("c3").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a4", "b4", "c4", "d5", "d6", "d7", "d8", "d3", "d2", "d1", "e4", "f4", "g4", "h4"},
                new Board("7k/8/8/8/3R4/8/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"a4", "b4", "c4", "d5", "d6", "d7", "d8", "d3", "d2", "d1", "e4", "f4", "g4", "h4"},
                new Board("7k/8/8/8/3r4/8/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c4", "d3", "d5", "e4"},
                new Board("7k/8/8/3R4/2RrR3/3R4/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c4", "d3", "d5", "e4", "b4", "d6", "f4", "d2"},
                new Board("7k/8/3R4/8/1R1r1R2/8/3R4/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{"c4", "d3", "d5", "e4"},
                new Board("7k/8/3r4/8/1r1r1r2/8/3r4/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );

        Util.assertMoveListEquals(
                new String[]{},
                new Board("7k/8/8/3r4/2rrr3/3r4/8/7K w - - 0 1").getPieceOn("d4").getLegalMoves()
        );
    }

    @Test
    void testGetChar() {
        Board board = new Board();
        Rook whiteRook = new Rook(Piece.Color.WHITE, new Square("a1"), board);
        Rook blackRook = new Rook(Piece.Color.BLACK, new Square("a2"), board);

        Assertions.assertEquals('R', whiteRook.getChar());
        Assertions.assertEquals('r', blackRook.getChar());
    }
//    @Test
//    void pawn() {
//        var previousTests = new ArrayList<TestCase>();
//
//        for (var test : testCases) {
//            // Check for duplicate test cases
//            for (var previousTest : previousTests) {
//                if (Objects.equals(previousTest.fen, test.fen) && Objects.equals(previousTest.square, test.square)) {
//                    System.out.printf("\033[33mWarning\033[0m: duplicate test case %s", test);
//                }
//            }
//
//            previousTests.add(test);
//
//            var board = new Board(test.fen);
//            var square = new Square(test.square);
//            var piece = board.getPieceOn(square);
//
//            var legalMoves = new ArrayList<String>();
//
//
//            for (Move move : piece.getLegalMoves()) {
//                legalMoves.add(move.destination().toString());
//            }
//
//            var isEqual = true;
//
//            for (String move : legalMoves) {
//                if (!Arrays.asList(test.legalMoves).contains(move)) {
//                    isEqual = false;
//                    break;
//                }
//            }
//
//            if (test.legalMoves.length != legalMoves.size() || !isEqual) {
//                Assertions.fail(String.format("%s: Expected %s got %s", test, Arrays.toString(test.legalMoves), legalMoves));
//            }
//        }
//    }

//    record TestCase(String fen, String square, String[] legalMoves) {
//    }
}
