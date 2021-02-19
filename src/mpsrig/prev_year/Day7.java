package mpsrig.prev_year;

import mpsrig.Runner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Day7 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/7.txt", new Day7());
    }

    private List<Integer> parsedProgram = null;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }

    private int runAmplifierChain(int[] phaseSettings) {
        if (phaseSettings.length != 5) {
            throw new IllegalArgumentException();
        }
        int signal = 0;
        for (var phaseSetting : phaseSettings) {
            var computer = new IntcodeComputer(parsedProgram, Arrays.asList(phaseSetting, signal));
            computer.run();
            var output = computer.getOutput();
            if (output.size() != 1) {
                throw new IllegalStateException();
            }
            signal = output.get(0);
        }
        return signal;
    }

    // Heap's Algorithm
    public static void permutate(int[] list, int n, Consumer<int[]> consumer) {
        if (n == 1) {
            consumer.accept(list);
        } else {
            for (int i = 0; i < n; i++) {
                permutate(list, n - 1, consumer);

                int j = (n % 2 == 0) ? i : 0;

                int t = list[n - 1];
                list[n - 1] = list[j];
                list[j] = t;
            }
        }
    }

    @Override
    public Object computePart1() {
        AtomicInteger maximumOutput = new AtomicInteger();
        permutate(new int[]{0, 1, 2, 3, 4}, 5, x -> maximumOutput.set(Math.max(maximumOutput.get(), runAmplifierChain(x))));
        return maximumOutput.get();
    }

    @Override
    public Object computePart2() {
        return null;
    }
}
