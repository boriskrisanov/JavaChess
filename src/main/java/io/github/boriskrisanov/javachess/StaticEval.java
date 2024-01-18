package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
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

    private final static int[] whiteKingOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            2, 1, 0, 0, 0, 0, 1, 2,
    };

    private final static int[] blackKingOpeningScores = {
            2, 1, 0, 0, 0, 0, 1, 2,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int[] whiteBishopOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 0, 0, 0, 0, 3, 0,
            0, 1, 3, 1, 1, 3, 1, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 2, 0, 0, 0, 0, 2, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int[] blackBishopOpeningScores = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 2, 0,
            0, 0, 1, 2, 2, 1, 0, 0,
            0, 1, 3, 1, 1, 3, 1, 0,
            0, 3, 0, 0, 0, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };

    private final static int MAX_OPENING_WEIGHT = getOpeningWeight(new Board());

    private static int getPieceValue(Piece piece, int openingWeight, int endgameWeight) {
        // TODO: Use popcount on bitboard
        int value = piece.getValue();
        int position = piece.getPosition();

        int openingScore = 0;
        if (piece instanceof Pawn) {
            openingScore = piece.getColor() == WHITE ? whitePawnOpeningScores[position] : blackPawnOpeningScores[position];
            openingScore *= 2;
        } else if (piece instanceof Knight) {
            openingScore = piece.getColor() == WHITE ? whiteKnightOpeningScores[position] : blackKnightOpeningScores[position];
        } else if (piece instanceof Bishop) {
            openingScore = piece.getColor() == WHITE ? whiteBishopOpeningScores[position] : blackBishopOpeningScores[position];
        } else if (piece instanceof King) {
            openingScore = piece.getColor() == WHITE ? whiteKingOpeningScores[position] : blackKingOpeningScores[position];
            openingScore *= 8;
        }

        value += openingScore * openingWeight;

        if (piece instanceof Pawn) {
            Direction pawnDirection = piece.getColor() == WHITE ? UP : DOWN;
            int distanceToPromotion = EdgeDistance.get(position, pawnDirection);
            value += (7 - distanceToPromotion) * 2 * (endgameWeight + 1);
        }

        return value;
    }

    public static int getOpeningWeight(Board position) {
        int totalMaterial = 0;
        for (Piece piece : position.getBoardArray()) {
            if (piece != null) {
                totalMaterial += piece.getValue();
            }
        }
        return Math.max((totalMaterial / 1024) - 2, 0);
    }

    public static int getEndgameWeight(Board position, int openingWeight) {
        return MAX_OPENING_WEIGHT - openingWeight;
    }

    public static int evaluate(Board position) {
        // TODO: Use bitcount to count material
        int eval = 0;
        int openingWeight = getOpeningWeight(position);
        int endgameWeight = getEndgameWeight(position, openingWeight);

        for (Piece piece : position.getBoardArray()) {
            if (piece == null) {
                continue;
            }

            if (piece.getColor() == WHITE) {
                eval += getPieceValue(piece, openingWeight, endgameWeight);
            } else {
                eval -= getPieceValue(piece, openingWeight, endgameWeight);
            }
        }

        return eval;
    }
}
