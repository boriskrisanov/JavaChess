package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

public class Search {
    private static long debugPositionsEvaluated = 0;
    // +1 and -1 to avoid overflow when multiplying by -1
    private static final int POSITIVE_INFINITY = Integer.MAX_VALUE - 1;
    private static final int NEGATIVE_INFINITY = Integer.MIN_VALUE + 1;
    private final static boolean USE_CACHE = false;
    private static volatile boolean stopSearch = false;

    public static synchronized void stop() {
        stopSearch = true;
    }

    public static boolean wasInterrupted() {
        return stopSearch;
    }

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
        stopSearch = false;
        debugPositionsEvaluated = 0;
        EvalCache.clearDebugStats();

        Move bestMove = null;
        int bestEval = NEGATIVE_INFINITY;

        var moves = board.getLegalMovesForSideToMove();

        // TODO: Use alpha beta pruning at root node
        for (Move move : moves) {
            if (stopSearch) {
                break;
            }
            board.makeMove(move);
            int eval = -evaluate(board, depth - 1, NEGATIVE_INFINITY, POSITIVE_INFINITY);
            if (eval >= bestEval) {
                bestEval = eval;
                bestMove = move;
            }
            board.unmakeMove();
        }

        return new SearchResult(bestMove, bestEval, debugPositionsEvaluated);
    }

    public static int evaluate(Board board, int depth, int alpha, int beta) {
        if (depth == 0) {
            debugPositionsEvaluated++;
            return StaticEval.evaluate(board) * (board.getSideToMove() == WHITE ? 1 : -1);
        }

        var moves = board.getLegalMovesForSideToMove();

        if (moves.isEmpty()) {
            if (board.isDraw()) {
                return 0;
            }
            if (board.isCheck()) {
                return NEGATIVE_INFINITY;
            }
        }

        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());

        for (Move move : moves) {
            if (stopSearch) {
                break;
            }
            board.makeMove(move);
            int eval = -evaluate(board, depth - 1, -beta, -alpha);
            if (eval >= beta) {
                board.unmakeMove();
                return beta;
            }
            alpha = Math.max(alpha, eval);
            board.unmakeMove();
        }

        return alpha;
    }
}
