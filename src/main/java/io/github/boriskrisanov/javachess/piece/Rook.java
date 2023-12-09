package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        long bitboard = 0;
        var directions = new ArrayList<Direction>();

        directions.add(UP);
        directions.add(DOWN);
        directions.add(LEFT);
        directions.add(RIGHT);

        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    protected long getAttackingSquaresIncludingPins() {
        long bitboard = 0;
        var directions = new ArrayList<Direction>();

        if (pinDirection == null) {
            directions.add(UP);
            directions.add(DOWN);
            directions.add(LEFT);
            directions.add(RIGHT);
        } else if (pinDirection == PinDirection.VERTICAL) {
            directions.add(UP);
            directions.add(DOWN);
        } else if (pinDirection == PinDirection.HORIZONTAL) {
            directions.add(LEFT);
            directions.add(RIGHT);
        }

        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public int getValue() {
        return 500;
    }
}
