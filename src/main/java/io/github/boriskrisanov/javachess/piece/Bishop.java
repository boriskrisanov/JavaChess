package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;



public class Bishop extends Piece {
    @Override
    public ArrayList<Integer> getAttackingSquares() {
        Direction[] directions = {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};
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
