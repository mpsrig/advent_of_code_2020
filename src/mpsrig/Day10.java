package mpsrig;

import java.util.*;

public class Day10 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/10.txt", new Day10());
    }

    private List<Integer> sorted;

    @Override
    protected void init() {
        super.init();
        sorted = ListUtils.map(input, Integer::parseInt);
        sorted.sort(null);
        sorted.add(sorted.get(sorted.size() - 1) + 3);
    }

    @Override
    public Object computePart1() {
        int differenceOfOneCount = 0;
        int differenceOfThreeCount = 0;

        int cur = 0;
        for (int next : sorted) {
            int difference = next - cur;
            if (difference == 3) {
                differenceOfThreeCount++;
            } else if (difference == 1) {
                differenceOfOneCount++;
            } else {
                throw new IllegalStateException("Cannot calculate");
            }
            cur = next;
        }
        return differenceOfOneCount * differenceOfThreeCount;
    }

    private final Memoize<Long> m = new Memoize<>();

//    private final HashMap<List<Integer>, Long> numHits = new HashMap<>();

    private long countPossibleRemovalsCached(int idx, int previous) {
        var key = Arrays.asList(idx, previous);
//        numHits.compute(key, (k, v) -> {
//            return v == null ? 1 : v + 1;
//        });
        return m.getOrCompute(key, () -> countPossibleRemovals(idx, previous));
    }

    private long countPossibleRemovals(int idx, int previous) {
        if (idx + 1 == sorted.size()) {
            return 0;
        }
        long count = 0;
        if (sorted.get(idx + 1) - previous < 4) {
            // then idx could be removed
            count++;
            count += countPossibleRemovalsCached(idx + 1, previous);
        }
        count += countPossibleRemovalsCached(idx + 1, sorted.get(idx));
        return count;
    }

//    private void printNumHits() {
//        var sb = new StringBuilder();
//        sb.append("idx,previous,numHits,\n");
//        for (var elem : numHits.entrySet()) {
//            sb.append(elem.getKey().get(0));
//            sb.append(',');
//            sb.append(elem.getKey().get(1));
//            sb.append(',');
//            sb.append(elem.getValue());
//            sb.append('\n');
//        }
//        System.err.println(sb);
//    }

    @Override
    public Object computePart2() {
        long count = countPossibleRemovalsCached(0, 0);
//        printNumHits();
        return count + 1;
    }
}
