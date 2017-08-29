/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package first;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JComboBox;

/**
 *
 * @author jhill
 */
public class NewUser {

    private StackPane root;
    private Stage user;
    private Statement statement;
    private DBConnection connection;
    private Main mainScreen;
    private String logInUser;

    NewUser(Main mainScreen, String logInUser) throws ClassNotFoundException, SQLException {

        this.mainScreen = mainScreen;

        connection = new DBConnection();
        connection.connect();
        this.logInUser = logInUser;

        //Lamda used to activate error screen
        Lamda lamda = (String myString) -> {
            StackPane broke = new StackPane();
            Label text = new Label(myString);
            broke.getChildren().add(text);
            Stage stage = new Stage();
            Scene scene = new Scene(broke, 500, 200);
            stage.setScene(scene);
            stage.show();
        };

        //Data to be insereted after the database is restored. ONly do this once and then comment out.
        Statement statement = connection.getConn().createStatement();
        //root pane
        root = new StackPane();
        VBox main = new VBox();

        //HBOX for title for top of the page
        HBox title = new HBox();
        title.setPadding(new Insets(20, 50, 0, 5));
        Label newUser = new Label("New Customer");
        title.getChildren().add(newUser);

        //top hbox for first name
        HBox firstNameB = new HBox();
        firstNameB.setPadding(new Insets(30, 50, 10, 10));

        Label firstName = new Label("First Name: ");
        TextField firstNameTF = new TextField();

        firstNameB.getChildren().addAll(firstName, firstNameTF);

        //second hbox for last name
        HBox lastNameB = new HBox();
        lastNameB.setPadding(new Insets(5, 50, 10, 10));
        Label lastName = new Label("Last Name: ");
        TextField lastNameTF = new TextField();
        lastNameB.getChildren().addAll(lastName, lastNameTF);

        //third hbox for Address 1
        HBox address1 = new HBox();
        address1.setPadding(new Insets(5, 50, 10, 10));
        Label address = new Label("Address 1:  ");
        TextField addressTF = new TextField();
        address1.getChildren().addAll(address, addressTF);

        //fourth hbox for Address 2
        HBox address2 = new HBox();
        address2.setPadding(new Insets(5, 50, 10, 10));
        Label address2L = new Label("Address 2:  ");
        TextField address2TF = new TextField();
        address2.getChildren().addAll(address2L, address2TF);

        //fith hbox for Phone number
        HBox phoneNumber = new HBox();
        phoneNumber.setPadding(new Insets(5, 50, 10, 10));
        Label number = new Label("Phone #:     ");
        TextField numberTF = new TextField();
        phoneNumber.getChildren().addAll(number, numberTF);

        //sixth hbox for City
        HBox city = new HBox();
        city.setPadding(new Insets(5, 50, 10, 10));
        Label cityName = new Label("City:            ");

        ObservableList<String> cityNames = FXCollections.observableArrayList("London", "Pheonix", "New York");
        ComboBox cityDropDown = new ComboBox(cityNames);
        cityDropDown.getSelectionModel().select(0);

        city.getChildren().addAll(cityName, cityDropDown);

        //Seventh hbox for counry
        HBox country = new HBox();
        country.setPadding(new Insets(5, 50, 10, 10));
        Label countryName = new Label("Country:     ");

        ObservableList<String> countryNames = FXCollections.observableArrayList("England", "USA");
        ComboBox countryDropDown = new ComboBox(countryNames);
        countryDropDown.getSelectionModel().select(0);

        country.getChildren().addAll(countryName, countryDropDown);

        //eigth hbox for zip code
        HBox zip = new HBox();
        zip.setPadding(new Insets(5, 50, 10, 10));
        Label zipCode = new Label("Zip Code:    ");
        TextField zipTF = new TextField();
        zip.getChildren().addAll(zipCode, zipTF);

        //nineth hbox for Button
        HBox button = new HBox();
        button.setPadding(new Insets(5, 50, 10, 10));
        Button save = new Button();
        save.setText("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            //button takes all info from fields and enters it into sql database
            @Override
            public void handle(ActionEvent event) {
                String output = cityDropDown.getSelectionModel().getSelectedItem().toString();

                String firstName = firstNameTF.getText().toString();
                String lastName = lastNameTF.getText().toString();
                String address = addressTF.getText().toString();
                String address2 = address2TF.getText().toString();
                String phoneNumber = numberTF.getText().toString();
                String zip = zipTF.getText().toString();

                //Sets booleans to test if there are fields
                boolean isThereFirstName = false;
                boolean isThereLastName = false;
                boolean isThereAddress = false;
                boolean isTherePhoneNumber = false;
                boolean isThereZip = false;

                //Creates a throwable exeption
                IOException e = new IOException();

                int cityId = 0;

                if (output.equals("London")) {
                    cityId = 1;
                } else if (output.equals("Pheonix")) {
                    cityId = 2;
                } else {
                    cityId = 3;
                }
                //creates a string for the address entry statement

                String addressString = "'" + address + "' , '" + address2 + "' , '" + cityId + "' , '" + zip + "' , '" + phoneNumber + "' , " + "now()" + " , '" + logInUser + "' , " + "now()" + " , '" + logInUser + "'";

                //executes address and customer info in tables
                try {
                    //tests to see if each field has information
                    if (firstName.length() != 0) {
                        isThereFirstName = false;

                    } else {
                        isThereFirstName = true;
                        throw e;
                    }

                    if (lastName.length() != 0) {
                        isThereLastName = false;
                    } else {

                        isThereLastName = true;
                        throw e;
                    }
                    if (address.length() != 0) {
                        isThereAddress = false;
                    } else {

                        isThereAddress = true;
                        throw e;
                    }
                    if (phoneNumber.length() != 0) {
                        isTherePhoneNumber = false;
                    } else {

                        isTherePhoneNumber = true;
                        throw e;
                    }
                    if (zip.length() != 0) {
                        isThereZip = false;
                    } else {

                        isThereZip = true;
                        throw e;
                    }

                    statement.executeUpdate("INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) values (" + addressString + ")");
                    ResultSet rs = statement.executeQuery("SELECT * FROM address WHERE address = '" + address + "' and  address2 = '" + address2 + "';");
                    String addressId = "";
                    while (rs.next()) {
                        addressId = rs.getString("addressId");

                    }
                    statement.executeUpdate("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('" + firstName + " " + lastName + "' , " + addressId + ", 1, now(), '" + logInUser + "' , now(), '" + logInUser + "')");

                    //sets all textfield to blank
                    firstNameTF.setText("");
                    lastNameTF.setText("");
                    addressTF.setText("");
                    address2TF.setText("");
                    numberTF.setText("");
                    zipTF.setText("");

                    //creates an observalbe list to put in the table view so it updates
                    ObservableList<String> list = FXCollections.observableArrayList();
                    String name = "";

                    ResultSet observe = statement.executeQuery("SELECT * FROM customer;");
                    while (observe.next()) {
                        String current = observe.getString("customerName");

                        list.add(current);
                    }

                    //Hides the stage after info is imput
                    mainScreen.getTableView().setItems(list);
                    getStage().hide();

                } catch (SQLException ex) {
                    System.out.println("it didnt' work ");
                    Logger.getLogger(NewUser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {

                    ///If a field doesn't have info it gives the error message
                    if (isThereFirstName == true) {
                        lamda.foo("You need a First Name");

                    }
                    if (isThereLastName == true) {
                        lamda.foo("You need a Last Name");

                    }
                    if (isThereAddress == true) {
                        lamda.foo("You need an Address");

                    }
                    if (isTherePhoneNumber == true) {
                        lamda.foo("You need a Phone Number");

                    }
                    if (isThereZip == true) {
                        lamda.foo("You need a Zip Code");

                    }

                    Logger.getLogger(NewUser.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*
                //creates an observalbe list to put in the table view so it updates
                ObservableList<String> list = FXCollections.observableArrayList();
                String name = "";
                try {
                    ResultSet rs = statement.executeQuery("SELECT * FROM customer;");
                    while (rs.next()) {
                        String current = rs.getString("customerName");

                        list.add(current);

                    }

                } catch (SQLException ex) {
                    System.out.println("it didnt' work");
                    Logger.getLogger(NewUser.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Hides the stage after info is imput

                mainScreen.getTableView().setItems(list);
                getStage().hide();

                 */
            }

        });

        //cancel button
        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //sets all textfields to blank
                firstNameTF.setText("");
                lastNameTF.setText("");
                addressTF.setText("");
                address2TF.setText("");
                numberTF.setText("");
                zipTF.setText("");
                getStage().hide();
            }

        });

        //spacers to separate buttons
        Pane first = new Pane();
        first.setMinWidth(150);

        Pane second = new Pane();
        second.setMinWidth(15);
        button.getChildren().addAll(first, save, second, cancel);

        main.getChildren().addAll(title, firstNameB, lastNameB, address1, address2, phoneNumber, zip, city, country, button);

        root.getChildren().add(main);
        user = new Stage();
        Scene userScene = new Scene(root);
        user.setTitle("New Customer");
        user.setScene(userScene);

    }

    public Stage getStage() {

        return user;

    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

}
