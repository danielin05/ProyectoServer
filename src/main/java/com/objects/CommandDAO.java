package com.objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CommandDAO {
     
    private static final String DB_URL = "jdbc:mysql://localhost:3306/barretina";

    public static Connection DBConnect(String url){
            try {
                Connection conn = DriverManager.getConnection(DB_URL);
                System.out.println("CONECTION");
                return conn;
    
            } catch (SQLException e) {
                System.out.println("Error connecting to the database: " + e.getMessage());
            }
                return null;
        }
    
        public static void main(String[] args) {
            DBConnect(DB_URL);
    }




    public void ejemplo(Connection conn){
        
    }

}
