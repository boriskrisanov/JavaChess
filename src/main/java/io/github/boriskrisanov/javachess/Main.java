package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board("rnb1kbnr/pppp1ppp/8/4p1q1/4P3/3P4/PPP2PPP/RNBQKBNR w KQkq - 1 3");
        System.out.println(Search.bestMove(board, 4));
    }
}
