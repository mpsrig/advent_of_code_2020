package mpsrig;

import java.util.*;

public class Day23 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/23.txt", new Day23());
    }

    private List<Integer> startingCups;

    @Override
    protected void init() {
        super.init();
        startingCups = Collections.unmodifiableList(ListUtils.map(StringUtils.toCodePoints(input.get(0)), Integer::parseInt));
    }

    private Queue<Integer> run100Rounds() {
        var cups = new LinkedQueue<Integer>();
        cups.addAll(startingCups);
        for (int i = 0; i < 100; i++) {
            runRound(false, cups);
        }
        return cups;
    }

    private static void runRound(final boolean isPart2, LinkedQueue<Integer> cups) {
        var current = cups.remove();
        cups.add(current);

        var pickedUp = Arrays.asList(cups.remove(), cups.remove(), cups.remove());

        int destination = current - 1;
        if (destination == 0) {
            if (isPart2) {
                destination = 1000000;
            } else {
                destination = 9;
            }
        }
        while (pickedUp.contains(destination)) {
            destination--;
            if (destination == 0) {
                if (isPart2) {
                    destination = 1000000;
                } else {
                    destination = 9;
                }
            }
        }

        Integer destinationObj = destination;

        Collections.reverse(pickedUp);
        for (var elem : pickedUp) {
            cups.addAfter(destinationObj, elem);
        }

//        System.err.println(cups);
    }

    @Override
    public Object computePart1() {
        var cups = run100Rounds();
        while (cups.peek() != 1) {
            cups.add(cups.remove());
        }
        cups.remove();

        var sb = new StringBuilder();
        for (var elem : cups) {
            sb.append(elem);
        }
        return sb.toString();
    }

    @Override
    public Object computePart2() {
        var cups = new LinkedQueue<Integer>();
        cups.addAll(startingCups);
        for (int j = 10; j <= 1000000; j++) {
            cups.add(j);
        }
//        System.err.println("list filled");

        for (int i = 0; i < 10000000; i++) {
            runRound(true, cups);
//            if (i % 100000 == 0){
//                System.err.println("i = " + i);
//            }
        }

        var iter = cups.iteratorStartingAt(1);
        iter.next();
        long firstStar = iter.next();
        long secondStar = iter.next();

        return firstStar * secondStar;
    }
}
