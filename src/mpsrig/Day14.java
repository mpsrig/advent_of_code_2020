package mpsrig;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Day14 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/14.txt", new Day14());
    }

    private final static int WORD_SIZE = 36;

    private static long applyMask(String mask, long value) {
        return Long.parseLong(String.valueOf(applyMaskImpl(false, mask, value)), 2);
    }

    private static char[] applyMaskImpl(boolean part2, String mask, long value) {
        if (mask.length() != WORD_SIZE) {
            throw new IllegalArgumentException(mask);
        }
        var valueBinary = Long.toBinaryString(value);
        if (valueBinary.length() > WORD_SIZE) {
            throw new IllegalArgumentException("value: " + value);
        }
        var outputValue = new char[WORD_SIZE];
        int valueIdxOffset = valueBinary.length() - WORD_SIZE;
        for (int i = WORD_SIZE - 1; i >= 0; i--) {
            char currentValueChar;
            int valueIdx = i + valueIdxOffset;
            if (valueIdx >= 0) {
                currentValueChar = valueBinary.charAt(valueIdx);
            } else {
                currentValueChar = '0';
            }
            char maskChar = mask.charAt(i);
            if (!part2) {
                if (maskChar != 'X') {
                    outputValue[i] = maskChar;
                } else {
                    outputValue[i] = currentValueChar;
                }
            } else {
                if (maskChar == '0') {
                    outputValue[i] = currentValueChar;
                } else {
                    outputValue[i] = maskChar;
                }
            }
        }
        return outputValue;
    }

//    private static long parseLongFast(char[] bits) {
//        long out = 0;
//        long multiplier = 1;
//        for (int i = bits.length - 1; i >= 0; i--) {
//            if (bits[i] == '1') {
//                out += multiplier;
//            }
//            multiplier *= 2;
//        }
//        return out;
//    }

    private List<Long> getAllMemoryAddresses(String mask, long value) {
        var afterMaskAppliedChars = applyMaskImpl(true, mask, value);

        var indexesOfX = new ArrayList<Integer>();
        for (int i = 0; i < afterMaskAppliedChars.length; i++) {
            if (afterMaskAppliedChars[i] == 'X') {
                indexesOfX.add(i);
            }
        }

        int numPermutations = (int) Math.pow(2, indexesOfX.size());
        var out = new ArrayList<Long>(numPermutations);
        for (int j = 0; j < numPermutations; j++) {
            var binaryStr = Integer.toBinaryString(j);
            int binaryStrIdxOffset = binaryStr.length() - indexesOfX.size();
            for (int k = 0; k < indexesOfX.size(); k++) {
                int binaryStrIdx = k + binaryStrIdxOffset;
                char selected;
                if (binaryStrIdx >= 0) {
                    selected = binaryStr.charAt(binaryStrIdx);
                } else {
                    selected = '0';
                }
                afterMaskAppliedChars[indexesOfX.get(k)] = selected;
            }
            out.add(Long.parseLong(String.valueOf(afterMaskAppliedChars), 2));
        }
        return out;
    }

    private long impl(boolean part2) {
        var memory = new HashMap<Long, Long>();
        String currentMask = "X".repeat(36);
        for (var line : input) {
            var parts = line.split(" = ");
            if (parts.length != 2) {
                throw new IllegalArgumentException(line);
            }
            if ("mask".equals(parts[0])) {
                currentMask = parts[1];
                continue;
            }
            if (!parts[0].startsWith("mem[")) {
                throw new IllegalArgumentException(line);
            }
            long addr = Long.parseLong(parts[0].substring(parts[0].indexOf('[') + 1, parts[0].indexOf(']')));
            long val = Long.parseLong(parts[1]);

            if (!part2) {
                memory.put(addr, applyMask(currentMask, val));
            } else {
                for (var elem : getAllMemoryAddresses(currentMask, addr)) {
                    memory.put(elem, val);
                }
            }
        }

        long sum = 0;
        for (var entry : memory.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    @Override
    public Object computePart1() {
        return impl(false);
    }

    @Override
    public Object computePart2() {
        return impl(true);
    }
}
