package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Search {
    private static long debugPositionsEvaluated;

    public static SearchResult bestMove(Board position, int depth) {
        debugPositionsEvaluated = 0;

        Move bestMove = null;
        int bestEval = Integer.MIN_VALUE;

        for (Move move : position.getLegalMovesForSideToMove()) {
            position.makeMove(move);
            int eval = -evaluate(position, depth - 1);
            if (eval > bestEval) {
                bestEval = eval;
                bestMove = move;
            }
            position.unmakeMove();
        }

        return new SearchResult(bestMove, bestEval, debugPositionsEvaluated);
    }

    public static int evaluate(Board position, int depth) {
        if (depth == 0) {
            return StaticEval.evaluate(position);
        }

        ArrayList<Move> moves = position.getLegalMovesForSideToMove();
        if (position.isCheckmate(position.getSideToMove())) {
            return Integer.MIN_VALUE;
        }
        // TODO: Draw detection

        int bestEval = Integer.MIN_VALUE;

        for (Move move : moves) {
            position.makeMove(move);
            int evalAfterMove = -evaluate(position, depth - 1);
            debugPositionsEvaluated++;
            bestEval = Math.max(evalAfterMove, bestEval);
            position.unmakeMove();
        }

        return bestEval;
    }
}
