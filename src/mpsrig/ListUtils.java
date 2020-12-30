package mpsrig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ListUtils {
    public static <T, R> ArrayList<R> map(Collection<T> input, Function<? super T, ? extends R> mapper) {
        var out = new ArrayList<R>(input.size());
        for (var elem : input) {
            out.add(mapper.apply(elem));
        }
        return out;
    }
}
