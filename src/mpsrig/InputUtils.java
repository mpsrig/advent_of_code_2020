package mpsrig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputUtils {
    public static List<Integer> parseNewlineSeparatedInts(String input) {
        return parseInts(Arrays.asList(input.split("\n")));
    }

    public static List<Integer> parseInts(List<String> input) {
        return ListUtils.map(input, Integer::parseInt);
    }

    public static List<Long> parseLongs(List<String> input) {
        return ListUtils.map(input, Long::parseLong);
    }
}
