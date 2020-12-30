package mpsrig;

import java.util.*;
import java.util.regex.Pattern;

public class Day20 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/20.txt", new Day20());
    }

    private static final int TILE_DIMENSION = 10;
    private static final int TILE_IMAGE_DIMENSION = TILE_DIMENSION - 2;

    private static class Tile {
        final int id;
        final List<String> raw;

        private Tile(int id, List<String> raw) {
            this.id = id;
            for (var l : raw) {
                if (l.length() != TILE_DIMENSION) {
                    throw new IllegalArgumentException();
                }
            }
            this.raw = Collections.unmodifiableList(raw);
        }

        private static final Pattern ID_LINE_PATTERN = Pattern.compile("Tile (\\d+):");

        static Tile parse(Set<Integer> usedTileIds, List<String> lines) {
            if (lines.size() != TILE_DIMENSION + 1) {
                throw new IllegalArgumentException();
            }

            var m = ID_LINE_PATTERN.matcher(lines.get(0));
            if (!m.matches()) {
                throw new IllegalArgumentException();
            }
            var id = Integer.parseInt(m.group(1));

            if (usedTileIds.contains(id)) {
                throw new IllegalArgumentException();
            }
            usedTileIds.add(id);

            return new Tile(id, lines.subList(1, lines.size()));
        }

        @Override
        public String toString() {
            return "Tile{" +
                    "id=" + id +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tile tile = (Tile) o;

            return id == tile.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        private List<List<Character>> edges;

        private static final int TOP_EDGE_IDX = 0;
        private static final int BOTTOM_EDGE_IDX = 1;
        private static final int LEFT_EDGE_IDX = 2;
        private static final int RIGHT_EDGE_IDX = 3;

        public List<List<Character>> getEdges() {
            if (edges == null) {
                var top = new ArrayList<Character>(TILE_DIMENSION);
                var bottom = new ArrayList<Character>(TILE_DIMENSION);
                var left = new ArrayList<Character>(TILE_DIMENSION);
                var right = new ArrayList<Character>(TILE_DIMENSION);

                for (int i = 0; i < TILE_DIMENSION; i++) {
                    top.add(raw.get(0).charAt(i));
                    bottom.add(raw.get(TILE_DIMENSION - 1).charAt(i));
                    left.add(raw.get(i).charAt(0));
                    right.add(raw.get(i).charAt(TILE_DIMENSION - 1));
                }

                edges = List.of(Collections.unmodifiableList(top),
                        Collections.unmodifiableList(bottom),
                        Collections.unmodifiableList(left),
                        Collections.unmodifiableList(right));
            }
            return edges;
        }
    }

    private List<Tile> tiles;
    private int gridSize;

    @Override
    protected void init() {
        super.init();

        tiles = new ArrayList<>();
        var usedTileIds = new HashSet<Integer>();
        for (int i = 0; i < input.size(); i += 12) {
            tiles.add(Tile.parse(usedTileIds, input.subList(i, i + 11)));
        }

        gridSize = (int) Math.sqrt(tiles.size());
        if (gridSize * gridSize != tiles.size()) {
            throw new IllegalStateException();
        }
    }

    private static class EdgeMatch {
        final boolean isReverse;
        final Tile matchedTile;

        private EdgeMatch(boolean isReverse, Tile matchedTile) {
            this.isReverse = isReverse;
            this.matchedTile = Objects.requireNonNull(matchedTile);
        }
    }

    private static class EdgeMatchWithSourceIndex {
        final int edgeIndexOnSource;
        final boolean isReverse;
        final Tile matchedTile;

        private EdgeMatchWithSourceIndex(int edgeIndexOnSource, boolean isReverse, Tile matchedTile) {
            this.edgeIndexOnSource = edgeIndexOnSource;
            this.isReverse = isReverse;
            this.matchedTile = Objects.requireNonNull(matchedTile);
        }
    }

    private static <T> T getFirstNotEqual(Collection<T> c, T t) {
        for (var possibleMatch : c) {
            if (!possibleMatch.equals(t)) {
                return possibleMatch;
            }
        }
        return null;
    }

    private static <T> List<T> reverseCopy(List<T> l) {
        var out = new ArrayList<>(l);
        Collections.reverse(out);
        return out;
    }

    private List<EdgeMatchWithSourceIndex> matchEdges(Tile t) {
        var out = new ArrayList<EdgeMatchWithSourceIndex>(4);
        var sourceEdges = t.getEdges();
        for (int i = 0; i < 4; i++) {
            var e = sourceEdges.get(i);
            var match = matchEdge(t, e);
            if (match != null) {
                out.add(new EdgeMatchWithSourceIndex(i, match.isReverse, match.matchedTile));
            }
        }
        return out;
    }

    private EdgeMatch matchEdge(Tile t, List<Character> e) {
        var l = edgeMap.get(e);
        if (l != null) {
            if (l.size() == 0) {
                throw new IllegalStateException();
            }
            var matched = getFirstNotEqual(l, t);
            if (matched != null) {
                return new EdgeMatch(false, matched);
            }
        }

        var eReverse = reverseCopy(e);

        var lOfReverse = edgeMap.get(eReverse);
        if (lOfReverse != null) {
            var matched = getFirstNotEqual(lOfReverse, t);
            if (matched != null) {
                return new EdgeMatch(true, matched);
            }
        }
        return null;
    }

    private int computeMatchedEdgeCount(Tile t) {
        int matchedEdgeCount = matchEdges(t).size();
        if (matchedEdgeCount < 2 || matchedEdgeCount > 4) {
            throw new IllegalStateException();
        }
        return matchedEdgeCount;
    }

    private Map<List<Character>, Set<Tile>> edgeMap;

    private Set<Tile> cornerTiles;
    private Set<Tile> edgeTiles;
    private Set<Tile> otherTiles;

    @Override
    public Object computePart1() {
        edgeMap = new HashMap<>(gridSize * gridSize * 4);
        for (var t : tiles) {
            for (var e : t.getEdges()) {
                edgeMap.compute(e, (k, v) -> {
                    if (v == null) {
                        v = new LinkedHashSet<>(2);
                    }
                    v.add(t);
                    return v;
                });
            }
        }
        cornerTiles = new LinkedHashSet<>(4);
        edgeTiles = new LinkedHashSet<>(4 * gridSize - 8);
        otherTiles = new LinkedHashSet<>(gridSize * gridSize - (4 * gridSize - 8) - 4);
        for (var t : tiles) {
            switch (computeMatchedEdgeCount(t)) {
                case 2 -> cornerTiles.add(t);
                case 3 -> edgeTiles.add(t);
                case 4 -> otherTiles.add(t);
                default -> throw new IllegalStateException();
            }
        }
        if (cornerTiles.size() != 4) {
            throw new IllegalStateException();
        }

        long product = 1;
        for (var t : cornerTiles) {
            product *= t.id;
        }
        return product;
    }

    private static class TileAndLayout {
        final Tile tile;
        final boolean rotatedCounterClockwise;
        final boolean flippedLeftRight;
        final boolean flippedTopBottom;

        private TileAndLayout(Tile tile, boolean rotatedCounterClockwise, boolean flippedLeftRight, boolean flippedTopBottom) {
            this.tile = tile;
            this.rotatedCounterClockwise = rotatedCounterClockwise;
            this.flippedLeftRight = flippedLeftRight;
            this.flippedTopBottom = flippedTopBottom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TileAndLayout that = (TileAndLayout) o;
            return rotatedCounterClockwise == that.rotatedCounterClockwise &&
                    flippedLeftRight == that.flippedLeftRight &&
                    flippedTopBottom == that.flippedTopBottom &&
                    tile.equals(that.tile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tile, rotatedCounterClockwise, flippedLeftRight, flippedTopBottom);
        }

        @Override
        public String toString() {
            return "TileAndLayout{" +
                    "tile=" + tile +
                    ", rotatedCounterClockwise=" + rotatedCounterClockwise +
                    ", flippedLeftRight=" + flippedLeftRight +
                    ", flippedTopBottom=" + flippedTopBottom +
                    '}';
        }

        public char getCell(int x, int y) {
            if (rotatedCounterClockwise) {
                var prevX = x;
                var prevY = y;
                x = TILE_DIMENSION - 1 - prevY;
                y = prevX;
            }
            if (flippedLeftRight) {
                x = TILE_DIMENSION - 1 - x;
            }
            if (flippedTopBottom) {
                y = TILE_DIMENSION - 1 - y;
            }
            return tile.raw.get(y).charAt(x);
        }

        private List<List<Character>> layoutEdges;

        public List<List<Character>> getLayoutEdges() {
            if (layoutEdges == null) {
                var top = new ArrayList<Character>(TILE_DIMENSION);
                var bottom = new ArrayList<Character>(TILE_DIMENSION);
                var left = new ArrayList<Character>(TILE_DIMENSION);
                var right = new ArrayList<Character>(TILE_DIMENSION);

                for (int i = 0; i < TILE_DIMENSION; i++) {
                    top.add(getCell(i, 0));
                    bottom.add(getCell(i, TILE_DIMENSION - 1));
                    left.add(getCell(0, i));
                    right.add(getCell(TILE_DIMENSION - 1, i));
                }

                layoutEdges = List.of(Collections.unmodifiableList(top),
                        Collections.unmodifiableList(bottom),
                        Collections.unmodifiableList(left),
                        Collections.unmodifiableList(right));
            }
            return layoutEdges;
        }

    }

    @Override
    public Object computePart2() {
        solveGrid();
        assembleImage();
        initSearchPattern();
        var result = searchForPattern();
        return result.countRoughness();
    }

    private TileAndLayout[][] grid;

    private int imageSize;
    private Character[][] assembledImage;

    private void solveGrid() {
        grid = new TileAndLayout[gridSize][gridSize];
        fillTopLeftTile();
        fillLine(true, 0);
        fillLine(false, 0);
        fillLine(true, gridSize - 1);
        fillLine(false, gridSize - 1);
        if (cornerTiles.size() != 0 || edgeTiles.size() != 0) {
            throw new IllegalStateException();
        }
        for (int j = 1; j < gridSize - 1; j++) {
            fillLine(true, j);
        }
        if (otherTiles.size() != 0) {
            throw new IllegalStateException();
        }
    }

    private void fillTopLeftTile() {
        var t = cornerTiles.iterator().next();
        cornerTiles.remove(t);

        var matches = matchEdges(t);
        if (matches.size() != 2) {
            throw new IllegalStateException();
        }

        boolean hasTopMatch = false;
        boolean hasBottomMatch = false;
        boolean hasLeftMatch = false;
        boolean hasRightMatch = false;

        for (var m : matches) {
            switch (m.edgeIndexOnSource) {
                case Tile.TOP_EDGE_IDX -> hasTopMatch = true;
                case Tile.BOTTOM_EDGE_IDX -> hasBottomMatch = true;
                case Tile.LEFT_EDGE_IDX -> hasLeftMatch = true;
                case Tile.RIGHT_EDGE_IDX -> hasRightMatch = true;
                default -> throw new IllegalStateException();
            }
        }

        if (hasTopMatch && hasBottomMatch) {
            throw new IllegalStateException();
        }
        if (hasLeftMatch && hasRightMatch) {
            throw new IllegalStateException();
        }

        boolean needsTopBottomFlip = !hasBottomMatch;
        boolean needsLeftRightFlip = !hasRightMatch;

        grid[0][0] = new TileAndLayout(t, false, needsLeftRightFlip, needsTopBottomFlip);
//        printGrid();
    }

    private void printGrid() {
        System.err.println();
        for (var row : grid) {
            for (var tile : row) {
                System.err.print((tile == null ? "null" : tile.tile.id) + "\t");
            }
            System.err.println();
        }
    }

    private void printImage() {
        System.err.println();
        for (var row : assembledImage) {
            for (var c : row) {
                System.err.print(Objects.requireNonNull(c));
            }
            System.err.println();
        }
    }

    private void fillLine(final boolean traverseX, final int otherAxisPosition) {
        for (int i = 1; i < gridSize; i++) {
            var previousT = grid[traverseX ? otherAxisPosition : i - 1][traverseX ? i - 1 : otherAxisPosition];
            var previousTEdge = previousT.getLayoutEdges().get(
                    traverseX ? Tile.RIGHT_EDGE_IDX : Tile.BOTTOM_EDGE_IDX);
            var matched = Objects.requireNonNull(matchEdge(previousT.tile, previousTEdge));
            if (i == gridSize - 1) {
                var contained = cornerTiles.remove(matched.matchedTile);
                if (!contained &&
                        !matched.matchedTile.equals(
                                grid[traverseX ? otherAxisPosition : i][traverseX ? i : otherAxisPosition].tile)) {
                    throw new IllegalStateException();
                }
            } else {
                boolean contained;
                if (otherAxisPosition == 0 || otherAxisPosition == gridSize - 1) {
                    contained = edgeTiles.remove(matched.matchedTile);
                } else {
                    contained = otherTiles.remove(matched.matchedTile);
                }
                if (!contained) {
                    throw new IllegalStateException();
                }
            }

            var matchedEdge = matched.isReverse ? reverseCopy(previousTEdge) : previousTEdge;
            var matchedIdx = matched.matchedTile.getEdges().indexOf(matchedEdge);

            boolean needsRotate = false;
            boolean needsLeftRightFlip = false;
            boolean needsTopBottomFlip = false;

            int idxToCheck = -1;

            String branchTaken = "Unknown";

            if (traverseX) {
                idxToCheck = Tile.LEFT_EDGE_IDX;
                switch (matchedIdx) {
                    case Tile.LEFT_EDGE_IDX -> {
                        needsTopBottomFlip = matched.isReverse;
                        branchTaken = "top_LEFT_EDGE_IDX";
                    }
                    case Tile.RIGHT_EDGE_IDX -> {
                        needsLeftRightFlip = true;
                        needsTopBottomFlip = matched.isReverse;
                        branchTaken = "top_RIGHT_EDGE_IDX";
                    }
                    case Tile.BOTTOM_EDGE_IDX -> {
                        needsRotate = true;
                        needsTopBottomFlip = true;
                        needsLeftRightFlip = !matched.isReverse;
                        branchTaken = "top_BOTTOM_EDGE_IDX";
                    }
                    case Tile.TOP_EDGE_IDX -> {
                        needsRotate = true;
                        needsLeftRightFlip = !matched.isReverse;
                        branchTaken = "top_TOP_EDGE_IDX";
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + matchedIdx);
                }
            } else {
                idxToCheck = Tile.TOP_EDGE_IDX;
                switch (matchedIdx) {
                    case Tile.TOP_EDGE_IDX -> {
                        needsLeftRightFlip = matched.isReverse;
                        branchTaken = "not_top_TOP_EDGE_IDX";
                    }
                    case Tile.BOTTOM_EDGE_IDX -> {
                        needsTopBottomFlip = true;
                        needsLeftRightFlip = matched.isReverse;
                        branchTaken = "not_top_BOTTOM_EDGE_IDX";
                    }
                    case Tile.LEFT_EDGE_IDX -> {
                        needsRotate = true;
                        needsLeftRightFlip = true;
                        needsTopBottomFlip = matched.isReverse;
                        branchTaken = "not_top_LEFT_EDGE_IDX";
                    }
                    case Tile.RIGHT_EDGE_IDX -> {
                        needsRotate = true;
                        needsTopBottomFlip = matched.isReverse;
                        branchTaken = "not_top_RIGHT_EDGE_IDX";
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + matchedIdx);
                }
            }
            var t = new TileAndLayout(matched.matchedTile, needsRotate, needsLeftRightFlip, needsTopBottomFlip);
            if (!t.getLayoutEdges().get(idxToCheck).equals(previousTEdge)) {
                throw new IllegalStateException(branchTaken);
            }
            var previousEntry = grid[traverseX ? otherAxisPosition : i][traverseX ? i : otherAxisPosition];
            if (previousEntry != null && !previousEntry.equals(t)) {
                throw new IllegalStateException();
            }
            grid[traverseX ? otherAxisPosition : i][traverseX ? i : otherAxisPosition] = t;
//            printGrid();
        }
    }

    private void assembleImage() {
        imageSize = gridSize * TILE_IMAGE_DIMENSION;
        assembledImage = new Character[imageSize][imageSize];

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                int gridX = x / TILE_IMAGE_DIMENSION;
                int gridY = y / TILE_IMAGE_DIMENSION;
                var t = grid[gridY][gridX];
                assembledImage[y][x] = t.getCell((x % TILE_IMAGE_DIMENSION) + 1, (y % TILE_IMAGE_DIMENSION) + 1);
            }
        }
//        printImage();
    }

    private static final String PART_2_SEARCH_PATTERN_RAW =
            "                  # \n" +
                    "#    ##    ##    ###\n" +
                    " #  #  #  #  #  #   ";

    private char[][] part2SearchPattern;

    private void initSearchPattern() {
        var lines = PART_2_SEARCH_PATTERN_RAW.split("\n");
        part2SearchPattern = new char[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            part2SearchPattern[i] = lines[i].toCharArray();
        }
    }

    private static class ImageView {
        final Character[][] image;
        final boolean rotatedCounterClockwise;
        final boolean flippedLeftRight;
        final boolean flippedTopBottom;

        private ImageView(Character[][] image, boolean rotatedCounterClockwise, boolean flippedLeftRight, boolean flippedTopBottom) {
            this.image = image;
            this.rotatedCounterClockwise = rotatedCounterClockwise;
            this.flippedLeftRight = flippedLeftRight;
            this.flippedTopBottom = flippedTopBottom;
        }

        public char getCell(int x, int y) {
            if (rotatedCounterClockwise) {
                var prevX = x;
                var prevY = y;
                x = image.length - 1 - prevY;
                y = prevX;
            }
            if (flippedLeftRight) {
                x = image.length - 1 - x;
            }
            if (flippedTopBottom) {
                y = image.length - 1 - y;
            }
            return image[y][x];
        }
    }

    private static class ImageSearchResult {
        final ImageView imageView;
        final boolean[][] matchedCells;
        final int numMatches;

        private ImageSearchResult(ImageView imageView, boolean[][] matchedCells, int numMatches) {
            this.imageView = imageView;
            this.matchedCells = matchedCells;
            this.numMatches = numMatches;
        }

        public int countRoughness() {
            int count = 0;
            for (int y = 0; y < imageView.image.length; y++) {
                for (int x = 0; x < imageView.image.length; x++) {
                    if (imageView.getCell(x, y) == '#' && !matchedCells[y][x]) {
                        count++;
                    }
                }
            }
            return count;
        }
    }

    private ImageSearchResult searchForPattern() {
        var candidates = Arrays.asList(
                new ImageView(assembledImage, false, false, false),
                new ImageView(assembledImage, false, false, true),
                new ImageView(assembledImage, false, true, false),
                new ImageView(assembledImage, false, true, true),
                new ImageView(assembledImage, true, false, false),
                new ImageView(assembledImage, true, false, true),
                new ImageView(assembledImage, true, true, false),
                new ImageView(assembledImage, true, true, true)
        );

        var searched = ListUtils.map(candidates, this::searchImageViewForPattern);
        ImageSearchResult result = null;
        for (var s : searched) {
            if (result == null) {
                if (s.numMatches > 0) {
                    result = s;
                }
            } else if (s.numMatches != 0) {
                // Check that only one element of `searched` has matches
                throw new IllegalStateException();
            }
        }
        return result;
    }

    private ImageSearchResult searchImageViewForPattern(ImageView imageView) {
        var matchedCells = new boolean[imageSize][imageSize];
        int numMatches = 0;

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                if (matchesRegion(imageView, x, y)) {
                    numMatches++;
                    markMatch(matchedCells, x, y);
                }
            }
        }

        return new ImageSearchResult(imageView, matchedCells, numMatches);
    }

    private boolean matchesRegion(ImageView imageView, int x, int y) {
        for (int yOffset = 0; yOffset < part2SearchPattern.length; yOffset++) {
            var line = part2SearchPattern[yOffset];
            for (int xOffset = 0; xOffset < line.length; xOffset++) {
                var patternChar = line[xOffset];
                if (patternChar == ' ') {
                    continue;
                }
                if (patternChar != '#') {
                    throw new IllegalStateException();
                }
                if (x + xOffset >= imageSize || y + yOffset >= imageSize) {
                    return false;
                }
                if ('#' != imageView.getCell(x + xOffset, y + yOffset)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void markMatch(boolean[][] matchedCells, int x, int y) {
        for (int yOffset = 0; yOffset < part2SearchPattern.length; yOffset++) {
            var line = part2SearchPattern[yOffset];
            for (int xOffset = 0; xOffset < line.length; xOffset++) {
                if (line[xOffset] == '#') {
                    matchedCells[y + yOffset][x + xOffset] = true;
                }
            }
        }
    }
}
