package mpsrig.prev_year;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day5Test {
    private void runTest(String program, List<Integer> input, List<Integer> expectedOutput) {
        List<Integer> parsedProgram = Day2.parseProgram(program);
        IntcodeComputer c = new IntcodeComputer(parsedProgram, input);
        c.run();
        assertEquals(c.getOutput(), expectedOutput);
    }

    @Test
    void part2() {
        runTest("3,9,8,9,10,9,4,9,99,-1,8", Collections.singletonList(8), Collections.singletonList(1));
        runTest("3,9,8,9,10,9,4,9,99,-1,8", Collections.singletonList(7), Collections.singletonList(0));
    }
}