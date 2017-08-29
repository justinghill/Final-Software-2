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
public class EditAppointment {

    private DBConnection connection;
    private Stage user;
    private boolean sameName;
    private String logInUser;
    private boolean isThereAppointment;
    private boolean isStartBeforeEnd;
    private ZoneId currentTimeZone;
    private ZoneId utc;
    private Calendar calendar;

    EditAppointment(String logInUser) {
        Locale location = Locale.getDefault();
        this.logInUser = logInUser;
        currentTimeZone = ZoneId.systemDefault();

        utc = ZoneId.of("UTC");
    }

    public void screen(String appointmentId, boolean isWeek) throws ClassNotFoundException, SQLException {
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

        //lamda for updating appointments
        Lamda appointmentSchedule = (String appointmentString) -> {
            try {
                statement.executeUpdate(appointmentString);
            } catch (SQLException ex) {
                Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("this is how your lamda failed");
            }
        };

        //sets the start and end field to null so we can update an appointment
        Lamda startEndNull = (String appointmentIdString) -> {

            try {
                statement.executeUpdate("Update appointment set start = null where appointmentId = " + appointmentIdString);
                statement.executeUpdate("Update appointment set end = null where appointmentId = " + appointmentIdString);

            } catch (SQLException ex) {
                Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
            }

        };

        //lamda for updating reminders
        Lamda reminderUpdate = (String reminderString) -> {
            try {
                statement.executeUpdate(reminderString);
            } catch (SQLException ex) {
                Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("this is how your lamda failed");
            }
        };
        //Gets all the info from the SQL database
        ResultSet currentAppointment = statement.executeQuery("SELECT * FROM appointment Where appointmentId = " + appointmentId);
        currentAppointment.next();

        String getCustomerId = currentAppointment.getString("customerId");
        String getTitle = currentAppointment.getString("title");
        String getDescription = currentAppointment.getString("description");
        String getLocation = currentAppointment.getString("location");
        String getContact = currentAppointment.getString("contact");
        String getUrl = currentAppointment.getString("url");

        String getYear = currentAppointment.getString("start").substring(0, 4);
        String getMonth = currentAppointment.getString("start").substring(5, 7);
        String getDay = currentAppointment.getString("start").substring(8, 10);
        String getStartHour = currentAppointment.getString("start").substring(11, 13);
        String getStartMin = currentAppointment.getString("start").substring(14, 16);

        String getEndMonth = currentAppointment.getString("end").substring(5, 7);
        String getEndDay = currentAppointment.getString("end").substring(8, 10);
        String getEndHour = currentAppointment.getString("end").substring(11, 13);
        String getEndMin = currentAppointment.getString("end").substring(14, 16);

        //sets zonded date time for start and end dates pulls in the sql time and sets it utc
        ZonedDateTime startTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getMonth), Integer.parseInt(getDay), Integer.parseInt(getStartHour), Integer.parseInt(getStartMin), 0, 0, utc);
        ZonedDateTime endTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getEndMonth), Integer.parseInt(getEndDay), Integer.parseInt(getEndHour), Integer.parseInt(getEndMin), 0, 0, utc);

        //takes utc time and converts it to current timezone
        ZonedDateTime locatStartTimeSQL = startTimeSQL.withZoneSameInstant(currentTimeZone);
        ZonedDateTime locatEndTimeSQL = endTimeSQL.withZoneSameInstant(currentTimeZone);

        //finds all the customers with customer id
        ResultSet customerNameRs = statement.executeQuery("SELECT * FROM customer Where customerId = " + getCustomerId);
        customerNameRs.next();
        String customerNameSt = customerNameRs.getString("customerName");
        //formats date to check and see if time is within working hours
        DateTimeFormatter mins = DateTimeFormatter.ofPattern("mm");
        String startMin = locatStartTimeSQL.format(mins);
        String endMin = locatEndTimeSQL.format(mins);

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
        Label title = new Label("Edit Appointment");
        root.add(title, 2, 1);

        //Creats a spacer between cusotmer and title
        Pane titleSpace = new Pane();
        titleSpace.setMinHeight(25);
        root.add(titleSpace, 1, 2);
        // for the customer name field and textfield
        Label customerName = new Label("Customer Name");
        root.add(customerName, 1, 3);

        Label customerNameTF = new Label(customerNameSt);
        root.add(customerNameTF, 3, 3);

        //for title and textfield
        Label appointmentTitle = new Label("Title");
        root.add(appointmentTitle, 1, 4);

        TextField appointmentTitleTF = new TextField(getTitle);
        root.add(appointmentTitleTF, 3, 4);

        //for description and textfield
        Label description = new Label("Description");
        root.add(description, 1, 5);

        TextField descriptionTF = new TextField(getDescription);
        root.add(descriptionTF, 3, 5);

        //for location
        Label location = new Label("Location");
        ObservableList<String> cityNames = FXCollections.observableArrayList("London", "Pheonix", "New York");

        //assigns a location based on the sql location
        int locationNumber;
        if (getLocation.equals("London")) {
            locationNumber = 0;
        } else if (getLocation.equals("Pheonix")) {
            locationNumber = 1;
        } else {
            locationNumber = 2;
        }

        ComboBox cityDropDown = new ComboBox(cityNames);
        cityDropDown.getSelectionModel().select(locationNumber);

        root.add(location, 1, 6);
        root.add(cityDropDown, 3, 6);

        //for Contact
        Label contact = new Label("Contact");
        root.add(contact, 1, 7);

        TextField contactTF = new TextField(getContact);
        root.add(contactTF, 3, 7);

        //for url
        Label url = new Label("URL");
        root.add(url, 1, 8);

        TextField urlTF = new TextField(getUrl);
        root.add(urlTF, 3, 8);

        //HBOX for month
        HBox monthHB = new HBox();
        Label month = new Label("Month");
        Pane monthSpace = new Pane();
        monthSpace.setMinWidth(10);
        TextField monthTF = new TextField(getMonth);
        Pane monthDateSpace = new Pane();
        monthDateSpace.setMinWidth(10);
        monthHB.getChildren().addAll(month, monthSpace, monthTF);
        root.add(monthHB, 1, 9);
        //HBOX for day
        HBox dayHB = new HBox();
        Label date = new Label("Day");
        Pane dateSpace = new Pane();
        dateSpace.setMinWidth(10);
        TextField dateTF = new TextField(getDay);

        dayHB.getChildren().addAll(monthDateSpace, date, dateSpace, dateTF);

        root.add(dayHB, 3, 9);

        //Info for year
        Label year = new Label("Year");
        TextField yearTF = new TextField(getYear);

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

        //converts the start hour to a 12 hour clock
        int startHourConvert = locatStartTimeSQL.getHour();

        if (startHourConvert > 12) {
            startHourConvert = startHourConvert - 12;
        }

        TextField hourTF = new TextField(startHourConvert + "");

        Label min = new Label("Min");
        TextField minTF = new TextField(startMin);

        //dropdown for times
        ObservableList<String> ampm = FXCollections.observableArrayList("AM", "PM");
        ComboBox startDropDown = new ComboBox(ampm);
        //checks to see if the time is am or pm
        int startAMPM = 0;
        if (locatStartTimeSQL.getHour() <= 12) {
            startAMPM = 0;
        } else {
            startAMPM = 1;
        }

        startDropDown.getSelectionModel().select(startAMPM);
        start.getChildren().addAll(hour, hourTF, min, minTF, startDropDown);

        root.add(start, 1, 12);

        //HBOX for end time
        HBox end = new HBox();
        Label hourEnd = new Label("Hour");
        //converts the end hour to a 12 hour cloce
        int endHourConvert = locatEndTimeSQL.getHour();

        if (endHourConvert > 12) {
            endHourConvert = endHourConvert - 12;
        }
        TextField hourEndTF = new TextField(endHourConvert + "");

        Label minEnd = new Label("Min");
        TextField minEndTF = new TextField(endMin);

        ComboBox endDropDown = new ComboBox(ampm);
        //checks to see if time is am or pm
        int endAMPM = 0;
        if (locatEndTimeSQL.getHour() <= 12) {
            endAMPM = 0;
        } else {
            endAMPM = 1;
        }
        endDropDown.getSelectionModel().select(endAMPM);
        end.getChildren().addAll(hourEnd, hourEndTF, minEnd, minEndTF, endDropDown);

        root.add(end, 3, 12);

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

                    //changes time to a 12 hour clock
                    if (startOutput.equals("PM")) {
                        hourStart = hourStart + 12;
                    }

                    if (endOutput.equals("PM")) {
                        hourEnd = hourEnd + 12;
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

                    //Pulls in start time
                    ZonedDateTime startTime = ZonedDateTime.of(year, month, date, hourStart, minStart, 0, 0, currentTimeZone);

                    ZonedDateTime endTime = ZonedDateTime.of(year, month, date, hourEnd, minEnd, 0, 0, currentTimeZone);

                    //changes start time and endtime to utc
                    ZonedDateTime startUTCTime = startTime.withZoneSameInstant(utc);

                    ZonedDateTime endUTCTime = endTime.withZoneSameInstant(utc);
                    //sets reminder time
                    ZonedDateTime reminder = startUTCTime.minusMinutes(15);

                    String startTime2 = hourStart + ":" + minStart;
                    String endTime2 = hourEnd + ":" + minEnd;

                    //formats date to check and see if time is within working hours
                    DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH");
                    int startHour = Integer.parseInt(startUTCTime.format(hours));
                    int endHour = Integer.parseInt(endUTCTime.format(hours));

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
                    String sqlEnd = endUTCTime.format(sqlDate);
                    String reminderFormat = reminder.format(sqlDate);

                    ResultSet customerId = statement.executeQuery("Select * FROM customer WHERE customerName = '" + customerNameSt + " '");
                    customerId.next();
                    String customersId = customerId.getString("customerId");

                    //test to see if there is a conflicting appointment   tatkes out the current appointment         
                    String appointmentQuery = "select sub.* from ( SELECT * FROM appointment WHERE appointmentId != " + appointmentId + ") sub where start BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'or  end BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'";

                    // "SELECT * FROM appointment WHERE appointmentId != "+appointmentId+" and start BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'or  end BETWEEN '" + sqlStart + "' AND '" + sqlEnd + "'";
                    ResultSet rs = statement.executeQuery(appointmentQuery);

                    isThereAppointment = rs.next();

                    if (isThereAppointment == true) {

                        throw e;

                    } else {

                        //If there isn't a conflict then we add the data to sql
                        String appointmentString = "update appointment set title = '" + appointmentTitle + "' , description = '" + description + "' , location = '" + location + "', contact = '" + contact + "', url = '" + url + "', start = '" + sqlStart + "', end = '" + sqlEnd + "' , lastUpdate = now(), lastUpdateBy = '" + logInUser + "' where appointmentId ='" + appointmentId + "'";

                        //statement.executeUpdate("INSERT INTO appointment(title, description, location, contact, url, start, end,  lastUpdate, lastUpdateBy) values (" + appointmentString + ")");
                        appointmentSchedule.foo(appointmentString);

                        //updates the reminder time
                        String reminderString = "update reminder set reminderDate = '" + reminderFormat + "' where appointmentId = '" + appointmentId + "'";

                        reminderUpdate.foo(reminderString);

                        getStage().hide();
                        //returns you to the proper calendar after saving, and refreshes the calendar
                        if (isWeek == true) {
                            calendar.hideWeek();
                            calendar.week();
                        } else {
                            calendar.hideMonth();
                            calendar.month();
                        }
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
                    Logger.getLogger(EditAppointment.class.getName()).log(Level.SEVERE, null, ex);
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
        user.setTitle("Edit Appointment");
        user.setScene(userScene);

    }

    public Stage getStage() {

        return user;

    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

}
