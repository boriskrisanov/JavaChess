package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;
import java.util.concurrent.*;

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
                    System.out.println("eval " + bestMove.standardEval());
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
                    if (board.isCheckmate(Piece.Color.WHITE)) {
                        System.out.println("mate w");
                    } else if (board.isCheckmate(Piece.Color.BLACK)) {
                        System.out.println("mate b");
                    } else if (board.isThreefoldRepetition()) {
                        System.out.println("draw threefold_repetition");
                    } else if (board.isInsufficientMaterial()) {
                        System.out.println("draw insufficient_material");
                    } else if (board.isStalemate()) {
                        System.out.println("draw stalemate");
                    } else if (board.getHalfMoveClock() >= 50) {
                        System.out.println("draw 50_move_rule");
                    }
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
                case "undo_move" -> board.unmakeMove();
                case "get_pgn" -> System.out.println("pgn " + board.getPgn());
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
