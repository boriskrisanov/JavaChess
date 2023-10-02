package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class King extends Piece {

    public King(Color color, byte position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Byte> getAttackingSquares() {
        var moves = new ArrayList<Byte>();
        var edgeDist = new EdgeDistance(position);

        if (edgeDist.left >= 1) {
            moves.add((byte) (position - 1));
        }
        if (edgeDist.right >= 1) {
            moves.add((byte) (position + 1));
        }
        if (edgeDist.top >= 1) {
            moves.add((byte) (position - 8));
        }
        if (edgeDist.bottom >= 1) {
            moves.add((byte) (position + 8));
        }
        if (edgeDist.left >= 1 && edgeDist.top >= 1) {
            moves.add((byte) (position - 8 - 1));
        }
        if (edgeDist.right >= 1 && edgeDist.top >= 1) {
            moves.add((byte) (position - 8 + 1));
        }
        if (edgeDist.left >= 1 && edgeDist.bottom >= 1) {
            moves.add((byte) (position + 8 - 1));
        }
        if (edgeDist.right >= 1 && edgeDist.bottom >= 1) {
            moves.add((byte) (position + 8 + 1));
        }

        return moves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'K' : 'k';
    }
}
