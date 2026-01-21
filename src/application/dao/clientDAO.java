package application.dao;

import application.modeles.Client;
import application.modeles.Employe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class clientDAO {
    private Connection connection;
    public clientDAO(Connection connection) {
        this.connection = connection;
    }
    public Client FindByID(int id) throws SQLException {
        String query = "SELECT * FROM CLIENT WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone")

                );
            } else {
                return null;
            }
        }
    }
}
