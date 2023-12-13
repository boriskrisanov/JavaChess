package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;
import java.util.concurrent.*;

public class Search {
    private static long debugPositionsEvaluated = 0;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(12);
    public static final boolean USE_CACHE = true;

    private static int moveScore(Board board, Move move) {
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

    public static SearchResult bestMove(Board board, int depth) throws InterruptedException, ExecutionException {
        debugPositionsEvaluated = 0;

        Move bestMove = null;
        int bestEval;
        boolean maximizingPlayer = board.getSideToMove() == Piece.Color.WHITE;

        var moves = board.getLegalMovesForSideToMove();
        // moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move)).reversed());

        var executors = new ArrayList<SearchExecutor>();

        for (Move move : moves) {
            Board boardCopy = new Board(board.getFen());
            var executor = new SearchExecutor(boardCopy, move, depth - 1, !maximizingPlayer);
            executors.add(executor);
        }

        List<Future<MoveEval>> evaluations = threadPool.invokeAll(executors);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Future<MoveEval> eval : evaluations) {
                if (eval.get().eval() > maxEval) {
                    maxEval = eval.get().eval();
                    bestMove = eval.get().move();
                }
            }
            bestEval = maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Future<MoveEval> eval : evaluations) {
                if (eval.get().eval() < minEval) {
                    minEval = eval.get().eval();
                    bestMove = eval.get().move();
                }
            }
            bestEval = minEval;
        }

        return new SearchResult(bestMove, bestEval, debugPositionsEvaluated);
    }

    public static int evaluate(Board board, int depth, boolean maximizingPlayer, int alpha, int beta, EvalCache cache) {
        if (depth == 0) {
            long hash = Hash.hash(board);
            if (cache.hasEntry(hash) && USE_CACHE) {
                return cache.get(hash);
            } else {
                debugPositionsEvaluated++;
                int eval = StaticEval.evaluate(board);
                if (USE_CACHE) {
                    cache.put(hash, eval);
                }
                return eval;
            }
//            return evaluateCaptures(board, maximizingPlayer, alpha, beta);
        }

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

        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move)).reversed());

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, false, alpha, beta, cache);
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

            for (Move move : moves) {
                board.makeMove(move);
                int eval = evaluate(board, depth - 1, true, alpha, beta, cache);
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

    private static int evaluateCaptures(Board board, boolean maximizingPlayer, int alpha, int beta) {
        ArrayList<Move> captures = board.getCapturesForSideToMove();

        if (captures.isEmpty()) {
            debugPositionsEvaluated++;
            return StaticEval.evaluate(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            var moves = board.getCapturesForSideToMove();
            moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move)).reversed());
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
            moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move)).reversed());
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
