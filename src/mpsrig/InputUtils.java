package mpsrig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputUtils {
    public static List<Integer> parseNewlineSeparatedInts(String input) {
        return parseInts(Arrays.asList(input.split("\n")));
    }

    public static List<Integer> parseInts(List<String> input) {
        List<Integer> out = new ArrayList<>(input.size());
        for (String entry: input) {
            out.add(Integer.parseInt(entry));
        }
        return out;
    }
}
