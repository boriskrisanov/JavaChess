package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class SlidingPiece {
    public static ArrayList<Integer> getAttackingSquares(Piece[] board, Piece piece, List<Direction> directions) {
        var attackingSquares = new ArrayList<Integer>();

        for (Direction direction : directions) {
            for (int i = 0; i < EdgeDistance.get(piece.position, direction); i++) {
                int targetSquare = piece.position + direction.offset * (i + 1);

                if (board[targetSquare] == null) {
                    attackingSquares.add(targetSquare);
                    continue;
                }
                if (board[targetSquare].getColor() == piece.getColor().getOpposite()) {
                    // Enemy piece can be captured, but we can't move past it
                    attackingSquares.add(targetSquare);
                    break;
                }
                if (board[targetSquare].getColor() == piece.getColor()) {
                    // A friendly piece is in the way, so we can't move any further in this direction
                    // This piece doesn't technically attack this square, but it's needed to ensure that the enemy king can't capture defended pieces
                    attackingSquares.add(targetSquare);
                    break;
                }
            }
        }

        return attackingSquares;
    }
}
