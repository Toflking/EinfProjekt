package services;

import dao.MealDAO;
import model.Category;
import model.Meal;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MealFilterService {

    private final MealDAO mealDAO = new MealDAO();

    /**
     * Filters meals by search text and category
     */
    public List<Meal> searchMeals(
            String query,
            Category category) throws SQLException {

        List<Meal> meals = mealDAO.listMeals();

        return meals.stream()
                .filter(m ->
                        query == null ||
                                query.isEmpty() ||
                                m.getName()
                                        .toLowerCase()
                                        .contains(query.toLowerCase())
                )
                .filter(m ->
                        category == null ||
                                m.getCategory_id() != null &&
                                        m.getCategory_id()
                                                == category.getId()
                )
                .collect(Collectors.toList());
    }
}