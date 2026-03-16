package dao;

import db.Database;
import model.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Backend
// DAO steht für Data Access Object
// Alle Methoden für Interaktionen zwischen Area Objekten und der Datenbank
/* 
Jegliche Methoden hier funktionieren eigentlich nach demselben Prinzip:
1. Liste/Objekt erstellen, falls nötig
2. Verbindung mit der Datenbank aufnehmen
3. SQL query aufbauen (Unten immer PreparedStatement stmt)
4. Diesen Query ausführen, gibt ResultSet zurück
5. Resultset auswerten und entweder eine Liste/Objekt zurückgeben oder einen int, ob es funktioniert hat
 */
// Diese Klasse wurde vor dem Frontend gemacht, kann also Methoden beinhalten, die nie benutzt werden
// Auf die Delete Methode wurde verzichtet, da es eigentlich keinen Grund gibt, eine Area zu löschen
public class AreaDAO {
    // SQL Strings
    private static final String CREATE_AREA = "INSERT INTO areas (name) VALUES (?)";
    private static final String GET_AREA_BY_ID = "SELECT * FROM areas WHERE id = ?";
    private static final String GET_AREA_BY_NAME = "SELECT * FROM areas WHERE name = ?";
    private static final String LIST_AREAS = "SELECT * FROM areas";
    private static final String UPDATE_AREA = "UPDATE areas SET name = ? WHERE id = ?";

    // Create Methode zum Erstellen einer Area in der Datenbank
    public int createArea(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_AREA, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
    
    // Verschiedene Read Methoden:
    // Suche nach Area anhand der id in der DB
    public Area getAreaById(int id) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_AREA_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildArea(rs);
                }
            }
        }
        return null;
    }

    // Suche nach Area anhand des Namens in der DB
    public Area getAreaByName(String name) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_AREA_BY_NAME)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildArea(rs);
                }
            }
        }
        return null;
    }

    // Auflistung aller Areas
    public List<Area> listAreas() throws SQLException {
        List<Area> areas = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LIST_AREAS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                areas.add(buildArea(rs));
            }
        }
        return areas;
    }

    // Update Methode, wenn man eine Area in der DB verändern möchte
    public int updateArea(Area area) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_AREA)) {
            stmt.setString(1, area.getName());
            stmt.setInt(2, area.getId());
            return stmt.executeUpdate();
        }
    }

    // Helper Methoden, nur zum Organisieren des Codes, damit obige Methoden nicht zu gross sind
    // Baut ein Area Objekt anhand eines Result Sets auf
    private Area buildArea(ResultSet rs) throws SQLException {
        Area area = new Area();
        area.setId(rs.getInt("id"));
        area.setName(rs.getString("name"));
        return area;
    }
}

