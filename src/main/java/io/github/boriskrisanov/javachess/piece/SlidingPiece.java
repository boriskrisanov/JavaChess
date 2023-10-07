package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class SlidingPiece {
    public static ArrayList<Integer> getAttackingSquares(Piece[] board, Piece piece, Direction[] directions) {
        var attackingSquares = new ArrayList<Integer>();

        for (Direction direction : directions) {
            for (int i = piece.getPosition(); i < EdgeDistance.get(i, direction); i += direction.offset) {
                if (board[i] == null) {
                    continue;
                }
                if (board[i].getColor() == piece.getColor().getOpposite()) {
                    // Enemy piece can be captured, but we can't move past it
                    attackingSquares.add(i);
                    break;
                }
                if (board[i].getColor() == piece.getColor()) {
                    // A friendly piece is in the way, so we can't move any further in this direction
                    break;
                }
            }
        }

        return attackingSquares;
    }
}
