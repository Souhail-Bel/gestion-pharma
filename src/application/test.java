package application;
import application.modeles.*;
import application.dao.*;
import application.resources.*;

import java.sql.Connection;
import java.sql.SQLException;


public class test {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            employeeDAO edao = new employeeDAO(conn);
            Employe e = edao.findEmployeByUsername("he");
            if (e == null) {
                System.out.println("No employee found with username 'he'.");
            } else {
                System.out.println("Found employee: " + e.getNom());
            }
            conn.close();
        } catch (SQLException ex) {
            System.out.println("SQL error when attempting to connect or query: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
