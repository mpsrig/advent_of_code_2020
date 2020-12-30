package mpsrig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Day2Test {
    @Test
    void examplePasswordLines() {
        assertEquals(Day2.isValidPassword("1-3 a: abcde"), true);
        assertEquals(Day2.isValidPassword("1-3 b: cdefg"), false);
        assertEquals(Day2.isValidPassword("2-9 c: ccccccccc"), true);

        assertEquals(Day2.isValidPasswordPartTwo("1-3 a: abcde"), true);
        assertEquals(Day2.isValidPasswordPartTwo("1-3 b: cdefg"), false);
        assertEquals(Day2.isValidPasswordPartTwo("2-9 c: ccccccccc"), false);
    }
}