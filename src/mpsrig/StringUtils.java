package mpsrig;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class StringUtils {
    /**
     * Semantic replacement for String::toCharArray that correctly
     * handles any Unicode character, not only the BMP.
     */
    public static ArrayList<String> toCodePoints(String s) {
        // The number of code points is at most the number of
        // 16-bit chars in the string, or less if there are
        // surrogate pairs. So, we can create an ArrayList
        // with adequate capacity and avoid copies.
        int capacity = s.length();
        return s.codePoints().mapToObj(StringUtils::codePointToString)
                .collect(Collectors.toCollection(() -> new ArrayList<>(capacity)));
    }

    public static String codePointToString(int codePoint) {
        if (0 <= codePoint && codePoint < CACHE.length) {
            return CACHE[codePoint];
        }
        return Character.toString(codePoint);
    }

    private static final String[] CACHE;

    static {
        // Cache all single character ASCII strings (0-127 inclusive)
        var c = new String[128];
        for (int i = 0; i < c.length; i++) {
            c[i] = Character.toString(i);
        }
        CACHE = c;
    }
}
