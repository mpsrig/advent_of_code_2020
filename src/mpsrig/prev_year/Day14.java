package mpsrig.prev_year;

import mpsrig.ListUtils;
import mpsrig.Runner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/14-test.txt", new Day14());
    }

    @Override
    protected void init() {
        reactionLookup = buildReactionLookup(ListUtils.map(input, Day14::parse));
    }

    @Override
    public Object computePart1() {
        var m = new MutableReaction();
        m.process();
        return m;
    }

    @Override
    public Object computePart2() {
        return null;
    }

    static Equation parse(String line) {
        var leftAndRight = line.split(" => ");
        if (leftAndRight.length != 2) {
            throw new IllegalArgumentException("Could not parse " + line);
        }
        var leftPieces = leftAndRight[0].split(", ");
        var inputTerms = ListUtils.map(Arrays.asList(leftPieces), Day14::parseTerm);
        var outputTerm = parseTerm(leftAndRight[1]);
        return new Equation(inputTerms, outputTerm);
    }

    static Term parseTerm(String t) {
        var parts = t.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Could not parse term " + t);
        }
        return new Term(Integer.parseInt(parts[0]), parts[1]);
    }

    static class Term {
        final int n;
        final String chemical;

        Term(int n, String chemical) {
            this.n = n;
            this.chemical = chemical;
        }
    }

    static class Equation {
        final List<Term> input;
        final Term output;

        Equation(List<Term> input, Term output) {
            this.input = input;
            this.output = output;
        }
    }

    static Map<String, Equation> buildReactionLookup(List<Equation> l) {
        var out = new HashMap<String, Equation>();
        for (var elem : l) {
            var chem = elem.output.chemical;
            if (out.containsKey(chem)) {
                throw new IllegalStateException("Found two equations that produce: " + chem);
            }
            out.put(chem, elem);
        }
        return out;
    }

    Map<String, Equation> reactionLookup;

    class MutableReaction {
        Map<String, Integer> neededInputs = new HashMap<>();
        Map<String, Integer> availableOutputs = new HashMap<>();

        MutableReaction() {
            var e = reactionLookup.get("FUEL");
            availableOutputs.put(e.output.chemical, e.output.n);
            for (var i : e.input) {
                neededInputs.put(i.chemical, i.n);
            }
        }

        void simplify() {
            for (var iterator = neededInputs.entrySet().iterator(); iterator.hasNext();) {
                var elem = iterator.next();

                var avail = availableOutputs.get(elem.getKey());
                if (avail != null) {
                    int delta = Math.min(elem.getValue(), avail);
                    int newAvail = avail - delta;
                    int newNeeded = elem.getValue() - delta;

                    if (newNeeded == 0) {
                        iterator.remove();
                    } else {
                        elem.setValue(newNeeded);
                    }

                    if (newAvail == 0) {
                        availableOutputs.remove(elem.getKey());
                    } else {
                        availableOutputs.put(elem.getKey(), newAvail);
                    }
                }
            }
        }

        String getFirstNeededInput() {
            for (var elem : neededInputs.keySet()) {
                if (!elem.equals("ORE")) {
                    return elem;
                }
            }
            return null;
        }

        void substituteInput(String chem) {
            debug();
            if (availableOutputs.containsKey(chem)) {
                simplify();
                debug();
                if (!neededInputs.containsKey(chem)) {
                    // Simplify eliminated the input
                    return;
                }
            }

            var neededQty = neededInputs.remove(chem);
            var equation = reactionLookup.get(chem);

            if (!chem.equals(equation.output.chemical)) {
                throw new IllegalStateException("Did not look up correct reaction");
            }
            int multiplier = (int) Math.ceil(((double) neededQty) / equation.output.n);

            for (var input : equation.input) {
                int currentNeeded = neededInputs.getOrDefault(input.chemical, 0);
                int newNeeded = currentNeeded + multiplier * input.n;
                neededInputs.put(input.chemical, newNeeded);
            }

            if (availableOutputs.containsKey(chem)) {
                throw new IllegalStateException("Simplify failed");
            }
            int spareOutput = (multiplier * equation.output.n) - neededQty;
            if (spareOutput != 0)
            availableOutputs.put(chem, spareOutput);
            debug();
        }

        void process() {
            while (true) {
                var c = getFirstNeededInput();
                if (c == null) {
                    return;
                }
                substituteInput(c);
            }
        }

        @Override
        public String toString() {
            return "MutableReaction{" +
                    "neededInputs=" + neededInputs +
                    ", availableOutputs=" + availableOutputs +
                    '}';
        }
        
        private void debug() {
//            System.err.println(this);
        }
    }
}