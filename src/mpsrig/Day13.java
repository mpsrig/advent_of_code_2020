package mpsrig;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day13 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/13.txt", new Day13());
    }

    private int startTime;

    private static class FrequencyAndOffset {
        final int frequency;
        final int offset;

        private FrequencyAndOffset(int frequency, int offset) {
            this.frequency = frequency;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "FrequencyAndOffset{" +
                    "frequency=" + frequency +
                    ", offset=" + offset +
                    '}';
        }
    }

    private List<FrequencyAndOffset> frequencyAndOffsetList;

    @Override
    protected void init() {
        super.init();
        if (input.size() != 2) {
            throw new IllegalArgumentException();
        }

        startTime = Integer.parseInt(input.get(0));
        int offset = 0;
        frequencyAndOffsetList = new ArrayList<>();
        for (var elem : input.get(1).split(",")) {
            if (!"x".equals(elem)) {
                var parsed = Integer.parseInt(elem);
                frequencyAndOffsetList.add(new FrequencyAndOffset(parsed, offset));
            }
            offset++;
        }
    }

    @Override
    public Object computePart1() {
        long minDepartureTime = -1;
        int busFrequencyForMinDepartureTime = -1;
        for (var elem : frequencyAndOffsetList) {
            var freq = elem.frequency;
            double numOccurrences = (double) startTime / (double) freq;
            long multiple = Math.round(Math.ceil(numOccurrences));
            long nextOccurenceTime = multiple * freq;
            if (nextOccurenceTime < minDepartureTime || minDepartureTime == -1) {
                minDepartureTime = nextOccurenceTime;
                busFrequencyForMinDepartureTime = freq;
            }
        }
        long numMinutesWaiting = minDepartureTime - startTime;
        return numMinutesWaiting * busFrequencyForMinDepartureTime;
    }

    private static long leastCommonMultiple(long x, long y) {
        var xBig = BigInteger.valueOf(x);
        var yBig = BigInteger.valueOf(y);
        var gcd = xBig.gcd(yBig);
        // GCD is always 1 if x and y are prime, apparently the puzzle inputs are that way
        return xBig.multiply(yBig).divide(gcd).longValueExact();
    }

    @Override
    public Object computePart2() {
        // We start with no constraints - check every t (stepSize = 1) starting at t = 0
        //
        // It's possible to instead seed this with constraints calculated mathematically
        // from the first entry, but this approach makes the code simpler.
        //
        long t = 0;
        long stepSize = 1; // invariant: must be > 0 at all times, to make forward progress

        for (var elem : frequencyAndOffsetList) {
            // Find where existing constraints intersect with this bus's frequency
            while ((t + elem.offset) % elem.frequency != 0) {
                t += stepSize;
            }

            // At t, we intersect existing constraints and this bus
            // The new step size is the least common multiple of existing step size and this frequency
            stepSize = leastCommonMultiple(stepSize, elem.frequency);
        }

        return t;
    }
}
