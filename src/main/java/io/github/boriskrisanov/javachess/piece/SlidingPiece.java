package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

public class SlidingPiece {
    public static long getAttackingSquares(long blockers, int position, Direction[] directions) {
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
