package mpsrig;

import java.util.ArrayList;
import java.util.List;

public class Day8 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/8.txt", new Day8());
    }

    private static class Instruction {
        final String op;
        final int val;

        Instruction(String op, int val) {
            this.op = op;
            this.val = val;
        }

        static Instruction parse(String line) {
            var parsed = line.split(" ");
            return new Instruction(parsed[0], Integer.parseInt(parsed[1]));
        }
    }

    private List<Instruction> instructions;

    @Override
    protected void init() {
        super.init();
        instructions = ListUtils.map(input, Instruction::parse);
    }

    private static class ProgramResult {
        final boolean exitedOnInfiniteLoop;
        final int accumulatorValue;

        private ProgramResult(boolean exitedOnInfiniteLoop, int accumulatorValue) {
            this.exitedOnInfiniteLoop = exitedOnInfiniteLoop;
            this.accumulatorValue = accumulatorValue;
        }
    }

    public static ProgramResult runProgram(List<Instruction> instructions) {
        var sz = instructions.size();
        var visited = new boolean[sz];

        int pc = 0;
        int accumulator = 0;
        while (true) {
            if (pc == sz) {
                return new ProgramResult(false, accumulator);
            }
            if (visited[pc]) {
                return new ProgramResult(true, accumulator);
            }
            visited[pc] = true;
            var inst = instructions.get(pc);
            switch (inst.op) {
                case "nop" -> pc++;
                case "acc" -> {
                    accumulator += inst.val;
                    pc++;
                }
                case "jmp" -> pc += inst.val;
                default -> throw new IllegalArgumentException("Invalid instruction input: " + inst.op);
            }
        }
    }

    @Override
    public Object computePart1() {
        var result = runProgram(instructions);
        if (!result.exitedOnInfiniteLoop) {
            throw new IllegalStateException();
        }
        return result.accumulatorValue;
    }

    @Override
    public Object computePart2() {
        for (int i = 0; i < instructions.size(); i++) {
            var instr = instructions.get(i);
            var op = instr.op;
            if (op.equals("acc")) {
                continue;
            }

            var instructionsCopy = new ArrayList<>(instructions);
            var replacementOp = switch (op) {
                case "nop" -> "jmp";
                case "jmp" -> "nop";
                default -> throw new IllegalArgumentException("Invalid instruction input: " + op);
            };
            instructionsCopy.set(i, new Instruction(replacementOp, instr.val));

            var result = runProgram(instructionsCopy);
            if (!result.exitedOnInfiniteLoop) {
                return result.accumulatorValue;
            }
        }
        return null;
    }
}
