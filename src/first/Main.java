/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package first;

import java.awt.Frame;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.time.Clock.system;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author jhill
 */
public class Main {

    private Statement statement;
    private DBConnection connection;
    private TableView<String> users;
    private EditUser editUser;
    private NewAppointment appointment;
    private String logInUser;
    private EditAppointment edit;
    private ZonedDateTime now;
    private Statement reminderStatment;
    private Stage popUp;
    private Calendar calendar;
    private Statement sql;
    private JButton button;
    private JFrame frame;
    private ZoneId utc;
    private ZoneId currentTimeZone;

    Main(Statement statement, DBConnection connection, String logInUser) throws SQLException, ClassNotFoundException {

        this.logInUser = logInUser;
        NewUser userInfo = new NewUser(this, logInUser);
        editUser = new EditUser(this, logInUser);
        this.statement = statement;
        this.connection = connection;
        edit = new EditAppointment(logInUser);
        calendar = new Calendar(edit);
        edit.setCalendar(calendar);
        reminderStatment = connection.getConn().createStatement();
        //gets current time in utc time
        utc = ZoneId.of("UTC");
        now = ZonedDateTime.now().withZoneSameInstant(utc);

        currentTimeZone = ZoneId.systemDefault();
        sql = connection.getConn().createStatement();

        //creates a frame that will be used as a popup window in the   ScheduledExecutorService
        button = new JButton("You Have an appointment in 15 mins.");

        frame = new JFrame();
        frame.setContentPane(button);

        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        //Creates Border Pane
        BorderPane root = new BorderPane();

        //Sets up the right hand side of the border pane
        VBox right = new VBox();
        // Label for Right Pannls
        Label appointments = new Label("Appointments");

        //Space between Appointments and View calender labels
        Pane appoitmentsCalendarSpace = new Pane();
        appoitmentsCalendarSpace.setMinHeight(20);

        //Label for calendars
        Label viewCalendar = new Label("View Calendar");

        //Hbox for calendar buttons
        HBox calendarButtons = new HBox();
        //BUtton for month calendar
        Button month = new Button();
        month.setText("Month");
        month.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                calendar.hideWeek();
                calendar.month();

            }
        });

        //Button for Week Calendar
        Button week = new Button();
        week.setText("Week");
        week.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                calendar.hideMonth();
                try {
                    calendar.week();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //space for calendar buttons
        Pane buttonSpace = new Pane();
        buttonSpace.setMinWidth(10);

        // adds elemts to HBox calendarButtons
        calendarButtons.getChildren().addAll(month, buttonSpace, week);

        //Space between view Calendar and schedule appoinmtne
        Pane calendarScheduleSpace = new Pane();
        calendarScheduleSpace.setMinHeight(10);

        //Schedule Label
        Label schedule = new Label("Schedule New Appointment");

        //Schedule Button
        Button newAppointment = new Button();
        newAppointment.setText("New");
        newAppointment.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                try {
                    String output = users.getSelectionModel().getSelectedItem().toString();

                    appointment = new NewAppointment(logInUser);
                    appointment.screen(output);
                    appointment.getStage().show();
                } catch (NullPointerException ex) {
                    StackPane broke = new StackPane();
                    Label text = new Label("Select a Customer");
                    broke.getChildren().add(text);
                    Stage stage = new Stage();
                    Scene scene = new Scene(broke, 500, 200);
                    stage.setScene(scene);
                    stage.show();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Space for reports and Schedule
        Pane reportSceduleSpace = new Pane();
        reportSceduleSpace.setMinHeight(10);

        //Label to idetify reports
        Label reports = new Label("Reports");
        //Hyper link to run report for number of appointments by type
        Hyperlink appointmentType = new Hyperlink();
        appointmentType.setText("Count of Appoitments by Type");
        appointmentType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {

                    //Runs a query to get appointment type
                    ResultSet appointmentType = statement.executeQuery("SELECT  DISTINCT title From appointment ORDER BY title; ");
                    boolean isThereAppointment = appointmentType.next();
                    VBox appointment = new VBox();

                    //Takes the appoinment types and gets the count. Puts the type and count in an hbox, and adds that to the VBOX
                    while (isThereAppointment == true) {
                        HBox thisAppointment = new HBox();
                        Pane space = new Pane();
                        space.setMinWidth(20);
                        String yourTitle = appointmentType.getString("title");
                        ResultSet appointmentCount = sql.executeQuery("SELECT count(title) from appointment where title = '" + yourTitle + "'");
                        appointmentCount.next();
                        String count = appointmentCount.getString("count(title)");
                        String info = yourTitle + " has a count of " + count;
                        Label thisTitle = new Label(info);

                        thisAppointment.getChildren().addAll(space, thisTitle);
                        appointment.getChildren().add(thisAppointment);
                        isThereAppointment = appointmentType.next();
                    }

                    //Sets the stage for the appointment
                    Stage appointmentStage = new Stage();
                    appointmentStage.setTitle("Appointment Count By Type");
                    Scene appointmentScene = new Scene(appointment, 500, 200);
                    appointmentStage.setScene(appointmentScene);
                    appointmentStage.show();

                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        //hyperLink to show each consultants schedule
        Hyperlink consultantSchedule = new Hyperlink();
        consultantSchedule.setText("Schedule For Each Consultant");
        consultantSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                try {
                    //looks for all unique consultants and creates an hbox to hold them
                    ResultSet consultants = statement.executeQuery("SELECT distinct createdBy from appointment;  ");
                    boolean isThereConsultant = consultants.next();
                    HBox consultantList = new HBox();
                    //searches through unique consultants and then adds ther appointments to the list
                    while (isThereConsultant == true) {
                        VBox currentConsultant = new VBox();
                        String name = consultants.getString("createdBy");
                        Label cName = new Label(name);
                        Pane vSpace = new Pane();
                        vSpace.setMinHeight(15);
                        currentConsultant.getChildren().addAll(cName, vSpace);

                        //looks for the consultants appointments
                        ResultSet appointments = sql.executeQuery("Select * from appointment where createdBy =  '" + name + "' order by start asc");
                        boolean isThereAppointment = appointments.next();
                        while (isThereAppointment == true) {

                            //gets the string info so we can set up zone time
                            String getYear = appointments.getString("start").substring(0, 4);
                            String getMonth = appointments.getString("start").substring(5, 7);
                            String getDay = appointments.getString("start").substring(8, 10);
                            String getStartHour = appointments.getString("start").substring(11, 13);
                            String getStartMin = appointments.getString("start").substring(14, 16);

                            String getEndMonth = appointments.getString("end").substring(5, 7);
                            String getEndDay = appointments.getString("end").substring(8, 10);
                            String getEndHour = appointments.getString("end").substring(11, 13);
                            String getEndMin = appointments.getString("end").substring(14, 16);
                            //sets up utc time
                            ZonedDateTime startTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getMonth), Integer.parseInt(getDay), Integer.parseInt(getStartHour), Integer.parseInt(getStartMin), 0, 0, utc);
                            ZonedDateTime endTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getEndMonth), Integer.parseInt(getEndDay), Integer.parseInt(getEndHour), Integer.parseInt(getEndMin), 0, 0, utc);
                            //sets up current time
                            ZonedDateTime locatStartTimeSQL = startTimeSQL.withZoneSameInstant(currentTimeZone);
                            ZonedDateTime locatEndTimeSQL = endTimeSQL.withZoneSameInstant(currentTimeZone);
                            //allows us to get the date and time out from the zoneddate and time
                            DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH");
                            DateTimeFormatter min = DateTimeFormatter.ofPattern("mm");
                            DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
                            DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
                            DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");

                            int startHour = Integer.parseInt(locatStartTimeSQL.format(hour));
                            int endHour = Integer.parseInt(locatEndTimeSQL.format(hour));
                            String startMin = locatStartTimeSQL.format(min);
                            String startDate = locatStartTimeSQL.format(day);
                            String startMonth = locatStartTimeSQL.format(month);
                            String startYear = locatStartTimeSQL.format(year);
                            String endMin = locatEndTimeSQL.format(min);

                            //checks to see if it is am or pm
                            String startTime = "";
                            String endTime = "";
                            if (startHour >= 12) {
                                if (startHour > 12) {
                                    startHour = startHour - 12;
                                }
                                startTime = startHour + ":" + startMin + " PM";
                            } else {
                                startTime = startHour + ":" + startMin + " AM";
                            }
                            if (endHour >= 12) {
                                endHour = endHour - 12;
                                endTime = endHour + ":" + endMin + " PM";
                            } else {
                                endTime = endHour + ":" + endMin + " AM";
                            }
                            String appointmentInfo = "Date: " + startMonth + "/" + startDate + "/" + startYear + " " + "Start: " + startTime + "  End: " + endTime;

                            Label aName = new Label(appointmentInfo);
                            currentConsultant.getChildren().add(aName);
                            isThereAppointment = appointments.next();
                        }

                        //Space Betwee consultants
                        Pane space = new Pane();
                        space.setMinWidth(20);

                        consultantList.getChildren().addAll(currentConsultant, space);
                        isThereConsultant = consultants.next();
                    }

                    //sets the stage
                    Stage consultantStage = new Stage();
                    consultantStage.setTitle("Consultants Schedule");
                    Scene consultantScene = new Scene(consultantList);
                    consultantStage.setScene(consultantScene);
                    consultantStage.show();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        //hyperlink to show the person signed in complete schedule with hyperlicks to reschedule
        Hyperlink yourScheduleEdits = new Hyperlink();
        yourScheduleEdits.setText(logInUser + "'s Schedule Edits");
        yourScheduleEdits.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                ResultSet appointments;
                try {
                    //Sets up a vbox to hold the edits
                    VBox currentConsultant = new VBox(20);
                    //searches for appointment by lastupdatedby
                    appointments = sql.executeQuery("Select * from appointment where lastUpdateBy =  '" + logInUser + "' order by lastUpdate asc");
                    boolean isThereAppointment = appointments.next();
                    while (isThereAppointment == true) {
                        //gets string info to create zoneddate times
                        String getYear = appointments.getString("lastUpdate").substring(0, 4);
                        String getMonth = appointments.getString("lastUpdate").substring(5, 7);
                        String getDay = appointments.getString("lastUpdate").substring(8, 10);
                        String upDateHour = appointments.getString("lastUpdate").substring(11, 13);
                        String upDateMin = appointments.getString("lastUpdate").substring(14, 16);
                        String title = appointments.getString("title");

                        //creates zoneddateTimes at utc
                        ZonedDateTime upDateTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getMonth), Integer.parseInt(getDay), Integer.parseInt(upDateHour), Integer.parseInt(upDateMin), 0, 0, utc);

                        //converst to local time
                        ZonedDateTime locatUpDateTimeSQL = upDateTimeSQL.withZoneSameInstant(currentTimeZone);

                        DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH");
                        DateTimeFormatter min = DateTimeFormatter.ofPattern("mm");
                        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
                        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
                        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");

                        int upDateHourInt = Integer.parseInt(locatUpDateTimeSQL.format(hour));

                        String currentZoneMin = locatUpDateTimeSQL.format(min);
                        String currentZoneDate = locatUpDateTimeSQL.format(day);
                        String currentZoneMonth = locatUpDateTimeSQL.format(month);
                        String currentZoneYear = locatUpDateTimeSQL.format(year);

                        //creats the am or pm time
                        String currentZoneTime = "";

                        if (upDateHourInt >= 12) {
                            if (upDateHourInt > 12) {
                                upDateHourInt = upDateHourInt - 12;
                            }
                            currentZoneTime = upDateHourInt + ":" + currentZoneMin + " PM";
                        } else {
                            currentZoneTime = upDateHourInt + ":" + currentZoneMin + " AM";
                        }

                        String appointmentInfo = "Date: " + currentZoneMonth + "/" + currentZoneDate + "/" + currentZoneYear + " Type: " + title + " . Last Edited: " + currentZoneTime;
                        Label aName = new Label(appointmentInfo);
                        currentConsultant.getChildren().add(aName);
                        isThereAppointment = appointments.next();

                    }
                    Stage consultantStage = new Stage();
                    consultantStage.setTitle(logInUser + "'s Schedule Edits");

                    //creates an hbox so we can have a little border
                    HBox hold = new HBox(10);
                    Pane left = new Pane();
                    left.setMinWidth(10);
                    Pane right = new Pane();
                    right.setMinWidth(10);
                    hold.getChildren().addAll(left, currentConsultant, right);

                    //sets the stage
                    Scene consultantScene = new Scene(hold);
                    consultantStage.setScene(consultantScene);
                    consultantStage.show();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        //Adds elements to VBOx right
        right.getChildren().addAll(appointments, appoitmentsCalendarSpace, viewCalendar, calendarButtons, calendarScheduleSpace, schedule, newAppointment, reportSceduleSpace, reports, appointmentType, consultantSchedule, yourScheduleEdits);

        //Creates Left Vbox
        VBox left = new VBox();

        //Observable list off all users
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
        //Table view to hold user data
        users = new TableView<>();

        TableColumn<String, String> userName = new TableColumn<>("Customer Name");

        users.getColumns().add(userName);
        userName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        users.setItems(list);
        //HBox for buttons
        HBox userButtons = new HBox();

        // New user button
        Button newUser = new Button();
        newUser.setText("New");
        newUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                //shows the new user screen
                userInfo.getStage().show();

            }
        });
        // New user button
        Button editUser = new Button();
        editUser.setText("Edit");
        editUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {

                //executes address and customer info in tables
                try {
                    //will have to add user info stuff
                    String output = users.getSelectionModel().getSelectedItem().toString();

                    ResultSet rs = statement.executeQuery("SELECT * FROM customer where customerName = '" + output + "';");
                    String customerId = "";
                    while (rs.next()) {
                        customerId = rs.getString("customerId");

                    }
                    //EditUser editUser = new EditUser(customerId);

                    getEditUser().screen(customerId);
                    getEditUser().getStage().show();

                } catch (SQLException ex) {
                    System.out.println("it didnt' work ");
                    Logger.getLogger(NewUser.class.getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("it aint working");

                } catch (NullPointerException ex) {
                    StackPane broke = new StackPane();
                    Label text = new Label("Cant Edit with Nonexistent Customer Data. Select a Customer");
                    broke.getChildren().add(text);
                    Stage stage = new Stage();
                    Scene scene = new Scene(broke, 500, 200);
                    stage.setScene(scene);
                    stage.show();
                }

            }
        });

        //Pane to move buttons away from the edge
        Pane edge = new Pane();
        edge.setMinWidth(75);

        //Space between newUser and edit user
        Pane userSpace = new Pane();
        userSpace.setMinWidth(15);

        userButtons.getChildren().addAll(edge, newUser, userSpace, editUser);

        left.getChildren().addAll(users, userButtons);
        root.setRight(right);
        root.setLeft(left);
        Stage stage = new Stage();
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();

        // runs a check to see if there are any apointments. If there are a pop up window notifies them
        ScheduledExecutorService notification = Executors.newScheduledThreadPool(5);
        notification.scheduleAtFixedRate(() -> {

            now = ZonedDateTime.now().withZoneSameInstant(utc);
            String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00").format(now);

            try {
                //left thise in so you could know the test was running
                System.out.println(date + "this is the tested");

                System.out.println(now + "this is your now");
                ResultSet reminder = reminderStatment.executeQuery("SELECT * FROM reminder   where reminderDate = '" + date + "'");
                boolean isThereReminder = reminder.next();

                if (isThereReminder == true) {
                    String appointmentId = reminder.getString("appointmentId");
                    System.out.println(appointmentId + "this is the appointment id");
                    ResultSet user = reminderStatment.executeQuery("SELECT * FROM appointment  where appointmentId = '" + appointmentId + "'");
                    user.next();
                    String createdBy = user.getString("createdBy");

                    if (logInUser.equals(createdBy)) {
                        System.out.println("you can print this");

                        frame.setVisible(true);

                        //popUp().show();
                        System.out.println("this worked after popup");
                        // popUp().toFront();
                        System.out.println("it worked after too front");

                    }

                } else {
                    System.out.println("no match");
                }

            } catch (SQLException ex) {
                System.out.println("this is what broke");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }, 0, 1L, TimeUnit.MINUTES);

        //closes the entire application when you clsoe the main window
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public TableView getTableView() {
        return users;
    }

    public EditUser getEditUser() {
        return editUser;
    }

}
