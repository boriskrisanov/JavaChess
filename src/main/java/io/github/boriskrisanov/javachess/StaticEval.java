package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

public class StaticEval {
    private static int getPieceValue(Piece piece) {
        return piece.getValue();
    }

    private static int countMaterialDifference(Piece[] position) {
        int materialDifference = 0;

        for (Piece piece : position) {
            if (piece == null) {
                continue;
            }

            if (piece.getColor() == WHITE) {
                materialDifference += getPieceValue(piece);
            } else {
                materialDifference -= getPieceValue(piece);
            }
        }

        return materialDifference;
    }

    public static int evaluate(Board position) {
        int eval = 0;

        eval += countMaterialDifference(position.getBoard());

        return eval * (position.getSideToMove() == WHITE ? 1 : -1);
//        return eval;
    }
}
