package mpsrig;

import java.util.*;

public class Day15 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/15.txt", new Day15());
    }

    private List<Integer> numbers;

    @Override
    protected void init() {
        super.init();
        numbers = ListUtils.map(Arrays.asList(input.get(0).split(",")), Integer::parseInt);
    }

    private int impl(final int numIterations) {
//        long timeInNanosSpentInSection0 = 0;
//        long timeInNanosSpentInSection1 = 0;
//        long timeInNanosSpentInSection2 = 0;
        // We don't actually need to keep the whole list, but doing so doesn't really
        // seem to hurt anything as long as we preallocate it correctly.
        var numbersSpoken = new ArrayList<Integer>(numIterations);
        var indexesOf = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < numIterations; i++) {
            int numberToInsert;
            if (i < numbers.size()) {
                numberToInsert = numbers.get(i);
            } else {
                var previous = numbersSpoken.get(i - 1);
                var indexesOfPrevious = indexesOf.get(previous);
                if (indexesOfPrevious == null || indexesOfPrevious.size() == 0) {
                    throw new IllegalStateException();
                }
                if (indexesOfPrevious.size() == 1) {
                    numberToInsert = 0;
                } else {
                    numberToInsert = indexesOfPrevious.get(indexesOfPrevious.size() - 1) - indexesOfPrevious.get(indexesOfPrevious.size() - 2);
                }
            }
//            long start0 = System.nanoTime();
            numbersSpoken.add(numberToInsert);
//            long start1 = System.nanoTime();
            var l = indexesOf.computeIfAbsent(numberToInsert, k -> new ArrayList<>());
//            long start2 = System.nanoTime();
            l.add(i);
//            long end = System.nanoTime();
//            timeInNanosSpentInSection0 += (start1 - start0);
//            timeInNanosSpentInSection1 += (start2 - start1);
//            timeInNanosSpentInSection2 += (end - start2);
        }
//        System.err.println(timeInNanosSpentInSection0 / 1000000);
//        System.err.println(timeInNanosSpentInSection1 / 1000000);
//        System.err.println(timeInNanosSpentInSection2 / 1000000);
        return numbersSpoken.get(numbersSpoken.size() - 1);
    }

    // Approx 10x speedup
    private int implOptimized(final int numIterations) {
        int previousNumberSpoken = -1;
        var indexesOf = new int[numIterations * 2];
        Arrays.fill(indexesOf, -1);
        for (int i = 0; i < numIterations; i++) {
            int numberToInsert;
            if (i < numbers.size()) {
                numberToInsert = numbers.get(i);
            } else {
                final int previousNumberSpokenIdx0 = previousNumberSpoken * 2;
                final int previousNumberSpokenIdx1 = previousNumberSpokenIdx0 + 1;
                if (indexesOf[previousNumberSpokenIdx0] == -1 && indexesOf[previousNumberSpokenIdx1] == -1) {
                    throw new IllegalStateException();
                }
                if (indexesOf[previousNumberSpokenIdx0] == -1) {
                    numberToInsert = 0;
                } else {
                    numberToInsert = indexesOf[previousNumberSpokenIdx1] - indexesOf[previousNumberSpokenIdx0];
                }
            }
            previousNumberSpoken = numberToInsert;
            final int previousNumberSpokenIdx0 = previousNumberSpoken * 2;
            final int previousNumberSpokenIdx1 = previousNumberSpokenIdx0 + 1;
            indexesOf[previousNumberSpokenIdx0] = indexesOf[previousNumberSpokenIdx1];
            indexesOf[previousNumberSpokenIdx1] = i;
        }
        return previousNumberSpoken;
    }

    @Override
    public Object computePart1() {
        return implOptimized(2020);
    }

    @Override
    public Object computePart2() {
        return implOptimized(30000000);
    }
}
