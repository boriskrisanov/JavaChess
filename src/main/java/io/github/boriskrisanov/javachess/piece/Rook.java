package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {
    /**
     * The value at index n is bitboard of possible positions where pieces that block the movement of a rook on square n
     * could be. This is equivalent to the rook's legal moves from square n without squares on the edge, since it is
     * assumed that a blocking piece can always be captured, and thus has no effect on the rook's legal moves if it is on
     * the edge.
     */
    private static final long[] ROOK_BLOCKER_MASKS = new long[64];
    /**
     * The nth element stores, for a rook at square index n, a map of blocker bitboards to attacking squares (legal moves)
     */
    @SuppressWarnings("unchecked")
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];
    private static final long[][] attackingSquaresArray = new long[64][];

    private static final long[] ROOK_MAGICS = {0x725fb52ab5ff6ff5L, 0x51db3dd9529f37adL, 0x55f48977fbd1ede2L, 0xb34899f821a40b01L, 0x6b4afbe3effff783L, 0xc0bc972e2f353227L, 0x3be3211176ea1943L, 0x861cf83d77a98e61L, 0x2cce6ce5e3078d0eL, 0x75c4b3a38f6fd9a8L, 0x5846eafdeff00b4aL, 0x1eeb78850ac865b0L, 0x47a6b168faa5f2f4L, 0x96bf018c01b3c407L, 0x92f12799ed9d830cL, 0x57cdafd005e164acL, 0xecd4e82ef96bcccaL, 0xc031f715863f718dL, 0x8a296292c1159fe2L, 0x1da9fa251efe3f3L, 0x898ba7cf3f1c03fcL, 0x81f012abc7297510L, 0xd35ba88330115de5L, 0xefbcc789e430e265L, 0xf4143c9e55024281L, 0xfddfe04a60a207d9L, 0x9d7569f10f865814L, 0xb7e3421cfcfcea7bL, 0x155508b88d07c2fbL, 0xbbbd764b989c060fL, 0x35f81fb29332df96L, 0xb9621f884bfc7fe4L, 0xeb3e2cc071afe487L, 0x93ecec21b1e987c4L, 0x38289ac00620d745L, 0x27422349035ebb04L, 0x281074ba96a207e2L, 0x2fedabbc2502db8fL, 0xd960a22c6f8efb6cL, 0x44b380001db8cbf1L, 0xd105a495687d92adL, 0xe9c55d1f6ca89bc0L, 0xef54ab36e5620bbL, 0x63e8ab1801a17a5aL, 0xefb70ad7e943d0b1L, 0x2d10e5f7c386ba24L, 0x3637897df281cc0dL, 0x671e003c0184c1afL, 0xdc24fcef4cd91b11L, 0x71a8f8b0a679867L, 0x60cc27fcfec26100L, 0x5c3463f7fecfd00cL, 0xb5ea8013537654fL, 0x948ff2ad44892ea0L, 0xb7d90ba70c93e910L, 0x8f5d865f5ea1c58eL, 0x54b9dfd12d29e8f1L, 0xdbad112286dd908fL, 0x33c435534e0bd25fL, 0x260d41fab79e8d14L, 0x47742adc0090b61aL, 0x64264112d3ba1375L, 0x73ac4bc1a30324acL, 0xae07761705aa2542L};
    private static final long[] ROOK_SHIFTS = {49, 50, 50, 49, 50, 50, 50, 49, 51, 51, 51, 51, 51, 51, 52, 51, 51, 52, 51, 50, 51, 51, 52, 50, 50, 51, 51, 51, 51, 51, 51, 50, 50, 51, 51, 51, 51, 51, 51, 50, 50, 51, 51, 51, 51, 51, 51, 50, 50, 51, 51, 51, 51, 51, 51, 50, 49, 50, 49, 49, 49, 49, 50, 49};

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

        for (int i = 0; i < 64; i++) {
            int maxIndex = 0;
            for (long blockerBitboard : ATTACKING_SQUARES[i].keySet()) {
                maxIndex = Math.max(maxIndex, index(i, blockerBitboard));
            }
            attackingSquaresArray[i] = new long[maxIndex + 1];

            int finalI = i;
            ATTACKING_SQUARES[i].forEach((blockerBitboard, attackingSquaresBitboard) -> {
                attackingSquaresArray[finalI][index(finalI, blockerBitboard)] = attackingSquaresBitboard;
            });
        }
    }

    private static int index(int i, long blockerBitboard) {
        return (int) Math.abs((blockerBitboard * ROOK_MAGICS[i]) >> ROOK_SHIFTS[i]);
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
    }

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        // Assume blocking pieces can be captured, then filter using friendly pieces bitboard later
        long blockers = board.getAllPieces() & ROOK_BLOCKER_MASKS[position];
        return attackingSquaresArray[position][index(position, blockers)] & ~board.getPieces(color);
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
