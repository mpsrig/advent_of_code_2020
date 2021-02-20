package mpsrig.prev_year;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntcodeComputerTest {

    static List<Long> runSimpleProgram(List<Long> program) {
        var c = new IntcodeComputer(program, Collections.emptyList());
        c.run();
        return c.getOutput();
    }

    @Test
    void emitsCopyOfProgram() {
        var program = Day2.parseProgram("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99");
        assertEquals(program, runSimpleProgram(program));
    }

    @Test
    void handlesLargeNumbers() {
        var program = Day2.parseProgram("104,1125899906842624,99");
        assertEquals(Collections.singletonList(1125899906842624L), runSimpleProgram(program));
    }

    @Test
    void outputsLargeNumber() {
        var program = Day2.parseProgram("1102,34915192,34915192,7,4,7,99,0");
        var output = runSimpleProgram(program);
        assertEquals(1, output.size());
        assertEquals(16, output.get(0).toString().length());
    }
}
