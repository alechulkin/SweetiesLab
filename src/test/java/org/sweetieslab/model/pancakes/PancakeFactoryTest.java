package org.sweetieslab.model.pancakes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.EnumMap;
import org.junit.jupiter.api.Test;

class PancakeFactoryTest {

  @Test
  void testGetPancakeRecipe() {
    EnumMap<Ingredient, Integer> ingredients = new EnumMap<>(Ingredient.class);
    ingredients.put(Ingredient.FLOUR, 100);
    ingredients.put(Ingredient.EGG, 1);
    ingredients.put(Ingredient.MILK, 150);
    ingredients.put(Ingredient.DARK_CHOCOLATE, 50);

    PancakeRecipe pancake = PancakeFactory.getPancakeRecipe(ingredients);
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> retrievedIngredients = pancake.getIngredients();
    assertEquals(ingredients, retrievedIngredients);
  }

  @Test
  void testGetDarkChocolateWhippedCreamPancakeRecipe() {
    PancakeRecipe pancake = PancakeFactory.getDarkChocolateWhippedCreamPancake();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(50, ingredients.get(Ingredient.DARK_CHOCOLATE));
    assertEquals(150, ingredients.get(Ingredient.WHIPPED_CREAM));
  }

  @Test
  void testGetDarkChocolateWhippedCreamHazelnutPancake() {
    PancakeRecipe pancake = PancakeFactory.getDarkChocolateWhippedCreamHazelnutPancake();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(100, ingredients.get(Ingredient.FLOUR));
    assertEquals(1, ingredients.get(Ingredient.EGG));
    assertEquals(150, ingredients.get(Ingredient.WHIPPED_CREAM));
    assertEquals(50, ingredients.get(Ingredient.HAZELNUT));
    assertEquals(50, ingredients.get(Ingredient.DARK_CHOCOLATE));
  }

  @Test
  void testGetDarkChocolatePancake() {
    PancakeRecipe pancake = PancakeFactory.getDarkChocolatePancake();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(100, ingredients.get(Ingredient.FLOUR));
    assertEquals(1, ingredients.get(Ingredient.EGG));
    assertEquals(150, ingredients.get(Ingredient.MILK));
    assertEquals(50, ingredients.get(Ingredient.DARK_CHOCOLATE));
  }

  @Test
  void testGetMilkChocolateHazelnutPancakeRecipe() {
    PancakeRecipe pancake = PancakeFactory.getMilkChocolateHazelnutPancakeRecipe();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(100, ingredients.get(Ingredient.FLOUR));
    assertEquals(1, ingredients.get(Ingredient.EGG));
    assertEquals(200, ingredients.get(Ingredient.MILK));
    assertEquals(50, ingredients.get(Ingredient.MILK_CHOCOLATE));
    assertEquals(50, ingredients.get(Ingredient.HAZELNUT));
  }

  @Test
  void testGetMilkChocolatePancakeRecipe() {
    PancakeRecipe pancake = PancakeFactory.getMilkChocolatePancakeRecipe();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(100, ingredients.get(Ingredient.FLOUR));
    assertEquals(1, ingredients.get(Ingredient.EGG));
    assertEquals(200, ingredients.get(Ingredient.MILK));
    assertEquals(50, ingredients.get(Ingredient.MILK_CHOCOLATE));
  }

  @Test
  void testGetRandomPancakeRecipe() {
    PancakeRecipe pancake = PancakeFactory.getRandomPancakeRecipe();
    assertNotNull(pancake);
    EnumMap<Ingredient, Integer> ingredients = pancake.getIngredients();
    assertEquals(6, ingredients.size());
    assertEquals(2, ingredients.get(Ingredient.EGG));
    assertEquals(50, ingredients.get(Ingredient.FLOUR));
    assertEquals(100, ingredients.get(Ingredient.MILK));
  }
}