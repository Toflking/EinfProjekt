package services;

import model.MealIngredient;

import java.util.ArrayList;
import java.util.List;
// Zum Matchen von den Angaben vor den Ingredients
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Die Klasse ist für das Berechnen der Measures nach Anzahl Personen da
public class IngredientCalculationService {
    // Regex Pattern für das Filtern der Zahlen/Angaben vor den Zutaten
    private static final Pattern RANGE_PATTERN = Pattern.compile("^\\s*([0-9]+(?:\\s+[0-9]+/[0-9]+|/[0-9]+|\\.[0-9]+)?)\\s*-\\s*([0-9]+(?:\\s+[0-9]+/[0-9]+|/[0-9]+|\\.[0-9]+)?)(.*)$");
    private static final Pattern VALUE_UNIT_PATTERN = Pattern.compile("^\\s*([0-9]+(?:\\s+[0-9]+/[0-9]+|/[0-9]+|\\.[0-9]+)?)(.*)$");

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

        // Das ganze Matchen
        try {
            Matcher rangeMatcher = RANGE_PATTERN.matcher(measure);
            if (rangeMatcher.matches()) {
                double start = parseNumber(rangeMatcher.group(1));
                double end = parseNumber(rangeMatcher.group(2));
                String unit = rangeMatcher.group(3);
                return formatNumber(start * factor) + "-" + formatNumber(end * factor) + unit;
            }

            Matcher valueMatcher = VALUE_UNIT_PATTERN.matcher(measure);
            if (!valueMatcher.matches()) {
                return measure;
            }

            double value = parseNumber(valueMatcher.group(1));
            String unit = valueMatcher.group(2);
            return formatNumber(value * factor) + unit;

        } catch (Exception e) {
            // Für Angaben die keine Zahlen beinhalten, wie z.B. Salz nach Belieben oder so
            return measure;
        }
    }

    // Methode für das Parsen von Zahlen
    private double parseNumber(String value) {
        String trimmed = value.trim();

        if (trimmed.contains(" ")) {
            String[] parts = trimmed.split("\\s+", 2);
            return parseNumber(parts[0]) + parseNumber(parts[1]);
        }

        if (trimmed.startsWith("/")) {
            return parseFraction("1" + trimmed);
        }

        if (trimmed.contains("/")) {
            return parseFraction(trimmed);
        }

        return Double.parseDouble(trimmed);
    }

    // Für das Parsen von Brüchen wie zb 1/2 Löffel, dann wird das zu 0.5
    private double parseFraction(String value) {
        String[] parts = value.split("/", 2);
        return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
    }

    // Macht die Zahlen zu Strings
    private String formatNumber(double value) {
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        }

        return String.format(Locale.US, "%.2f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}
