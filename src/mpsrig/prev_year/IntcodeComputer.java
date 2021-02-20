package mpsrig.prev_year;

import java.util.*;

public class IntcodeComputer {
    private interface Param {
        int read();

        void write(int value);
    }

    private static abstract class Operation {
        private final int numParams;

        protected Operation(int numParams) {
            this.numParams = numParams;
        }

        public int getNumParams() {
            return numParams;
        }

        public abstract void execute(List<Param> params, Queue<Integer> input, List<Integer> output);
    }

    private static abstract class ArithmeticOperation extends Operation {
        protected ArithmeticOperation() {
            super(3);
        }

        public void execute(List<Param> params, Queue<Integer> input, List<Integer> output) {
            params.get(2).write(performArithmetic(params.get(0).read(), params.get(1).read()));
        }

        protected abstract int performArithmetic(int x, int y);
    }

    private static abstract class BooleanOperation extends ArithmeticOperation {
        protected int performArithmetic(int x, int y) {
            return performCheck(x, y) ? 1 : 0;
        }

        protected abstract boolean performCheck(int x, int y);
    }

    private class MemoryParam implements Param {
        public MemoryParam(int addr) {
            this.addr = addr;
        }

        private final int addr;

        @Override
        public int read() {
            return memory.get(addr);
        }

        @Override
        public void write(int value) {
            memory.set(addr, value);
        }
    }

    private static class ImmediateParam implements Param {
        private final int val;

        private ImmediateParam(int val) {
            this.val = val;
        }

        @Override
        public int read() {
            return val;
        }

        @Override
        public void write(int value) {
            throw new IllegalStateException("Immediate params can never be written");
        }
    }

    private static final Map<Integer, Operation> OPERATIONS_MAP = new HashMap<>();

    static {
        // ADD
        OPERATIONS_MAP.put(1, new ArithmeticOperation() {
            @Override
            public int performArithmetic(int x, int y) {
                return x + y;
            }
        });

        // MULTIPLY
        OPERATIONS_MAP.put(2, new ArithmeticOperation() {
            @Override
            public int performArithmetic(int x, int y) {
                return x * y;
            }
        });

        // INPUT
        OPERATIONS_MAP.put(3, new Operation(1) {
            @Override
            public void execute(List<Param> params, Queue<Integer> input, List<Integer> output) {
                params.get(0).write(input.remove());
            }
        });

        // OUTPUT
        OPERATIONS_MAP.put(4, new Operation(1) {
            @Override
            public void execute(List<Param> params, Queue<Integer> input, List<Integer> output) {
                output.add(params.get(0).read());
            }
        });

        // LESS THAN
        OPERATIONS_MAP.put(7, new BooleanOperation() {
            @Override
            public boolean performCheck(int x, int y) {
                return x < y;
            }
        });

        // EQUAL
        OPERATIONS_MAP.put(8, new BooleanOperation() {
            @Override
            public boolean performCheck(int x, int y) {
                return x == y;
            }
        });
    }

    static int[] parseOpcodeModes(int opcode, int numParams) {
        int[] out = new int[numParams];
        opcode /= 100;
        for (int i = 0; opcode != 0; i++) {
            out[i] = opcode % 10;
            opcode /= 10;
        }
        return out;
    }

    private final ArrayList<Integer> memory;
    private final ArrayDeque<Integer> input;
    private int pc = 0;
    private final ArrayList<Integer> output = new ArrayList<>();
    private boolean terminated = false;

    public IntcodeComputer(List<Integer> memory, List<Integer> input) {
        this.memory = new ArrayList<>(memory);
        this.input = new ArrayDeque<>(input);
    }

    public List<Integer> getMemory() {
        return Collections.unmodifiableList(memory);
    }

    public List<Integer> getOutput() {
        return Collections.unmodifiableList(output);
    }

    public void addInput(int inputVal) {
        input.add(inputVal);
    }

    public void run() {
        while (!terminated) {
            step();
        }
    }

    private void step() {
        if (terminated) {
            throw new IllegalStateException("Program already terminated!");
        }

        int opcode = memory.get(pc);
        int opcodeBase = opcode % 100;

        // TERMINATE
        if (opcodeBase == 99) {
            terminated = true;
            return;
        }

        // JUMP
        if (opcodeBase == 5 || opcodeBase == 6) {
            List<Param> params = parseParams(opcode, 2);
            int testVal = params.get(0).read();
            if ((opcodeBase == 5 && testVal != 0) || (opcodeBase == 6 && testVal == 0)) {
                pc = params.get(1).read();
            } else {
                pc += 3;
            }
            return;
        }

        Operation o = OPERATIONS_MAP.get(opcodeBase);
//            if (o == null) {
//                System.err.println(Day2.serializeProgram(memory));
//                System.err.println(input);
//                System.err.println(output);
//                System.err.println(pc);
//            }
        o.execute(parseParams(opcode, o.getNumParams()), input, output);
        pc += 1 + o.getNumParams();
    }

    public Integer runUntilOutputAndYield() {
        var startOutputSize = output.size();
        while (startOutputSize == output.size()) {
            step();
            if (terminated) {
                return null;
            }
        }
        if (startOutputSize + 1 != output.size()) {
            throw new IllegalStateException();
        }
        return output.get(startOutputSize);
    }

    private List<Param> parseParams(int opcode, int numParams) {
        int[] modes = parseOpcodeModes(opcode, numParams);
        List<Param> params = new ArrayList<>(modes.length);
        for (int i = 0; i < modes.length; i++) {
            int paramVal = memory.get(pc + 1 + i);
            params.add(makeParam(modes[i], paramVal));
        }
        return params;
    }

    private Param makeParam(int mode, int paramVal) {
        return switch (mode) {
            case 0 -> new MemoryParam(paramVal);
            case 1 -> new ImmediateParam(paramVal);
            default -> throw new IllegalArgumentException("Unknown param mode " + mode);
        };
    }
}
