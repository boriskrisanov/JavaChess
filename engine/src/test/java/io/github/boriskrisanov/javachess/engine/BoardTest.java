package io.github.boriskrisanov.javachess.engine;

// TODO
public class BoardTest {
//    @Test
//    void testGetFen() {
//        Board board = new Board();
//        String fen;
//
//        fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//        board.loadFen(fen);
//        assertEquals(fen, board.getFen());
//
////        fen = "2b1r3/2KPpPNp/1pP1p2P/1PPB1pb1/1n4P1/k1qppPr1/2RpR3/1Q2N1Bn w - - 0 1";
////        board.loadFen(fen);
////        assertEquals(fen, board.getFen());
//    }
//
//    @Test
//    void testLoadFen() {
//        Board board = new Board();
//
//        board.loadFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
//
//        Util.assertPieceIs(board.getPieceOn("a8"), BLACK, Rook.class);
//        Util.assertPieceIs(board.getPieceOn("b8"), BLACK, Knight.class);
//        Util.assertPieceIs(board.getPieceOn("c8"), BLACK, Bishop.class);
//        Util.assertPieceIs(board.getPieceOn("d8"), BLACK, Queen.class);
//        Util.assertPieceIs(board.getPieceOn("e8"), BLACK, King.class);
//        Util.assertPieceIs(board.getPieceOn("f8"), BLACK, Bishop.class);
//        Util.assertPieceIs(board.getPieceOn("g8"), BLACK, Knight.class);
//        Util.assertPieceIs(board.getPieceOn("h8"), BLACK, Rook.class);
//
//        Util.assertPieceIs(board.getPieceOn("a7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("b7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("c7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("d7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("e7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("f7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("g7"), BLACK, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("h7"), BLACK, Pawn.class);
//
//        assertNull(board.getPieceOn("a6"));
//        assertNull(board.getPieceOn("b6"));
//        assertNull(board.getPieceOn("c6"));
//        assertNull(board.getPieceOn("d6"));
//        assertNull(board.getPieceOn("e6"));
//        assertNull(board.getPieceOn("f6"));
//        assertNull(board.getPieceOn("g6"));
//        assertNull(board.getPieceOn("h6"));
//
//        assertNull(board.getPieceOn("a5"));
//        assertNull(board.getPieceOn("b5"));
//        assertNull(board.getPieceOn("c5"));
//        assertNull(board.getPieceOn("d5"));
//        assertNull(board.getPieceOn("e5"));
//        assertNull(board.getPieceOn("f5"));
//        assertNull(board.getPieceOn("g5"));
//        assertNull(board.getPieceOn("h5"));
//
//        assertNull(board.getPieceOn("a4"));
//        assertNull(board.getPieceOn("b4"));
//        assertNull(board.getPieceOn("c4"));
//        assertNull(board.getPieceOn("d4"));
//        assertNull(board.getPieceOn("e4"));
//        assertNull(board.getPieceOn("f4"));
//        assertNull(board.getPieceOn("g4"));
//        assertNull(board.getPieceOn("h4"));
//
//        assertNull(board.getPieceOn("a3"));
//        assertNull(board.getPieceOn("b3"));
//        assertNull(board.getPieceOn("c3"));
//        assertNull(board.getPieceOn("d3"));
//        assertNull(board.getPieceOn("e3"));
//        assertNull(board.getPieceOn("f3"));
//        assertNull(board.getPieceOn("g3"));
//        assertNull(board.getPieceOn("h3"));
//
//        Util.assertPieceIs(board.getPieceOn("a2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("b2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("c2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("d2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("e2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("f2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("g2"), WHITE, Pawn.class);
//        Util.assertPieceIs(board.getPieceOn("h2"), WHITE, Pawn.class);
//
//        Util.assertPieceIs(board.getPieceOn("a1"), WHITE, Rook.class);
//        Util.assertPieceIs(board.getPieceOn("b1"), WHITE, Knight.class);
//        Util.assertPieceIs(board.getPieceOn("c1"), WHITE, Bishop.class);
//        Util.assertPieceIs(board.getPieceOn("d1"), WHITE, Queen.class);
//        Util.assertPieceIs(board.getPieceOn("e1"), WHITE, King.class);
//        Util.assertPieceIs(board.getPieceOn("f1"), WHITE, Bishop.class);
//        Util.assertPieceIs(board.getPieceOn("g1"), WHITE, Knight.class);
//        Util.assertPieceIs(board.getPieceOn("h1"), WHITE, Rook.class);
//
//        assertNull(board.getEnPassantTargetSquare());
//        assertEquals(WHITE, board.getSideToMove());
//
//        board.loadFen("7k/8/8/8/8/8/8/N6K w - - 0 1");
//
//        Util.assertPieceIs(board.getPieceOn("h8"), BLACK, King.class);
//        Util.assertPieceIs(board.getPieceOn("a1"), WHITE, Knight.class);
//        Util.assertPieceIs(board.getPieceOn("h1"), WHITE, King.class);
//
//        assertNull(board.getEnPassantTargetSquare());
//        assertEquals(WHITE, board.getSideToMove());
//
//        board.loadFen("qbNrbNpN/PkkRbnpP/nNPBPPKB/pppBpRPB/kQrpkNPb/KkPpQBRn/npPQpqKr/QrrNqNPR b - e4 0 1");
//
//        Util.assertPieceIs(board.getPieceOn("a8"), BlackQueen);
//        Util.assertPieceIs(board.getPieceOn("b8"), BlackBishop);
//        Util.assertPieceIs(board.getPieceOn("c8"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("d8"), BlackRook);
//        Util.assertPieceIs(board.getPieceOn("e8"), BlackBishop);
//        Util.assertPieceIs(board.getPieceOn("f8"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("g8"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("h8"), WhiteKnight);
//
//        Util.assertPieceIs(board.getPieceOn("a7"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("b7"), BlackKing);
//        Util.assertPieceIs(board.getPieceOn("c7"), BlackKing);
//        Util.assertPieceIs(board.getPieceOn("d7"), WhiteRook);
//        Util.assertPieceIs(board.getPieceOn("e7"), BlackBishop);
//        Util.assertPieceIs(board.getPieceOn("f7"), BlackKnight);
//        Util.assertPieceIs(board.getPieceOn("g7"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("h7"), WhitePawn);
//
//        Util.assertPieceIs(board.getPieceOn("a6"), BlackKnight);
//        Util.assertPieceIs(board.getPieceOn("b6"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("c6"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("d6"), WhiteBishop);
//        Util.assertPieceIs(board.getPieceOn("e6"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("f6"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("g6"), WhiteKing);
//        Util.assertPieceIs(board.getPieceOn("h6"), WhiteBishop);
//
//        Util.assertPieceIs(board.getPieceOn("a5"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("b5"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("c5"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("d5"), WhiteBishop);
//        Util.assertPieceIs(board.getPieceOn("e5"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("f5"), WhiteRook);
//        Util.assertPieceIs(board.getPieceOn("g5"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("h5"), WhiteBishop);
//
//        Util.assertPieceIs(board.getPieceOn("a4"), BlackKing);
//        Util.assertPieceIs(board.getPieceOn("b4"), WhiteQueen);
//        Util.assertPieceIs(board.getPieceOn("c4"), BlackRook);
//        Util.assertPieceIs(board.getPieceOn("d4"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("e4"), BlackKing);
//        Util.assertPieceIs(board.getPieceOn("f4"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("g4"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("h4"), BlackBishop);
//
//        Util.assertPieceIs(board.getPieceOn("a3"), WhiteKing);
//        Util.assertPieceIs(board.getPieceOn("b3"), BlackKing);
//        Util.assertPieceIs(board.getPieceOn("c3"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("d3"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("e3"), WhiteQueen);
//        Util.assertPieceIs(board.getPieceOn("f3"), WhiteBishop);
//        Util.assertPieceIs(board.getPieceOn("g3"), WhiteRook);
//        Util.assertPieceIs(board.getPieceOn("h3"), BlackKnight);
//
//        Util.assertPieceIs(board.getPieceOn("a2"), BlackKnight);
//        Util.assertPieceIs(board.getPieceOn("b2"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("c2"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("d2"), WhiteQueen);
//        Util.assertPieceIs(board.getPieceOn("e2"), BlackPawn);
//        Util.assertPieceIs(board.getPieceOn("f2"), BlackQueen);
//        Util.assertPieceIs(board.getPieceOn("g2"), WhiteKing);
//        Util.assertPieceIs(board.getPieceOn("h2"), BlackRook);
//
//        Util.assertPieceIs(board.getPieceOn("a1"), WhiteQueen);
//        Util.assertPieceIs(board.getPieceOn("b1"), BlackRook);
//        Util.assertPieceIs(board.getPieceOn("c1"), BlackRook);
//        Util.assertPieceIs(board.getPieceOn("d1"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("e1"), BlackQueen);
//        Util.assertPieceIs(board.getPieceOn("f1"), WhiteKnight);
//        Util.assertPieceIs(board.getPieceOn("g1"), WhitePawn);
//        Util.assertPieceIs(board.getPieceOn("h1"), WhiteRook);
//
//        assertEquals(new Square("e4"), board.getEnPassantTargetSquare());
//        assertEquals(BLACK, board.getSideToMove());
//    }
}
