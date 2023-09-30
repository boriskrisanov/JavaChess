package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.WHITE;

public abstract class Piece {
    protected final Color color;
    protected Square position;
    protected Board board;

    public Piece(Color color, Square position, Board board) {
        this.color = color;
        this.position = position;
        this.board = board;
    }

    /**
     * Uses the algebraic notation letter to determine the type and color of a piece
     *
     * @param c        The piece's algebraic notation letter
     * @param position The square on which the piece should be placed
     * @param board    The board on which the piece should be placed
     */
    public static Piece fromChar(char c, Square position, Board board) {
        Piece piece = null;
        Color color = Character.isUpperCase(c) ? WHITE : Color.BLACK;

        switch (Character.toLowerCase(c)) {
            case 'p' -> piece = new Pawn(color, position, board);
            case 'n' -> piece = new Knight(color, position, board);
            case 'b' -> piece = new Bishop(color, position, board);
            case 'r' -> piece = new Rook(color, position, board);
            case 'q' -> piece = new Queen(color, position, board);
            case 'k' -> piece = new King(color, position, board);
        }

        return piece;
    }


    public abstract ArrayList<Square> getAttackingSquares();


    public abstract ArrayList<Move> getLegalMoves();

    /**
     * @return The piece's algebraic notation letter
     */
    public abstract char getChar();

    public Color getColor() {
        return color;
    }

    public Square getPosition() {
        return position;
    }

    public void setPosition(Square position) {
        this.position = position;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public enum Color {
        WHITE,
        BLACK;

        public Color getOpposite() {
            return this == WHITE ? BLACK : WHITE;
        }
    }
}
