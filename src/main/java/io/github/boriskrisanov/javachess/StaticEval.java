package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

public class StaticEval {
    private final static int[] whitePawnOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 4, 4, 0, 0, 0,
            0, 0, 1, 3, 3, 2, 1, 0,
            0, 1, 1, 2, 2, 1, 1, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int[] blackPawnOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 1, 2, 2, 1, 1, 0,
            0, 0, 1, 3, 3, 2, 1, 0,
            0, 0, 0, 4, 4, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int[] whiteKnightOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 0, 4, 4, 0, 3, 0,
            0, 0, 0, 3, 3, 0, 0, 0,
            1, 0, 2, 0, 0, 2, 0, 1,
            0, 0, 0, 1, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int[] blackKnightOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 0, 0, 0,
            1, 0, 2, 0, 0, 2, 0, 1,
            0, 0, 0, 3, 3, 0, 0, 0,
            0, 3, 0, 4, 4, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private static int getPieceValue(Piece piece, int openingWeight) {
        int value = piece.getValue();
        int position = piece.getPosition();

        int openingScore = 0;
        if (piece instanceof Pawn) {
            openingScore = piece.getColor() == WHITE ? whitePawnOpeningScores[position] : blackPawnOpeningScores[position];
        } else if (piece instanceof Knight) {
            openingScore = piece.getColor() == WHITE ? whiteKnightOpeningScores[position] : blackKnightOpeningScores[position];
        }

        value += openingScore * openingWeight;

        return value;
    }

    public static int getOpeningWeight(Board position) {
        int totalMaterial = 0;
        for (Piece piece : position.getBoard()) {
            if (piece != null) {
                totalMaterial += piece.getValue();
            }
        }
        return Math.max((totalMaterial / 1024) - 2, 0);
    }

    public static int evaluate(Board position) {
        int eval = 0;
        int openingWeight = getOpeningWeight(position);

        for (Piece piece : position.getBoard()) {
            if (piece == null) {
                continue;
            }

            if (piece.getColor() == WHITE) {
                eval += getPieceValue(piece, openingWeight);
            } else {
                eval -= getPieceValue(piece, openingWeight);
            }
        }

        return eval;
//        return eval;
    }
}
