import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MealDAO {
    private Meal buildMeal(ResultSet rs) throws SQLException {
        Meal meal = new Meal();
        meal.setId(rs.getInt("id"));
        meal.setExternal_id(rs.getInt("external_id"));
        meal.setName(rs.getString("name"));
        meal.setCategory_id(rs.getInt("category_id"));
        meal.setArea_id(rs.getInt("area_id"));
        meal.setInstructions(rs.getString("instructions"));
        meal.setThumb(rs.getString("thumb"));
        meal.setYoutube(rs.getString("youtube"));
        meal.setSource(rs.getString("source"));
        meal.setTags(rs.getString("tags"));
        meal.setCreated_at(rs.getTimestamp("created_at"));
        meal.setUpdated_at(rs.getTimestamp("updated_at"));
        return meal;
    }

    public void CreateMeal(Meal meal) throws SQLException {
        String sql = "INSERT INTO meals (name, category_id, area_id, instructions, thumb, youtube, source, tags) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, meal.getName());
            stmt.setInt(2, meal.getCategory_id());
            stmt.setInt(3, meal.getArea_id());
            stmt.setString(4, meal.getInstructions());
            stmt.setString(5, meal.getThumb());
            stmt.setString(6, meal.getYoutube());
            stmt.setString(7, meal.getSource());
            stmt.setString(8, meal.getTags());
            stmt.executeUpdate();
        }
    }
}
