package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;
import java.util.concurrent.*;

class Node {
    public long wins = 0;
    public long losses = 0;
    public long draws = 0;
    public long visits = 0;
}

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Board board = new Board();
        Hash.init();
        var scanner = new Scanner(System.in);
        String[] command;
        String commandString;
        boolean isUciMode = true;
        System.out.println("JavaChess version 0.0.1");
        do {
            commandString = scanner.nextLine();
            command = commandString.split(" ");
            if (command.length == 0) {
                continue;
            }

            switch (command[0]) {
                // Standard UCI commands
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
                        long startTime = System.currentTimeMillis();
                        bestMove = Search.bestMove(board, depth);
                        long endTime = System.currentTimeMillis();
                        System.out.println("debug: search took " + (endTime - startTime) + " ms");
                    }
                    if (isUciMode) {
                        System.out.println("bestmove " + bestMove.bestMove());
                    } else {
                        String promotion = "";
                        if (bestMove.bestMove().promotion() != null) {
                            promotion = "=" + bestMove.bestMove().promotion();
                        }
                        System.out.println("bestmove " + bestMove.bestMove().start() + ":" + bestMove.bestMove().destination() + promotion);
                    }
                    System.out.println("eval " + ((double) bestMove.eval()) / 100);
                }
                case "d" -> {
                    System.out.println(board);
                    System.out.println("FEN: " + board.getFen());
                    System.out.println("Hash: " + HexFormat.of().toHexDigits(Hash.hash(board)));
                }
                case "setoption" -> {
                    // setoption name <name> value <value>
                    if (command[2].equals("uciMode")) {
                        // TODO: Better error handling
                        isUciMode = command[4].equals("true");
                    }
                }
                // Custom Non-UCI commands
                case "legal_moves" -> {
                    // start:end,end,end=promotion
                    StringBuilder output = new StringBuilder();

                    HashMap<Integer, ArrayList<Move>> legalMovesFromSquares = new HashMap<>();
                    for (Move move : board.getLegalMovesForSideToMove()) {
                        legalMovesFromSquares.computeIfAbsent(move.start(), key -> new ArrayList<>());
                        legalMovesFromSquares.get(move.start()).add(move);
                    }

                    legalMovesFromSquares.forEach((startSquare, moves) -> {
                        output.append(startSquare).append(":");
                        for (Move move : moves) {
                            output.append(move.destination());
                            if (move.promotion() != null) {
                                output.append("=").append(move.toUciString().charAt(4));
                            }
                            output.append(",");
                        }
                        // Remove trailing comma
                        output.deleteCharAt(output.length() - 1);
                        output.append(" ");
                    });
                    // Remove trailing space
                    output.deleteCharAt(output.length() - 1);

                    System.out.println("legal_moves " + output);
                }
                case "get_position" -> {
                    StringBuilder output = new StringBuilder();
                    for (int i = 0; i < 64; i++) {
                        if (board.getPieceOn(i) == null) {
                            continue;
                        }
                        output
                                .append(i)
                                .append(":")
                                .append(board.getPieceOn(i).getChar())
                                .append(" ");
                    }
                    // Remove trailing space
                    output.deleteCharAt(output.length() - 1);
                    System.out.println("position " + output);
                    int checkIndex = board.isCheck() ? board.getKingPosition(board.getSideToMove()) : -1;
                    System.out.println("check " + checkIndex);
                }
                case "make_move" -> {
                    // start:end=promotion
                    String move = command[1];
                    int start = Integer.parseInt(move.split(":")[0]);
                    int end = Integer.parseInt(move.split(":")[1].split("=")[0]);
                    String promotion = "";
                    if (move.contains("=")) {
                        promotion = move.split("=")[1];
                    }

                    board.makeMove(new Square(start).toString() + new Square(end) + promotion);
                }
                case "mcts" -> {
                    HashMap<Long, Node> nodes = new HashMap<>();
                    int n = Integer.parseInt(command[1]);
                    int whiteWins = 0;
                    int blackWins = 0;
                    int draws = 0;
                    final Piece.Color side = board.getSideToMove();
                    for (int i = 0; i < n; i++) {
                        ArrayList<Long> visitedNodes = new ArrayList<>();
                        Board board2 = new Board(board.getFen());
                        long lastNode = Hash.hash(board2);
                        while (true) {
                            if (!nodes.containsKey(lastNode)) {
                                nodes.put(lastNode, new Node());
                            }
                            nodes.get(lastNode).visits++;
                            if (board2.isCheckmate(Piece.Color.WHITE)) {
                                blackWins++;
                                if (side == Piece.Color.WHITE) {
                                    nodes.get(lastNode).losses++;
                                    for (long nodeHash : visitedNodes) {
                                        nodes.get(nodeHash).losses++;
                                    }
                                } else {
                                    nodes.get(lastNode).wins++;
                                    for (long nodeHash : visitedNodes) {
                                        nodes.get(nodeHash).wins++;
                                    }
                                }
                                break;
                            }
                            if (board2.isCheckmate(Piece.Color.BLACK)) {
                                whiteWins++;
                                if (side == Piece.Color.WHITE) {
                                    nodes.get(lastNode).wins++;
                                    for (long nodeHash : visitedNodes) {
                                        nodes.get(nodeHash).wins++;
                                    }
                                } else {
                                    nodes.get(lastNode).losses++;
                                    for (long nodeHash : visitedNodes) {
                                        nodes.get(nodeHash).losses++;
                                    }
                                }
                                break;
                            }
                            if (board2.isDraw()) {
                                draws++;
                                nodes.get(lastNode).draws++;
                                for (long nodeHash : visitedNodes) {
                                    nodes.get(nodeHash).draws++;
                                }
                                break;
                            }
                            var moves = board2.getLegalMovesForSideToMove();
                            double maxScore = Integer.MIN_VALUE + 1;
                            Move bestMove = moves.getFirst();
                            for (Move move : moves) {
                                board2.makeMove(move);
                                long hash = Hash.hash(board2);
                                if (!nodes.containsKey(hash)) {
                                    nodes.put(hash, new Node());
                                }
                                nodes.get(hash).visits++;
                                double winRatio = (double) nodes.get(hash).wins / nodes.get(hash).visits;
                                double lnParentVisitCount = Math.log(nodes.get(lastNode).visits);
                                double score = winRatio + Math.sqrt(5) * Math.sqrt(lnParentVisitCount / (nodes.get(hash).visits));
                                board2.unmakeMove();
                                if (score > maxScore) {
                                    maxScore = score;
                                    bestMove = move;
                                }
                            }
                            visitedNodes.add(Hash.hash(board2));
                            lastNode = Hash.hash(board2);
                            board2.makeMove(bestMove);
                        }
                    }
                    System.out.println("W: " + whiteWins);
                    System.out.println("B: " + blackWins);
                    System.out.println("D: " + draws);
                    System.out.println("P(W) = " + (double) whiteWins / n);
                    System.out.println("P(L) = " + (double) blackWins / n);
                    System.out.println("P(D) = " + (double) draws / n);

                    long maxVisits = 0;
                    Move bestMove = null;
                    for (Move move : board.getLegalMovesForSideToMove()) {
                        board.makeMove(move);
                        long hash = Hash.hash(board);
                        board.unmakeMove();
                        if (nodes.get(hash).visits > maxVisits) {
                            maxVisits = nodes.get(hash).visits;
                            bestMove = move;
                        }
                    }
                    System.out.println("bestmove " + bestMove);
                }
                case "find_magics" -> {
                    var rookMagicBitboard = Rook.getMagicBitboard();
                    rookMagicBitboard.findMagics();

                    var bishopMagicBitboard = Bishop.getMagicBitboard();
                    bishopMagicBitboard.findMagics();

                    Thread.sleep(Integer.parseInt(command[1]) * 1000L);

                    var rookMagics = rookMagicBitboard.stop();
                    var bishopMagics = bishopMagicBitboard.stop();

                    var rookCodeString = new StringBuilder();
                    var bishopCodeString = new StringBuilder();

                    rookCodeString.append("final long[] ROOK_MAGICS = {");
                    for (long magic : rookMagics.magics()) {
                        rookCodeString
                                .append("0x")
                                .append(Long.toHexString(magic))
                                .append("L")
                                .append(", ");
                    }
                    rookCodeString.deleteCharAt(rookCodeString.length() - 1);
                    rookCodeString.deleteCharAt(rookCodeString.length() - 1);
                    rookCodeString.append("};\n");
                    rookCodeString.append("final long[] ROOK_SHIFTS = {");
                    for (int shift : rookMagics.shifts()) {
                        rookCodeString
                                .append(shift)
                                .append(", ");
                    }
                    rookCodeString.deleteCharAt(rookCodeString.length() - 1);
                    rookCodeString.deleteCharAt(rookCodeString.length() - 1);
                    rookCodeString.append("};");

                    System.out.println(rookCodeString);

                    bishopCodeString.append("final long[] BISHOP_MAGICS = {");
                    for (long magic : bishopMagics.magics()) {
                        bishopCodeString
                                .append("0x")
                                .append(Long.toHexString(magic))
                                .append("L")
                                .append(", ");
                    }
                    bishopCodeString.deleteCharAt(bishopCodeString.length() - 1);
                    bishopCodeString.deleteCharAt(bishopCodeString.length() - 1);
                    bishopCodeString.append("};\n");
                    bishopCodeString.append("final long[] BISHOP_SHIFTS = {");
                    for (int shift : bishopMagics.shifts()) {
                        bishopCodeString
                                .append(shift)
                                .append(", ");
                    }
                    bishopCodeString.deleteCharAt(bishopCodeString.length() - 1);
                    bishopCodeString.deleteCharAt(bishopCodeString.length() - 1);
                    bishopCodeString.append("};");

                    System.out.println(bishopCodeString);
                }

            }
        } while (!command[0].equals("quit"));
    }
}
