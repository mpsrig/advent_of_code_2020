package mpsrig.twentytwentyone;

import mpsrig.InputUtils;
import mpsrig.Runner;

import java.util.List;

public class Day1 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/twentytwentyone/1.txt", new Day1());
    }

    private List<Integer> inputInts;

    @Override
    protected void init() {
        inputInts = InputUtils.parseInts(input);
    }

    @Override
    public Object computePart1() {
        int count = 0;
        for (int i = 1; i < inputInts.size(); i++) {
            if (inputInts.get(i) > inputInts.get(i-1)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Object computePart2() {
        int count = 0;
        for (int i = 3; i < inputInts.size(); i++) {
            int common = inputInts.get(i-2) + inputInts.get(i-1);
            int prevWindow = common + inputInts.get(i-3);
            int currWindow = common + inputInts.get(i);
            if (currWindow > prevWindow) {
                count++;
            }
        }
        return count;
    }
}
