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
            clientDAO cdao= new clientDAO(conn);
            Client client = cdao.FindByID(1);
            if (client != null) {
                System.out.println("Client trouvé : " + client.getNom() );
            } else {
                System.out.println("Client non trouvé.");
            }


    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
