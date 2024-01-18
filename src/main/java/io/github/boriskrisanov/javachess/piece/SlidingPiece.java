package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class SlidingPiece {
    public static long getAttackingSquares(Board board, Piece piece, List<Direction> directions) {
        long attackingSquares = 0;

        for (Direction direction : directions) {
            for (int i = 0; i < EdgeDistance.get(piece.position, direction); i++) {
                int targetSquare = piece.position + direction.offset * (i + 1);

                if (board.isSquareEmpty(targetSquare)) {
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    continue;
                }
                if (board.getPieceOn(targetSquare).getColor() == piece.getColor().getOpposite()) {
                    // Enemy piece can be captured, but we can't move past it
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    break;
                }
                if (board.getPieceOn(targetSquare).getColor() == piece.getColor()) {
                    // A friendly piece is in the way, so we can't move any further in this direction
                    // This piece doesn't technically attack this square, but it's needed to ensure that the enemy king can't capture defended pieces
                    attackingSquares |= BitboardUtils.withSquare(targetSquare);
                    break;
                }
            }
        }

        return attackingSquares;
    }
}
