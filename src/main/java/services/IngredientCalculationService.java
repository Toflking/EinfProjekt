package services;

import model.MealIngredient;

import java.util.ArrayList;
import java.util.List;

// Die Klasse ist für das Berechnen der Measures nach Anzahl Personen da
public class IngredientCalculationService {

    // Skaliert die Ingredients basierend auf dem int "servings" also Anzahl Personen
    public List<String> scaleIngredients(
            List<MealIngredient> baseIngredients,
            int servings) {
        // Liste für Rückgabe mit angepassten Werten
        List<String> result = new ArrayList<>();
        // Schleife die durch alle MealIngredients durchgeht
        for (MealIngredient mi : baseIngredients) {
            // Skalierung
            String scaledMeasure =
                    scaleMeasure(mi.getMeasure(), servings);
            // Zusammensetzung des Strings und hinzufügen zu result Liste
            result.add(
                    scaledMeasure + " - "
                            + mi.getIngredient_name()
            );
        }

        return result;
    }

    // Methode für die Berechnung der Skalierung
    private String scaleMeasure(String measure, int factor) {

        if (measure == null || measure.isEmpty())
            return "";

        try {
            // Filtern mit Regex
            String number =
                    measure.replaceAll("[^0-9.]", "");

            String unit =
                    measure.replaceAll("[0-9.]", "");
            // Zahl rausparsen
            double value = Double.parseDouble(number);
            // Multiplikation
            double result = value * factor;

            if (result % 1 == 0) {
                return (int) result + unit;
            }

            return result + unit;

        } catch (Exception e) {
            // Für Angaben die keine Zahlen beinhalten, wie z.B. Salz nach Belieben oder so
            return measure;
        }
    }
}