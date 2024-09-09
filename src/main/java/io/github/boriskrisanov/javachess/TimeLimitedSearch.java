package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;
import java.util.concurrent.*;

public class TimeLimitedSearch {
    public static SearchResult bestMove(Board board, long searchTimeMilliseconds) throws InterruptedException, ExecutionException {
        FutureTask<SearchResult> searchThread = new FutureTask<>(() -> {
            SearchResult bestMove = null;
            int currentDepth = 1;
            while (true) {
                System.out.println("depth " + currentDepth);
                SearchResult possibleBestMove = Search.bestMove(board, currentDepth);
                // Search is incomplete, so the results may be incorrect
                // TODO: Use partial search info for move ordering in the next search
                if (Search.wasInterrupted()) {
                    break;
                }
                bestMove = possibleBestMove;
                currentDepth++;
            }
            return bestMove;
        });


        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Search.stop();
                    }
                },
                searchTimeMilliseconds
        );

        searchThread.run();

        SearchResult bestMove = searchThread.get();

        if (bestMove == null) {
            throw new IllegalStateException("Did not have enough time to search to depth 1");
        }

        return bestMove;
    }
}
