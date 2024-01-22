package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        var scanner = new Scanner(System.in);
        String[] command;
        String commandString;
        System.out.println("JavaChess version 0.0.1");
        do {
            commandString = scanner.nextLine();
            command = commandString.split(" ");
            if (command.length == 0) {
                continue;
            }

            switch (command[0]) {
                case "position" -> {
                    String type = command[1];
                    if (type.equals("fen")) {
                        board.loadFen(commandString.split("fen")[1].trim());
                        // TODO: Support moves after FEN
                    } else if (type.equals("startpos")) {
                        board.loadStartingPosition();
                        if (command[2].equals("moves")) {
                            String[] moves = commandString.split("moves")[1].trim().split(" ");
                            for (String move : moves) {
                                board.makeMove(move);
                            }
                        }
                    }
                }
                case "go" -> {
                    int depth = 5;
                    if (command.length > 1) {
                        if (command[1].equals("depth")) {
                            depth = Integer.parseInt(command[2]);
                        }
                    }
                    System.out.println("bestmove " + Search.bestMove(board, depth).bestMove());
                }
            }

        } while (!command[0].equals("quit"));
    }
}
