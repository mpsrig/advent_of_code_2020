package mpsrig;

import java.util.HashMap;
import java.util.Map;

public class Deduplicator<T> {
    private final Map<T, T> cache;

    public Deduplicator() {
        cache = new HashMap<>();
    }

    public T get(T val) {
        var cached = cache.putIfAbsent(val, val);
        if (cached != null) {
            return cached;
        }
        return val;
    }

    @SafeVarargs
    public final void seed(T... elems) {
        for (T elem : elems) {
            get(elem);
        }
    }
}
