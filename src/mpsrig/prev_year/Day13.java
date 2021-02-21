package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.*;

public class Day13 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/13.txt", new Day13());
    }

    private List<Long> parsedProgram;

    private int displayWidth;
    private int displayHeight;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }


    @Override
    public Object computePart1() {
        var computer = new IntcodeComputer(parsedProgram, Collections.emptyList());
        var grid = new HashMap<Coordinate, Integer>();

        while (true) {
            var x = computer.runUntilOutputAndYield();
            if (x == null) {
                break;
            }
            var y = computer.runUntilOutputAndYield();
            Objects.requireNonNull(y);
            var tileId = computer.runUntilOutputAndYield();
            Objects.requireNonNull(tileId);

            grid.put(new Coordinate(Math.toIntExact(x), Math.toIntExact(y)), Math.toIntExact(tileId));
        }

        var minX = grid.keySet().stream().mapToInt(c -> c.x).min().orElseThrow();
        var maxX = grid.keySet().stream().mapToInt(c -> c.x).max().orElseThrow();
        var minY = grid.keySet().stream().mapToInt(c -> c.y).min().orElseThrow();
        var maxY = grid.keySet().stream().mapToInt(c -> c.y).max().orElseThrow();

        if (minX != 0 || minY != 0) {
            throw new IllegalStateException();
        }
        displayWidth = maxX + 1;
        displayHeight = maxY + 1;

//        System.err.println("\n" + renderGrid(grid) + "\n");

        int count = 0;
        for (var elem : grid.values()) {
            if (elem == 2) {
                count++;
            }
        }
        return count;
    }

    private String renderGrid(Map<Coordinate, Integer> grid) {
        if (displayWidth <= 0 || displayHeight <= 0) {
            throw new IllegalStateException();
        }
        var displayGrid = new int[displayHeight][displayWidth];
        for (int y = 0; y < displayHeight; y++) {
            for (int x = 0; x < displayWidth; x++) {
                var val = grid.get(new Coordinate(x, y));
                displayGrid[y][x] = val != null ? val : 0;
            }
        }
        return renderGrid(displayGrid);
    }

    private static String renderGrid(int[][] displayGrid) {
        var sb = new StringBuilder();
        for (int y = 0; y < displayGrid.length; y++) {
            for (int x = 0; x < displayGrid[y].length; x++) {
                sb.append(renderPixel(displayGrid[y][x]));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static char renderPixel(int pixelValue) {
        return switch (pixelValue) {
            case 0 -> ' ';
            case 1 -> '|';
            case 2 -> '#';
            case 3 -> '_';
            case 4 -> 'o';
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public Object computePart2() {
        return null;
    }
}
