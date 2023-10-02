package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

public abstract class Piece {
    protected final Color color;
    protected byte position;
    protected Board board;

    public Piece(Color color, byte position, Board board) {
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

    // TODO: Create separate function for filtering out pseudo legal moves
    public static Piece fromChar(char c, byte position, Board board) {
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


    public abstract ArrayList<Byte> getAttackingSquares();

    public ArrayList<Move> getLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (byte attackingSquare : getAttackingSquares()) {
            Piece capturedPiece = board.getPieceOn(attackingSquare);
            Move move = new Move(position, attackingSquare, capturedPiece);

            if (!board.isSideInCheckAfterMove(this.color, move)
                    && (board.isSquareEmpty(attackingSquare)
                    || board.getPieceOn(attackingSquare).getColor() == this.color.getOpposite()
            )) {
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

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
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
