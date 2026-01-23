public class MealIngredient {
    private int meal_id;
    private int ingredient_id;
    private String measure;

    public int getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "MealIngredient{" +
                "ingredient_id=" + ingredient_id +
                ", meal_id=" + meal_id +
                ", measure='" + measure + '\'' +
                '}';
    }
}
