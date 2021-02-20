package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day5 extends Runner.Computation {
    private List<Long> parsedProgram = null;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }

    private Object compute(Long... input) {
        IntcodeComputer c = new IntcodeComputer(parsedProgram, Arrays.asList(input));
        c.run();
        return c.getOutput();
    }

    @Override
    public Object computePart1() {
        return compute(1L);
    }

    @Override
    public Object computePart2() {
        return compute(5L);
    }

    public static void main(String[] args) {
        Runner.run("/prev_year/5.txt", new Day5());
    }
}
