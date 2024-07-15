package io.github.boriskrisanov.javachess.piece;

import java.lang.reflect.*;
import java.util.*;

public class MagicBitboard {
    private final Random random = new Random();
    private static final int DEFAULT_SHIFT = 48;
    private boolean shouldStop = false;
    private final long[] magics = new long[64];
    // Stores the largest known shift value for each square
    private final long[] shifts = new long[64];
    private static final boolean[] foundMagics = new boolean[64];
    private final List<List<Long>> _possibleBlockerPositions;
    private final ArrayList<Thread> searchThreads = new ArrayList<>();;

    public MagicBitboard(List<List<Long>> possibleBlockerPositions) {
        this._possibleBlockerPositions = possibleBlockerPositions;
        // Subtract 1 because findMagics() will start with this value + 1
        Arrays.fill(shifts, DEFAULT_SHIFT - 1);
    }

    public record Magics(long[] magics, long[] shifts) {
    }

    /**
     * For each square, find a unique magic and shift value that allows for a unique mapping between the 64-bit bitboard
     * and a smaller value, which can be used as an array index in an array of attacking squares. The aim is to maximise
     * the shift in order to minimise the range of indexes and hence the array size.
     */
    public void findMagics(int numThreads) {
        // Stores which magic indexes a thread should generate
//        ArrayList<ArrayList<Integer>> threadMagicIndexes = new ArrayList<>();
//        ArrayList<Thread> threads = new ArrayList<>();

//        for (int i = 0; i < numThreads; i++) {
//            threadMagicIndexes.add(new ArrayList<>());
//        }

        // Assign magic indexes to threads
//        for (int i = 0; i < 63; i++) {
//            threadMagicIndexes.get(i % numThreads).add(i);
//        }
        var searchThread = new Thread(() -> {
            ArrayList<Long> usedKeys = new ArrayList<>();
            boolean allMagicsFound = false;
            while (!shouldStop || !allMagicsFound) {
                for (int i = 0; i < 64; i++) {
                    usedKeys.clear();
                    var possibleBlockerPositions = _possibleBlockerPositions.get(i);
                    boolean collision = false;
                    long magic = random.nextLong();
                    for (long blockerPositions : possibleBlockerPositions) {
                        // shifts[i] is the best current value, so add 1 to search for a larger shift
                        long key = Math.abs((blockerPositions * magic) >> shifts[i] + 1);
                        if (usedKeys.contains(key)) {
                            collision = true;
                            break;
                        }
                        usedKeys.add(key);
                    }
                    if (!collision) {
                        // Found magic
                        System.out.println("Found magic " + magic + " shift " + shifts[i]);
                        foundMagics[i] = true;
                        magics[i] = magic;
                        shifts[i]++;
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
        searchThreads.add(searchThread);
        searchThread.start();

//        for (int threadNumber = 0; threadNumber < numThreads; threadNumber++) {
//            int finalThreadNumber = threadNumber;
//            threads.add(new Thread(() -> {
//                while (!shouldStop) {
//                    for (int magicIndex : threadMagicIndexes.get(finalThreadNumber)) {
//                        var possibleBlockerPositions = _possibleBlockerPositions.get(magicIndex);
//                        ArrayList<Integer> usedKeys = new ArrayList<>();
//                        boolean collision = false;
//                        long magic = random.nextLong();
//                        for (long blockerPositions : possibleBlockerPositions) {
//                            int key = (int) Math.abs((blockerPositions * magic) >> shifts[magicIndex] + 1);
//                            if (usedKeys.contains(key)) {
//                                collision = true;
//                                break;
//                            }
//                            usedKeys.add(key);
//                        }
//                        if (!collision) {
//                            // Found magic
//                            System.out.println("Found magic " + magic + " shift " + shifts[magicIndex]);
//                            magics[magicIndex] = magic;
//                            shifts[magicIndex]++;
//                        }
//                    }
//                }
//            }));
//        }

//        for (Thread thread : threads) {
//            searchThreads.add(thread);
//            thread.start();
//        }
    }

    public Magics stop() throws InterruptedException {
        shouldStop = true;
        for (Thread thread : searchThreads) {
            thread.join();
        }
        return new Magics(magics, shifts);
    }
}
