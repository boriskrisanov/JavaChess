package io.github.boriskrisanov.javachess;

public class EvalCache {
    public static final int MAX_ENTRIES = 1_000_000;
    private static final CacheEntry[] cache = new CacheEntry[MAX_ENTRIES];

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

        return value;
    }

    public static synchronized void put(long positionHash, NodeKind kind, int depth, int eval) {
        cache[index(positionHash)] = new CacheEntry(eval, kind, depth, positionHash);
    }
}
