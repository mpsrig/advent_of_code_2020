package mpsrig;

import java.util.*;

public class SetUtils {
    public static <T> Set<T> intersection(Collection<? extends Collection<T>> inputs) {
//        System.err.println();
//        System.err.println("Start intersection");
//        System.err.println(inputs);
        ArrayList<Collection<T>> sorted = new ArrayList<>(inputs);
        sorted.sort((o1, o2) -> o1.size() - o2.size());
//        System.err.println(sorted);

        LinkedHashSet<T> out = null;
        for (Collection<T> elem : sorted) {
//            System.err.println("Top of loop: out = " + out + ", elem = " + elem);
            if (out == null) {
                out = new LinkedHashSet<>(elem);
                continue;
            }
            if (out.size() == 0) {
//                System.err.println("Returning empty set early");
                return out;
            }
            if (out.size() == 1 || elem.size() == 1 || elem instanceof HashSet) {
//                System.err.println("Fast path");
                // O(n) * O(1) = O(n)
                out.retainAll(elem);
            } else {
//                System.err.println("Slow path");
                // Will need to compare more than one, so let's make sure contains() is O(1)
                HashSet<T> elemSet = new HashSet<>(elem);
                out.retainAll(elemSet);
            }
        }
//        System.err.println("Returning out = " + out);
        return out;
    }

    public static <T> Set<T> union(Collection<? extends Collection<T>> inputs) {
        var out = new LinkedHashSet<T>();
        for (var elem : inputs) {
            out.addAll(elem);
        }
        return out;
    }
}
