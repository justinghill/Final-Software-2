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
public class EditUser {

    private StackPane root;
    private Stage user;
    private Statement statement;
    private String customerId;
    private String addressId;
    private DBConnection connection;
    private Main mainScreen;
    private String logInUser;

    EditUser(Main mainScreen, String logInUser) {
        this.mainScreen = mainScreen;
        this.logInUser = logInUser;
    }

    public void screen(String customerId) throws SQLException, ClassNotFoundException {
        //Sets customerId 
        this.customerId = customerId;
        connection = new DBConnection();
        connection.connect();

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

        Statement statement = connection.getConn().createStatement();

        //sets all fields equl to nothing so we can rplace them with sql info
        String customerName = "";
        String addressIdSQL = "";
        String addressSQL = "";
        String address2SQL = "";
        String cityIdSQL = "";
        String postalCodeSQL = "";
        String phoneSQL = "";

        try {
            //sets field info
            ResultSet rs = statement.executeQuery("SELECT * FROM customer WHERE customerId = " + this.customerId + ";");

            while (rs.next()) {

                customerName = rs.getString("customerName");
                addressIdSQL = rs.getString("addressId");
                addressId = addressIdSQL;

            }

        } catch (SQLException ex) {
            System.out.println("it didnt' work ");
            Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            ResultSet rs = statement.executeQuery("SELECT * FROM address WHERE addressId = " + addressIdSQL + ";");

            while (rs.next()) {

                addressSQL = rs.getString("address");
                address2SQL = rs.getString("address2");
                cityIdSQL = rs.getString("cityId");
                postalCodeSQL = rs.getString("postalCode");
                phoneSQL = rs.getString("phone");

            }

        } catch (SQLException ex) {
            System.out.println("it didnt' work ");
            Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
        }

        //root pane
        root = new StackPane();
        VBox main = new VBox();

        //HBOX for title for top of the page
        HBox title = new HBox();
        title.setPadding(new Insets(20, 50, 0, 5));
        Label newUser = new Label("Edit Customer");
        title.getChildren().add(newUser);

        //top hbox for first name
        HBox userNameB = new HBox();
        userNameB.setPadding(new Insets(30, 50, 10, 10));

        Label userName = new Label("User Name: ");
        TextField userNameTF = new TextField(customerName);

        userNameB.getChildren().addAll(userName, userNameTF);

        //second hbox for last name
        HBox lastNameB = new HBox();
        lastNameB.setPadding(new Insets(5, 50, 10, 10));
        Label lastName = new Label("Last Name: ");
        TextField lastNameTF = new TextField();
        lastNameB.getChildren().addAll(lastName, lastNameTF);
        //Spacer middle = new Spacer(50);

        //third hbox for Address 1
        HBox address1 = new HBox();
        address1.setPadding(new Insets(5, 50, 10, 10));
        Label address = new Label("Address 1:  ");
        TextField addressTF = new TextField(addressSQL);
        address1.getChildren().addAll(address, addressTF);

        //fourth hbox for Address 2
        HBox address2 = new HBox();
        address2.setPadding(new Insets(5, 50, 10, 10));
        Label address2L = new Label("Address 2:  ");
        TextField address2TF = new TextField(address2SQL);
        address2.getChildren().addAll(address2L, address2TF);

        //fith hbox for Phone number
        HBox phoneNumber = new HBox();
        phoneNumber.setPadding(new Insets(5, 50, 10, 10));
        Label number = new Label("Phone #:     ");
        TextField numberTF = new TextField(phoneSQL);
        phoneNumber.getChildren().addAll(number, numberTF);

        //sixth hbox for City
        HBox city = new HBox();
        city.setPadding(new Insets(5, 50, 10, 10));
        Label cityName = new Label("City:            ");

        ObservableList<String> cityNames = FXCollections.observableArrayList("London", "Pheonix", "New York");
        ComboBox cityDropDown = new ComboBox(cityNames);
        int cityNumber = Integer.parseInt(cityIdSQL);
        cityDropDown.getSelectionModel().select(cityNumber - 1);

        city.getChildren().addAll(cityName, cityDropDown);

        //Seventh hbox for counry
        HBox country = new HBox();
        country.setPadding(new Insets(5, 50, 10, 10));
        Label countryName = new Label("Country:     ");

        ObservableList<String> countryNames = FXCollections.observableArrayList("England", "USA");
        ComboBox countryDropDown = new ComboBox(countryNames);

        if (cityNumber == 1) {
            countryDropDown.getSelectionModel().select(0);
        } else {
            countryDropDown.getSelectionModel().select(1);
        }

        country.getChildren().addAll(countryName, countryDropDown);

        //eigth hbox for zip code
        HBox zip = new HBox();
        zip.setPadding(new Insets(5, 50, 10, 10));
        Label zipCode = new Label("Zip Code:    ");
        TextField zipTF = new TextField(postalCodeSQL);
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

                String userName = userNameTF.getText().toString();
                //String lastName = lastNameTF.getText().toString();
                String address = addressTF.getText().toString();
                String address2 = address2TF.getText().toString();
                String phoneNumber = numberTF.getText().toString();
                String zip = zipTF.getText().toString();

                //sets the city info
                int cityId = 0;

                if (output.equals("London")) {
                    cityId = 1;
                } else if (output.equals("Pheonix")) {
                    cityId = 2;
                } else {
                    cityId = 3;
                }

                //Sets booleans to test if there are fields
                boolean isThereName = false;
                boolean isThereAddress = false;
                boolean isTherePhoneNumber = false;
                boolean isThereZip = false;

                IOException e = new IOException();

                //executes address and customer info in tables
                try {

                    //tests to see if each field has information
                    if (userName.length() != 0) {
                        isThereName = false;
                    } else {

                        isThereName = true;
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

                    statement.executeUpdate(
                            "UPDATE address Set address =  '" + address + "', address2 = '" + address2 + "', cityId = '" + cityId + "', postalCode = '" + zip + "', phone = '" + phoneNumber + "', lastUpdate = now(),  lastUpdateBy =  '" + logInUser + "' WHERE addressId = '" + addressId + "'");

                    statement.executeUpdate("UPDATE customer Set customerName = '" + userName + "', lastUpdate = now(), lastUpdateBy = '" + logInUser + "' WHERE customerId = '" + customerId + "'");

                    //creates an observalbe list to put in the table view so it updates
                    ObservableList<String> list = FXCollections.observableArrayList();
                    String name = "";

                    ResultSet rs = statement.executeQuery("SELECT * FROM customer;");
                    while (rs.next()) {
                        String current = rs.getString("customerName");

                        list.add(current);

                    }
                    //Hides the stage after info is imput

                    mainScreen.getTableView().setItems(list);
                    getStage().hide();

                } catch (SQLException ex) {
                    System.out.println("it didn't work");
                    Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    if (isThereName == true) {
                        lamda.foo("You need a Name");

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

                    Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
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
                    System.out.println("it didn't work");
                    Logger.getLogger(NewUser.class.getName()).log(Level.SEVERE, null, ex);
                }
                 */

            }

        });

        //cancel button
        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                getStage().hide();
            }

        });

        //spacers to separate buttons
        Pane first = new Pane();
        first.setMinWidth(150);

        Pane second = new Pane();
        second.setMinWidth(15);
        button.getChildren().addAll(first, save, second, cancel);

        main.getChildren().addAll(title, userNameB, address1, address2, phoneNumber, zip, city, country, button);

        root.getChildren().add(main);
        user = new Stage();
        Scene userScene = new Scene(root);
        user.setTitle("Edit Customer");
        user.setScene(userScene);

    }

    public Stage getStage() {

        return user;

    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public void setMain(Main mainScreen) {
        this.mainScreen = mainScreen;
    }

}
