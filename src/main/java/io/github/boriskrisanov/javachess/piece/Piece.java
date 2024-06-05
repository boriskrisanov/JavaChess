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
        for (Move move : getPseudoLegalMoves()) {
            if (board.isPseudoLegalMoveLegal(move)) {
                moves.add(move);
            }
        }
        return moves;
    }

    public ArrayList<Move> getPseudoLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        var attackingSquares = getAttackingSquares();

        // Ignore squares that are occupied by friendly pieces
        attackingSquares &= ~board.getPieces(this.color);

        for (int attackingSquare : BitboardUtils.squaresOf(attackingSquares)) {
            if (board.isSideInCheck(this.color)) {
                for (int resolution : board.getCheckResolutions()) {
                    // Check if this piece can move to the resolution square
                    // TODO: Some of this might be redundant

                    if (resolution != attackingSquare || board.getPieceOn(resolution) instanceof King) {
                        continue;
                    }

                    if ((pinDirection == PinDirection.HORIZONTAL && Square.getRank(position) != Square.getRank(resolution))
                            || (pinDirection == PinDirection.VERTICAL && Square.getFile(position) != Square.getFile(resolution))) {
                        continue;
                    }

                    // No need to check whether the square is empty because either the king is in check from a sliding
                    // piece, in which case the resolution squares must be empty, or the king is in check from a pawn or
                    // a knight, in which case it can be captured. A resolution square can never contain a friendly piece.

                    moves.add(new Move(position, resolution, board.getBoard()[resolution]));
                }
            } else {
                Piece capturedPiece = board.getPieceOn(attackingSquare);
                Move move = new Move(position, attackingSquare, capturedPiece);
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
