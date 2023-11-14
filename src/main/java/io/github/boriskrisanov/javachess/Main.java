package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board("rnbqkbnr/ppp1pppp/3p4/8/4P1Q1/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2");
        System.out.println(Search.bestMove(board, 6));
    }
}
