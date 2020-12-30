package mpsrig;

public class Day18 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/18.txt", new Day18());
    }

    @Override
    protected void init() {
        super.init();
    }

    private static long eval(String computation, boolean part2) {
        var tokens = computation.replace("(", "( ").replace(")", " )").split(" ");
        var r = part2 ? evalTokensPart2(tokens, 0) : evalTokens(tokens, 0);
        if (r.nextTokenIdx != tokens.length) {
            throw new IllegalStateException();
        }
        return r.val;
    }

    private static class SubResult {
        final long val;
        final int nextTokenIdx;

        private SubResult(long val, int nextTokenIdx) {
            this.val = val;
            this.nextTokenIdx = nextTokenIdx;
        }
    }

    private static SubResult evalTokens(String[] tokens, final int begin) {
        char currentOperator = '+';
        long runningVal = 0;
        int i = begin;
        while (true) {
            if (i == tokens.length) {
                return new SubResult(runningVal, i);
            }
            var currentToken = tokens[i];
            Long arg = null;
            if (currentToken.equals("+")) {
                currentOperator = '+';
                i++;
            } else if (currentToken.equals("*")) {
                currentOperator = '*';
                i++;
            } else if (currentToken.equals("(")) {
                var subResult = evalTokens(tokens, i + 1);
                arg = subResult.val;
                i = subResult.nextTokenIdx;
            } else if (currentToken.equals(")")) {
                return new SubResult(runningVal, i + 1);
            } else {
                arg = Long.parseLong(currentToken);
                i++;
            }
            if (arg != null) {
                if (currentOperator == '+') {
                    runningVal += arg;
                } else if (currentOperator == '*') {
                    runningVal *= arg;
                }
            }
        }
    }

    private static SubResult evalTokensPart2(String[] tokens, int i) {
        long runningVal = 0;
        while (true) {
            if (i == tokens.length) {
                return new SubResult(runningVal, i);
            }
            var currentToken = tokens[i];
            if (currentToken.equals("(")) {
                var subResult = evalTokensPart2(tokens, i + 1);
                runningVal += subResult.val;
                i = subResult.nextTokenIdx;
            } else if (currentToken.equals(")")) {
                return new SubResult(runningVal, i + 1);
            } else if (currentToken.equals("*")) {
                var subResult = evalTokensPart2(tokens, i + 1);
                runningVal *= subResult.val;
                i = subResult.nextTokenIdx;
                // This is tricky
                // When handling multiplication inside parenthesis,
                // a closing parenthesis needs to pop *2* calls
                // up the stack, since it terminates the evaluation of both
                // the multiplication and the parenthesis
                //
                // Reaching the end of the string also has a similar concern
                // (it ends all nested calculations), but that case is already handled
                // implicitly because every iteration of the loop checks i == tokens.length
                if (tokens[i-1].equals(")")) {
                    return new SubResult(runningVal, i);
                }
            } else if (currentToken.equals("+")) {
                i++;
            } else {
                runningVal += Long.parseLong(currentToken);
                i++;
            }
        }
    }

    @Override
    public Object computePart1() {
        return input.stream().mapToLong(elem -> eval(elem, false)).sum();
    }

    @Override
    public Object computePart2() {
        return input.stream().mapToLong(elem -> eval(elem, true)).sum();
    }
}
