package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        BitboardUtils.squaresOf(
                0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L
        ).stream().map(Square::new).forEach(System.out::println);
//        Board board = new Board("rnbqkbnr/ppp1pppp/3p4/8/4P1Q1/8/PPPP1PPP/RNB1KBNR b KQkq - 1 2");
//        System.out.println(Search.bestMove(board, 9));
    }
}
