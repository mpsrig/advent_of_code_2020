package mpsrig.prev_year;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Day4Test {

    @Test
    void digits() {
        assertEquals(Day4.getDigits(111111), Arrays.asList(1, 1, 1, 1, 1, 1));
        assertEquals(Day4.getDigits(223450), Arrays.asList(2, 2, 3, 4, 5, 0));
    }

    @Test
    void checkPassword() {
        assertTrue(Day4.checkPassword(111111));
        assertFalse(Day4.checkPassword(223450));
        assertFalse(Day4.checkPassword(123789));
    }

    @Test
    void checkPasswordPart2() {
        assertTrue(Day4.checkPasswordPart2(112233));
        assertFalse(Day4.checkPasswordPart2(123444));
        assertTrue(Day4.checkPasswordPart2(111122));
    }
}