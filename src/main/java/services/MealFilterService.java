package services;

import dao.MealDAO;
import model.Area;
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
            Category category,
            Area area) throws SQLException {

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
                .filter(m ->
                        area == null ||
                                m.getArea_id() != null &&
                                        m.getArea_id()
                                                == area.getId()
                )
                .collect(Collectors.toList());
    }
}