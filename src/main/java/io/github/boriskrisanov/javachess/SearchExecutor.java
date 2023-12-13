package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.concurrent.*;

public class SearchExecutor implements Callable<MoveEval> {
    private final Board board;
    private final Move move;
    private final int depth;
    private final boolean maximizingPlayer;
    private final EvalCache cache = new EvalCache();

    public SearchExecutor(Board board, Move move, int depth, boolean maximizingPlayer) {
        this.board = board;
        this.move = move;
        this.depth = depth;
        this.maximizingPlayer = maximizingPlayer;
        cache.clearDebugStats();
    }

    @Override
    public MoveEval call() throws Exception {
        board.makeMove(move);
        int eval = Search.evaluate(board, depth - 1, maximizingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE, cache);
        board.unmakeMove();
        return new MoveEval(move, eval);
    }
}
