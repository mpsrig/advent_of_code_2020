package mpsrig.twentytwentyone;

import mpsrig.Runner;

public class Day2 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/twentytwentyone/2.txt", new Day2());
    }

    @Override
    public Object computePart1() {
        long x = 0;
        long y = 0;

        for (var line : input) {
            var parts = line.split(" ");
            var amount = Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "forward" -> x += amount;
                case "up" -> y += amount;
                case "down" -> y -= amount;
                default -> throw new IllegalArgumentException("Bad line: " + line);
            }
        }

        return x * -y;
    }

    @Override
    public Object computePart2() {
        long horizontal = 0;
        long depth = 0;
        long aim = 0;

        for (var line : input) {
            var parts = line.split(" ");
            var amount = Integer.parseInt(parts[1]);
            switch (parts[0]) {
                case "forward" -> {
                    horizontal += amount;
                    depth += (aim * amount);
                }
                case "up" -> aim -= amount;
                case "down" -> aim += amount;
                default -> throw new IllegalArgumentException("Bad line: " + line);
            }
        }

        return horizontal * depth;
    }
}
