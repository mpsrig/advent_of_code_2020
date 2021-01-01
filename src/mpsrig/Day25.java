package mpsrig;

public class Day25 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/25.txt", new Day25());
    }

    private long publicKeyA;
    private long publicKeyB;

    @Override
    protected void init() {
        super.init();
        publicKeyA = Long.parseLong(input.get(0));
        publicKeyB = Long.parseLong(input.get(1));
    }

    public static long runTransform(long subjectNumber, int loopSize) {
        long value = 1;
        for (int i = 0; i < loopSize; i++) {
            value = transform(subjectNumber, value);
        }
        return value;
    }

    public static long transform(long subjectNumber, long value) {
        value *= subjectNumber;
        value %= 20201227;
        return value;
    }

    @Override
    public Object computePart1() {
        long value = 1;
        for (int i = 0;; i++) {
            if (value == publicKeyA) {
                return runTransform(publicKeyB, i);
            }
            if (value == publicKeyB) {
                return runTransform(publicKeyA, i);
            }
            value = transform(7, value);
        }
    }

    @Override
    public Object computePart2() {
        return null;
    }
}
