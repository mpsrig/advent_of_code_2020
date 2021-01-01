package mpsrig;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

public class Day16 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/16.txt", new Day16());
    }

    private static class Range {
        final int lo;
        final int hi;

        private Range(int lo, int hi) {
            if (lo > hi) {
                throw new IllegalStateException();
            }
            this.lo = lo;
            this.hi = hi;
        }

        public boolean contains(int x) {
            return lo <= x && x <= hi;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "lo=" + lo +
                    ", hi=" + hi +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return lo == range.lo && hi == range.hi;
        }

        @Override
        public int hashCode() {
            return Objects.hash(lo, hi);
        }
    }

    private static class FieldConstraints {
        final String fieldName;
        final List<Range> ranges;

        private FieldConstraints(String fieldName, List<Range> ranges) {
            this.fieldName = fieldName;
            this.ranges = Collections.unmodifiableList(ranges);
        }

        private static final Pattern PATTERN = Pattern.compile("^(.*): (\\d+)-(\\d+) or (\\d+)-(\\d+)$");

        static FieldConstraints parse(String line) {
            var m = PATTERN.matcher(line);
            Day7.check(m.find());
            return new FieldConstraints(
                    m.group(1),
                    Arrays.asList(
                            new Range(
                                    Integer.parseInt(m.group(2)),
                                    Integer.parseInt(m.group(3))),
                            new Range(
                                    Integer.parseInt(m.group(4)),
                                    Integer.parseInt(m.group(5)))
                    )
            );
        }

        public boolean rangesContain(int x) {
            for (var r : ranges) {
                if (r.contains(x)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "FieldConstraints{" +
                    "fieldName='" + fieldName + '\'' +
                    ", ranges=" + ranges +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldConstraints that = (FieldConstraints) o;
            return Objects.equals(fieldName, that.fieldName) && Objects.equals(ranges, that.ranges);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldName, ranges);
        }
    }

    private List<FieldConstraints> fieldConstraintsList;
    private List<Integer> yourTicket;
    private List<List<Integer>> nearbyTickets;

    private static List<Integer> parseTicket(String line) {
        return ListUtils.map(Arrays.asList(line.split(",")), Integer::parseInt);
    }

    @Override
    protected void init() {
        super.init();
        var iter = input.iterator();

        fieldConstraintsList = new ArrayList<>();
        for (var line = iter.next(); line.length() != 0; line = iter.next()) {
            fieldConstraintsList.add(FieldConstraints.parse(line));
        }

        Day7.check("your ticket:".equals(iter.next()));
        yourTicket = parseTicket(iter.next());

        Day7.check("".equals(iter.next()));
        Day7.check("nearby tickets:".equals(iter.next()));

        nearbyTickets = new ArrayList<>();
        while (iter.hasNext()) {
            nearbyTickets.add(parseTicket(iter.next()));
        }

//        System.err.println(fieldConstraintsList);
//        System.err.println(yourTicket);
//        System.err.println(nearbyTickets);
    }

    private boolean isFieldPossiblyValid(int x) {
        for (var f : fieldConstraintsList) {
            if (f.rangesContain(x)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object computePart1() {
        long sum = 0;
        for (var ticket : nearbyTickets) {
            for (var x : ticket) {
                if (!isFieldPossiblyValid(x)) {
                    sum += x;
                }
            }
        }
        return sum;
    }

    private boolean isTicketPossiblyValid(List<Integer> ticket) {
        for (var x : ticket) {
            if (!isFieldPossiblyValid(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object computePart2() {
        var validNearbyTickets = new ArrayList<List<Integer>>(nearbyTickets.size());
        for (var t : nearbyTickets) {
            if (isTicketPossiblyValid(t)) {
                validNearbyTickets.add(t);
            }
        }
//        System.err.println(validNearbyTickets);
        var orderedConstraints = deriveOrderedConstraints(validNearbyTickets);
//        System.err.println(orderedConstraints);

        long product = 1;
        for (int i = 0; i < orderedConstraints.size(); i++) {
            if (!orderedConstraints.get(i).fieldName.startsWith("departure")) {
                continue;
            }
            // else starts with departure
            product *= yourTicket.get(i);
        }
        return product;
    }

//    private static boolean isSolved(List<Set<FieldConstraints>> possibleConstraintsByPosition) {
//        for (var p : possibleConstraintsByPosition) {
//            if (p.size() == 0) {
//                throw new IllegalStateException();
//            }
//            if (p.size() != 1) {
//                return false;
//            }
//        }
//        return true;
//    }

    private List<FieldConstraints> deriveOrderedConstraints(List<List<Integer>> validNearbyTickets) {
        final int numFields = fieldConstraintsList.size();
        var possibleConstraintsByPosition = new ArrayList<Set<FieldConstraints>>(numFields);
        // Start with all constraints for each field
        for (int i = 0; i < numFields; i++) {
            possibleConstraintsByPosition.add(new LinkedHashSet<>(fieldConstraintsList));
        }

        for (var ticket : validNearbyTickets) {
            reduceConstraints(possibleConstraintsByPosition, ticket);
        }

        return ListUtils.map(possibleConstraintsByPosition, constraintsSet -> {
            if (constraintsSet.size() != 1) {
                throw new IllegalStateException();
            }
            return constraintsSet.iterator().next();
        });
    }

    private static void reduceConstraints(List<Set<FieldConstraints>> possibleConstraintsByPosition, List<Integer> ticket) {
        Day7.check(possibleConstraintsByPosition.size() == ticket.size());

        for (int i = 0; i < possibleConstraintsByPosition.size(); i++) {
            var possibleConstraints = possibleConstraintsByPosition.get(i);
            var x = ticket.get(i);
            if (possibleConstraints.size() == 0) {
                throw new IllegalStateException();
            }
            if (possibleConstraints.size() == 1) {
                if (!possibleConstraints.iterator().next().rangesContain(x)) {
                    throw new IllegalStateException();
                }
                continue;
            }
            reduceConstraintsForField(possibleConstraints, x);
            if (possibleConstraints.size() == 0) {
                throw new IllegalStateException();
            }
            if (possibleConstraints.size() == 1) {
                // Was more than 1, became 1
                // We now know what this field must be, so remove it from others
                //
                // Note: that can cause a cascade reaction where others become 1,
                // so they must be filtered from others, etc.
                removeSingleElementFromAllOtherIndexes(possibleConstraintsByPosition, i);
            }
        }
    }


    public static <E> void removeSingleElementFromAllOtherIndexes(List<Set<E>> possibleConstraintsByPosition, int idx) {
//        System.err.println("removeConstraintFromAllOtherIndexesImpl");

        var positionConstraints = possibleConstraintsByPosition.get(idx);
        if (positionConstraints.size() != 1) {
            throw new IllegalStateException();
        }
        var c = positionConstraints.iterator().next();
        for (int j = 0; j < possibleConstraintsByPosition.size(); j++) {
            if (j == idx) {
                continue;
            }
            var jConstraints = possibleConstraintsByPosition.get(j);
            if (jConstraints.size() == 0) {
                throw new IllegalStateException();
            }
            if (jConstraints.size() == 1) {
                if (jConstraints.contains(c)) {
                    throw new IllegalStateException();
                }
                continue;
            }
            jConstraints.remove(c);
            if (jConstraints.size() == 1) {
                // recurse
                removeSingleElementFromAllOtherIndexes(possibleConstraintsByPosition, j);
            }
        }
    }

    private static void reduceConstraintsForField(Set<FieldConstraints> possibleConstraints, int value) {
        // int beginSize = possibleConstraints.size();
        if (possibleConstraints.size() < 2) {
            throw new IllegalArgumentException();
        }
        possibleConstraints.removeIf(c -> !c.rangesContain(value));
        // System.err.println("Removed some constraints. Begin size = " + beginSize + ", current size: " + possibleConstraints.size());
    }
}
