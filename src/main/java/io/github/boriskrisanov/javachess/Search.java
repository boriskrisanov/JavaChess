package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

public class Search {
    private static long debugPositionsEvaluated = 0;

    public static SearchResult bestMove(Board board, int depth) {
        debugPositionsEvaluated = 0;

        Move bestMove = null;
        int bestEval;
        boolean maximizingPlayer = board.getSideToMove() == Piece.Color.WHITE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getLegalMovesForSideToMove()) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, false, alpha, beta);

                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }

                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }

                board.unmakeMove();
            }

            bestEval = maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getLegalMovesForSideToMove()) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, true, alpha, beta);

                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }

                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }

                board.unmakeMove();
            }

            bestEval = minEval;
        }

        return new SearchResult(bestMove, bestEval, debugPositionsEvaluated);
    }

//    public static int evaluate(Board position, int depth, int alpha, int beta) {
//        if (depth == 0) {
//            return StaticEval.evaluate(position);
//        }
//
//        ArrayList<Move> moves = position.getLegalMovesForSideToMove();
//        if (position.isCheckmate(position.getSideToMove())) {
//            return Integer.MIN_VALUE;
//        }
//        // TODO: Draw detection
//
//
//        for (Move move : moves) {
//            position.makeMove(move);
//            int eval = -evaluate(position, depth - 1, -beta, -alpha);
//            debugPositionsEvaluated++;
//            position.unmakeMove();
//            if (eval >= beta) {
//                return beta;
//            }
//            alpha = Math.max(alpha, eval);
//        }
//
//        return alpha;
//    }

    public static int evaluate(Board board, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0) {
            debugPositionsEvaluated++;
            return StaticEval.evaluate(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getLegalMovesForSideToMove()) {
                board.makeMove(move);
                debugPositionsEvaluated++;
                int eval = evaluate(board, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }
                board.unmakeMove();
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getLegalMovesForSideToMove()) {
                board.makeMove(move);
                debugPositionsEvaluated++;
                int eval = evaluate(board, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }
                board.unmakeMove();
            }
            return minEval;
        }
    }
}
