package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        var directions = new ArrayList<Direction>();

        if (pinDirection != PinDirection.VERTICAL) {
            directions.add(UP);
            directions.add(DOWN);
        }

        if (pinDirection != PinDirection.HORIZONTAL) {
            directions.add(LEFT);
            directions.add(RIGHT);
        }

        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'R' : 'r';
    }
}
