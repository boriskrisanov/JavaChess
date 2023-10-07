package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Queen extends Piece {

    public Queen(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        var rook = new Rook(color, position, board);
        var bishop = new Bishop(color, position, board);

        var moves = new ArrayList<Integer>();

        moves.addAll(rook.getAttackingSquares());
        moves.addAll(bishop.getAttackingSquares());

        return moves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'Q' : 'q';
    }
}
