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
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author jhill
 */
public class NewAppointment {

    private DBConnection connection;
    private Stage user;
    private boolean sameName;
    private String logInUser;
    private boolean isThereAppointment;
    private boolean isStartBeforeEnd;

    NewAppointment(String logInUser) {
        Locale location = Locale.getDefault();
        this.logInUser = logInUser;
    }

    public void screen(String nameOfCustomer) throws ClassNotFoundException, SQLException {
        connection = new DBConnection();
        connection.connect();
        //boolean test to see if name has been changed
        sameName = true;

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

        //lamda for scheduling appointments
        Lamda appointmentSchedule = (String appointmentString) -> {
            try {
                statement.executeUpdate("INSERT INTO appointment(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) values (" + appointmentString + ")");
            } catch (SQLException ex) {
                Logger.getLogger(NewAppointment.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("this is how your lamda failed");
            }
        };

        //lamda for scheduling reminders
        Lamda reminderSchedule = (String reminderString) -> {
            try {
                statement.executeUpdate("INSERT INTO reminder(reminderDate, snoozeIncrement, snoozeIncrementTypeId, appointmentId, createdBy, createdDate, remindercol) values (" + reminderString + ")");
            } catch (SQLException ex) {
                Logger.getLogger(NewAppointment.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("this is how your lamda failed");
            }
        };
        //sets the root for the scene

        GridPane root = new GridPane();
        root.setHgap(15);
        root.setVgap(15);

        //Creates a space for the top
        Pane top = new Pane();
        top.setMinHeight(10);
        top.setMinWidth(10);
        root.add(top, 0, 0);
        //Creates a label for the root
        Label title = new Label("New Appointment");
        root.add(title, 2, 1);

        //Creats a spacer between cusotmer and title
        Pane titleSpace = new Pane();
        titleSpace.setMinHeight(25);
        root.add(titleSpace, 1, 2);
        // for the customer name field and textfield
        Label customerName = new Label("Customer Name");
        root.add(customerName, 1, 3);

        TextField customerNameTF = new TextField(nameOfCustomer);
        root.add(customerNameTF, 3, 3);

        //for title and textfield
        Label appointmentTitle = new Label("Title");
        root.add(appointmentTitle, 1, 4);

        TextField appointmentTitleTF = new TextField("");
        root.add(appointmentTitleTF, 3, 4);

        //for description and textfield
        Label description = new Label("Description");
        root.add(description, 1, 5);

        TextField descriptionTF = new TextField("");
        root.add(descriptionTF, 3, 5);

        //for location
        Label location = new Label("Location");
        ObservableList<String> cityNames = FXCollections.observableArrayList("London", "Pheonix", "New York");
        ComboBox cityDropDown = new ComboBox(cityNames);
        cityDropDown.getSelectionModel().select(0);

        root.add(location, 1, 6);
        root.add(cityDropDown, 3, 6);

        //for Contact
        Label contact = new Label("Contact");
        root.add(contact, 1, 7);

        TextField contactTF = new TextField("");
        root.add(contactTF, 3, 7);

        //for url
        Label url = new Label("URL");
        root.add(url, 1, 8);

        TextField urlTF = new TextField("");
        root.add(urlTF, 3, 8);

        //HBOX for month
        HBox monthHB = new HBox();
        Label month = new Label("Month");
        Pane monthSpace = new Pane();
        monthSpace.setMinWidth(10);
        TextField monthTF = new TextField();
        Pane monthDateSpace = new Pane();
        monthDateSpace.setMinWidth(10);
        monthHB.getChildren().addAll(month, monthSpace, monthTF);
        root.add(monthHB, 1, 9);
        //HBOX for day
        HBox dayHB = new HBox();
        Label date = new Label("Day");
        Pane dateSpace = new Pane();
        dateSpace.setMinWidth(10);
        TextField dateTF = new TextField();

        dayHB.getChildren().addAll(monthDateSpace, date, dateSpace, dateTF);

        root.add(dayHB, 3, 9);

        //Info for year
        Label year = new Label("Year");
        TextField yearTF = new TextField();

        root.add(year, 1, 10);
        root.add(yearTF, 3, 10);

        //lable for start and end times
        Label startTime = new Label("Start Time");
        root.add(startTime, 1, 11);

        Label endTime = new Label("End Time");
        root.add(endTime, 3, 11);
        //HBOX for start time
        HBox start = new HBox();
        Label hour = new Label("Hour");
        TextField hourTF = new TextField();

        Label min = new Label("Min");
        TextField minTF = new TextField();

        //dropdown for times
        ObservableList<String> ampm = FXCollections.observableArrayList("AM", "PM");
        ComboBox startDropDown = new ComboBox(ampm);
        startDropDown.getSelectionModel().select(0);
        start.getChildren().addAll(hour, hourTF, min, minTF, startDropDown);

        root.add(start, 1, 12);

        //HBOX for end time
        HBox end = new HBox();
        Label hourEnd = new Label("Hour");
        TextField hourEndTF = new TextField();

        Label minEnd = new Label("Min");
        TextField minEndTF = new TextField();

        ComboBox endDropDown = new ComboBox(ampm);
        endDropDown.getSelectionModel().select(0);
        end.getChildren().addAll(hourEnd, hourEndTF, minEnd, minEndTF, endDropDown);

        root.add(end, 3, 12);

        //save button adds all infor to sql
        Button save = new Button();
        save.setText("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {

                    //takes in all info
                    String startOutput = startDropDown.getSelectionModel().getSelectedItem().toString();
                    String endOutput = endDropDown.getSelectionModel().getSelectedItem().toString();
                    String customerName = customerNameTF.getText().toString();
                    String appointmentTitle = appointmentTitleTF.getText().toString();
                    String description = descriptionTF.getText().toString();
                    String contact = contactTF.getText().toString();
                    String url = urlTF.getText().toString();
                    String location = cityDropDown.getSelectionModel().getSelectedItem().toString();

                    int month = Integer.parseInt(monthTF.getText().toString());
                    int date = Integer.parseInt(dateTF.getText().toString());
                    int hourStart = Integer.parseInt(hourTF.getText().toString());
                    int minStart = Integer.parseInt(minTF.getText().toString());
                    int hourEnd = Integer.parseInt(hourEndTF.getText().toString());
                    int minEnd = Integer.parseInt(minEndTF.getText().toString());
                    int year = Integer.parseInt(yearTF.getText().toString());

                    //Sets a throwable offense
                    IOException e = new IOException();

                    //tests to see if customer name has changed
                    if (!nameOfCustomer.equals(customerName)) {
                        // lamda.foo("Why would you go and change the customers name?");
                        sameName = false;
                        throw e;

                    } else {
                        sameName = true;
                    }

                    //changes time to a 24 hour clock
                    if (startOutput.equals("PM")) {
                        hourStart = hourStart + 12;
                    }

                    if (endOutput.equals("PM")) {
                        //because of the 5 mins appointment buffer we need to make sure that at 12 the time stays at 12 instead of 24
                        if (hourEnd == 12) {
                            //nothing happens to the hour because we will subtract 5 mins form it
                        } else {
                            hourEnd = hourEnd + 12;
                        }
                    }

                    //This is where we will check to make sure start is before end
                    isStartBeforeEnd = true;
                    if (hourStart <= hourEnd) {

                        isStartBeforeEnd = true;

                        if (hourStart < hourEnd) {
                            isStartBeforeEnd = true;
                        } else if (minStart < minEnd && hourStart == hourEnd) {
                            isStartBeforeEnd = true;
                        } else {
                            isStartBeforeEnd = false;
                            throw e;
                        }

                    } else {
                        isStartBeforeEnd = false;
                        throw e;
                    }

                    //sets the current time zone
                    ZoneId currentTimeZone = ZoneId.systemDefault();

                    //Pulls in start time
                    ZonedDateTime startTime = ZonedDateTime.of(year, month, date, hourStart, minStart, 0, 0, currentTimeZone);

                    ZonedDateTime endTime = ZonedDateTime.of(year, month, date, hourEnd, minEnd, 0, 0, currentTimeZone);

                    //Sets utc zone
                    ZoneId utc = ZoneId.of("UTC");

                    //changes start time and endtime to utc
                    ZonedDateTime startUTCTime = startTime.withZoneSameInstant(utc);

                    ZonedDateTime endUTCTime = endTime.withZoneSameInstant(utc);

                    //Minus 5 mins from endtime to reduce confilcts of hour appointments Creates a buffer window
                    ZonedDateTime endUTCMinusFive = endUTCTime.minusMinutes(5);

                    //sets zone time for reminder
                    ZonedDateTime reminder = startUTCTime.minusMinutes(15);

                    String startTime2 = hourStart + ":" + minStart;
                    String endTime2 = hourEnd + ":" + minEnd;

                    //formats date to check and see if time is within working hours
                    DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH");
                    int startHour = Integer.parseInt(startUTCTime.format(hours));
                    int endHour = Integer.parseInt(endUTCMinusFive.format(hours));

                    //Checks to see if start time and end time are within working hours
                    if (startHour < 14 || startHour > 22) {
                        throw e;
                    } else {

                        if (endHour < 14 || endHour > 22) {

                            throw e;
                        } else {

                        }
                    }

                    //formats date for SQL entry and then creates a string for Start and end
                    DateTimeFormatter sqlDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String sqlStart = startUTCTime.format(sqlDate);
                    String sqlEnd = endUTCMinusFive.format(sqlDate);

                    String sqlReminder = reminder.format(sqlDate);

                    //searches searchers customrer for customer name and gets you the customer id
                    ResultSet customerId = statement.executeQuery("Select * FROM customer WHERE customerName = '" + nameOfCustomer + " '");
                    customerId.next();
                    String customersId = customerId.getString("customerId");

                    //test to see if there is a conflicting appointment            
                    String appointmentQuery = "SELECT * FROM appointment WHERE start BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'or  end BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'";

                    ResultSet rs = statement.executeQuery(appointmentQuery);

                    isThereAppointment = rs.next();

                    if (isThereAppointment == true) {

                        throw e;

                    } else {

                        //If there isn't a conflict then we add the data to sql
                        String appointmentString = "'" + customersId + "' , '" + appointmentTitle + "' , '" + description + "' , '" + location + "' , '" + contact + "' , " + "'" + url + "' , '" + sqlStart + "' , '" + sqlEnd + "' ,  now() , '" + logInUser + "' , now(), '" + logInUser + "'";

                        //statement.executeUpdate("INSERT INTO appointment(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) values (" + appointmentString + ")");
                        appointmentSchedule.foo(appointmentString);

                        ResultSet appointmentId = statement.executeQuery("SELECT * FROM appointment WHERE start = '" + sqlStart + "' AND  end ='" + sqlEnd + "'");
                        appointmentId.next();
                        String appointmentIdString = appointmentId.getString("appointmentId");

                        String reminderString = "'" + sqlReminder + "' , '15' , '1' , '" + appointmentIdString + "','" + logInUser + " ' ,  now() ,  'Your reminder'";

                        reminderSchedule.foo(reminderString);
                        getStage().hide();
                    }

                } catch (DateTimeException e) {

                    //pop up screen to tell you to use an actual date
                    lamda.foo("Use an actual Date");
                } catch (NumberFormatException e) {
                    //
                    lamda.foo("Required Fields. Month, Day, Year, Start and End Times");
                } catch (IOException e) {

                    if (sameName == false) {
                        lamda.foo("Why would you go and change the customers name?");
                    } else if (isThereAppointment == true) {
                        lamda.foo("There is a conflict of appointments. Check the calendar");

                    } else if (isStartBeforeEnd == false) {
                        lamda.foo("Start time must be before End time");

                    } else {
                        lamda.foo("Start/End times Must be beteen 9am to 5pm New York time");

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(NewAppointment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        root.add(save, 1, 13);

        Button cancel = new Button();
        cancel.setText("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                getStage().hide();
            }

        });

        root.add(cancel, 3, 13);

        user = new Stage();
        Scene userScene = new Scene(root);
        user.setTitle("New Appointment");
        user.setScene(userScene);

    }

    public Stage getStage() {

        return user;

    }

}
