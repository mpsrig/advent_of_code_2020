package mpsrig;

public class Day12 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/12.txt", new Day12());
    }

    @Override
    public Object computePart1() {
        int x = 0;
        int y = 0;
        int currentDirectionDegrees = 0;

        for (var line : input) {
            var amount = Integer.parseInt(line.substring(1));
            switch(line.charAt(0)) {
                case 'N': y += amount; break;
                case 'S': y -= amount; break;
                case 'E': x += amount; break;
                case 'W': x -= amount; break;
                case 'L': currentDirectionDegrees += amount; break;
                case 'R': currentDirectionDegrees += 360 - amount; break;
                case 'F':
                    switch (currentDirectionDegrees % 360) {
                        case 0 -> x += amount;
                        case 90 -> y += amount;
                        case 180 -> x -= amount;
                        case 270 -> y -= amount;
                        default -> throw new IllegalArgumentException("Bad angle: " + currentDirectionDegrees);
                    }
                    break;
                default: throw new IllegalArgumentException("Bad line: " + line);
            }
        }

        return Math.abs(x) + Math.abs(y);
    }

    @Override
    public Object computePart2() {
        int waypointX = 10;
        int waypointY = 1;

        int x = 0;
        int y = 0;

        for (var line : input) {
            var amount = Integer.parseInt(line.substring(1));
            switch(line.charAt(0)) {
                case 'N': waypointY += amount; break;
                case 'S': waypointY -= amount; break;
                case 'E': waypointX += amount; break;
                case 'W': waypointX -= amount; break;
                case 'R': amount = 360 - amount; // intentional fallthrough
                case 'L':
                    if (amount < 0 || amount >= 360) {
                        throw new IllegalArgumentException("Bad angle: " + amount);
                    }
                    for (int i = 0; i < amount; i += 90) {
                        var prevWaypointX = waypointX;
                        var prevWaypointY = waypointY;
                        waypointX = -prevWaypointY;
                        //noinspection SuspiciousNameCombination
                        waypointY = prevWaypointX;
                    }
                    break;
                case 'F':
                    x += amount * waypointX;
                    y += amount * waypointY;
                    break;
                default: throw new IllegalArgumentException("Bad line: " + line);
            }
        }
        return Math.abs(x) + Math.abs(y);
    }
}
