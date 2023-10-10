package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
import static io.github.boriskrisanov.javachess.board.PinDirection.*;



public class Bishop extends Piece {
    @Override
    public ArrayList<Integer> getAttackingSquares() {
        var directions = new ArrayList<Direction>();

        if (pinDirection != POSITIVE_DIAGONAL) {
            directions.add(TOP_RIGHT);
            directions.add(BOTTOM_LEFT);
        }

        if (pinDirection != NEGATIVE_DIAGONAL) {
            directions.add(TOP_LEFT);
            directions.add(BOTTOM_RIGHT);
        }

        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    public Bishop(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'B' : 'b';
    }
}
