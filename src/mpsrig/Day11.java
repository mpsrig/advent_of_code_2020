package mpsrig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day11 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/11.txt", new Day11());
    }

    private List<List<String>> grid;

    @Override
    protected void init() {
        super.init();
        grid = ListUtils.map(input, StringUtils::toCodePoints);
    }

    private int countAdjacentOccupied(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y) {
                    continue;
                }
                if (j < 0 || j >= grid.size()) {
                    continue;
                }
                var line = grid.get(j);
                if (i < 0 || i >= line.size()) {
                    continue;
                }
                if (line.get(i).equals("#")) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isEmptySeatInDirection(int x, int y, int deltaX, int deltaY) {
//        System.err.println(x + "," + y + "," + deltaX + "," + deltaY);
        while (true) {
            y += deltaY;
            if (y < 0 || y >= grid.size()) {
                return false;
            }
            var line = grid.get(y);
            x += deltaX;
            if (x < 0 || x >= line.size()) {
                return false;
            }
            var cell = line.get(x);
            if (cell.equals("#")) {
                return true;
            }
            if (cell.equals("L")) {
                return false;
            }
        }
    }

    private int countAdjacentOccupiedPart2(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (isEmptySeatInDirection(x, y, i, j)) {
                    count++;
                }
            }
        }
//        System.err.println("At " + x + "," + y + " : " + count);
        return count;
    }

    private int runRound(boolean part2) {
        runRoundCalls++;
        var newGrid = new ArrayList<List<String>>(grid.size());
        int numMutations = 0;
        for (int y = 0; y < grid.size(); y++) {
            var line = grid.get(y);
            var newLine = new ArrayList<String>(line.size());
            for (int x = 0; x < line.size(); x++) {
                var cell = line.get(x);
                if (!Arrays.asList("L", "#").contains(cell)) {
                    newLine.add(cell);
                    continue;
                }
                var adjacentOccupied = part2 ? countAdjacentOccupiedPart2(x, y) : countAdjacentOccupied(x, y);
                String newCellValue = null;
                if (adjacentOccupied == 0 && cell.equals("L")) {
                    newCellValue = "#";
                } else if (adjacentOccupied > (part2 ? 4 : 3) && cell.equals("#")) {
                    newCellValue = "L";
                }
                if (newCellValue != null) {
                    newLine.add(newCellValue);
                    numMutations++;
                } else {
                    newLine.add(cell);
                }
            }
            newGrid.add(newLine);
        }
        grid = newGrid;
        return numMutations;
    }

    private void printGrid() {
        var joinedLines = ListUtils.map(grid, x -> String.join("", x));
        var joined = String.join("\n", joinedLines);
        System.err.println();
        System.err.println(joined);
        System.err.println();
    }

    private int runRoundCalls = 0;

    private void dumpStats() {
//        System.err.println("runRoundCalls: " + runRoundCalls);
    }

    public int impl(boolean part2) {
        int numMutations;
        do {
//            printGrid();
            numMutations = runRound(part2);
//            System.err.println(numMutations);
        } while (numMutations > 0);

        int numOccupied = 0;
        for (var line : grid) {
            for (var cell : line) {
                if (cell.equals("#")) {
                    numOccupied++;
                }
            }
        }
        return numOccupied;
    }

    @Override
    public Object computePart1() {
        var result = impl(false);
        dumpStats();
        return result;
    }

    @Override
    public Object computePart2() {
        init();
        var result = impl(true);
        dumpStats();
        return result;
    }
}
