package org.sweetieslab.model.pancakes;

import java.util.EnumMap;
import java.util.Objects;

public class PancakeRecipe {

    private final EnumMap<Ingredient, Integer> ingredients;

    public PancakeRecipe(EnumMap<Ingredient, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public EnumMap<Ingredient, Integer> getIngredients() {
        return new EnumMap<>(ingredients);
    }

    @Override
    public String toString() {
        StringBuilder description = new StringBuilder("Delicious pancake with ");
        ingredients.forEach((ingredient, quantity) ->
            description.append(ingredient.name().toLowerCase().replace('_', ' '))
                .append(" (")
                .append(quantity)
                .append("), ")
        );
        if (!description.isEmpty()) {
            description.setLength(description.length() - 2);
        }
        description.append("!");
        return description.toString();
    }

    @Override
    public int hashCode() {
        return ingredients.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PancakeRecipe)) {
            return false;
        }
        return Objects.equals(ingredients, ((PancakeRecipe) obj).ingredients);
    }
}
