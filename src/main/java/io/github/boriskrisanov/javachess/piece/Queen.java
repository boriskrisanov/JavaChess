package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
import static io.github.boriskrisanov.javachess.board.PinDirection.*;

public class Queen extends Piece {

    public Queen(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {

        var directions = new ArrayList<>(List.of(Direction.values()));
        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    @Override
    protected ArrayList<Integer> getAttackingSquaresIncludingPins() {
        var directions = new ArrayList<Direction>();

        if (pinDirection == null) {
            directions.addAll(List.of(Direction.values()));
        } else if (pinDirection == VERTICAL) {
            directions.add(UP);
            directions.add(DOWN);
        } else if (pinDirection == HORIZONTAL) {
            directions.add(LEFT);
            directions.add(RIGHT);
        } else if (pinDirection == POSITIVE_DIAGONAL) {
            directions.add(TOP_RIGHT);
            directions.add(BOTTOM_LEFT);
        } else if (pinDirection == NEGATIVE_DIAGONAL) {
            directions.add(TOP_LEFT);
            directions.add(BOTTOM_RIGHT);
        }

        return SlidingPiece.getAttackingSquares(board.getBoard(), this, directions);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'Q' : 'q';
    }
}
