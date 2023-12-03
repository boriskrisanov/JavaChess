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
                squares.add(i);
            }
            bitboard >>= 1;
        }

        return squares;
    }
}
