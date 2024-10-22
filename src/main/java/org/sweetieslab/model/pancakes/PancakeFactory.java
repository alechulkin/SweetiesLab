package org.sweetieslab.model.pancakes;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PancakeFactory {

  private static final ConcurrentMap<EnumMap<Ingredient, Integer>, PancakeRecipe> PANCAKES
      = new ConcurrentHashMap<>();
  private static final ConcurrentMap<String, PancakeRecipe> POPULAR_PANCAKES
      = new ConcurrentHashMap<>();
  private static final java.util.Random random = new java.util.Random();

  public static final String DARK_CHOCOLATE_WHIPPED_CREAM_PANCAKE =
      "Dark Chocolate Whipped Cream Pancake";
  public static final String DARK_CHOCOLATE_WHIPPED_CREAM_HAZELNUT_PANCAKE =
      "Dark Chocolate Whipped Cream Hazelnut Pancake";
  public static final String DARK_CHOCOLATE_PANCAKE = "Dark Chocolate Pancake";
  public static final String MILK_CHOCOLATE_HAZELNUT_PANCAKE = "Milk Chocolate Hazelnut Pancake";
  public static final String MILK_CHOCOLATE_PANCAKE = "Milk Chocolate Pancake";

  static {
    EnumMap<Ingredient, Integer> darkChocolateWhippedCreamIngredients = new EnumMap<>(
        Map.of(Ingredient.DARK_CHOCOLATE, 50, Ingredient.WHIPPED_CREAM, 150));
    addPopularPancake(darkChocolateWhippedCreamIngredients,
        DARK_CHOCOLATE_WHIPPED_CREAM_PANCAKE);

    EnumMap<Ingredient, Integer> darkChocolateWhippedCreamHazelnutIngredients = new EnumMap<>(
        Map.of(Ingredient.FLOUR, 100, Ingredient.EGG, 1,
            Ingredient.WHIPPED_CREAM, 150, Ingredient.HAZELNUT, 50,
            Ingredient.DARK_CHOCOLATE, 50));
    addPopularPancake(darkChocolateWhippedCreamHazelnutIngredients,
        DARK_CHOCOLATE_WHIPPED_CREAM_HAZELNUT_PANCAKE);

    EnumMap<Ingredient, Integer> darkChocolatePancakeIngredients = new EnumMap<>(
        Map.of(Ingredient.FLOUR, 100, Ingredient.EGG, 1,
            Ingredient.MILK, 150, Ingredient.DARK_CHOCOLATE, 50));
    addPopularPancake(darkChocolatePancakeIngredients, DARK_CHOCOLATE_PANCAKE);

    EnumMap<Ingredient, Integer> milkChocolateHazelnutsPancakeIngredients = new EnumMap<>(
        Map.of(Ingredient.FLOUR, 100, Ingredient.EGG, 1,
            Ingredient.MILK, 200, Ingredient.MILK_CHOCOLATE, 50,
            Ingredient.HAZELNUT, 50));
    addPopularPancake(milkChocolateHazelnutsPancakeIngredients, MILK_CHOCOLATE_HAZELNUT_PANCAKE);

    EnumMap<Ingredient, Integer> milkChocolatePancakeIngredients = new EnumMap<>(
        Map.of(Ingredient.FLOUR, 100, Ingredient.EGG, 1, Ingredient.MILK, 200,
            Ingredient.MILK_CHOCOLATE, 50));
    addPopularPancake(milkChocolatePancakeIngredients, MILK_CHOCOLATE_PANCAKE);
  }

  public static PancakeRecipe getPancakeRecipe(EnumMap<Ingredient, Integer> ingredients) {
    PancakeRecipe result = PANCAKES.get(ingredients);
    if (result != null) {
      return result;
    }
    return addPancake(ingredients);
  }

  public static PancakeRecipe getDarkChocolateWhippedCreamPancake() {
    return getPopularPancakeRecipe(DARK_CHOCOLATE_WHIPPED_CREAM_PANCAKE);
  }

  public static PancakeRecipe getDarkChocolateWhippedCreamHazelnutPancake() {
    return getPopularPancakeRecipe(DARK_CHOCOLATE_WHIPPED_CREAM_HAZELNUT_PANCAKE);
  }

  public static PancakeRecipe getDarkChocolatePancake() {
    return getPopularPancakeRecipe(DARK_CHOCOLATE_PANCAKE);
  }

  public static PancakeRecipe getMilkChocolateHazelnutPancakeRecipe() {
    return getPopularPancakeRecipe(MILK_CHOCOLATE_HAZELNUT_PANCAKE);
  }

  public static PancakeRecipe getMilkChocolatePancakeRecipe() {
    return getPopularPancakeRecipe(MILK_CHOCOLATE_PANCAKE);
  }

  public static PancakeRecipe getRandomPancakeRecipe() {
    EnumSet<Ingredient> randomIngredients = EnumSet.of(getRandomIngredient());
    while (randomIngredients.size() < 3) {
      randomIngredients.add(getRandomIngredient());
    }

    EnumMap<Ingredient, Integer> ingredientsMap = new EnumMap<>(Ingredient.class);

    ingredientsMap.put(Ingredient.EGG, 2);
    ingredientsMap.put(Ingredient.FLOUR, 50);
    ingredientsMap.put(Ingredient.MILK, 100);
    int[] randomQuantities = {10, 30, 70};
    int currentRandomQty = 0;
    while (ingredientsMap.size() < 6) {
      Ingredient randomIngredient = getRandomIngredient();
      if (!ingredientsMap.containsKey(randomIngredient)) {
        ingredientsMap.put(randomIngredient, randomQuantities[currentRandomQty++]);
      }
    }

    return getPancakeRecipe(ingredientsMap);
  }

  private static Ingredient getRandomIngredient() {
    return Ingredient.values()[random.nextInt(Ingredient.values().length)];
  }

  private static PancakeRecipe getPopularPancakeRecipe(String name) {
    PancakeRecipe result = POPULAR_PANCAKES.get(name);
    if (result != null) {
      return result;
    }
    throw new IllegalStateException("No such pancake recipe: " + name);
  }

  private static PancakeRecipe addPancake(EnumMap<Ingredient, Integer> ingredients) {
    PancakeRecipe pancake = new PancakeRecipe(ingredients);
    PANCAKES.put(ingredients, pancake);
    return pancake;
  }

  private static void addPopularPancake(EnumMap<Ingredient, Integer> ingredients, String name) {
    POPULAR_PANCAKES.put(name, addPancake(ingredients));
  }
}
