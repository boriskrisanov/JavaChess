package io.github.boriskrisanov.javachess.piece;

import java.util.*;

public class MagicBitboard {
    private final Random random = new Random();
    private static final int DEFAULT_SHIFT = 48;
    private boolean shouldStop = false;
    private final long[] magics = new long[64];
    // Stores the largest known shift value for each square
    private final int[] shifts = new int[64];
    private static final boolean[] foundMagics = new boolean[64];
    private final List<List<Long>> _possibleBlockerPositions;
    private Thread searchThread;

    public MagicBitboard(List<List<Long>> possibleBlockerPositions) {
        this._possibleBlockerPositions = possibleBlockerPositions;
        // Subtract 1 because findMagics() will start with this value + 1
        Arrays.fill(shifts, DEFAULT_SHIFT - 1);
    }

    public record Magics(long[] magics, int[] shifts) {
    }

    /**
     * For each square, find a unique magic and shift value that allows for a unique mapping between the 64-bit bitboard
     * and a smaller value, which can be used as an array index in an array of attacking squares. The aim is to maximise
     * the shift in order to minimise the range of indexes and hence the array size.
     */
    public void findMagics() {
        searchThread = new Thread(() -> {
            ArrayList<Long> usedKeys = new ArrayList<>();
            boolean allMagicsFound = false;
            while (!shouldStop || !allMagicsFound) {
                for (int i = 0; i < 64; i++) {
                    usedKeys.clear();
                    var possibleBlockerPositions = _possibleBlockerPositions.get(i);
                    boolean collision = false;
                    long magic = random.nextLong();
                    // shifts[i] is the best current value, so add 1 to search for a larger shift
                    int newShift = shifts[i] + 1;
                    for (long blockerPositions : possibleBlockerPositions) {
                        long key = Math.abs((blockerPositions * magic) >> newShift);
                        if (usedKeys.contains(key)) {
                            collision = true;
                            break;
                        }
                        usedKeys.add(key);
                    }
                    if (!collision) {
                        // Found magic
                        System.out.println("Found magic " + magic + " shift " + newShift);
                        foundMagics[i] = true;
                        magics[i] = magic;
                        shifts[i] = newShift;
                    }
                }
                if (!allMagicsFound) {
                    allMagicsFound = true;
                    for (boolean foundMagic : foundMagics) {
                        if (!foundMagic) {
                            allMagicsFound = false;
                            break;
                        }
                    }
                }
            }
        });
        searchThread.start();
    }

    public Magics stop() throws InterruptedException {
        shouldStop = true;
        searchThread.join();
        return new Magics(magics, shifts);
    }
}
