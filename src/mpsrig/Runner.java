package mpsrig;

import java.util.Collections;
import java.util.List;

public class Runner {
    public static abstract class Computation {
        protected List<String> input;

        private void setInput(List<String> input) {
            this.input = input;
        }

        protected void init() {}

        public abstract Object computePart1();
        public abstract Object computePart2();
    }

    public static void run(String res, Computation computation) {
        long start = System.currentTimeMillis();
        var input = ResourceUtils.getLinesFromResource(res);
        computation.setInput(Collections.unmodifiableList(input));
        long beforeInit = System.currentTimeMillis();
        computation.init();
        long afterInit = System.currentTimeMillis();
        System.out.println("Part 1: " + computation.computePart1());
        long afterPart1 = System.currentTimeMillis();
        System.out.println("Part 2: " + computation.computePart2());
        long end = System.currentTimeMillis();
        System.out.println("Run took " + (end - start) +
                "ms. Loading input: " + (beforeInit - start)  +
                " ms, Init: " + (afterInit - beforeInit) +
                " ms, Part 1: " + (afterPart1 - afterInit) +
                " ms, Part 2: " + (end - afterPart1) + " ms.");
    }
}
