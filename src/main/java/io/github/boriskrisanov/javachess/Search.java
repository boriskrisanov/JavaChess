package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

public class Search {
    public static int evaluate(Board position, int depth, Piece.Color maximizingPlayer) {
        if (depth == 0) {
            return StaticEval.evaluate(position);
        }

        if (maximizingPlayer == Piece.Color.WHITE) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : position.getLegalMovesForSideToMove()) {
                position.makeMove(move);

                int evalAfterMove = evaluate(position, depth - 1, Piece.Color.BLACK);
                maxEval = Math.max(maxEval, evalAfterMove);

                position.unmakeMove();
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : position.getLegalMovesForSideToMove()) {
                position.makeMove(move);

                int evalAfterMove = evaluate(position, depth - 1, Piece.Color.WHITE);
                minEval = Math.min(minEval, evalAfterMove);

                position.unmakeMove();
            }
            return minEval;
        }
    }

    public static Move bestMove(Board position, int depth, Piece.Color maximizingPlayer) {
        Move bestMove = null;
        int bestEval = maximizingPlayer == Piece.Color.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (maximizingPlayer == Piece.Color.WHITE) {
            for (Move move : position.getLegalMovesForSideToMove()) {
                position.makeMove(move);

                int evalAfterMove = evaluate(position, depth - 1, Piece.Color.BLACK);
                if (evalAfterMove > bestEval) {
                    bestEval = evalAfterMove;
                    bestMove = move;
                }

                position.unmakeMove();
            }
        } else {
            for (Move move : position.getLegalMovesForSideToMove()) {
                position.makeMove(move);

                int evalAfterMove = evaluate(position, depth - 1, Piece.Color.WHITE);
                if (evalAfterMove < bestEval) {
                    bestEval = evalAfterMove;
                    bestMove = move;
                }

                position.unmakeMove();
            }
        }

        return bestMove;
    }
}
