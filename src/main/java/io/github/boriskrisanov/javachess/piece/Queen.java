package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Queen extends Piece {

    public Queen(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        return SlidingPiece.getAttackingSquares(board.getBoard(), this, Direction.values());
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'Q' : 'q';
    }
}
