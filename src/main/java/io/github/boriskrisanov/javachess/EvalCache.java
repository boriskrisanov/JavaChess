package io.github.boriskrisanov.javachess;

import java.util.*;

public class EvalCache {
    public final long MAX_ENTRIES = 100_000;
    public final boolean DEBUG = true;
    private final HashMap<Long, Integer> cache = new HashMap<>();
    private long lastKey;
    private long debugInsertions = 0;
    private long debugEvictions = 0;
    private long debugHits = 0;
    private long debugMisses = 0;

    public boolean hasEntry(long positionHash) {
        boolean contains = cache.containsKey(positionHash);
        if (DEBUG) {
            if (contains) {
                debugHits++;
            } else {
                debugMisses++;
            }
        }
        return contains;
    }

    public int get(long positionHash) {
        return cache.get(positionHash);
    }

    public synchronized void put(long positionHash, int eval) {
        if (DEBUG) {
            debugInsertions++;
        }
        if (cache.size() >= MAX_ENTRIES) {
            cache.remove(lastKey); // TODO: Improve replacement strategy
            if (DEBUG) {
                debugEvictions++;
            }
        }
        cache.put(positionHash, eval);
        lastKey = positionHash;
    }

    public void clearDebugStats() {
        if (DEBUG) {
            debugInsertions = 0;
            debugEvictions = 0;
            debugHits = 0;
            debugMisses = 0;
        }
    }

    public long getDebugInsertions() {
        return debugInsertions;
    }

    public long getDebugEvictions() {
        return debugEvictions;
    }

    public long getDebugHits() {
        return debugHits;
    }

    public long getDebugMisses() {
        return debugMisses;
    }
}
