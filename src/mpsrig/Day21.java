package mpsrig;

import java.util.*;
import java.util.regex.Pattern;

public class Day21 extends Runner.Computation {
    public static void main(String[] args) {
        Runner.run("/21.txt", new Day21());
    }

    private static class Food {
        final Set<String> ingredients;
        final Set<String> allergens;

        private Food(Set<String> ingredients, Set<String> allergens) {
            this.ingredients = ingredients;
            this.allergens = allergens;
        }

        private static final Pattern LINE_REGEX = Pattern.compile("(.*) \\(contains (.*)\\)");

        static Food parse(String line) {
            var m = LINE_REGEX.matcher(line);
            if (!m.matches()) {
                throw new IllegalArgumentException("Did not match regex: " + line);
            }

            var ingredients = m.group(1).split(" ");
            var allergens = m.group(2).split(", ");
            return new Food(Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(ingredients))),
                    Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(allergens))));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Food food = (Food) o;
            return Objects.equals(ingredients, food.ingredients) && Objects.equals(allergens, food.allergens);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ingredients, allergens);
        }

        @Override
        public String toString() {
            return "Food{" +
                    "ingredients=" + ingredients +
                    ", allergens=" + allergens +
                    '}';
        }
    }

    private List<Food> foodList;

    @Override
    protected void init() {
        super.init();
        foodList = ListUtils.map(input, Food::parse);
    }

    // Sorting needed for part 2
    private SortedMap<String, Set<String>> allergenToPossibleIngredients;

    @Override
    public Object computePart1() {
        var allIngredients = new LinkedHashSet<String>();
        var allergenToContainingFood = new LinkedHashMap<String, List<Food>>();
        for (var f : foodList) {
            allIngredients.addAll(f.ingredients);
            for (var a : f.allergens) {
                allergenToContainingFood.computeIfAbsent(a, k -> new ArrayList<>()).add(f);
            }
        }

        allergenToPossibleIngredients = new TreeMap<>();
        var valuesList = new ArrayList<Set<String>>(allergenToContainingFood.size());
        int idxOfSingleElementSet = -1;
        for (var entry : allergenToContainingFood.entrySet()) {
            var v = SetUtils.intersection(ListUtils.map(entry.getValue(), f -> f.ingredients));
            allergenToPossibleIngredients.put(entry.getKey(), v);

            if (idxOfSingleElementSet == -1 && v.size() == 1) {
                idxOfSingleElementSet = valuesList.size();
            }
            valuesList.add(v);
        }
        Day16.removeSingleElementFromAllOtherIndexes(valuesList, idxOfSingleElementSet);

        var ingredientsWithoutAllergens = new LinkedHashSet<>(allIngredients);
        for (var elem : valuesList) {
            ingredientsWithoutAllergens.removeAll(elem);
        }

        long count = 0;
        for (var f : foodList) {
            count += SetUtils.intersection(List.of(f.ingredients, ingredientsWithoutAllergens)).size();
        }

        return count;
    }

    @Override
    public Object computePart2() {
        var sj = new StringJoiner(",");
        for (var elem : allergenToPossibleIngredients.entrySet()) {
            if (elem.getValue().size() != 1) {
                throw new IllegalStateException(elem.toString());
            }
            sj.add(elem.getValue().iterator().next());
        }
        return sj.toString();
    }
}
