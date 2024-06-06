package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class SlidingPiece {
    public static long getAttackingSquares(Piece[] board, Piece piece, List<Direction> directions) {
        long attackingSquares = 0;

        for (Direction direction : directions) {
            for (int i = 0; i < EdgeDistance.get(piece.position, direction); i++) {
                int targetSquare = piece.position + direction.offset * (i + 1);

                if (board[targetSquare] == null) {
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    continue;
                }
                if (board[targetSquare].getColor() == piece.getColor().getOpposite()) {
                    // Enemy piece can be captured, but we can't move past it
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    break;
                }
                if (board[targetSquare].getColor() == piece.getColor()) {
                    // A friendly piece is in the way, so we can't move any further in this direction
                    // This piece doesn't technically attack this square, but it's needed to ensure that the enemy king can't capture defended pieces
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    break;
                }
            }
        }

        return attackingSquares;
    }

    public static long getAttackingSquares2(long blockers, int position, Direction[] directions) {
        long attackingSquares = 0;

        for (Direction direction : directions) {
            for (int i = 0; i < EdgeDistance.get(position, direction); i++) {
                int targetSquare = position + direction.offset * (i + 1);
                attackingSquares |= BitboardUtils.withSquare(targetSquare);

                if ((blockers & BitboardUtils.withSquare(targetSquare)) != 0) {
                    break;
                }
            }
        }

        return attackingSquares;
    }
}
