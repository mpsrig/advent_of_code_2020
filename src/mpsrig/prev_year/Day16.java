package mpsrig.prev_year;

import mpsrig.Memoize;
import mpsrig.Runner;
import mpsrig.StringUtils;

import java.util.Arrays;

public class Day16 extends Runner.Computation {
    private static final boolean IS_DEBUG = false;

    public static void main(String[] args) {
        Runner.run("/prev_year/16.txt", new Day16());
    }

    private static int[] parse(String s) {
        return StringUtils.toCodePoints(s).stream().mapToInt(Integer::parseInt).toArray();
    }

    private static String stringify(Result r, final int begin, final int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < end; i++) {
            int cur = r.get(i);
            if (IS_DEBUG) {
                System.err.println("Calculated " + cur + " for position " + i);
            }
            sb.append(cur);
        }
        return sb.toString();
    }

    private static final int[] BASE_PATTERN = {0, 1, 0, -1};

    public abstract static class Pattern {
        int multiplier;

        public Pattern(int m) {
            multiplier = m;
        }

        abstract int get(int idx);

        boolean areAllZeroUntil(final int idx) {
            boolean result = (idx + 1) < multiplier;

            if (IS_DEBUG && result) {
                for (int i = 0; i < idx; i++) {
                    if (get(i) != 0) {
                        throw new IllegalStateException("get(i) != 0");
                    }
                }
            }

            return result;
        }

        // Returns the index (in caller's index space) of the first 1 if true, else -1 to signal false
        int containsOnlyZeroThenOneUntil(final int length) {
            if (areAllZeroUntil(length)) {
                return -1;
            }
            boolean result = (length + 1) < (multiplier * 2);
            if (!result) {
                return -1;
            }
            int out = multiplier - 1;
            if (IS_DEBUG) {
                if (!areAllZeroUntil(out - 1)) {
                    throw new IllegalStateException("!areAllZeroUntil(out) " + out);
                }
                for (int i = out; i < length; i++) {
                    if (get(i) != 1) {
                        throw new IllegalStateException("get(i) != 1");
                    }
                }
            }
            return out;
        }
    }

//    private static class MaterializedPattern extends Pattern {
//        int[] elements;
//
//        public MaterializedPattern(int multiplier) {
//            this(materialize(multiplier), multiplier);
//        }
//
//        public MaterializedPattern(int[] e, int m) {
//            super(m);
//            elements = e;
//
////            if (elements[multiplier] == 0) {
////                throw new IllegalStateException("elements[multiplier] == 0");
////            }
////            if (elements[multiplier - 1] != 0) {
////                throw new IllegalStateException("elements[multiplier - 1] != 0");
////            }
//        }
//
//        @Override
//        int get(int idx) {
//            return elements[(idx + 1) % elements.length];
//        }
//
//        private static int[] materialize(final int multiplier) {
//            int[] out = new int[multiplier * BASE_PATTERN.length];
//            for (int i = 0; i < BASE_PATTERN.length; i++) {
//                for (int j = 0; j < multiplier; j++) {
//                    out[multiplier * i + j] = BASE_PATTERN[i];
//                }
//            }
//            return out;
//        }
//    }

    private static class OnTheFlyPattern extends Pattern {
        public OnTheFlyPattern(int m) {
            super(m);
        }

        @Override
        int get(int idx) {
            final int patternLength = multiplier * BASE_PATTERN.length;
            final int offset = (idx + 1) % patternLength;

            final int pos = offset / multiplier;

            return BASE_PATTERN[pos];
        }
    }

//    private static class CheckedOnTheFlyPattern extends Pattern {
//        private Pattern p1;
//        private Pattern p2;
//
//        public CheckedOnTheFlyPattern(int m) {
//            super(m);
//            p1 = new MaterializedPattern(m);
//            p2 = new OnTheFlyPattern(m);
//        }
//
//        @Override
//        int get(int idx) {
//            int ret = p2.get(idx);
//            if (ret != p1.get(idx)) {
//                throw new IllegalStateException();
//            }
//            return ret;
//        }
//    }

    private static Pattern calculatePattern(final int idx) {
        final int multiplier = idx + 1;
//        if (multiplier < 10) {
//            return new CheckedOnTheFlyPattern(multiplier);
//        }
        return new OnTheFlyPattern(multiplier);
    }

    private Memoize<Pattern> patternMemoizer = new Memoize<>();

    private Pattern calculatePatternMemoized(final int idx) {
        return patternMemoizer.getOrCompute(idx, () -> calculatePattern(idx));
    }

//    private static int accumulate(final int[] input, final Pattern pattern) {
//        int elem = 0;
//        for (int j = input.length - 1; j >= 0; j--) {
//            elem += input[j] * pattern.get(j);
//            if (pattern.areAllZeroBefore(j)) {
//                return elem;
//            }
//        }
//        return elem;
//    }

    static abstract class Result {
        abstract int length();

        abstract int get(int idx);

        int[] materialize() {
            int[] out = new int[length()];
            for (int i = 0; i < out.length; i++) {
                out[i] = get(i);
            }
            return out;
        }
    }

    static class IdentityResult extends Result {
        private final int[] r;

        IdentityResult(int[] r) {
            this.r = r;
        }

        public int length() {
            return r.length;
        }

        public int get(int idx) {
            return r[idx];
        }
    }

    class PassResult extends Result {
        private final int iteration;
        private final Result previous;
        private final int[] cache;

        private static final int NOT_COMPUTED = -1;

        public PassResult(Result previous, int iteration) {
            this.iteration = iteration;
            this.previous = previous;
            this.cache = new int[this.previous.length()];
            Arrays.fill(this.cache, NOT_COMPUTED);
        }

        private int[] sumOfPreviousFromIdxUntilEnd;
        private int sumOfPreviousFromIdxUntilEndPopulatedUntil = -1;

        private int sumOfPreviousFromIdxUntilEndAt(final int idx) {
            if (sumOfPreviousFromIdxUntilEnd == null) {
                sumOfPreviousFromIdxUntilEnd = new int[length()];
                Arrays.fill(sumOfPreviousFromIdxUntilEnd, NOT_COMPUTED);

                int lastIdx = length() - 1;
                sumOfPreviousFromIdxUntilEnd[lastIdx] = previous.get(lastIdx);
                sumOfPreviousFromIdxUntilEndPopulatedUntil = lastIdx;
            }

            if (sumOfPreviousFromIdxUntilEnd[idx] == NOT_COMPUTED) {
                for (int i = sumOfPreviousFromIdxUntilEndPopulatedUntil - 1; i >= idx; i--) {
                    int nextEntry = sumOfPreviousFromIdxUntilEnd[i + 1];
                    if (IS_DEBUG && nextEntry == NOT_COMPUTED) {
                        throw new IllegalStateException("nextEntry == NOT_COMPUTED");
                    }
                    sumOfPreviousFromIdxUntilEnd[i] = previous.get(i) + nextEntry;
                }
                sumOfPreviousFromIdxUntilEndPopulatedUntil = idx;
            }
            int ret = sumOfPreviousFromIdxUntilEnd[idx];
            if (IS_DEBUG) {
                if (ret == NOT_COMPUTED) {
                    throw new IllegalStateException("ret == NOT_COMPUTED");
                }
                if (sumOfPreviousFromIdxUntilEndPopulatedUntil > idx) {
                    throw new IllegalStateException("sumOfPreviousFromIdxUntilEndPopulatedUntil > idx");
                }
                int check = 0;
                for (int i = idx; i < length(); i++) {
                    check += previous.get(i);
                }
                if (check != ret) {
                    throw new IllegalStateException("check != ret");
                }
            }
            return ret;
        }

        public int length() {
            return cache.length;
        }

        private int accumulate(int idx) {
            final Pattern pattern = calculatePatternMemoized(idx);
            if (pattern.areAllZeroUntil(length())) {
                System.err.println("Really early return");
                return 0;
            }
            int maybe = pattern.containsOnlyZeroThenOneUntil(length());
            if (maybe > -1) {
                return sumOfPreviousFromIdxUntilEndAt(maybe);
            }
            int elem = 0;
            for (int j = length() - 1; j >= 0; j--) {
                elem += previous.get(j) * pattern.get(j);
                if (pattern.areAllZeroUntil(j)) {
                    return elem;
                }
            }
            return elem;
        }

        public int get(int idx) {
            if (cache[idx] == NOT_COMPUTED) {
                cache[idx] = Math.abs(accumulate(idx)) % 10;
            }
            return cache[idx];
        }
    }

//    private int[] applyPass(final int[] input) {
//        int[] out = new int[input.length];
//        for (int i = 0; i < out.length; i++) {
//            final Pattern pattern = calculatePatternMemoized(i);
//            int elem = accumulate(input, pattern);
//            out[i] = Math.abs(elem) % 10;
//        }
//        return out;
//    }
//
//    private int[] applyMultiplePasses(int[] arr, final int n) {
//        for (int k = 0; k < n; k++) {
//            arr = applyPass(arr);
//        }
//        return arr;
//    }

    private Result buildResultChain(Result r, int n) {
        for (int k = 0; k < n; k++) {
            r = new PassResult(r, k);
        }
        return r;
    }

    @Override
    public Object computePart1() {
//        System.err.println(stringify(applyPass(parse("12345678"))));
//        System.err.println(stringify(applyPass(parse("1234567812345678"))));

//        System.err.println(stringify(buildResultChain(new IdentityResult(parse("80871224585914546619083218645595")), 100), 0, 8));

        return stringify(buildResultChain(new IdentityResult(parse(input.get(0))), 100), 0, 8);
    }

    private int[] repeat(final int[] input, final int n) {
        int[] out = new int[n * input.length];
        for (int i = 0; i < n; i++) {
            System.arraycopy(input, 0, out, i * input.length, input.length);
        }
        return out;
    }

    @Override
    public Object computePart2() {
//        return null;
        String in = input.get(0);
        int[] parsed = repeat(parse(in), 10000);
        int offset = Integer.parseInt(in.substring(0, 7));

        Result r = buildResultChain(new IdentityResult(parsed), 100);

        return stringify(r, offset, offset + 8);
    }
}
