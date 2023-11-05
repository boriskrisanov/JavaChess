package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        Board board = new Board();

        while (!command.equals("quit")) {
            String[] line = scanner.nextLine().split(" ");
            command = line[0];

            switch (command) {
                case "get_position" -> System.out.println(board.getFen());
                case "make_move" -> board.makeMove(line[1]);
                case "unmake_move" -> board.unmakeMove();
                case "get_legal_moves" -> {
                    board.getLegalMovesForSideToMove().forEach(move -> {
                        System.out.print(move + " ");
                    });
                    System.out.println("\n");
                }
            }
        }
    }
}
