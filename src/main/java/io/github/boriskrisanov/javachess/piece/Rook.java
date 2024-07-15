package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {
    /**
    The value at index n is bitboard of possible positions where pieces that block the movement of a rook on square n
    could be. This is equivalent to the rook's legal moves from square n without squares on the edge, since it is
    assumed that a blocking piece can always be captured, and thus has no effect on the rook's legal moves if it is on
    the edge.
    */
    private static final long[] ROOK_BLOCKER_MASKS = new long[64];
    /**
     * The nth element stores, for a rook at square index n, a map of blocker bitboards to attacking squares (legal moves)
     */
    @SuppressWarnings("unchecked")
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];

    static {
        for (int i = 0; i < 64; i++) {
            ATTACKING_SQUARES[i] = new HashMap<>();
        }

        for (int rookIndex = 0; rookIndex < 64; rookIndex++) {
            ROOK_BLOCKER_MASKS[rookIndex] = 0;

            for (int i = 0; i < 64; i++) {
                if (i != rookIndex && (Square.getRank(i) == Square.getRank(rookIndex) || Square.getFile(i) == Square.getFile(rookIndex))) {
                    // Don't add the square if it's on the edge
                    if ((Square.getFile(i) == 1 && Square.getFile(rookIndex) != 1) || (Square.getFile(i) == 8 && Square.getFile(rookIndex) != 8)) {
                        continue;
                    }
                    if ((Square.getRank(i) == 1 && Square.getRank(rookIndex) != 1) || (Square.getRank(i) == 8 && Square.getRank(rookIndex) != 8)) {
                        continue;
                    }

                    ROOK_BLOCKER_MASKS[rookIndex] |= BitboardUtils.withSquare(i);
                }
            }

            for (long blockerPositions : computePossibleBlockerPositions(rookIndex)) {
                ATTACKING_SQUARES[rookIndex].put(blockerPositions, SlidingPiece.getAttackingSquares2(blockerPositions, rookIndex, new Direction[]{UP, DOWN, LEFT, RIGHT}));
            }
        }
    }

    /**
     * Computes a list of all possible blocker configuration bitboards for a given rook position
     */
    private static List<Long> computePossibleBlockerPositions(int rookIndex) {
        long blockerMask = ROOK_BLOCKER_MASKS[rookIndex];

        ArrayList<Long> possibleBlockerPositions = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (((1L << i) & blockerMask) != 0) {
                indexes.add(63 - i);
            }
        }

        // 2^k possible blocker configurations
        int n = 1 << Long.bitCount(blockerMask);
        for (int configuration = 0; configuration < n; configuration++) {
            int j = 0;
            long finalConfig = 0;
            for (int i = 0; i < Long.bitCount(blockerMask); i++) {
                if (((configuration >> i) & 1) != 0) {
                    finalConfig |= (1L << (63 - indexes.get(j)));
                }
                j++;
            }
            possibleBlockerPositions.add(finalConfig);
        }

        return possibleBlockerPositions;
    }

    public static MagicBitboard getMagicBitboard() {
        List<List<Long>> possibleBlockerPositions = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            possibleBlockerPositions.add(computePossibleBlockerPositions(i));
        }

        return new MagicBitboard(possibleBlockerPositions);

//        StringBuilder codeString = new StringBuilder();
//        codeString.append("final long[] ROOK_MAGICS = {");
//        for (long magic : magics) {
//            codeString
//                    .append("0x")
//                    .append(Long.toHexString(magic))
//                    .append(", ");
//        }
//        codeString.deleteCharAt(codeString.length() - 1);
//        codeString.deleteCharAt(codeString.length() - 1);
//        codeString.append("};\n");
//        codeString.append("final long[] ROOK_SHIFTS = {");
//        for (long bitCount : bitCounts) {
//            codeString
//                    .append(64 - bitCount + 1)
//                    .append(", ");
//        }
//        codeString.deleteCharAt(codeString.length() - 1);
//        codeString.deleteCharAt(codeString.length() - 1);
//        codeString.append("};");
//        System.out.println(codeString);
//        System.out.println("Total index bits: " + (Arrays.stream(bitCounts).sum() + 64));
//        return magics;
    }

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        // Assume blocking pieces can be captured, then filter using friendly pieces bitboard later
        long blockers = board.getAllPieces() & ROOK_BLOCKER_MASKS[position];
        return ATTACKING_SQUARES[position].get(blockers) & ~board.getPieces(color);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public int getValue() {
        return 500;
    }
}
