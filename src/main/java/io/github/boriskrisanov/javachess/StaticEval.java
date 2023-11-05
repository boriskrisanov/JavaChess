package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

public class StaticEval {
    private static int countMaterial(Piece[] position) {
        int materialDifference = 0;

        for (Piece piece : position) {
            if (piece == null) {
                continue;
            }

            if (piece.getColor() == Piece.Color.WHITE) {
                materialDifference += piece.getValue();
            } else {
                materialDifference -= piece.getValue();
            }
        }

        return materialDifference;
    }

    public static int evaluate(Board position) {
        int eval = 0;

        eval += countMaterial(position.getBoard());

        return eval;
    }
}
