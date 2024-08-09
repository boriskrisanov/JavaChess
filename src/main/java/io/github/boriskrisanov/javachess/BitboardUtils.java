package io.github.boriskrisanov.javachess;

import java.util.*;

public class BitboardUtils {
    /**
     * Returns a bitboard with the selected square as 1 and all other squares as 0
     */
    public static long withSquare(int index) {
        return 1L << (63 - index);
    }

    public static ArrayList<Integer> squaresOf(long bitboard) {
        var squares = new ArrayList<Integer>();

        for (int i = 0; i < 64; i++) {
            if ((bitboard & 1) == 1) {
                squares.add(63 - i);
            }
            bitboard >>= 1;
        }

        return squares;
    }

    // Used for debug renderers
    public static String bitboardToString(long bitboard) {
        StringBuilder string = new StringBuilder();
        String bitboardString = Long.toBinaryString(bitboard);

        for (int i = 0; i < 64 - bitboardString.length(); i++) {
            string.append('0');
            if ((string.length() + 1) % 9 == 0) {
                string.append('\n');
            }
        }
        for (int i = 0; i < bitboardString.length(); i++) {
            string.append(bitboardString.charAt(i));
            if ((string.length() + 1) % 9 == 0) {
                string.append("\n");
            }
        }

        return string.toString();
    }

    /**
     * Computes a list of all possible blocker configuration bitboards for a given piece position
     * TODO: Write better comment
     */
    public static List<Long> computePossibleBlockerPositions(int pieceIndex, long blockerMask) {
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
}
