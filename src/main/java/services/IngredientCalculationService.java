package services;

import model.MealIngredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientCalculationService {

    /**
     * Scales ingredients based on servings
     */
    public List<String> scaleIngredients(
            List<MealIngredient> baseIngredients,
            int servings) {

        List<String> result = new ArrayList<>();

        for (MealIngredient mi : baseIngredients) {

            String scaledMeasure =
                    scaleMeasure(mi.getMeasure(), servings);

            result.add(
                    scaledMeasure + " - "
                            + mi.getIngredient_name()
            );
        }

        return result;
    }

    /**
     * Multiplies numeric value inside measure string
     * Example: 300g -> 600g
     */
    private String scaleMeasure(String measure, int factor) {

        if (measure == null || measure.isEmpty())
            return "";

        try {

            String number =
                    measure.replaceAll("[^0-9.]", "");

            String unit =
                    measure.replaceAll("[0-9.]", "");

            double value = Double.parseDouble(number);

            double result = value * factor;

            if (result % 1 == 0) {
                return (int) result + unit;
            }

            return result + unit;

        } catch (Exception e) {
            // text like "to taste"
            return measure;
        }
    }
}