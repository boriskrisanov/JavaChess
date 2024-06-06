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

            for (long blockerPositions : possibleBlockerPositions) {
                ATTACKING_SQUARES[rookIndex].put(blockerPositions, SlidingPiece.getAttackingSquares2(blockerPositions, rookIndex, new Direction[]{UP, DOWN, LEFT, RIGHT}));
            }
        }
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
