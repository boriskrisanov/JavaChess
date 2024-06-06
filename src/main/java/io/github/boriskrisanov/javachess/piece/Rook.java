package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {
    private static final long[] ROOK_BLOCKER_MASKS = new long[64];
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];

    static {
        for (int i = 0; i < 64; i++) {
            ATTACKING_SQUARES[i] = new HashMap<>();
        }

        for (int rookIndex = 0; rookIndex < 64; rookIndex++) {
            ROOK_BLOCKER_MASKS[rookIndex] = 0;

            for (int i = 0; i < 64; i++) {
                if (i != rookIndex && (Square.getRank(i) == Square.getRank(rookIndex) || Square.getFile(i) == Square.getFile(rookIndex))) {
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

    public static long[] findMagics(int minIterations) {
        Random random = new Random();
        long[] magics = new long[64];
        long[] bitCounts = new long[64];

        for (int rookIndex = 0; rookIndex < 64; rookIndex++) {
            var possibleBlockerPositions = computePossibleBlockerPositions(rookIndex);
            bitCounts[rookIndex] = 16;
            boolean foundMagic = false;
            for (int i = 0; i < minIterations || !foundMagic; i++) {
                ArrayList<Integer> usedKeys = new ArrayList<>();
                boolean collision = false;
                long magic = random.nextLong();
                for (long blockerPositions : possibleBlockerPositions) {
                    int key = (int) Math.abs((blockerPositions * magic) >> (64 - bitCounts[rookIndex]));
                    if (usedKeys.contains(key)) {
                        collision = true;
                        break;
                    }
                    usedKeys.add(key);
                }
                if (!collision) {
                    foundMagic = true;
                    magics[rookIndex] = magic;
                    bitCounts[rookIndex]--;
                }
            }
            System.out.println("Found magic for index " + rookIndex + ": " + Long.toHexString(magics[rookIndex]) + " (" + (bitCounts[rookIndex] + 1) + " bits)");
        }
        StringBuilder codeString = new StringBuilder();
        codeString.append("final long[] ROOK_MAGICS = {");
        for (long magic : magics) {
            codeString
                    .append("0x")
                    .append(Long.toHexString(magic))
                    .append(", ");
        }
        codeString.deleteCharAt(codeString.length() - 1);
        codeString.deleteCharAt(codeString.length() - 1);
        codeString.append("};");
        System.out.println(codeString);
        System.out.println("Total index bits: " + (Arrays.stream(bitCounts).sum() + 64));
        return magics;
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
