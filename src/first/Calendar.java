/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package first;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import static javafx.geometry.Pos.CENTER;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * @author jhill
 */
public class Calendar {

    private int today;
    private DateTimeFormatter date;
    private LocalDate localDate;
    private int startOfWeek;
    private int endOfWeek;
    private Stage week;
    private Stage month;
    private DBConnection connection;
    private Statement statement;
    private ZoneId currentTimeZone;
    private ZoneId utc;
    private Statement statment2;
    private EditAppointment edit;
    private boolean isWeek;

    public Calendar(EditAppointment edit) throws ClassNotFoundException, SQLException {
       
        localDate = LocalDate.now();

        today = Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(localDate));

        startOfWeek = Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(localDate.with(DayOfWeek.MONDAY)));
        endOfWeek = Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(localDate.with(DayOfWeek.SUNDAY)));
        week = new Stage();
        month = new Stage();
        connection = new DBConnection();
        connection.connect();
        statement = connection.getConn().createStatement();
        date = DateTimeFormatter.ofPattern("yyy/MM/dd HH:mm:ss");
        currentTimeZone = ZoneId.systemDefault();
        statment2 = connection.getConn().createStatement();

        utc = ZoneId.of("UTC");
        this.edit = edit;
    }

    public Stage week() throws SQLException {
        //Sets a boolean to test if its a week 
        isWeek = true;
        //creates a GridPane that we will attach all the calendar items to
        GridPane root = new GridPane();
        //Sets the Days across the top of the page
        Label mon = new Label("Monday");
        Label tues = new Label("Tuesday");
        Label wed = new Label("Wednesday");
        Label thurs = new Label("Thursday");
        Label fri = new Label("Friday");
        Label sat = new Label("Saturday");
        Label sun = new Label("Sunday");
        //Sets the width of GridPane
        root.setMinWidth(300);
        //adds the top line of Days
        root.add(mon, 1, 1);
        root.add(tues, 2, 1);
        root.add(wed, 3, 1);
        root.add(thurs, 4, 1);
        root.add(fri, 5, 1);
        root.add(sat, 6, 1);
        root.add(sun, 7, 1);
        //centers each day
        root.setHalignment(mon, HPos.CENTER);
        root.setHalignment(tues, HPos.CENTER);
        root.setHalignment(wed, HPos.CENTER);
        root.setHalignment(thurs, HPos.CENTER);
        root.setHalignment(fri, HPos.CENTER);
        root.setHalignment(sat, HPos.CENTER);
        root.setHalignment(sun, HPos.CENTER);

        //Creates counter to mark out 7 days
        int i = 1;
        //creates a counter to add the calendar days
        int dayCount = startOfWeek;
        LocalDate start = localDate.with(DayOfWeek.MONDAY);
        //creats a counter to add the grid numbers
        int gridCount = 1;
        //adds boxes to the grid of the calendar
        while (i <= 7) {

            VBox main = new VBox(30);
            main.setStyle("-fx-border-style: solid");

            main.setMinSize(300, 600);
            main.setMaxSize(300, 600);
            HBox top = new HBox();

            Label date = new Label("" + Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(start.plusDays(i - 1))));
            date.setPadding(new Insets(2, 2, 0, 0));
            top.setAlignment(Pos.TOP_RIGHT);
            top.getChildren().add(date);
            main.getChildren().add(top);

            //Gets the date
            String dateString = "" + Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(start.plusDays(i - 1)));

            try {

                //sets up a query to test for the current date in loop
                String query = "select * from appointment where start between '2017-08-" + dateString + " 00:0:01' and '2017-08-" + dateString + " 23:59:00' order by start asc";

                ResultSet currentAppointment = statement.executeQuery(query);

                //checks to see if there is an appointment
                boolean isThereAnAppoitment = currentAppointment.next();
                while (isThereAnAppoitment == true) {

                    //gets proper info
                    String getCustomerId = currentAppointment.getString("customerId");
                    String getAppointmentId = currentAppointment.getString("appointmentId");
                    String getStartTime = currentAppointment.getString("start");
                    String getYear = currentAppointment.getString("start").substring(0, 4);
                    String getMonth = currentAppointment.getString("start").substring(5, 7);
                    String getDay = currentAppointment.getString("start").substring(8, 10);
                    String getStartHour = currentAppointment.getString("start").substring(11, 13);
                    String getStartMin = currentAppointment.getString("start").substring(14, 16);

                    String getEndMonth = currentAppointment.getString("end").substring(5, 7);
                    String getEndDay = currentAppointment.getString("end").substring(8, 10);
                    String getEndHour = currentAppointment.getString("end").substring(11, 13);
                    String getEndMin = currentAppointment.getString("end").substring(14, 16);

                    //sets time to utc
                    ZonedDateTime startTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getMonth), Integer.parseInt(getDay), Integer.parseInt(getStartHour), Integer.parseInt(getStartMin), 0, 0, utc);
                    ZonedDateTime endTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getEndMonth), Integer.parseInt(getEndDay), Integer.parseInt(getEndHour), Integer.parseInt(getEndMin), 0, 0, utc);
                    //gets current time
                    ZonedDateTime locatStartTimeSQL = startTimeSQL.withZoneSameInstant(currentTimeZone);
                    ZonedDateTime locatEndTimeSQL = endTimeSQL.withZoneSameInstant(currentTimeZone);

                    //sets query to find customer id
                    String tq = "select * from customer where customerId = " + getCustomerId;

                    ResultSet customerName = statment2.executeQuery(tq);

                    customerName.next();

                    String getCustomerName = customerName.getString("customerName");

                    //Creates a start hour integer
                    int startHour = Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(locatStartTimeSQL));
                    //creates an end min string
                    String startMin = DateTimeFormatter.ofPattern("mm").format(locatStartTimeSQL);

                    //Creates a start hour integer
                    int endHour = Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(locatEndTimeSQL));
                    //creates an end min string
                    String endMin = DateTimeFormatter.ofPattern("mm").format(locatEndTimeSQL);

                    //creates a vbox to hold appintments
                    VBox appointment = new VBox();
                    //Adds customer name
                    Label name = new Label(getCustomerName);

                    //HBox for time and reschudle link
                    HBox timeBox = new HBox();

                    //checks to see if time is am or pm and then creats a time string
                    String startTimeString = "";
                    if (startHour > 12) {
                        startTimeString = startHour - 12 + ":" + startMin + " PM";
                    } else if (startHour == 12) {
                        startTimeString = startHour + ":" + startMin + " PM";
                    } else if (startHour == 0) {
                        startTimeString = startHour + 12 + ":" + startMin + " AM";
                    } else {
                        startTimeString = startHour + ":" + startMin + " AM";
                    }

                    //checks to see if time is am or pm and then creats a time string
                    String endTimeString = "";
                    if (endHour > 12) {
                        endTimeString = endHour - 12 + ":" + endMin + " PM";

                    } else if (endHour == 12) {
                        endTimeString = endHour + ":" + endMin + " PM";
                    } else if (endHour == 0) {
                        endTimeString = endHour + 12 + ":" + endMin + " AM";
                    } else {
                        endTimeString = endHour + ":" + endMin + " AM";
                    }

                    Label time = new Label(startTimeString + " - " + endTimeString);
                    Label space = new Label("    ");
                    //hperlink to reschedule appointment
                    Hyperlink reschedule = new Hyperlink();
                    reschedule.setText("Reschedule");
                    reschedule.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {

                            try {
                                edit.screen(getAppointmentId, isWeek);
                                edit.getStage().show();
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(Calendar.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(Calendar.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    });
                    //adds the time, a space between time, and the reschedule to the time box
                    timeBox.getChildren().addAll(time, space, reschedule);
                    //adds the name and the timebox to the appointment vbox
                    appointment.getChildren().addAll(name, timeBox);
                    //adds vbox to the main box
                    main.getChildren().addAll(appointment);

                    //checks to see if there is another appointment
                    isThereAnAppoitment = currentAppointment.next();

                }

            } catch (SQLException ex) {
                System.out.println("SQL DIDNT WORK");
            }
           
            root.add(main, gridCount, 2);
            i++;

            dayCount++;
            gridCount++;
        }

        //sets the scene
        Scene weekScene = new Scene(root);

        week.setScene(weekScene);
        week.show();

        return week;
    }

    public Stage month() {
        isWeek = false;
        LocalDate start = localDate.withDayOfMonth(1);
        LocalDate end = localDate.withDayOfMonth(localDate.lengthOfMonth());
        //creates a GridPane that we will attach all the calendar items to
        GridPane root = new GridPane();
        //Sets the Days across the top of the page
        Label mon = new Label("Monday");
        Label tues = new Label("Tuesday");
        Label wed = new Label("Wednesday");
        Label thurs = new Label("Thursday");
        Label fri = new Label("Friday");
        Label sat = new Label("Saturday");
        Label sun = new Label("Sunday");
        //Sets the width of GridPane
        root.setMinWidth(300);
        //adds the top line of Days
        root.add(mon, 1, 1);
        root.add(tues, 2, 1);
        root.add(wed, 3, 1);
        root.add(thurs, 4, 1);
        root.add(fri, 5, 1);
        root.add(sat, 6, 1);
        root.add(sun, 7, 1);
        //centers each day
        root.setHalignment(mon, HPos.CENTER);
        root.setHalignment(tues, HPos.CENTER);
        root.setHalignment(wed, HPos.CENTER);
        root.setHalignment(thurs, HPos.CENTER);
        root.setHalignment(fri, HPos.CENTER);
        root.setHalignment(sat, HPos.CENTER);
        root.setHalignment(sun, HPos.CENTER);
        
        //Finds when the first day of the month is and puts it in the appropriate point in the caneldar
        int i = 0;
        if (start.getDayOfWeek().toString().equals("MONDAY")) {
            i = 1;

        } else if (start.getDayOfWeek().toString().equals("TUESDAY")) {
            i = 0;

        } else if (start.getDayOfWeek().toString().equals("WEDNESDAY")) {
            i = -1;

        } else if (start.getDayOfWeek().toString().equals("THURSDAY")) {
            i = -2;

        } else if (start.getDayOfWeek().toString().equals("FRIDAY")) {
            i = -3;

        } else if (start.getDayOfWeek().toString().equals("SATURDAY")) {
            i = -4;

        } else if (start.getDayOfWeek().toString().equals("SUNDAY")) {
            i = -5;

        }

        int row = 2;

        while (row <= 7) {
            int col = 1;
            while (col <= 7) {

                if (i <= 0) {
                    VBox main = new VBox(0);
                    main.setStyle("-fx-border-style: solid");
                    HBox blank = new HBox();
                    Label empty = new Label("");
                    blank.getChildren().add(empty);
                    main.getChildren().add(blank);
                    root.add(main, col, row);

                } else if (i <= 31) {
                    VBox main = new VBox(0);
                    main.setStyle("-fx-border-style: solid");
                    HBox days = new HBox();
                    Label number = new Label(i + "");
                    number.setPadding(new Insets(2, 2, 0, 0));
                    days.setAlignment(Pos.TOP_RIGHT);

                    days.getChildren().add(number);
                    main.setMinSize(200, 200);
                    main.setMaxSize(200, 200);
                    main.getChildren().add(days);
                    //Gets the date
                    String dateString = "" + Integer.parseInt(DateTimeFormatter.ofPattern("dd").format(start.plusDays(i - 1)));

                    try {

                        //sets up a query to test for the current date in loop
                        String query = "select * from appointment where start between '2017-08-" + dateString + " 00:0:01' and '2017-08-" + dateString + " 23:59:00' order by start asc";

                        ResultSet currentAppointment = statement.executeQuery(query);

                        //checks to see if there is an appointment
                        boolean isThereAnAppoitment = currentAppointment.next();
                        while (isThereAnAppoitment == true) {

                            //gets proper info
                            String getCustomerId = currentAppointment.getString("customerId");
                            String getAppointmentId = currentAppointment.getString("appointmentId");
                            String getStartTime = currentAppointment.getString("start");
                            String getYear = currentAppointment.getString("start").substring(0, 4);
                            String getMonth = currentAppointment.getString("start").substring(5, 7);
                            String getDay = currentAppointment.getString("start").substring(8, 10);
                            String getStartHour = currentAppointment.getString("start").substring(11, 13);
                            String getStartMin = currentAppointment.getString("start").substring(14, 16);

                            String getEndMonth = currentAppointment.getString("end").substring(5, 7);
                            String getEndDay = currentAppointment.getString("end").substring(8, 10);
                            String getEndHour = currentAppointment.getString("end").substring(11, 13);
                            String getEndMin = currentAppointment.getString("end").substring(14, 16);

                            //sets time to utc
                            ZonedDateTime startTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getMonth), Integer.parseInt(getDay), Integer.parseInt(getStartHour), Integer.parseInt(getStartMin), 0, 0, utc);
                            ZonedDateTime endTimeSQL = ZonedDateTime.of(Integer.parseInt(getYear), Integer.parseInt(getEndMonth), Integer.parseInt(getEndDay), Integer.parseInt(getEndHour), Integer.parseInt(getEndMin), 0, 0, utc);
                            //gets current time
                            ZonedDateTime locatStartTimeSQL = startTimeSQL.withZoneSameInstant(currentTimeZone);
                            ZonedDateTime locatEndTimeSQL = endTimeSQL.withZoneSameInstant(currentTimeZone);

                            //sets query to find customer id
                            String tq = "select * from customer where customerId = " + getCustomerId;

                            ResultSet customerName = statment2.executeQuery(tq);

                            customerName.next();

                            String getCustomerName = customerName.getString("customerName");

                            //Creates a start hour integer
                            int startHour = Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(locatStartTimeSQL));
                            //creates an end min string
                            String startMin = DateTimeFormatter.ofPattern("mm").format(locatStartTimeSQL);

                            //Creates a start hour integer
                            int endHour = Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(locatEndTimeSQL));
                            //creates an end min string
                            String endMin = DateTimeFormatter.ofPattern("mm").format(locatEndTimeSQL);

                            //creates a vbox to hold appintments
                            VBox appointment = new VBox();
                            //Adds customer name
                            Label name = new Label(getCustomerName);

                            //HBox for time 
                            HBox timeBox = new HBox();

                            String startTimeString = "";
                            if (startHour > 12) {
                                startTimeString = startHour - 12 + ":" + startMin + " PM";
                            } else if (startHour == 12) {
                                startTimeString = startHour + ":" + startMin + " PM";
                            } else if (startHour == 0) {
                                startTimeString = startHour + 12 + ":" + startMin + " AM";
                            } else {
                                startTimeString = startHour + ":" + startMin + " AM";
                            }

                            //checks to see if time is am or pm and then creats a time string
                            String endTimeString = "";
                            if (endHour > 12) {
                                endTimeString = endHour - 12 + ":" + endMin + " PM";

                            } else if (endHour == 12) {
                                endTimeString = endHour + ":" + endMin + " PM";
                            } else if (endHour == 0) {
                                endTimeString = endHour + 12 + ":" + endMin + " AM";
                            } else {
                                endTimeString = endHour + ":" + endMin + " AM";
                            }

                            Label time = new Label(startTimeString + " - " + endTimeString);
                            Label space = new Label("    ");
                            //hperlink to reschedule appointment
                            Hyperlink reschedule = new Hyperlink();
                            reschedule.setText(getCustomerName);
                            reschedule.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {

                                    try {
                                        edit.screen(getAppointmentId, isWeek);
                                        edit.getStage().show();
                                    } catch (ClassNotFoundException ex) {
                                        Logger.getLogger(Calendar.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(Calendar.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            });
                            //adds the time, a space between time, and the reschedule to the time box
                            timeBox.getChildren().addAll(time, space);
                            //adds the name and the timebox to the appointment vbox
                            appointment.getChildren().addAll(reschedule, timeBox);
                            //adds vbox to the main box
                            main.getChildren().addAll(appointment);

                            //checks to see if there is another appointment
                            isThereAnAppoitment = currentAppointment.next();

                        }

                    } catch (SQLException ex) {
                        System.out.println("SQL DIDNT WORK");
                    }

                    root.add(main, col, row);

                } else {
                    VBox main = new VBox(30);
                    main.setStyle("-fx-border-style: solid");
                    HBox blank = new HBox();
                    Label empty = new Label("");
                    blank.getChildren().add(empty);
                    main.getChildren().add(blank);
                    root.add(main, col, row);

                }

                i++;
                col++;
                // root.getChildren().add(main);
            }
            row++;
        }

        Scene monthScene = new Scene(root);
        month.setScene(monthScene);
        month.show();

        return month;
    }

    public LocalDate getDate() {
        return localDate;
    }

    public void hideWeek() {
        week.hide();
    }

    public void hideMonth() {
        month.hide();
    }
}
