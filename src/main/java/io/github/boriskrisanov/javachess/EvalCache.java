package io.github.boriskrisanov.javachess;

import java.util.*;
import java.util.concurrent.atomic.*;

public class EvalCache {
    public static final int MAX_ENTRIES = 1_000_000;
    public static final boolean DEBUG = true;
    private static final HashMap<Long, CacheEntry> cache = new HashMap<>(MAX_ENTRIES);
    private final static HashMap<Long, Long> keyUsages = new HashMap<>();
    private static long lastKey;
    private static long debugInsertions = 0;
    private static long debugEvictions = 0;
    private static AtomicLong debugHits = new AtomicLong(0);
    private static AtomicLong debugMisses = new AtomicLong(0);

    public record CacheEntry(int eval, int depth) {

    }

    public static Optional<CacheEntry> get(long positionHash) {
        Optional<CacheEntry> eval = Optional.ofNullable(cache.get(positionHash));
        if (DEBUG) {
            if (eval.isPresent()) {
                debugHits.incrementAndGet();
            } else {
                debugMisses.incrementAndGet();
            }
        }
        return eval;
    }

    public static synchronized void put(long positionHash, int depth, int eval) {
        if (DEBUG) {
            debugInsertions++;
        }
        if (cache.size() >= MAX_ENTRIES) {
            if (cache.get(positionHash) != null) {
                // This position is in the cache but the eval is from a lower depth, so we can remove the lower depth eval
                // This assumes that new entries will always have a higher depth than older ones (this is checked in the search before inserting a new entry)
//                cache.remove(positionHash);
            } else {
                // TODO: Replacement policy
            }
            if (DEBUG) {
                debugEvictions++;
            }
        }
        cache.put(positionHash, new CacheEntry(eval, depth));
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

    public static long getPercentUsed() {
        return cache.size() / MAX_ENTRIES * 100;
    }
}
