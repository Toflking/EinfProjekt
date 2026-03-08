package services;

import dao.MealDAO;
import model.Area;
import model.Category;
import model.Meal;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

// Klassen zum Filtern einer Sucheingabe
public class MealFilterService {
    // Erstellen von MealDao Objekt
    private final MealDAO mealDAO = new MealDAO();

    // Suche und Filter Methode
    public List<Meal> searchMeals(
            String query,
            Category category,
            Area area) throws SQLException {
        // Liste erstellen die alle angezeigten meals am Ende beinhält
        List<Meal> meals = mealDAO.listMeals();
        // Filtern
        return meals.stream()
                // Filtern nach Query
                .filter(m ->
                        query == null ||
                                query.isEmpty() ||
                                m.getName()
                                        .toLowerCase()
                                        .contains(query.toLowerCase())
                )
                // Filtern nach Kategorie
                .filter(m ->
                        category == null ||
                                m.getCategory_id() != null &&
                                        m.getCategory_id()
                                                == category.getId()
                )
                // Filtern nach Area
                .filter(m ->
                        area == null ||
                                m.getArea_id() != null &&
                                        m.getArea_id()
                                                == area.getId()
                )
                // Hinzufügen der Meals die alle Filter "bestanden" haben zur Liste
                .collect(Collectors.toList());
    }
}