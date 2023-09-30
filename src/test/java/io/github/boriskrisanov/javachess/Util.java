package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;
import io.github.boriskrisanov.javachess.piece.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Util {
    public static void assertSquareListEquals(List<Square> expected, List<Square> actual) {
        expected.sort(Comparator.comparing(Square::getIndex));
        actual.sort(Comparator.comparing(Square::getIndex));
        Assertions.assertEquals(expected, actual);
    }

    public static void assertMoveListEquals(String[] expected, List<Move> actual) {
        var expectedSquares = new ArrayList<>(
                Arrays.stream(expected)
                        .map(Square::new)
                        .toList()
        );

        var actualSquares = new ArrayList<>(
                actual.stream()
                        .map(Move::destination)
                        .toList()
        );

        assertSquareListEquals(expectedSquares, actualSquares);
    }

    public static ArrayList<Square> squareListFromStringArray(String[] array) {
        var squares = new ArrayList<Square>();

        Arrays.stream(array).forEach(s -> squares.add(new Square(s)));

        return squares;
    }

    /**
     * Asserts that actualPiece has the color expectedColor and is of type expectedPiece
     */
    public static void assertPieceIs(Piece actualPiece, Piece.Color expectedColor, Class<? extends Piece> expectedPiece) {
        Assertions.assertSame(actualPiece.getColor(), expectedColor);
        Assertions.assertInstanceOf(expectedPiece, actualPiece);
    }

    public static void assertPieceIs(Piece actualPiece, TestPiece expectedPiece) {
        switch (expectedPiece) {
            case WhitePawn -> assertPieceIs(actualPiece, Piece.Color.WHITE, Pawn.class);
            case WhiteKnight -> assertPieceIs(actualPiece, Piece.Color.WHITE, Knight.class);
            case WhiteBishop -> assertPieceIs(actualPiece, Piece.Color.WHITE, Bishop.class);
            case WhiteRook -> assertPieceIs(actualPiece, Piece.Color.WHITE, Rook.class);
            case WhiteQueen -> assertPieceIs(actualPiece, Piece.Color.WHITE, Queen.class);
            case WhiteKing -> assertPieceIs(actualPiece, Piece.Color.WHITE, King.class);
            case BlackPawn -> assertPieceIs(actualPiece, Piece.Color.BLACK, Pawn.class);
            case BlackKnight -> assertPieceIs(actualPiece, Piece.Color.BLACK, Knight.class);
            case BlackBishop -> assertPieceIs(actualPiece, Piece.Color.BLACK, Bishop.class);
            case BlackRook -> assertPieceIs(actualPiece, Piece.Color.BLACK, Rook.class);
            case BlackQueen -> assertPieceIs(actualPiece, Piece.Color.BLACK, Queen.class);
            case BlackKing -> assertPieceIs(actualPiece, Piece.Color.BLACK, King.class);
        }
    }

    enum TestPiece {
        WhitePawn,
        WhiteKnight,
        WhiteBishop,
        WhiteRook,
        WhiteQueen,
        WhiteKing,
        BlackPawn,
        BlackKnight,
        BlackBishop,
        BlackRook,
        BlackQueen,
        BlackKing
    }
}
