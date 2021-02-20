package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.Collections;
import java.util.List;

public class Day9 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/9.txt", new Day9());
    }

    private List<Long> parsedProgram = null;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }

    private Object compute(long inputVal) {
        var c = new IntcodeComputer(parsedProgram, Collections.singletonList(inputVal));
        c.run();
        return c.getOutput();
    }

    @Override
    public Object computePart1() {
        return compute(1L);
    }

    @Override
    public Object computePart2() {
        return compute(2L);
    }
}
