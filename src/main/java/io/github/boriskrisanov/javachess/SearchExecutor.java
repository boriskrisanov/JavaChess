package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.concurrent.*;

public class SearchExecutor implements Callable<MoveEval> {
    private Board board;
    private Move move;
    private int depth;
    private boolean maximizingPlayer;

    public SearchExecutor(Board board, Move move, int depth, boolean maximizingPlayer) {
        this.board = board;
        this.move = move;
        this.depth = depth;
        this.maximizingPlayer = maximizingPlayer;
    }

    @Override
    public MoveEval call() throws Exception {
        board.makeMove(move);
        int eval = Search.evaluate(board, depth - 1, maximizingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
        board.unmakeMove();
        return new MoveEval(move, eval);
    }
}
