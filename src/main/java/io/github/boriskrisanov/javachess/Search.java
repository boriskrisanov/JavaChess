package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;

public class Search {
    private static long debugPositionsEvaluated = 0;
    private final static boolean USE_CACHE = true;

    private static int moveScore(Board board, Move move, Piece.Color side) {
        if (USE_CACHE) {
            // If the position is in the cache, return its evaluation as the score
            // The exact values don't matter, only the relative order, as this determines which moves will be searched first
            board.makeMove(move);
            long hash = Hash.hash(board);
            var cachedEval = EvalCache.get(hash);
            board.unmakeMove();
            if (cachedEval.isPresent()) {
                return cachedEval.get().eval() * (side == Piece.Color.BLACK ? -1 : 1);
            }
        }

        int score = 0;
        Piece piece = board.getPieceOn(move.start());
        Piece capturedPiece = move.capturedPiece();

        // Capturing a high value piece with a low value piece is likely to be a good move
        if (capturedPiece != null) {
            score += (capturedPiece.getValue() - piece.getValue());
        }

        if (move.promotion() != null) {
            score += 500;
        }

        return score;
    }

    public static SearchResult bestMove(Board board, int depth) {
        debugPositionsEvaluated = 0;
        EvalCache.clearDebugStats();

        Move bestMove = null;
        int bestEval;
        boolean maximizingPlayer = board.getSideToMove() == Piece.Color.WHITE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        var moves = board.getLegalMovesForSideToMove();
        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
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
            for (Move move : moves) {
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

    public static int evaluate(Board board, int depth, boolean maximizingPlayer, int alpha, int beta) {
        boolean cacheEval = false;
        var hash = Hash.hash(board);

        if (USE_CACHE) {
            var cachedEval = EvalCache.get(hash);
            if (cachedEval.isPresent() && cachedEval.get().depth() >= depth) {
                return cachedEval.get().eval();
            } else {
                cacheEval = true;
            }
        }

        if (depth == 0) {
            debugPositionsEvaluated++;
            return StaticEval.evaluate(board);
        }
//            return evaluateCaptures(board, maximizingPlayer, alpha, beta);

        var moves = board.getLegalMovesForSideToMove();

        if (moves.isEmpty()) {
            if (board.isCheckmate(Piece.Color.WHITE)) {
                return Integer.MIN_VALUE;
            } else if (board.isCheckmate(Piece.Color.BLACK)) {
                return Integer.MAX_VALUE;
            } else {
                return 0;
            }
        }

        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }
                board.unmakeMove();
            }
            if (cacheEval) {
                EvalCache.put(hash, depth, maxEval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    board.unmakeMove();
                    break;
                }
                board.unmakeMove();
            }

            if (cacheEval) {
                EvalCache.put(hash, depth, minEval);
            }
            return minEval;
        }
    }

    private static int evaluateCaptures(Board board, boolean maximizingPlayer, int alpha, int beta) {
        ArrayList<Move> captures = board.getCapturesForSideToMove();

        if (captures.isEmpty()) {
            debugPositionsEvaluated++;
            return StaticEval.evaluate(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            var moves = board.getCapturesForSideToMove();
            moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());
            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluateCaptures(board, false, alpha, beta);
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
            var moves = board.getCapturesForSideToMove();
            moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());
            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluateCaptures(board, true, alpha, beta);
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
