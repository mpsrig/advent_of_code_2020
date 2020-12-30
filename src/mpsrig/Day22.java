package mpsrig;

import java.util.*;

public class Day22 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/22.txt", new Day22());
    }

    private List<Integer> player1StartingCards;
    private List<Integer> player2StartingCards;

    @Override
    protected void init() {
        super.init();

        var idxOfDividerLine = input.indexOf("");
        player1StartingCards = ListUtils.map(input.subList(1, idxOfDividerLine), Integer::parseInt);
        player2StartingCards = ListUtils.map(input.subList(idxOfDividerLine + 2, input.size()), Integer::parseInt);
    }

    @Override
    public Object computePart1() {
        var player1Deck = new ArrayDeque<>(player1StartingCards);
        var player2Deck = new ArrayDeque<>(player2StartingCards);

        while (!player1Deck.isEmpty() && !player2Deck.isEmpty()) {
            int player1Card = player1Deck.removeFirst();
            int player2Card = player2Deck.removeFirst();
            if (player1Card == player2Card) {
                throw new IllegalStateException();
            } else if (player1Card > player2Card) {
                player1Deck.addLast(player1Card);
                player1Deck.addLast(player2Card);
            } else {
                player2Deck.addLast(player2Card);
                player2Deck.addLast(player1Card);
            }
        }

        var winner = player1Deck.isEmpty() ? player2Deck : player1Deck;
        return calculateScore(winner);
    }

    static long calculateScore(Collection<Integer> winningDeck) {
        long score = 0;
        long multiplier = winningDeck.size();
        for (var card : winningDeck) {
            score += multiplier * card;
            multiplier--;
        }

        return score;
    }

    @Override
    public Object computePart2() {
        var result = recursiveCombatGame(1, player1StartingCards, player2StartingCards);
//        System.err.println("result.winner: " + result.winner);
        return calculateScore(result.winningDeck);
    }

    private static final Integer[] DUMMY_TYPEHINT = new Integer[]{};

    private int gameIDCounter = 0;

    RecursiveCombatGameResult recursiveCombatGame(final int recursionLevel, Collection<Integer> player1StartingCards,
                                                  Collection<Integer> player2StartingCards) {
        final int gameID = ++gameIDCounter;
        var player1Deck = new ArrayDeque<>(player1StartingCards);
        var player2Deck = new ArrayDeque<>(player2StartingCards);

        var infiniteLoopChecker = new InfinteLoopChecker();

        int roundCounter = 1;
        while (!player1Deck.isEmpty() && !player2Deck.isEmpty()) {
//            System.err.println("Game ID: " + gameID + " Recursion level: " + recursionLevel + " Round Counter: " + roundCounter);
            if (infiniteLoopChecker.checkInfiniteLoop(player1Deck, player2Deck)) {
                // Player 1 automatically wins this game
//                System.err.println("Hit infinite loop case");
                return new RecursiveCombatGameResult(false, player1Deck);
            }

            int player1Card = player1Deck.removeFirst();
            int player2Card = player2Deck.removeFirst();

            if (player1Card <= player1Deck.size() && player2Card <= player2Deck.size()) {
                var subResult = recursiveCombatGame(recursionLevel + 1,
                        Arrays.asList(player1Deck.toArray(DUMMY_TYPEHINT)).subList(0, player1Card),
                        Arrays.asList(player2Deck.toArray(DUMMY_TYPEHINT)).subList(0, player2Card));
                if (subResult.winner) {
//                    System.err.println("Player 2 wins recursive game");
                    // player 2 winner
                    player2Deck.addLast(player2Card);
                    player2Deck.addLast(player1Card);
                } else {
//                    System.err.println("Player 1 wins recursive game");
                    // player 1 winner
                    player1Deck.addLast(player1Card);
                    player1Deck.addLast(player2Card);
                }
            } else {
                if (player1Card == player2Card) {
                    throw new IllegalStateException();
                } else if (player1Card > player2Card) {
                    player1Deck.addLast(player1Card);
                    player1Deck.addLast(player2Card);
                } else {
                    player2Deck.addLast(player2Card);
                    player2Deck.addLast(player1Card);
                }
            }
            roundCounter++;
        }

        if (player1Deck.isEmpty()) {
            return new RecursiveCombatGameResult(true, player2Deck);
        }
        return new RecursiveCombatGameResult(false, player1Deck);
    }

    private static class RecursiveCombatGameResult {
        final boolean winner;
        final ArrayDeque<Integer> winningDeck;

        private RecursiveCombatGameResult(boolean winner, ArrayDeque<Integer> winningDeck) {
            this.winner = winner;
            this.winningDeck = winningDeck;
        }
    }

    private static class InfinteLoopChecker {
        final Set<Object> decksStates = new HashSet<>();

        // Returns true if an infinite loop has been detected
        boolean checkInfiniteLoop(Collection<Integer> player1Deck,
                                  Collection<Integer> player2Deck) {
            var decksState = List.of(Arrays.asList(player1Deck.toArray()), Arrays.asList(player2Deck.toArray()));
            return !decksStates.add(decksState);
        }
    }
}
