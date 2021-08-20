package mpsrig.prev_year;

import mpsrig.ListUtils;
import mpsrig.Runner;

import java.util.*;

public class Day22 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/22.txt", new Day22());
//        String test = "deal into new stack\n" +
//                "cut -2\n" +
//                "deal with increment 7\n" +
//                "cut 8\n" +
//                "cut -4\n" +
//                "deal with increment 7\n" +
//                "cut 3\n" +
//                "deal with increment 9\n" +
//                "deal with increment 3\n" +
//                "cut -1";
//        var l = test.lines().toList();
//        System.err.println(process(l, 10));
    }

    @Override
    public Object computePart1() {
        int sz = 10007;
        var d = process(input, sz);

        return new ArrayList<>(d).indexOf(2019);
    }

    @Override
    public Object computePart2() {
        return null;
    }

    static Deque<Integer> process(List<String> instructions, int sz) {
        var operations = ListUtils.map(instructions, Day22::parseOperation);

        Deque<Integer> d = new ArrayDeque<>(sz);
        for (int i = 0; i < sz; i++) {
            d.addLast(i);
        }

        for (var op : operations) {
            d = op.execute(d);
        }

        return d;
    }

    interface Operation {
        Deque<Integer> execute(Deque<Integer> d);
    }

    static class DealIntoNewStack implements Operation {
        @Override
        public Deque<Integer> execute(Deque<Integer> d) {
            var out = new ArrayDeque<Integer>(d.size());
            for (var elem : d) {
                out.addFirst(elem);
            }
            return out;
        }
    }

    static class CutN implements Operation {
        final int n;

        CutN(int n) {
            this.n = n;
        }

        @Override
        public Deque<Integer> execute(Deque<Integer> d) {
            if (n == 0) {
                System.err.println("CutN: n was 0");
                return d;
            }
            if (n < 0) {
                for (int i = 0; i < (-n); i++) {
                    var last = d.removeLast();
                    d.addFirst(last);
                }
            }
            for (int i = 0; i < n; i++) {
                var first = d.removeFirst();
                d.addLast(first);
            }
            return d;
        }
    }

    static class DealWithIncrementN implements Operation {
        final int n;

        DealWithIncrementN(int n) {
            this.n = n;
        }

        @Override
        public Deque<Integer> execute(Deque<Integer> d) {
            final var sz = d.size();
            var temp = new Integer[sz];
            int pos = 0;
            for (var elem : d) {
                if (temp[pos] != null) {
                    throw new IllegalStateException("temp[pos] != null");
                }
                temp[pos] = elem;
                pos = (pos + n) % sz;
            }
            return new ArrayDeque<>(Arrays.asList(temp));
        }
    }

    static final String DEAL_INTO_NEW_STACK = "deal into new stack";
    static final String CUT = "cut ";
    static final String DEAL_WITH_INCREMENT = "deal with increment ";

    static Operation parseOperation(String line) {
        if (DEAL_INTO_NEW_STACK.equals(line)) {
            return new DealIntoNewStack();
        }
        if (line.startsWith(CUT)) {
            int n = Integer.parseInt(line.substring(CUT.length()));
            return new CutN(n);
        }
        if (line.startsWith(DEAL_WITH_INCREMENT)) {
            int n = Integer.parseInt(line.substring(DEAL_WITH_INCREMENT.length()));
            return new DealWithIncrementN(n);
        }
        throw new IllegalArgumentException("Could not parse instruction: " + line);
    }
}
