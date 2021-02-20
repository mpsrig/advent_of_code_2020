package mpsrig.prev_year;

import mpsrig.InputUtils;
import mpsrig.Runner;
import mpsrig.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Day8 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/8.txt", new Day8());
    }

    private List<Integer> rawData;
    private int numLayers;

    @Override
    protected void init() {
        super.init();
        rawData = InputUtils.parseInts(StringUtils.toCodePoints(input.get(0)));
    }

    private static final int WIDTH = 25;
    private static final int HEIGHT = 6;

    private static final int LAYER_LENGTH = WIDTH * HEIGHT;

    private static <T> int countOccurrences(Collection<T> c, T needle) {
        int count = 0;
        for (var elem : c) {
            if (Objects.equals(needle, elem)) {
                count++;
            }
        }
        return count;
    }

    private List<Integer> getLayerData(int layer) {
        var beginIdx = layer * LAYER_LENGTH;
        return rawData.subList(beginIdx, beginIdx + LAYER_LENGTH);
    }

    @Override
    public Object computePart1() {
        numLayers = rawData.size() / LAYER_LENGTH;
        if (numLayers * LAYER_LENGTH != rawData.size()) {
            throw new IllegalStateException();
        }

        int layerWithMinNumZeroes = -1;
        int minNumZeroes = Integer.MAX_VALUE;
        for (int l = 0; l < numLayers; l++) {
            var c = countOccurrences(getLayerData(l), 0);
            if (c < minNumZeroes) {
                minNumZeroes = c;
                layerWithMinNumZeroes = l;
            }
        }

        var selectedLayerData = getLayerData(layerWithMinNumZeroes);
        var numOnes = countOccurrences(selectedLayerData, 1);
        var numTwos = countOccurrences(selectedLayerData, 2);

        return numOnes * numTwos;
    }

    private static char renderPixel(int pixelValue) {
        return switch (pixelValue) {
            case 0 -> ' ';
            case 1 -> '|';
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public Object computePart2() {
        var finalImageData = new int[LAYER_LENGTH];
        Arrays.fill(finalImageData, 2);
        for (int i = 0; i < finalImageData.length; i++) {
            for (int l = 0; l < numLayers; l++) {
                if (finalImageData[i] == 2) {
                    finalImageData[i] = getLayerData(l).get(i);
                }
            }
        }

        var sb = new StringBuilder();
        for (int j = 0; j < finalImageData.length; j++) {
            if (j % WIDTH == 0) {
                sb.append('\n');
            }
            sb.append(renderPixel(finalImageData[j]));
        }
        return sb.toString();
    }
}
