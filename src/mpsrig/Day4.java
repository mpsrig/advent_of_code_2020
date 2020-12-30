package mpsrig;

import java.util.*;

public class Day4 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/4.txt", new Day4());
    }

    @Override
    public Object computePart1() {
        return impl(false);
    }

    private int impl(boolean part2) {
        int countValid = 0;

        var currentPassport = new ArrayList<String>();
        for (String elem : input) {
            if (elem.equals("")) {
                if (isValid(currentPassport, part2)) {
                    countValid++;
                }
                currentPassport.clear();
            } else {
                currentPassport.add(elem);
            }
        }
        if (isValid(currentPassport, part2)) {
            countValid++;
        }

        return countValid;
    }

    private static boolean isValid(List<String> current, boolean part2) {
        var kv = new HashMap<String, String>();
        for (String line : current) {
            for (String token : line.split(" ")) {
                var parsed = token.split(":", 2);
                kv.put(parsed[0], parsed[1]);
            }
        }
        if (!kv.keySet().containsAll(Arrays.asList("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"))) {
            return false;
        }
        if (!part2) {
            return true;
        }

        String hgt = kv.get("hgt");
        // This check prevents a hypothetical exception from
        // substring(0, -2) or substring(0, -1), but isn't
        // actually needed on my input.
        if (hgt.length() < 4) {
            return false;
        }
        String hgtSubstr = hgt.substring(0, hgt.length() - 2);
        if (hgt.endsWith("cm")) {
            if (!checkIntString(hgtSubstr, 3, 150, 193)) {
                return false;
            }
        } else if (hgt.endsWith("in")) {
            if (!checkIntString(hgtSubstr, 2, 59, 76)) {
                return false;
            }
        } else {
            return false;
        }

        if (!Arrays.asList("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(kv.get("ecl"))) {
            return false;
        }

        var hcl = kv.get("hcl");
        // Also works on my input (regex not necessary):
        // if (!(hcl.length() == 7 && hcl.charAt(0) == '#')) {
        if (!hcl.matches("^#([0-9]|[a-f]){6}$")) {
            return false;
        }

        return checkIntString(kv.get("byr"), 4, 1920, 2002) &&
                checkIntString(kv.get("iyr"), 4, 2010, 2020) &&
                checkIntString(kv.get("eyr"), 4, 2020, 2030) &&
                checkIntString(kv.get("pid"), 9);

    }

    private static boolean checkIntString(String s, int len, int min, int max) {
        if (s.length() != len) {
            return false;
        }
        try {
            int x = Integer.parseInt(s);
            return min <= x && x <= max;
        } catch (NumberFormatException e) {
            // This catch block is never needed on my input
            return false;
        }
    }

    private static boolean checkIntString(String s, int len) {
        if (s.length() != len) {
            return false;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            // This catch block is never needed on my input
            return false;
        }
    }

    @Override
    public Object computePart2() {
        return impl(true);
    }
}
