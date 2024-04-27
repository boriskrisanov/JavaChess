package io.github.boriskrisanov.javachess;

import java.util.*;
import java.util.concurrent.atomic.*;

public class EvalCache {
    public static final int MAX_ENTRIES = 100_000_000;
    public static final boolean DEBUG = false;
    private static final CacheEntry[] cache = new CacheEntry[MAX_ENTRIES];
    private static long debugInsertions = 0;
    private static long debugEvictions = 0;
    private static AtomicLong debugHits = new AtomicLong(0);
    private static AtomicLong debugMisses = new AtomicLong(0);

    public enum NodeKind {
        EXACT,
        UPPER,
        LOWER
    }

    public record CacheEntry(int eval, NodeKind kind, int depth, long hash) {
    }

    private static int index(long hash) {
        return (int) Math.abs(hash % MAX_ENTRIES);
    }

    public static CacheEntry get(long positionHash) {
        var value = cache[index(positionHash)];
        if (value != null && value.hash != positionHash) {
            // Index collision
            value = null;
        }

        if (DEBUG) {
            if (value != null) {
                debugHits.incrementAndGet();
            } else {
                debugMisses.incrementAndGet();
            }
        }

        return value;
    }

    public static synchronized void put(long positionHash, NodeKind kind, int depth, int eval) {
        if (DEBUG) {
            debugInsertions++;
        }

        cache[index(positionHash)] = new CacheEntry(eval, kind, depth, positionHash);
    }

    public static void clearDebugStats() {
        if (DEBUG) {
            debugInsertions = 0;
            debugEvictions = 0;
            debugHits.set(0);
            debugMisses.set(0);
        }
    }

    public static long getDebugInsertions() {
        return debugInsertions;
    }

    public static long getDebugEvictions() {
        return debugEvictions;
    }

    public static long getDebugHits() {
        return debugHits.get();
    }

    public static long getDebugMisses() {
        return debugMisses.get();
    }

    public static double getPercentUsed() {
        return 0;
        // TODO
//        return ((double) cache.size() / MAX_ENTRIES) * 100;
    }

    public static void clear() {
        Arrays.fill(cache, null);
    }
}
