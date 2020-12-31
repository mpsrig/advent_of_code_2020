package mpsrig;

import java.util.*;

public class Day24 extends Runner.Computation {
    public static void main(String[] args) {
        System.err.println(directionsToCoordinate(parseDirections("nwwswee")));
        Runner.run("/24.txt", new Day24());
    }

    public enum Direction {
        EAST, SOUTHEAST, SOUTHWEST, WEST, NORTHWEST, NORTHEAST
    }

    private static class Coordinate {
        final int x;
        final int y;

        private Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Day24.Coordinate that = (Day24.Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public Coordinate newInDirection(Direction d) {
            int x = this.x;
            int y = this.y;

            switch (d) {
                case EAST -> x++;
                case WEST -> x--;
                case NORTHWEST -> {
                    if (y % 2 == 0) {
                        y--;
                    } else {
                        y--;
                        x--;
                    }
                }
                case NORTHEAST -> {
                    if (y % 2 == 0) {
                        y--;
                        x++;
                    } else {
                        y--;
                    }
                }
                case SOUTHWEST -> {
                    if (y % 2 == 0) {
                        y++;
                    } else {
                        y++;
                        x--;
                    }
                }
                case SOUTHEAST -> {
                    if (y % 2 == 0) {
                        y++;
                        x++;
                    } else {
                        y++;
                    }
                }
            }

            return new Coordinate(x, y);
        }
    }

    static List<Direction> parseDirections(String s) {
        var out = new ArrayList<Direction>();
        for (int i = 0; i < s.length(); i++) {
            var c = s.charAt(i);
            if (c == 'e') {
                out.add(Direction.EAST);
            } else if (c == 'w') {
                out.add(Direction.WEST);
            } else {
                i++;
                var c2 = s.charAt(i);
                if (c == 's') {
                    if (c2 == 'e') {
                        out.add(Direction.SOUTHEAST);
                    } else if (c2 == 'w') {
                        out.add(Direction.SOUTHWEST);
                    } else {
                        throw new IllegalArgumentException("Invalid character: " + c2);
                    }
                } else if (c == 'n') {
                    if (c2 == 'e') {
                        out.add(Direction.NORTHEAST);
                    } else if (c2 == 'w') {
                        out.add(Direction.NORTHWEST);
                    } else {
                        throw new IllegalArgumentException("Invalid character: " + c2);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid character: " + c);
                }
            }
        }
        return out;
    }

    static Coordinate directionsToCoordinate(List<Direction> directions) {
        var c = new Coordinate(0, 0);
        for (var d : directions) {
            c = c.newInDirection(d);
        }
        return c;
    }

    private Set<Coordinate> part1BlackTiles;

    @Override
    public Object computePart1() {
        var blackTiles = new LinkedHashSet<Coordinate>(input.size());
        for (var elem : input) {
            var c = directionsToCoordinate(parseDirections(elem));
            if (blackTiles.contains(c)) {
                blackTiles.remove(c);
            } else {
                blackTiles.add(c);
            }
        }
        part1BlackTiles = Collections.unmodifiableSet(blackTiles);
        return blackTiles.size();
    }

    private static List<Coordinate> getNeighbors(Coordinate c) {
        return ListUtils.map(Arrays.asList(Direction.values()), c::newInDirection);
    }

    private static Set<Coordinate> runStateChangesForCycle(Set<Coordinate> blackTiles) {
        var neighborBlackCount = new LinkedHashMap<Coordinate, Integer>();
        for (var b : blackTiles) {
            for (var n : getNeighbors(b)) {
                neighborBlackCount.compute(n, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }

        var out = new LinkedHashSet<Coordinate>();
        for (var elem : neighborBlackCount.entrySet()) {
            if (blackTiles.contains(elem.getKey())) {
                if (elem.getValue() == 1 || elem.getValue() == 2) {
                    out.add(elem.getKey());
                }
            } else if (elem.getValue() == 2) {
                out.add(elem.getKey());
            }
        }
        return out;
    }

    @Override
    public Object computePart2() {
        var current = part1BlackTiles;
        for (int i = 1; i <= 100; i++) {
            current = runStateChangesForCycle(current);
//            System.err.println("Day " + i + ": " + current.size());
        }
        return current.size();
    }
}
