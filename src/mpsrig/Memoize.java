package mpsrig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Memoize<T> {
    private final Map<Object, T> cache = new HashMap<>();

    public T getOrCompute(Object cacheKey, Supplier<T> computation) {
        Objects.requireNonNull(cacheKey);
        var cacheValue = cache.get(cacheKey);
        if (cacheValue != null) {
            return cacheValue;
        }
        var value = Objects.requireNonNull(computation.get());
        cache.put(cacheKey, value);
        return value;
    }

    @Override
    public String toString() {
        return "Memoize{" +
                "cache=" + cache +
                '}';
    }
}
