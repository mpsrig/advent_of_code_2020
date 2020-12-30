package mpsrig;

import java.util.List;

public class Day1 {
    public static int compute(List<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                int x = input.get(i);
                int y = input.get(j);
                if (x + y == 2020) {
                    return x * y;
                }
            }
        }
        throw new IllegalArgumentException("Unsolvable");
    }

    public static int computePart2(List<Integer> input) {
        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {
                for (int k = j + 1; k < input.size(); k++) {
                    int x = input.get(i);
                    int y = input.get(j);
                    int z = input.get(k);
                    if (x + y + z == 2020) {
                        System.err.println("i = " + i + " j = " + j + " k = " + k);
                        System.err.println("x = " + x + " y = " + y + " z = " + z);
                        return x * y * z;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unsolvable");
    }

    public static void main(String[] args) {
        List<String> input = ResourceUtils.getLinesFromResource("/1.txt");
        List<Integer> parsedInput = InputUtils.parseInts(input);
        System.out.println("Part 1: " + compute(parsedInput));
        System.out.println("Part 2: " + computePart2(parsedInput));
    }
}
