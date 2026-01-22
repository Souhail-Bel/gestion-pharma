package application.dao;

import application.modeles.Client;
import application.modeles.Fournisseur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {
    private Connection connection;

    public FournisseurDAO(Connection connection) { this.connection = connection; }

    public ArrayList<Fournisseur> getAll() throws SQLException {
        ArrayList<Fournisseur> list = new ArrayList<>();
        String query = "SELECT * FROM FOURNISSEUR";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Fournisseur(rs.getInt("id"), rs.getString("nom"), rs.getString("telephone"), rs.getString("email"), rs.getString("adresse")));
            }
        }
        return list;
    }

    public void save(Fournisseur f) throws SQLException {
        String query = "INSERT INTO FOURNISSEUR (nom, telephone, email, adresse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getTelephone());
            stmt.setString(3, f.getEmail());
            stmt.setString(4, f.getAdresse());
            stmt.executeUpdate();
        }
    }

	public Fournisseur findByID(int fournisseurId) throws SQLException {
        String query = "SELECT * FROM FOURNISSEUR WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, fournisseurId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse")
                );
            } else {
                return null;
            }
        }
	}
	
	public List<Fournisseur> findByFilter(String keyword) throws SQLException {
	    List<Fournisseur> list = new ArrayList<>();
	    
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return getAll();
	    }

	    String sql = "SELECT * FROM FOURNISSEUR WHERE nom LIKE ? OR telephone LIKE ? OR email LIKE ? OR adresse LIKE ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        String rechFormat = "%" + keyword + "%";
	        pstmt.setString(1, rechFormat);
	        pstmt.setString(2, rechFormat);
	        pstmt.setString(3, rechFormat);
	        pstmt.setString(4, rechFormat);
	        
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            list.add(new Fournisseur(
	                rs.getInt("id"),
	                rs.getString("nom"),
	                rs.getString("telephone"),
	                rs.getString("email"),
	                rs.getString("adresse")
	            ));
	        }
	    }
	    return list;
	}
	
	
	public void update(Fournisseur f) throws SQLException {
        String query = "UPDATE FOURNISSEUR SET nom=?, telephone=?, email=?, adresse=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, f.getNom());
            stmt.setString(2, f.getTelephone());
            stmt.setString(3, f.getEmail());
            stmt.setString(4, f.getAdresse());
            stmt.setInt(5, f.getId());
            stmt.executeUpdate();
        }
    }
}