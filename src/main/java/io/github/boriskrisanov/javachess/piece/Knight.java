package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Knight extends Piece {

    public Knight(Color color, byte position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Byte> getAttackingSquares() {
        ArrayList<Byte> moves = new ArrayList<>();

        var edgeDistance = new EdgeDistance(position);

        if (edgeDistance.left >= 2 && edgeDistance.top >= 1) {
            moves.add((byte) (position - 8 - 2));
        }
        if (edgeDistance.left >= 1 && edgeDistance.top >= 2) {
            moves.add((byte) (position - 8 * 2 - 1));
        }
        if (edgeDistance.right >= 1 && edgeDistance.top >= 2) {
            moves.add((byte) (position - 8 * 2 + 1));
        }
        if (edgeDistance.left >= 2 && edgeDistance.bottom >= 1) {
            moves.add((byte) (position - 2 + 8));
        }
        if (edgeDistance.right >= 2 && edgeDistance.bottom >= 1) {
            moves.add((byte) (position + 2 + 8));
        }
        if (edgeDistance.left >= 1 && edgeDistance.bottom >= 2) {
            moves.add((byte) (position + 8 * 2 - 1));
        }
        if (edgeDistance.right >= 1 && edgeDistance.bottom >= 2) {
            moves.add((byte) (position + 8 * 2 + 1));
        }
        if (edgeDistance.right >= 2 && edgeDistance.top >= 1) {
            moves.add((byte) (position - 8 + 2));
        }

        return moves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'N' : 'n';
    }
}
