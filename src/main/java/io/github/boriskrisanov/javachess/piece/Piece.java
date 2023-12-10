package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

public abstract class Piece {
    protected final Color color;
    protected int position;
    protected Board board;
    protected PinDirection pinDirection;

    public Piece(Color color, int position, Board board) {
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

    public static Piece fromChar(char c, int position, Board board) {
        Color color = Character.isUpperCase(c) ? WHITE : BLACK;

        return switch (Character.toLowerCase(c)) {
            case 'p' -> new Pawn(color, position, board);
            case 'n' -> new Knight(color, position, board);
            case 'b' -> new Bishop(color, position, board);
            case 'r' -> new Rook(color, position, board);
            case 'q' -> new Queen(color, position, board);
            case 'k' -> new King(color, position, board);
            default -> throw new IllegalArgumentException(String.valueOf(c));
        };
    }


    public abstract long getAttackingSquares();

    protected abstract long getAttackingSquaresIncludingPins();

    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        var attackingSquares = getAttackingSquaresIncludingPins();

        // Ignore squares that are occupied by friendly pieces
        attackingSquares &= ~board.getPieces(this.color);

        for (int attackingSquare : BitboardUtils.squaresOf(attackingSquares)) {
            Piece capturedPiece = board.getPieceOn(attackingSquare);
            Move move = new Move(position, attackingSquare, capturedPiece);
            if (!board.isSideInCheckAfterMove(color, move)) {
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * @return The piece's algebraic notation letter
     */
    public abstract char getChar();

    public Color getColor() {
        return color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isSlidingPiece() {
        return this instanceof Rook || this instanceof Bishop || this instanceof Queen;
    }

    public abstract int getValue();

    public enum Color {
        WHITE,
        BLACK;

        public Color getOpposite() {
            return this == WHITE ? BLACK : WHITE;
        }
    }

    public PinDirection getPinDirection() {
        return pinDirection;
    }

    public void setPinDirection(PinDirection pinDirection) {
        this.pinDirection = pinDirection;
    }
}
