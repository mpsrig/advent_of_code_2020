package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day5 extends Runner.Computation {
    private List<Integer> parsedProgram = null;

    @Override
    protected void init() {
        if (input.size() != 1) {
            throw new IllegalArgumentException();
        }
        parsedProgram = Day2.parseProgram(input.get(0));
    }

    private Object compute(Integer... input) {
        IntcodeComputer c = new IntcodeComputer(parsedProgram, Arrays.asList(input));
        c.run();
        return c.getOutput();
    }

    @Override
    public Object computePart1() {
        return compute(1);
    }

    @Override
    public Object computePart2() {
        return compute(5);
    }

    public static void main(String[] args) {
        Runner.run("/prev_year/5.txt", new Day5());
    }
}
