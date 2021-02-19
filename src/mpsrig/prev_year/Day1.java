package mpsrig.prev_year;

import mpsrig.ListUtils;
import mpsrig.Runner;

import java.util.List;

public class Day1 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/1.txt", new Day1());
    }

    private List<Integer> parsedInput;

    protected void init() {
        parsedInput = ListUtils.map(input, Integer::parseInt);
    }

    static int fuelNeeded(int mass) {
        return Math.max((mass / 3) - 2, 0);
    }

    private long compute(boolean isPart2) {
        long result = 0;
        for (var elem : parsedInput) {
            if (isPart2) {
                result += recursiveFuelNeeded(elem);
            } else {
                result += fuelNeeded(elem);
            }
        }
        return result;
    }

    @Override
    public Object computePart1() {
        return compute(false);
    }

    static int recursiveFuelNeeded(int mass) {
        int result = fuelNeeded(mass);
        if (result == 0) {
            return 0;
        }
        return result + recursiveFuelNeeded(result);
    }

    @Override
    public Object computePart2() {
        return compute(true);
    }
}
