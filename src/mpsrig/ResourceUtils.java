package mpsrig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceUtils {
    public static List<String> getLinesFromResource(String res) {
        try (InputStream resourceAsStream = Main.class.getResourceAsStream(res)) {
            return new BufferedReader(new InputStreamReader(resourceAsStream,
                            StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getDataFromBase64Resource(String res) {
        List<String> lines = getLinesFromResource(res);
        String allB64Data = String.join("", lines);
        return Base64.getDecoder().decode(allB64Data);
    }
}
