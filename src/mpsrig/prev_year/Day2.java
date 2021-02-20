package mpsrig.prev_year;

import mpsrig.InputUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2 {
    public static List<Long> parseProgram(String program) {
        return InputUtils.parseLongs(Arrays.asList(program.split(",")));
    }

    public static List<Long> parseProgramFromPuzzleInput(List<String> input) {
        if (input.size() != 1) {
            throw new IllegalArgumentException();
        }
        return parseProgram(input.get(0));
    }

    public static String serializeProgram(List<Long> program) {
        return program.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    public static void runProgram(List<Long> program) {
        for (int pc = 0; ; pc += 4) {
            int opcode = Math.toIntExact(program.get(pc));
            if (opcode == 99) {
                return;
            }
            if (opcode != 1 && opcode != 2) {
                throw new IllegalStateException("Unknown opcode: " + opcode + " at pc: " + pc);
            }
            int x = Math.toIntExact(program.get(Math.toIntExact(program.get(pc + 1))));
            int y = Math.toIntExact(program.get(Math.toIntExact(program.get(pc + 2))));
            int destIdx = Math.toIntExact(program.get(pc + 3));
            int result;
            if (opcode == 1) {
                // ADD
                result = x + y;
            } else {
                // MULTIPLY
                result = x * y;
            }
            program.set(destIdx, (long) result);
        }
    }

    public static void main(String[] args) {
        String puzzleInput = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,19,9,23,1,5,23,27,1,27,9,31,1,6,31,35,2,35,9,39,1,39,6,43,2,9,43,47,1,47,6,51,2,51,9,55,1,5,55,59,2,59,6,63,1,9,63,67,1,67,10,71,1,71,13,75,2,13,75,79,1,6,79,83,2,9,83,87,1,87,6,91,2,10,91,95,2,13,95,99,1,9,99,103,1,5,103,107,2,9,107,111,1,111,5,115,1,115,5,119,1,10,119,123,1,13,123,127,1,2,127,131,1,131,13,0,99,2,14,0,0";
        doPart1(puzzleInput);
        doPart2(puzzleInput);
    }

    public static void doPart1(String puzzleInput) {
        System.out.println("Part 1: " + runPuzzleWithParams(puzzleInput, 12, 2));
    }

    public static void doPart2(String puzzleInput) {
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                int result = Math.toIntExact(runPuzzleWithParams(puzzleInput, noun, verb));
                if (result == 19690720) {
                    int answer = 100 * noun + verb;
                    System.out.println("Part 2: Noun: " + noun + " Verb: " + verb + " Answer: " + answer);
                    return;
                }
            }
        }
    }

    public static long runPuzzleWithParams(String puzzleInput, int noun, int verb) {
        var parsedProgram = parseProgram(puzzleInput);
        parsedProgram.set(1, (long)noun);
        parsedProgram.set(2, (long)verb);
        runProgram(parsedProgram);
        return parsedProgram.get(0);
    }
}
