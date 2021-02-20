package mpsrig.prev_year;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day5Test {
    private void runTest(String program, List<Long> input, List<Long> expectedOutput) {
        var parsedProgram = Day2.parseProgram(program);
        IntcodeComputer c = new IntcodeComputer(parsedProgram, input);
        c.run();
        assertEquals(c.getOutput(), expectedOutput);
    }

    @Test
    void part2() {
        runTest("3,9,8,9,10,9,4,9,99,-1,8", Collections.singletonList(8L), Collections.singletonList(1L));
        runTest("3,9,8,9,10,9,4,9,99,-1,8", Collections.singletonList(7L), Collections.singletonList(0L));
    }
}