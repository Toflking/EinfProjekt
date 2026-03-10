package model;

import java.sql.Timestamp;

// Klasse für User Objekte
public class UserFavorite {
    // Klassenvariablen
    private int user_id;
    private int meal_id;
    private Timestamp created_at;

    // Getter und Setter
    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(int meal_id) {
        this.meal_id = meal_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
