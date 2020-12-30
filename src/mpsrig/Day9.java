package mpsrig;

import java.util.Collections;
import java.util.List;

public class Day9 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/9.txt", new Day9());
    }

    static final int PREAMBLE = 25;

    private List<Long> parsed;

    @Override
    protected void init() {
        super.init();
        parsed = ListUtils.map(input, Long::parseLong);
        for (var elem: parsed) {
            if (elem < 0) {
                throw new IllegalStateException();
            }
        }
    }

    static boolean isSumOfTwo(List<Long> input, long sum) {
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                var x = input.get(i);
                var y = input.get(j);
                if (x + y == sum) {
                    return true;
                }
            }
        }
        return false;
    }

    private Long part1Result;

    @Override
    public Object computePart1() {
        for (int i = PREAMBLE; i < parsed.size(); i++) {
            var current = parsed.get(i);
            var previousList = parsed.subList(i - PREAMBLE, i);
            if (!isSumOfTwo(previousList, current)) {
                part1Result = current;
                return current;
            }
        }
        return null;
    }

    private List<Long> subListThatSumsTo(long sum) {
        for (int i = 0; i < parsed.size() - 1; i++) {
            for (int j = i + 2; j <= parsed.size(); j++) {
                var subList = parsed.subList(i, j);
                if (subList.stream().mapToLong(l -> l).sum() == sum) {
                    return subList;
                }
            }
        }
        throw new IllegalStateException();
    }

    private List<Long> subListThatSumsToV2(long neededSum) {
        int begin = 0;
        long currentSum = parsed.get(0) + parsed.get(1);
        for (int i = 2;; i++) {
            while (currentSum > neededSum && begin < i - 2) {
                currentSum -= parsed.get(begin);
                begin++;
            }

            if (currentSum == neededSum) {
                return parsed.subList(begin, i);
            }

            if (parsed.size() <= i) {
                throw new IllegalStateException();
            }

            currentSum += parsed.get(i);
        }
    }

    @Override
    public Object computePart2() {
        var subList = subListThatSumsToV2(part1Result);
        var min = Collections.min(subList);
        var max = Collections.max(subList);
        return min + max;
    }
}
