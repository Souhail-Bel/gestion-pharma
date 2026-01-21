package application;
import application.modeles.*;
import application.dao.*;
import application.resources.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class test {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            commandefournisseurDAO fDao= new commandefournisseurDAO(conn);
            Fournisseur f=new Fournisseur(1,"gary","20696942","vjefhbimhbiu","la7bibya");
            CommandeFournisseur comF=new CommandeFournisseur(1,f, LocalDateTime.now(),"CREATED");
            fDao.register(comF);

    } catch (SQLException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }
}
