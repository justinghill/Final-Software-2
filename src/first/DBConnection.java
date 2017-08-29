/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package first;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author jhill
 */
public class DBConnection {

    Connection conn = null;
    String driver = "com.mysql.jdbc.Driver";
    String db = "U040rY";
    String url = "jdbc:mysql://52.206.157.109/" + db;
    String user = "U040rY";
    String pass = "53688142714";

    public DBConnection() {

    }

    public void connect() throws ClassNotFoundException {
        try {

            // Makes a connection to the database and prints out that you are connected
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to database : " + db);

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }

    public Connection getConn() {
        return conn;
    }
}
