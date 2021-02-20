package mpsrig.prev_year;

import java.util.*;

public class IntcodeComputer {
    private interface Param {
        long read();

        void write(long value);
    }

    private static abstract class Operation {
        private final int numParams;

        protected Operation(int numParams) {
            this.numParams = numParams;
        }

        public int getNumParams() {
            return numParams;
        }

        public abstract void execute(List<Param> params, IntcodeComputer computer);
    }

    private static abstract class ArithmeticOperation extends Operation {
        protected ArithmeticOperation() {
            super(3);
        }

        public void execute(List<Param> params, IntcodeComputer computer) {
            params.get(2).write(performArithmetic(params.get(0).read(), params.get(1).read()));
        }

        protected abstract long performArithmetic(long x, long y);
    }

    private static abstract class BooleanOperation extends ArithmeticOperation {
        protected long performArithmetic(long x, long y) {
            return performCheck(x, y) ? 1 : 0;
        }

        protected abstract boolean performCheck(long x, long y);
    }

    private static class JumpOperation extends Operation {
        private final boolean jumpOnNonZero;

        protected JumpOperation(boolean jumpOnNonZero) {
            super(2);
            this.jumpOnNonZero = jumpOnNonZero;
        }

        @Override
        public void execute(List<Param> params, IntcodeComputer computer) {
            var testVal = params.get(0).read();
            if ((jumpOnNonZero && testVal != 0) || (!jumpOnNonZero && testVal == 0)) {
                // upper level will unconditionally add 3 to pc
                // to compensate for the size of the instruction
                computer.pc = Math.toIntExact(params.get(1).read()) - 3;
            }
        }
    }

    private class MemoryParam implements Param {
        public MemoryParam(int addr) {
            while (addr >= memory.size()) {
                memory.add(0L);
            }
            this.addr = addr;
        }

        private final int addr;

        @Override
        public long read() {
            return memory.get(addr);
        }

        @Override
        public void write(long value) {
            memory.set(addr, value);
        }
    }

    private static class ImmediateParam implements Param {
        private final long val;

        private ImmediateParam(long val) {
            this.val = val;
        }

        @Override
        public long read() {
            return val;
        }

        @Override
        public void write(long value) {
            throw new IllegalStateException("Immediate params can never be written");
        }
    }

    private static final Map<Integer, Operation> OPERATIONS_MAP = new HashMap<>();

    static {
        // ADD
        OPERATIONS_MAP.put(1, new ArithmeticOperation() {
            @Override
            public long performArithmetic(long x, long y) {
                return x + y;
            }
        });

        // MULTIPLY
        OPERATIONS_MAP.put(2, new ArithmeticOperation() {
            @Override
            public long performArithmetic(long x, long y) {
                return x * y;
            }
        });

        // INPUT
        OPERATIONS_MAP.put(3, new Operation(1) {
            @Override
            public void execute(List<Param> params, IntcodeComputer computer) {
                params.get(0).write(computer.input.remove());
            }
        });

        // OUTPUT
        OPERATIONS_MAP.put(4, new Operation(1) {
            @Override
            public void execute(List<Param> params, IntcodeComputer computer) {
                computer.output.add(params.get(0).read());
            }
        });

        // RELATIVE BASE OFFSET
        OPERATIONS_MAP.put(9, new Operation(1) {
            @Override
            public void execute(List<Param> params, IntcodeComputer computer) {
                computer.relativeBase += params.get(0).read();
            }
        });

        // LESS THAN
        OPERATIONS_MAP.put(7, new BooleanOperation() {
            @Override
            public boolean performCheck(long x, long y) {
                return x < y;
            }
        });

        // EQUAL
        OPERATIONS_MAP.put(8, new BooleanOperation() {
            @Override
            public boolean performCheck(long x, long y) {
                return x == y;
            }
        });

        // TERMINATE
        OPERATIONS_MAP.put(99, new Operation(0) {
            @Override
            public void execute(List<Param> params, IntcodeComputer computer) {
                computer.terminated = true;
            }
        });

        // JUMP
        OPERATIONS_MAP.put(5, new JumpOperation(true));
        OPERATIONS_MAP.put(6, new JumpOperation(false));
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

    private final ArrayList<Long> memory;
    private final ArrayDeque<Long> input;
    private int pc = 0;
    private final ArrayList<Long> output = new ArrayList<>();
    private boolean terminated = false;
    private long relativeBase = 0;

    public IntcodeComputer(List<Long> memory, List<Long> input) {
        this.memory = new ArrayList<>(memory);
        this.input = new ArrayDeque<>(input);
    }

    public List<Long> getMemory() {
        return Collections.unmodifiableList(memory);
    }

    public List<Long> getOutput() {
        return Collections.unmodifiableList(output);
    }

    public void addInput(long inputVal) {
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

        int opcode = Math.toIntExact(memory.get(pc));
        int opcodeBase = opcode % 100;

        Operation o = OPERATIONS_MAP.get(opcodeBase);
        o.execute(parseParams(opcode, o.getNumParams()), this);
        pc += 1 + o.getNumParams();
    }

    public Long runUntilOutputAndYield() {
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
            long paramVal = memory.get(pc + 1 + i);
            params.add(makeParam(modes[i], paramVal));
        }
        return params;
    }

    private Param makeParam(int mode, long paramVal) {
        return switch (mode) {
            case 0 -> new MemoryParam(Math.toIntExact(paramVal));
            case 1 -> new ImmediateParam(paramVal);
            case 2 -> new MemoryParam(Math.toIntExact(paramVal + relativeBase));
            default -> throw new IllegalArgumentException("Unknown param mode " + mode);
        };
    }
}
