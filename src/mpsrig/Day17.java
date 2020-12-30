package mpsrig;

import java.util.*;

public class Day17 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/17.txt", new Day17());
    }

    private static class Coordinate {
        final int x;
        final int y;
        final int z;
        final int w;

        private Coordinate(int x, int y, int z, int w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y && z == that.z && w == that.w;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, w);
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", w=" + w +
                    '}';
        }
    }

    private Set<Coordinate> initialActivePositions;

    @Override
    protected void init() {
        super.init();

        var initialActivePositionsMutable = new LinkedHashSet<Coordinate>();

        int y = 0;
        for (var line : input) {
            int x = 0;
            for (var val : line.toCharArray()) {
                if (val == '#') {
                    initialActivePositionsMutable.add(new Coordinate(x, y, 0, 0));
                }
                x++;
            }
            y++;
        }

        initialActivePositions = Collections.unmodifiableSet(initialActivePositionsMutable);
    }

    private static final int NUM_BOOT_CYCLES = 6;

    private Set<Coordinate> runBootProcess(final boolean hasWDimension) {
        var activePositions = initialActivePositions;
        for (int i = 0; i < NUM_BOOT_CYCLES; i++) {
            activePositions = Collections.unmodifiableSet(runStateChangesForCycle(activePositions, hasWDimension));
        }
        return activePositions;
    }

    private static Set<Coordinate> runStateChangesForCycle(Set<Coordinate> activePositions, final boolean hasWDimension) {
        var neighborActiveCount = new LinkedHashMap<Coordinate, Integer>();
        for (var p : activePositions) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        for (int wOffset = -1; wOffset <= 1; wOffset++) {
                            if (!hasWDimension && wOffset != 0) {
                                continue;
                            }
                            if (xOffset == 0 && yOffset == 0 && zOffset == 0 && wOffset == 0) {
                                continue;
                            }
                            var key = new Coordinate(
                                    p.x + xOffset,
                                    p.y + yOffset,
                                    p.z + zOffset,
                                    p.w + wOffset);
                            neighborActiveCount.compute(key, (k, v) -> (v == null) ? 1 : v + 1);
                        }
                    }
                }
            }
        }

        var out = new LinkedHashSet<Coordinate>();
        for (var elem : neighborActiveCount.entrySet()) {
            if (elem.getValue() == 3) {
                out.add(elem.getKey());
            } else if (elem.getValue() == 2 && activePositions.contains(elem.getKey())) {
                out.add(elem.getKey());
            }
        }

        return out;
    }

    @Override
    public Object computePart1() {
        var booted = runBootProcess(false);
        return booted.size();
    }

    @Override
    public Object computePart2() {
        var booted = runBootProcess(true);
        return booted.size();
    }
}
