package mpsrig;

public class Day5 extends Runner.Computation {
    public static void main(String[] args) {
        System.out.println(getSeatID("FBFBBFFRLR"));
        Runner.run("/5.txt", new Day5());
    }

    public static int getSeatID(String x) {
        var binaryStr = x
                .replace('F', '0')
                .replace('B', '1')
                .replace('R', '1')
                .replace('L', '0');
        return Integer.parseInt(binaryStr, 2);
    }

    @Override
    public Object computePart1() {
        return input.stream().mapToInt(Day5::getSeatID).max();
    }

    @Override
    public Object computePart2() {
        var ordered = input.stream()
                .mapToInt(Day5::getSeatID)
                .sorted().toArray();
        for (int i = 0; i < ordered.length - 1; i++) {
            if (ordered[i] + 1 != ordered[i+1]) {
                return ordered[i] + 1;
            }
        }
        return null;
    }
}
