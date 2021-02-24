package mpsrig.prev_year;

import mpsrig.Runner;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Day13 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/prev_year/13.txt", new Day13());
    }

    private List<Long> parsedProgram;

    private int displayWidth = 35;
    private int displayHeight = 21;

    @Override
    protected void init() {
        parsedProgram = Day2.parseProgramFromPuzzleInput(input);
    }


    @Override
    public Object computePart1() {
        var computer = new IntcodeComputer(parsedProgram, Collections.emptyList());
        var grid = new HashMap<Coordinate, Integer>();

        while (true) {
            var x = computer.runUntilOutputAndYield();
            if (x == null) {
                break;
            }
            var y = computer.runUntilOutputAndYield();
            Objects.requireNonNull(y);
            var tileId = computer.runUntilOutputAndYield();
            Objects.requireNonNull(tileId);

            grid.put(new Coordinate(Math.toIntExact(x), Math.toIntExact(y)), Math.toIntExact(tileId));
        }

        var minX = grid.keySet().stream().mapToInt(c -> c.x).min().orElseThrow();
        var maxX = grid.keySet().stream().mapToInt(c -> c.x).max().orElseThrow();
        var minY = grid.keySet().stream().mapToInt(c -> c.y).min().orElseThrow();
        var maxY = grid.keySet().stream().mapToInt(c -> c.y).max().orElseThrow();

        if (minX != 0 || minY != 0) {
            throw new IllegalStateException();
        }
        displayWidth = maxX + 1;
        displayHeight = maxY + 1;

        int count = 0;
        for (var elem : grid.values()) {
            if (elem == 2) {
                count++;
            }
        }
        return count;
    }

//    private String renderGrid(Map<Coordinate, Integer> grid) {
//        if (displayWidth <= 0 || displayHeight <= 0) {
//            throw new IllegalStateException();
//        }
//        var displayGrid = new int[displayHeight][displayWidth];
//        for (int y = 0; y < displayHeight; y++) {
//            for (int x = 0; x < displayWidth; x++) {
//                var val = grid.get(new Coordinate(x, y));
//                displayGrid[y][x] = val != null ? val : 0;
//            }
//        }
//        return renderGrid(displayGrid);
//    }

    private static StringBuilder renderGrid(int[][] displayGrid) {
        var sb = new StringBuilder();
        for (int[] row : displayGrid) {
            for (int pixel : row) {
                sb.append(renderPixel(pixel));
            }
            sb.append('\n');
        }
        return sb;
    }

    private static char renderPixel(int pixelValue) {
        return switch (pixelValue) {
            case 0 -> ' ';
            case 1 -> '|';
            case 2 -> '#';
            case 3 -> '_';
            case 4 -> 'o';
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public Object computePart2() {
        var displayGrid = new int[displayHeight][displayWidth];

        var tw = new TextWindow();
        tw.init(displayHeight + 2, displayWidth);
        sleep(500);

        var program = new ArrayList<>(parsedProgram);
        program.set(0, 2L);
        var computer = new IntcodeComputer(program, Collections.emptyList());

        long score = 0;

        var sharedData = new Object() {
            int ballX = -1;
            int paddleX = -1;
        };

        computer.registerInputSupplier(() -> {
            if (sharedData.ballX == -1 || sharedData.paddleX == -1) {
                return 0L;
            }
            if (sharedData.ballX < sharedData.paddleX) {
                return -1L;
            }
            if (sharedData.ballX > sharedData.paddleX) {
                return 1L;
            }
            return 0L;
        });

        while (true) {
            var x = computer.runUntilOutputAndYield();
            if (x == null) {
                break;
            }
            var y = computer.runUntilOutputAndYield();
            Objects.requireNonNull(y);
            var val = computer.runUntilOutputAndYield();
            Objects.requireNonNull(val);

            if (x == -1) {
                if (y != 0) {
                    throw new IllegalStateException();
                }
                score = val;
            } else {
                var intVal = Math.toIntExact(val);
                var intX = Math.toIntExact(x);
                displayGrid[Math.toIntExact(y)][intX] = intVal;

                if (intVal == 3) {
                    sharedData.paddleX = intX;
                }
                if (intVal == 4) {
                    sharedData.ballX = intX;
                }
            }

            tw.setString(renderGrid(displayGrid).append("\nScore: ").append(score).toString());
            sleep(1);
        }
        return score;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class TextWindow {
        private JTextArea textArea;

        void init(int rows, int cols) {
            var notifier = new Object() {
                boolean complete = false;
            };

            SwingUtilities.invokeLater(() -> {
                //Create and set up the window.
                var frame = new JFrame("TextWindow");

                textArea = new JTextArea(rows, cols);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monaco", Font.PLAIN, 12));

                //Add contents to the window.
                frame.add(textArea);

                //Display the window.
                frame.pack();
                frame.setVisible(true);

                synchronized (notifier) {
                    notifier.complete = true;
                    notifier.notifyAll();
                }
            });

            try {
                synchronized (notifier) {
                    while (!notifier.complete) {
                        notifier.wait();
                    }
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        void setString(String content) {
            SwingUtilities.invokeLater(() -> textArea.setText(content));
        }

//        public static void main(String[] args) throws InterruptedException {
//            var tw = new TextWindow();
//            tw.init(20, 20);
//            for (int i = 0; i < 100; i++) {
//                Thread.sleep(1000);
//                tw.setString("i = " + i);
//            }
//        }
    }
}
