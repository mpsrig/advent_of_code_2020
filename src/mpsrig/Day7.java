package mpsrig;

import java.util.*;
import java.util.regex.Pattern;

public class Day7 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/7.txt", new Day7());
    }

    private static class Rule {
//        public String getType() {
//            return type;
//        }
//
//        public Map<String, Integer> getContains() {
//            return contains;
//        }

        private final String type;
        private final Map<String, Integer> contains = new LinkedHashMap<>();

        private static final Pattern LEFT_PATTERN = Pattern.compile("(.*) bag");
        private static final Pattern RIGHT_PATTERN = Pattern.compile("(\\d+) (.*) bag");
        private static final Pattern CONTAIN_PATTERN = Pattern.compile("contain");

        Rule(String english, Deduplicator<String> deduplicator) {
            var parts = CONTAIN_PATTERN.split(english);
            check(parts.length == 2);

            var left = parts[0];
            var leftMatcher = LEFT_PATTERN.matcher(left);
            check(leftMatcher.find());
            type = deduplicator.get(leftMatcher.group(1));

            var right = parts[1];
            // No Pattern.compile needed, leverages special case for 1 char split:
            var rightParts = right.split(",");
            for (var p : rightParts) {
                var m = RIGHT_PATTERN.matcher(p);
                if (m.find()) {
                    contains.put(deduplicator.get(m.group(2)), Integer.parseInt(m.group(1)));
                }
            }
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "type='" + type + '\'' +
                    ", contains=" + contains +
                    '}';
        }
    }

    static void check(boolean state) {
        if (!state) {
            throw new IllegalArgumentException();
        }
    }

    private Map<String, Rule> rulesMap;

    @Override
    protected void init() {
        super.init();
        rulesMap = new LinkedHashMap<>();
        var deduplicator = new Deduplicator<String>();
        for (var elem : input) {
            var r = new Rule(elem, deduplicator);
            rulesMap.put(r.type, r);
        }

//        try {
//            var om = new ObjectMapper();
//            System.err.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(rulesMap));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
    }

    private final Memoize<Boolean> canEventuallyContainMemoize = new Memoize<>();
    private boolean canEventuallyContainCached(String containerType, String innerType) {
        return canEventuallyContainMemoize.getOrCompute(
                Arrays.asList(containerType, innerType),
                () -> canEventuallyContainDFS(containerType, innerType));
    }

    private boolean canEventuallyContainDFS(String containerType, String innerType) {
        var r = rulesMap.get(containerType);
        for (var elem : r.contains.keySet()) {
            if (innerType.equals(elem)) {
                return true;
            }
            if (canEventuallyContainCached(elem, innerType)) {
                return true;
            }
        }
        return false;
    }

    private int bagsContainedDFS(String containerType) {
        int count = 0;
        var r = rulesMap.get(containerType);
        for (var elem : r.contains.entrySet()) {
            count += elem.getValue() * (1 + bagsContainedDFS(elem.getKey()));
        }
        return count;
    }

    @Override
    public Object computePart1() {
        return rulesMap.keySet().stream().filter(c -> canEventuallyContainCached(c, "shiny gold")).count();
    }

    @Override
    public Object computePart2() {
        return bagsContainedDFS("shiny gold");
    }
}
