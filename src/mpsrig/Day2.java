package mpsrig;

public class Day2 {
    private static class ParsedLine {
        public final int firstInt;
        public final int secondInt;
        public final char letter;
        public final String password;

        public ParsedLine(String line) {
            int idxOfDash = line.indexOf("-");
            int idxOfSpace = line.indexOf(" ");
            firstInt = Integer.parseInt(line.substring(0, idxOfDash));
            secondInt = Integer.parseInt(line.substring(idxOfDash + 1, idxOfSpace));
            letter = line.charAt(idxOfSpace + 1);
            password = line.substring(idxOfSpace + 4);
        }

        public boolean isValidPart1() {
            int countOfLetter = 0;
            for (char elem : password.toCharArray()) {
                if (elem == letter) {
                    countOfLetter++;
                }
            }
            return firstInt <= countOfLetter && countOfLetter <= secondInt;
        }

        public boolean isValidPart2() {
            boolean firstPositionHasLetter = password.charAt(firstInt - 1) == letter;
            boolean secondPositionHasLetter = password.charAt(secondInt - 1) == letter;

            return firstPositionHasLetter ^ secondPositionHasLetter;
        }
    }

    public static boolean isValidPassword(String line) {
        return new ParsedLine(line).isValidPart1();
    }

    public static boolean isValidPasswordPartTwo(String line) {
        return new ParsedLine(line).isValidPart2();
    }

    public static void main(String[] args) {
        Runner.run("/2.txt", new Runner.Computation() {
            @Override
            public Object computePart1() {
                int countValid = 0;
                for (String elem : input) {
                    if (isValidPassword(elem)) {
                        countValid++;
                    }
                }
                return countValid;
            }

            @Override
            public Object computePart2() {
                int countValidPart2 = 0;
                for (String elem : input) {
                    if (isValidPasswordPartTwo(elem)) {
                        countValidPart2++;
                    }
                }
                return countValidPart2;
            }
        });

    }
}
