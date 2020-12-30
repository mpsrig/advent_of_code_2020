package mpsrig;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Day1Test {
    @Test
    void compute() {
        String exampleStr = "1721\n979\n366\n299\n675\n1456\n";
        List<Integer> example = InputUtils.parseNewlineSeparatedInts(exampleStr);
        assertEquals(Day1.compute(example), 514579);
        assertEquals(Day1.computePart2(example), 241861950);
    }
}