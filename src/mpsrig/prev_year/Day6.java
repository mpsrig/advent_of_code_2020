package mpsrig.prev_year;

import mpsrig.Memoize;
import mpsrig.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day6 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/6.txt", new Day6());
    }

    // key orbits value
    private Map<String, String> orbits = new HashMap<>();

    protected void init() {
        for (var l : input) {
            var parts = l.split("\\)");
            if (parts.length != 2) {
                throw new IllegalStateException();
            }
            if (orbits.containsKey(parts[1])) {
                throw new IllegalStateException();
            }
            orbits.put(parts[1], parts[0]);
        }
    }

    private final Memoize<Integer> countMemoizer = new Memoize<>();

    private int countDirectAndIndirectOrbits(String x) {
        return countMemoizer.getOrCompute(x, () -> countDirectAndIndirectOrbitsImpl(x));
    }

    private int countDirectAndIndirectOrbitsImpl(String x) {
        if ("COM".equals(x)) {
            return 0;
        }
        return 1 + countDirectAndIndirectOrbits(orbits.get(x));
    }

    @Override
    public Object computePart1() {
        long result = 0;
        for (var k : orbits.keySet()) {
            result += countDirectAndIndirectOrbits(k);
        }
        return result;
    }

    private List<String> getListOfOrbitsToCOM(String x) {
        var out = new ArrayList<String>();
        while (!"COM".equals(x)) {
            x = orbits.get(x);
            out.add(x);
        }
        return out;
    }

    @Override
    public Object computePart2() {
        var youToCOM = getListOfOrbitsToCOM("YOU");
        var sanToCOM = getListOfOrbitsToCOM("SAN");
        for (int i = 0; i < youToCOM.size(); i++) {
            var idx = sanToCOM.indexOf(youToCOM.get(i));
            if (idx > -1) {
                return i + idx;
            }
        }
        throw new IllegalStateException();
    }
}
