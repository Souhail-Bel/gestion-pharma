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
            produitDAO pdao= new produitDAO(conn);


    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
