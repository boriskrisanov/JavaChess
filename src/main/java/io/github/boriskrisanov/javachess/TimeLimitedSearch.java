package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class TimeLimitedSearch {
    private static SearchResult bestMove;

    public static SearchResult bestMove(Board board, long searchTimeMilliseconds) throws InterruptedException {
        bestMove = null;
        Thread searchThread = new Thread(() -> {
            int currentDepth = 1;
            do {
                System.out.println("depth " + currentDepth);
                bestMove = Search.bestMove(board, currentDepth);
                currentDepth++;
            } while (!Search.wasInterrupted());
        });
        searchThread.start();

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Search.stop();
                    }
                },
                searchTimeMilliseconds
        );

        searchThread.join();

        if (bestMove == null) {
            throw new IllegalStateException("Did not have enough time to search to depth 1");
        }

        return bestMove;
    }
}
