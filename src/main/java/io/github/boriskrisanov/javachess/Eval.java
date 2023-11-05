package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.piece.*;

public class Eval {
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

    public static int evaluate(Piece[] position) {
        int eval = 0;

        eval += countMaterial(position);

        return eval;
    }
}
