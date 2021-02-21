package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Day11 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/11.txt", new Day11());
    }

    private List<Long> parsedProgram;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }

    private enum Direction {
        NORTH, WEST, SOUTH, EAST
    }

    public static Coordinate newInDirection(Coordinate coordinate, Direction d) {
        int x = coordinate.x;
        int y = coordinate.y;

        switch(d) {
            case NORTH -> y++;
            case WEST -> x--;
            case SOUTH -> y--;
            case EAST -> x++;
        }

        return new Coordinate(x, y);
    }

    private static Direction computeDirectionFromTurn(Direction d, long turn) {
        return switch (Math.toIntExact(turn)) {
            case 0 -> switch (d) {
                case NORTH -> Direction.WEST;
                case WEST -> Direction.SOUTH;
                case SOUTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
            };
            case 1 -> switch (d) {
                case NORTH -> Direction.EAST;
                case WEST -> Direction.NORTH;
                case SOUTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
            };
            default -> throw new IllegalArgumentException();
        };
    }

    private HashMap<Coordinate, Integer> runPaint(boolean isPart2) {
        var computer = new IntcodeComputer(parsedProgram, Collections.emptyList());
        var cells = new HashMap<Coordinate, Integer>();
        if (isPart2) {
            cells.put(new Coordinate(0, 0), 1);
        }


        var currentPosition = new Coordinate(0, 0);
        var currentDirection = Direction.NORTH;

        while (true) {
            var currentColor = cells.get(currentPosition);
            computer.addInput(currentColor != null ? currentColor : 0);

            var newColor = computer.runUntilOutputAndYield();
            if (newColor == null) {
                break;
            }
            var turn = computer.runUntilOutputAndYield();
            Objects.requireNonNull(turn);

            cells.put(currentPosition, Math.toIntExact(newColor));

            currentDirection = computeDirectionFromTurn(currentDirection, turn);
            currentPosition = newInDirection(currentPosition, currentDirection);
        }

        return cells;
    }

    @Override
    public Object computePart1() {
        var cells = runPaint(false);
        return cells.size();
    }

    private static char renderPixel(int pixelValue) {
        return switch (pixelValue) {
            case 0 -> ' ';
            case 1 -> '|';
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public Object computePart2() {
        var cells = runPaint(true);

        var minX = cells.keySet().stream().mapToInt(c -> c.x).min().orElseThrow();
        var maxX = cells.keySet().stream().mapToInt(c -> c.x).max().orElseThrow();
        var minY = cells.keySet().stream().mapToInt(c -> c.y).min().orElseThrow();
        var maxY = cells.keySet().stream().mapToInt(c -> c.y).max().orElseThrow();

        var sb = new StringBuilder();
        for (int y = maxY; y >= minY; y--) {
            sb.append('\n');
            for (int x = minX; x <= maxX; x++) {
                var val = cells.get(new Coordinate(x, y));
                sb.append(renderPixel(val != null ? val : 0));
            }
        }
        return sb.toString();
    }
}
