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
    private final static boolean USE_CACHE = true;
    private static volatile boolean stopSearch = false;

    public static synchronized void stop() {
        stopSearch = true;
    }

    public static boolean wasInterrupted() {
        return stopSearch;
    }

    private static int moveScore(Board board, Move move, Piece.Color side) {
        int score = 0;
        Piece piece = board.getPieceOn(move.start());
        Piece capturedPiece = move.capturedPiece();

        // Moving a piece to an empty square defended by an enemy pawn is likely a bad move
        if (board.isSquareEmpty(move.destination()) && !(piece instanceof Pawn)) {
            long pawnAttackingSquares = side == WHITE ? board.getBlackPawnAttackingSquares() : board.getWhitePawnAttackingSquares();
            if ((pawnAttackingSquares & BitboardUtils.withSquare(move.destination())) == 0) {
                score -= 1000;
            }
        }

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

        Move bestMove = null;
        int bestEval = NEGATIVE_INFINITY;

        var moves = board.getLegalMovesForSideToMove();

        // TODO: Use alpha beta pruning at root node
        for (Move move : moves) {
            if (stopSearch) {
                break;
            }
            board.makeMove(move);
            int eval = -evaluate(board, depth - 1, 1, NEGATIVE_INFINITY, POSITIVE_INFINITY, 0);
            if (eval >= bestEval) {
                bestEval = eval;
                bestMove = move;
            }
            board.unmakeMove();
        }

        return new SearchResult(bestMove, bestEval, debugPositionsEvaluated);
    }

    public static int evaluate(Board board, int depth, int ply, int alpha, int beta, int extensionCount) {
        boolean shouldCache = false;
        EvalCache.NodeKind nodeKind = EvalCache.NodeKind.UPPER;
        long hash = Hash.hash(board);

        if (USE_CACHE) {
            var existingEntry = EvalCache.get(hash);
            if (existingEntry != null && existingEntry.depth() < ply) {
                if (existingEntry.kind() == EvalCache.NodeKind.EXACT) {
                    return existingEntry.eval();
                }
                if (existingEntry.kind() == EvalCache.NodeKind.UPPER && existingEntry.eval() <= alpha) {
                    return alpha;
                }
                if (existingEntry.kind() == EvalCache.NodeKind.LOWER && existingEntry.eval() >= beta) {
                    return beta;
                }
            } else {
                shouldCache = true;
            }
        }

        if (depth == 0) {
            return evaluateCaptures(board, alpha, beta);
        }

        var moves = board.getPseudoLegalMoves();
        int moveCount = moves.size();

        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());

        for (Move move : moves) {
            if (stopSearch) {
                break;
            }
            if (!board.isPseudoLegalMoveLegal(move)) {
                moveCount--;
                continue;
            }
            board.makeMove(move);
            int extension = 0;
            if (extensionCount <= 5) {
                if (board.isCheck()) {
                    extension = 1;
                }
            }
            int eval = -evaluate(board, depth - 1 + extension, ply + 1, -beta, -alpha, extensionCount + extension);
            board.unmakeMove();
            if (eval >= beta) {
                if (shouldCache && !stopSearch) {
                    EvalCache.put(hash, EvalCache.NodeKind.LOWER, ply, beta);
                }
                return beta;
            }
            if (eval > alpha) {
                nodeKind = EvalCache.NodeKind.EXACT;
                alpha = eval;
            }
        }

        if (moveCount == 0) {
            if (board.isDraw()) {
                if (shouldCache) {
                    EvalCache.put(hash, EvalCache.NodeKind.EXACT, ply, 0);
                }
                return 0;
            }
            if (board.isCheck()) {
                // Checkmates closer to the root are better, so they should have a lower score
                // Not doing this causes the engine to make draws and not play the best move, even if it knows that it
                // can be played.
                int mateEval = NEGATIVE_INFINITY + 255 - depth;
                if (shouldCache) {
                    EvalCache.put(hash, EvalCache.NodeKind.EXACT, ply, mateEval);
                }
                return mateEval;
            }
        }

        if (shouldCache && !stopSearch) {
            EvalCache.put(hash, nodeKind, ply, alpha);
        }

        return alpha;
    }

    private static int evaluateCaptures(Board board, int alpha, int beta) {
        int eval = StaticEval.evaluate(board);
        if (eval >= beta) {
            return beta;
        }
        alpha = Math.max(alpha, eval);

        var moves = board.getPseudoLegalCaptures();

        moves.sort(Comparator.comparingInt(move -> moveScore(board, (Move) move, board.getSideToMove())).reversed());

        for (Move move : moves) {
            if (stopSearch) {
                break;
            }
            if (!board.isPseudoLegalMoveLegal(move)) {
                continue;
            }

            board.makeMove(move);
            eval = -evaluateCaptures(board, -beta, -alpha);
            board.unmakeMove();

            if (eval >= beta) {
                return beta;
            }
            alpha = Math.max(alpha, eval);
        }

        return alpha;
    }
}
