package mpsrig.prev_year;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day2Test {
    private void runTest(String input, String expected) {
        List<Long> parsedProgram = Day2.parseProgram(input);
//        Day2.runProgram(parsedProgram);
        IntcodeComputer c = new IntcodeComputer(parsedProgram, Collections.emptyList());
        c.run();
        assertEquals(Day2.serializeProgram(c.getMemory()), expected);
    }

    @Test
    void examplePrograms() {
        runTest("1,0,0,0,99", "2,0,0,0,99");
        runTest("2,3,0,3,99", "2,3,0,6,99");
        runTest("2,4,4,5,99,0", "2,4,4,5,99,9801");
        runTest("1,1,1,4,99,5,6,0,99", "30,1,1,4,2,5,6,0,99");
        runTest("1002,4,3,4,33", "1002,4,3,4,99");
    }
}