package application.dao;

import application.modeles.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private Connection connection;
    public ClientDAO(Connection connection) {
        this.connection = connection;
    }
    
    public Client findById(int id) throws SQLException {
        String query = "SELECT * FROM CLIENT WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone")

                );
            } else {
                return null;
            }
        }
    }

    public Client findByTelephone(String telephone) throws SQLException {
        String query = "SELECT * FROM CLIENT WHERE telephone = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, telephone);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone")
                );
            }
        }
        return null;
    }
    
    public ArrayList<Client> getAllClients() throws SQLException {
        ArrayList<Client> list = new ArrayList<>();
        String query = "SELECT * FROM CLIENT";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Client(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), rs.getString("telephone")));
            }
        }
        return list;
    }
    
    
    
    public int save(Client c) throws SQLException {
        String query = "INSERT INTO CLIENT (nom, prenom, telephone) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getNom());
            stmt.setString(2, c.getPrenom());
            stmt.setString(3, c.getTelephone());
            
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}
