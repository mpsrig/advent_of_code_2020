package mpsrig;

import java.util.*;

public class Day6 extends ComputationWithGroups {
    public static void main(String[] args) {
        Runner.run("/6.txt", new Day6());
    }

    private List<Set<String>> groupUnion;
    private List<Set<String>> groupIntersection;

    @Override
    protected void init() {
        super.init();
        groupUnion = new ArrayList<>(groups.size());
        groupIntersection = new ArrayList<>(groups.size());
        for (var g : groups) {
            var inputs = ListUtils.map(g, StringUtils::toCodePoints);
            groupUnion.add(SetUtils.union(inputs));
            groupIntersection.add(SetUtils.intersection(inputs));
        }
    }

    private static void dumpMemory() {
        Runtime runtime = Runtime.getRuntime();
        System.err.println("Used Memory in KiB: "
                + (runtime.totalMemory() - runtime.freeMemory()) / 1024);
    }

    @Override
    public Object computePart1() {
        return groupUnion.stream().mapToInt(Collection::size).sum();
    }

    @Override
    public Object computePart2() {
        return groupIntersection.stream().mapToInt(Collection::size).sum();
    }
}
