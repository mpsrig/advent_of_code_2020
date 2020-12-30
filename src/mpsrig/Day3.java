package mpsrig;

import java.util.List;

public class Day3 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/3.txt", new Day3());
    }

    @Override
    public Object computePart1() {
        return computeTrees(input, 3, 1);
    }

    @Override
    public Object computePart2() {
        return (long)computeTrees(input, 1, 1) *
                computeTrees(input, 3, 1) *
                computeTrees(input, 5, 1) *
                computeTrees(input, 7, 1) *
                computeTrees(input, 1, 2);
    }

    public static int computeTrees(List<String> lines, int deltaX, int deltaY) {
        int count = 0;
        for (int x = 0, y = 0; y < lines.size(); x+=deltaX, y+=deltaY) {
            if (hasTree(lines, x, y)) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasTree(List<String> lines, int x, int y) {
        String line = lines.get(y);
        return line.charAt(x % line.length()) == '#';
    }
}
