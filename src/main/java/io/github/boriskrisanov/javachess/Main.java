package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Board board = new Board();
        Hash.init();
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
                    board.getMoveHistoryStack().clear();
                    board.boardHistory.clear();
                    board.hashHistory.clear();
                    String type = command[1];
                    if (type.equals("fen")) {
                        board.loadFen(commandString.split("fen")[1].trim());
                        // TODO: Support moves after FEN
                    } else if (type.equals("startpos")) {
                        board.loadStartingPosition();
                        if (command.length > 3 && command[2].equals("moves")) {
                            String[] moves = commandString.split("moves")[1].trim().split(" ");
                            for (String move : moves) {
                                board.makeMove(move);
                            }
                        }
                    }
                }
                case "go" -> {
                    EvalCache.clearDebugStats();
                    int searchTimeMilliseconds = -1;
                    int depth = 5;
                    if (command.length > 1) {
                        if (command[1].equals("depth")) {
                            depth = Integer.parseInt(command[2]);
                        }
                        if (command[1].equals("time")) {
                            // This is not a standard UCI command, but it will be used for testing
                            // TODO: Improve iterative deepening
                            searchTimeMilliseconds = Integer.parseInt(command[2]);
                        }
                    }
                    SearchResult bestMove;
                    if (searchTimeMilliseconds != -1) {
                        bestMove = TimeLimitedSearch.bestMove(board, searchTimeMilliseconds);
                    } else {
                        bestMove = Search.bestMove(board, depth);
                    }
                    System.out.println("bestmove " + bestMove.bestMove());
                    System.out.println("eval " + bestMove.eval());
                }
                case "d" -> {
                    System.out.println(board);
                    System.out.println("FEN: " + board.getFen());
                    System.out.println("Hash: " + HexFormat.of().toHexDigits(Hash.hash(board)));
                }
            }

        } while (!command[0].equals("quit"));
    }
}
