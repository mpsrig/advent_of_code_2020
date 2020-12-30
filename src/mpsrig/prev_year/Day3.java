package mpsrig.prev_year;

import mpsrig.ResourceUtils;

import java.util.*;

public class Day3 {
    public static final class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public int getManhattanDistanceFromOrigin() {
            return Math.abs(x) + Math.abs(y);
        }
    }

    public static Map<Point, Integer> renderPath(String path) {
        Map<Point, Integer> out = new LinkedHashMap<>();
        int curX = 0;
        int curY = 0;
        int stepCount = 1;
        for (String elem : path.split(",")) {
            char direction = elem.charAt(0);
            int amount = Integer.parseInt(elem.substring(1));
            for (int i = 0; i < amount; i++) {
                switch (direction) {
                    case 'R' -> curX++;
                    case 'L' -> curX--;
                    case 'U' -> curY++;
                    case 'D' -> curY--;
                    default -> throw new IllegalArgumentException("Unknown direction " + direction + " in element " + elem);
                }
                out.putIfAbsent(new Point(curX, curY), stepCount);
                stepCount++;
            }
        }
        return out;
    }

    public static int minIntersectionDistance(String path1, String path2) {
        Set<Point> intersection = new HashSet<>(renderPath(path1).keySet());
        intersection.retainAll(renderPath(path2).keySet());
        return intersection.stream().mapToInt(Point::getManhattanDistanceFromOrigin).min().orElseThrow();
    }

    public static int minIntersectionStepDistance(String path1, String path2) {
        Map<Point, Integer> renderPath1 = renderPath(path1);
        Map<Point, Integer> renderPath2 = renderPath(path2);

        Set<Point> intersection = new HashSet<>(renderPath1.keySet());
        intersection.retainAll(renderPath2.keySet());

        return intersection.stream().mapToInt(p -> renderPath1.get(p) + renderPath2.get(p)).min().orElseThrow();
    }

    public static void main(String[] args) {
        List<String> lines = ResourceUtils.getLinesFromResource("/prev_year/3.txt");
        System.out.println("Part 1: " + minIntersectionDistance(lines.get(0), lines.get(1)));
        System.out.println("Part 2: " + minIntersectionStepDistance(lines.get(0), lines.get(1)));
    }
}
