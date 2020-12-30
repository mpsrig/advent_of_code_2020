package mpsrig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Day3Test {
    @Test
    void example() {
        assertEquals(Day3.computeTrees(ResourceUtils.getLinesFromResource("/3-test.txt"), 3, 1), 7);
    }
}