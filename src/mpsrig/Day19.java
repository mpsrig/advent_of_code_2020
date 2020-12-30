package mpsrig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/19.txt", new Day19());
    }

    private Map<Integer, String> ruleDefinitons;
    private List<String> candidates;
    private int maxCandidateLength;

    @Override
    protected void init() {
        super.init();
        ruleDefinitons = new HashMap<>();

        var iter = input.listIterator();
        for (var line = iter.next(); line.length() != 0; line = iter.next()) {
            var s = line.split(": ", 2);
            ruleDefinitons.put(Integer.parseInt(s[0]), s[1]);
        }

        candidates = input.subList(iter.nextIndex(), input.size());
        maxCandidateLength = candidates.stream().mapToInt(String::length).max().orElseThrow();
    }

    private static final Pattern SINGLE_CHAR_DEFINITON_PATTERN = Pattern.compile("^\"(.)\"$");

    private final Memoize<String> getRegexStrMemoizer = new Memoize<>();

    private String getRegexStrCached(int ruleNumber, final boolean isPart2) {
        return getRegexStrMemoizer.getOrCompute(Arrays.asList(ruleNumber, isPart2), () -> getRegexStr(ruleNumber, isPart2));
    }

    private String getRegexStr(final int ruleNumber, final boolean isPart2) {
        if (isPart2) {
            if (ruleNumber == 8) {
                // def = "42 | 42 8";
                return "(" + getRegexStrCached(42, true) + ")+";
            } else if (ruleNumber == 11) {
                // def = "42 31 | 42 11 31";
                var rule42 = getRegexStrCached(42, true);
                var rule31 = getRegexStrCached(31, true);
                var sb = new StringBuilder();
                sb.append("(");
                for (int i = 1; i < ((maxCandidateLength + 1) / 2); i++) {
                    if (i != 1) {
                        sb.append("|");
                    }
                    for (int j = 0; j < i; j++) {
                        sb.append(rule42);
                    }
                    for (int j = 0; j < i; j++) {
                        sb.append(rule31);
                    }
                }
                sb.append(")");
                return sb.toString();
            }
        }

        var def = Objects.requireNonNull(ruleDefinitons.get(ruleNumber));
        {
            var m = SINGLE_CHAR_DEFINITON_PATTERN.matcher(def);
            if (m.find()) {
                return m.group(1);
            }
        }
        var sb = new StringBuilder();
        var orGroups = def.split("\\|");
        if (orGroups.length > 1) {
            sb.append("(");
        }
        for (int i = 0; i < orGroups.length; i++) {
            String g = orGroups[i];
            if (i != 0) {
                sb.append("|");
            }
            for (var elem : g.trim().split(" ")) {
                var innerRuleNum = Integer.parseInt(elem);
                sb.append(getRegexStrCached(innerRuleNum, isPart2));
            }
        }
        if (orGroups.length > 1) {
            sb.append(")");
        }
        return sb.toString();
    }

    private long impl(boolean isPart2) {
        var s = getRegexStrCached(0, isPart2);
//        long t0 = System.currentTimeMillis();
        var p = Pattern.compile(s);
//        System.err.println(System.currentTimeMillis() - t0);

        long counter = 0;
        for (var line : candidates) {
            var m = p.matcher(line);
            if (m.matches()) {
                counter++;
            }
        }
        return counter;
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
