package mpsrig.prev_year;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day4 {
    public static ArrayList<Integer> getDigits(int val) {
        ArrayList<Integer> out = new ArrayList<>();
        while (val != 0) {
            out.add(val % 10);
            val /= 10;
        }
        Collections.reverse(out);
        return out;
    }

    public static boolean checkIncreasing(List<Integer> digits) {
        int previous = 0;
        for (int elem : digits) {
            if (elem < previous) {
                return false;
            }
            previous = elem;
        }
        return true;
    }

    public static boolean checkAdjacent(List<Integer> digits) {
        for (int i = 0; i < digits.size() - 1; i++) {
            if (digits.get(i).equals(digits.get(i+1))) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkAdjacentPart2(List<Integer> digits) {
        for (int i = 0; i < digits.size() - 1; i++) {
            int x = digits.get(i);
            if (x == digits.get(i+1)) {
                boolean foundAdditional = false;
                for (int j = i + 2; j < digits.size() && digits.get(j) == x; j++) {
                    i++;
                    foundAdditional = true;
                }
                if (!foundAdditional) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkPassword(int candidate) {
        List<Integer> digits = getDigits(candidate);
        return checkAdjacent(digits) && checkIncreasing(digits);
    }

    public static boolean checkPasswordPart2(int candidate) {
        List<Integer> digits = getDigits(candidate);
        return checkAdjacentPart2(digits) && checkIncreasing(digits);
    }

    public static void main(String[] args) {
        int part1Count = 0;
        int part2Count = 0;
        for (int x = 158126; x <= 624574; x++) {
            if (checkPassword(x)) {
                part1Count++;
            }
            if (checkPasswordPart2(x)) {
                part2Count++;
            }
        }
        System.out.println("Part 1: " + part1Count);
        System.out.println("Part 2: " + part2Count);
    }
}
