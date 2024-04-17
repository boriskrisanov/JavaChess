package io.github.boriskrisanov.javachess.engine;

import io.github.boriskrisanov.javachess.engine.board.*;

import java.util.*;

public class BitboardUtils {
    public final static long[] FILES = new long[8];

    static {
        Arrays.fill(FILES, 0);
        for (int i = 0; i < 64; i++) {
            FILES[Square.getFile(i) - 1] |= withSquare(i);
        }
    }

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
}
