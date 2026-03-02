package model;

// Klasse für MealIngredient Objekte
public class MealIngredient {
    // Klassenvariablen
    private int meal_id;
    private int ingredient_id;
    private String ingredient_name;
    private String measure;

    // Getter und Setter
    public int getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public int getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    // toString Methode
    @Override
    public String toString() {
        return measure + " - " + ingredient_name;
    }
}