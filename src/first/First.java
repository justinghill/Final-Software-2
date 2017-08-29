/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package first;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;

/**
 *
 * @author jhill
 */
public class First extends Application {

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException, SQLException {

        //creates the database connectioin       
        DBConnection connection = new DBConnection();
        connection.connect();

        //Data to be insereted after the database is restored. ONly do this once and then comment out.
        Statement statement = connection.getConn().createStatement();

        /*
        
        
        This is a copy of all my database entry info
        
        
        
       statement.executeUpdate("INSERT INTO user " + "VALUES ('1', 'admin', 'admin', '1', 'justin', now(), now(), 'justin')");
        statement.executeUpdate("INSERT INTO user " + "VALUES ('2', 'test', 'test', '1', 'justin', now(), now(), 'justin')");
       statement.executeUpdate("INSERT INTO country " + "VALUES ('1', 'England', now(), 'admin', now(), 'admin')");
       statement.executeUpdate("INSERT INTO country " + "VALUES ('2', 'USA', now(), 'admin', now(), 'admin')");
       statement.executeUpdate("INSERT INTO city " + "VALUES ('1', 'London','1', now(), 'admin', now(), 'admin')");
       statement.executeUpdate("INSERT INTO city " + "VALUES ('2', 'Pheonix','2', now(), 'admin', now(), 'admin')");
       statement.executeUpdate("INSERT INTO city " + "VALUES ('3', 'New York','2', now(), 'admin', now(), 'admin')");
         */
 /*
        auto increment examples
        Ï
        
        ALTER TABLE document MODIFY COLUMN document_id INT auto_increment
        ALTER TABLE customer MODIFY COLUMN customerId INT auto_increment;
        ALTER TABLE address MODIFY COLUMN addressId INT auto_increment;
        Alter Table appointment MODIFY COLUMN appointmentId INT auto_increment;
        
 ALTER TABLE reminder MODIFY COLUMN reminderId INT auto_increment
        INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)
        VALUES ('Bob',  1, 1, now(), 'Bob', now(), 'Bob')
        
        INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)
        values ('afasda','',1, '9432', '9845', now(),'bob',now(),'BOB')
        
        INSERT INTO appointment(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)

        values (2, 'tasfdasdf', 'asdfas', 'location', 'cosdf', 'uasfdarl', '2017-08-10 18:00',  '2017-08-10 19:00', now(), 'justin', now(), 'justin')
        
        
        
        
        
        
        that is to make sure there isn't a conflict
        
        SELECT * FROM appointment WHERE start BETWEEN '2017-08-11 16:00:00' AND '2017-08-11 17:00:00'
or  end BETWEEN '2017-08-10 16:00:00' AND '2017-08-10 17:00:00'
        
        
        
        
        
        
        
        
      this is to find all the appointments within my date range but maybee Ii should just do it by day to match the calendar so it's easier to search then I can just add by day
        select * from appointment 
where start between '2017-08-10 12:00:00' and '2017-08-11 15:00:00'
order by start asc
        
        
        
         */
 /*
        LocalDateTime test = LocalDateTime.of(2017, 8, 15, 12, 50);
        LocalDateTime mins = test.minusMinutes(15);

        LocalDateTime now = LocalDateTime.now();

        if (now.equals(mins)) {
            System.out.println("if statemnt");
        }
         */
        //Gets the location
        Locale location = Locale.getDefault();

        
        // Test to see if the location is the US. if it isn't it gives a french screen
        if (location.getCountry().equals("US")) {
            english(primaryStage);

        } else {
            french(primaryStage);
        }

    }

    // Creates a screen for those located in the us
    public void english(Stage primaryStage) throws ClassNotFoundException, SQLException {

        DBConnection connection = new DBConnection();
        connection.connect();

        //Data to be insereted after the database is restored. ONly do this once and then comment out.
        Statement statement = connection.getConn().createStatement();
        //creates the main root pane
        StackPane root = new StackPane();

        //Sets a vbox on the root pane
        VBox center = new VBox();
        //Creates an HBox for the top part of the Vbox. 
        HBox login = new HBox();
        Text screenTitle = new Text("Login");
        login.setPadding(new Insets(10, 50, 10, 25));
        login.getChildren().add(screenTitle);
        //Creates an HBox to collect user name
        HBox userInfo = new HBox();
        userInfo.setPadding(new Insets(10, 0, 10, 10));
        Label userName = new Label("User Name :    ");
        TextField nameTextField = new TextField();
        userInfo.getChildren().addAll(userName, nameTextField);
        //creats an Hbox to collect password
        HBox password = new HBox();
        password.setPadding(new Insets(10, 0, 10, 10));
        Label passLabel = new Label("Password :      ");
        TextField userPassword = new TextField();
        password.getChildren().addAll(passLabel, userPassword);

        // Creates an Hbox for the error screen
        VBox error = new VBox();
        Text errorMessage = new Text("Incorrect. Try");
        Text errorMessage2 = new Text("User Name: admin");
        Text errorMessage3 = new Text("Password: admin");
        error.getChildren().addAll(errorMessage, errorMessage2, errorMessage3);

        // Creates an hbox for the button
        HBox button = new HBox();
        button.setPadding(new Insets(10, 10, 10, 200));
        Button btn = new Button();
        btn.setText("Login");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //thi sis the code you'll use to check and see if the user is correct
                String userName = nameTextField.getText().toString();
                String password = userPassword.getText().toString();
                try {

                    Statement stat = connection.getConn().createStatement();
                    ResultSet rs = stat.executeQuery("SELECT * FROM user  WHERE  userName = '" + userName + "' and password = '" + password + "';");

                    //this is for testing purposes. we have an auto login delete the line under this when done, and only the line directly under this
                    // ResultSet rs = stat.executeQuery("SELECT * FROM user  WHERE  userName = 'admin' and password = 'admin';");
                    rs.next();
                    System.out.println(rs.getString("userName") + " this is the user name you did it");

                    //add a user name string. that way we can pass that around for all the updates
                    Main loggedIn = new Main(statement, connection, userName);

                    primaryStage.hide();
                    //Creates a file and adds login info        
                    String path = System.getProperty("user.home") + "/Desktop/class.txt";
                    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String date = now.format(format);
                    String zone = ZoneId.systemDefault() + "";

                    String text = "Loggedin: " + userName + " Time: " + date + " " + zone;
                    File file = new File(path);

                    if (file.createNewFile()) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                        writer.write(text);

                        writer.close();
                    } else {
                        BufferedWriter append = new BufferedWriter(new FileWriter(file, true));

                        append.newLine();
                        append.write(text);

                        append.close();
                    }

                } catch (SQLException e) {
                    //Grabs the error message for the button and puts it on login screen
                    center.getChildren().add(error);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(First.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(First.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });
        //adds btn to the button hbox
        button.getChildren().add(btn);
        //adds all info to hbox
        center.getChildren().addAll(login, userInfo, password, button);
        //adds the vbox to the root
        root.getChildren().add(center);
        //adds the root to the sceen
        Scene scene = new Scene(root, 300, 250);
        //sets the primary stage
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    //creates a screen for those outside the us. It's in french
    public void french(Stage primaryStage) throws ClassNotFoundException, SQLException {

        DBConnection connection = new DBConnection();
        connection.connect();

        //Data to be insereted after the database is restored. ONly do this once and then comment out.
        Statement statement = connection.getConn().createStatement();
        //creates the main root pane
        StackPane root = new StackPane();

        //Sets a vbox on the root pane
        VBox center = new VBox();
        //Creates an HBox for the top part of the Vbox. 
        HBox login = new HBox();
        Text screenTitle = new Text("S'identifier");
        login.setPadding(new Insets(10, 50, 10, 25));
        login.getChildren().add(screenTitle);
        //Creates an HBox to collect user name
        HBox userInfo = new HBox();
        userInfo.setPadding(new Insets(10, 0, 10, 10));
        Label userName = new Label("Nom d'utilisateur: : ");
        TextField nameTextField = new TextField();
        userInfo.getChildren().addAll(userName, nameTextField);
        //creats an Hbox to collect password
        HBox password = new HBox();
        password.setPadding(new Insets(10, 0, 10, 10));
        Label passLabel = new Label("Mot de passe: :      ");
        TextField userPassword = new TextField();
        password.getChildren().addAll(passLabel, userPassword);

        // Creates an Hbox for the error screen
        VBox error = new VBox();
        Text errorMessage = new Text("Incorrect. Essayer");
        Text errorMessage2 = new Text("Nom d'utilisateur:: admin");
        Text errorMessage3 = new Text("Mot de passe:: admin");
        error.getChildren().addAll(errorMessage, errorMessage2, errorMessage3);

        // Creates an hbox for the button
        HBox button = new HBox();
        button.setPadding(new Insets(10, 10, 10, 200));
        Button btn = new Button();
        btn.setText("S'identifier");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //this is the code you'll use to check and see if the user is correct
                String userName = nameTextField.getText().toString();
                String password = userPassword.getText().toString();
                try {

                    Statement stat = connection.getConn().createStatement();
                    ResultSet rs = stat.executeQuery("SELECT * FROM user  WHERE  userName = '" + userName + "' and password = '" + password + "';");
                    rs.next();

                    Main loggedIn = new Main(statement, connection, userName);
                    primaryStage.hide();

                    //Creates a file and adds login info        
                    String path = System.getProperty("user.home") + "/Desktop/class.txt";
                    ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String date = now.format(format);
                    String zone = ZoneId.systemDefault() + "";

                    String text = "Loggedin: " + userName + " Time: " + date + " " + zone;
                    File file = new File(path);

                    if (file.createNewFile()) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
                        writer.write(text);

                        writer.close();
                    } else {
                        BufferedWriter append = new BufferedWriter(new FileWriter(file, true));

                        append.newLine();
                        append.write(text);

                        append.close();
                    }
                } catch (SQLException e) {
                    //Grabs the error message for the button and puts it on login screen
                    center.getChildren().add(error);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(First.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(First.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });
        //adds btn to the button hbox
        button.getChildren().add(btn);
        //adds all info to hbox
        center.getChildren().addAll(login, userInfo, password, button);
        //adds the vbox to the root
        root.getChildren().add(center);
        //adds the root to the sceen
        Scene scene = new Scene(root, 300, 250);
        //sets the primary stage
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

}

interface Lamda {

    void foo(String myString);
}
