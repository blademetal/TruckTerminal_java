package ui.addEmployee;

import classes.BackgroundHelper;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;


import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import ui.addTransCompany.AddTransCompanyController;

/**
 * The aim of the class is to give an employee, with all his data to the database.
 * The only authorized person to do this is the CEO of the company, who is in one person the admin also.
 * The user have to fill the almost all of the next fields:
 * - surname
 * - first name
 * - phone number
 * - city
 * - address
 * - postal Code (ZIP)
 * - citizenship
 * - birth date
 * - start date at this company
 * - end of employmentship in at this company
 * - username
 * - password
 * - password recap
 *
 */
public class AddEmployeeController implements Initializable{

    /**
     * The main JavaFX pane of this window
     */
    @FXML private Pane rootPane;

    /**
     * TextField to enter surname
     */
    @FXML private JFXTextField empSurname;

    /**
     * TextField to enter the first name
     */
    @FXML private JFXTextField empFirstname;

    /**
     * TextField to enter the phone number
     */
    @FXML private JFXTextField empPhone;

    /**
     * TextField to enter the city, where the employee lives
     */
    @FXML private JFXTextField empCity;

    /**
     * TextField to enter the address in the city
     */
    @FXML private JFXTextField empAddress;

    /**
     * TextField to enter ZIP Code
     */
    @FXML private JFXTextField empZIP;


    /**
     * List, which contains the occupations, which are obtainable for the employees
     */
    private ObservableList occupationList = FXCollections.observableArrayList();

    /**
     * ChoiceBox to choose the desired occupation for the employee
     */
    @FXML private ChoiceBox<String> empPosition;

    /**
     * DatePicker to select the employee birth date
     */
    @FXML private JFXDatePicker empBirth;

    /**
     * DatePicker to select the employee's start date at this company
     */
    @FXML private JFXDatePicker empStart;

    /**
     * DatePicker to select the employees's last working day at this company
     */
    @FXML private JFXDatePicker empEnd;

    /**
     * TextField to enter the employee's username
     */
    @FXML private JFXTextField empUsername;

    /**
     * TextField to enter the employee's password
     */
    @FXML private JFXTextField empPassword;

    /**
     * TextField to enter the employee's password recap
     */
    @FXML private JFXTextField empPasswordVal;

    /**
     * Instantiate database handler globally
     */
    private DatabaseHandler databaseHandler;

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * This method runs the initialization process.
     * @param url The URL which provides the path where the needed FXML file is.
     * @param rb Contains local specific objects.
     */
    public void initialize(URL url, ResourceBundle rb){
        try {
            databaseHandler = DatabaseHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);

        loadEmpData();
    }


    /**
     * The method get the datas from the UI parts of the window
     * @param event Handles the ActionEvent, which is the clicking
     */
    @FXML
    private void addNewEmployee(ActionEvent event) {
        String addEmpID = databaseHandler.createID();
        String addEmpSurname = empSurname.getText();
        String addEmpFirstname = empFirstname.getText();
        LocalDate addEmpBirthDate = empBirth.getValue();
        String addEmpPhone = empPhone.getText();
        String addEmpCity = empCity.getText();
        String addEmpAddress = empAddress.getText();
        String addEmpZIP;
        LocalDate addEmpStartDate = empStart.getValue();
        LocalDate addEmpEndDate = empEnd.getValue();
        String addEmpUsername = empUsername.getText();
        String addEmpPassword = empPassword.getText();
        String addEmpPasswordVal = empPasswordVal.getText();
        addEmpZIP = checkZIP(empZIP.getText());
        String addTransCompNation = empPosition.getValue();


        /**
         * Checks taht the folowing fields are not empty:
         *      - surename
         *      - first name
         *      - phone number
         *      - birth date
         *      - start date at this company
         * If empty, alerts the user that he/she should fill all the required fields
         */
        if(addEmpSurname.isEmpty()
                || addEmpFirstname.isEmpty()
                || addEmpPhone.isEmpty()
                || addEmpBirthDate == null
                || addEmpStartDate == null
                )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A mezők kitöltése kötelező!");
            alert.showAndWait();
            return;
        }


        /**
         * Checks that the given password and the password recap are the same
         * If not, then alerts the user about it
         */
        if(!addEmpPassword.equals(addEmpPasswordVal)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A jelszónak és a jelszóismétlő nem egyezik meg!\n Próbáld újra!");
            alert.showAndWait();
            return;
        }

        /**
         * Checks that the given password is not the same as the username
         * If is, alerts the user about it
         */
        if(addEmpPassword.equals(addEmpUsername)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A jelszónak és a felhasználónév nem egyezhet meg!\n Próbáld újra!");
            alert.showAndWait();
            return;
        }


        /**
         * The regular expression is used to validate the password and the username
         */
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";


        /**
         * If the username is not adequate, the program will signal it,
         * and the user can try to enter the username again
         */
        if(!addEmpUsername.matches(pattern)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A felhasználónévnek tartalmaznia kell:\n  - legalább egy kisbetűt\n   - legalább egy nagybetűt\n"+
                    "  - legalább egy számot\n  - összefüggőnek kell lennie\n   - legalább 8 karakter hosszúnak kell lennie!");
            alert.showAndWait();
            return;
        }


        /**
         * If the password is not adequate, the program will signal it,
         * and the user can try to enter the password again
         */
        if(!addEmpPassword.matches(pattern)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A jelszónak tartalmaznia kell:\n  - legalább egy kisbetűt\n   - legalább egy nagybetűt\n"+
                    "  - legalább egy számot\n  - összefüggőnek kell lennie\n   - legalább 8 karakter hosszúnak kell lennie!");
            alert.showAndWait();
            return;
        }


        /**
         * This SQLite string gives the right syntax to save all the employee's data into the database
         */
        String act = "INSERT INTO Employee_Data VALUES (" +
                "'" + addEmpID + "'," +
                "'" + addEmpUsername + "'," +
                "'" + addEmpPassword + "'," +
                "'" + addEmpSurname + "'," +
                "'" + addEmpFirstname + "'," +
                "'" + addEmpBirthDate +"'," +
                "'" + addEmpPhone + "'," +
                "'" + addEmpCity + "'," +
                "'" + addEmpAddress + "'," +
                "'" + addEmpZIP + "'," +
                "'" + addEmpStartDate + "'," +
                "'" + addEmpEndDate + "'" +
                ")";

        System.out.println(act);

        /**
         * If saving the employee's data was successfull, then signal it with some confirmation,
         * if not, then warn the user about is
         */
        if (databaseHandler.execAction(act)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Az adatok sikeresen hozzá lettek adva az adatbázishoz.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Az adatok hozzáadása az adatbázishoz sikertelen!");
            alert.showAndWait();
        }

        /**
         * This method will clear all the input fields, regardless of success or failure
         */
        clearFields();
    }

    /**
     * Redirect the user to the Main window
     * @param event Listen to the click event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads the obtainable positions into a ChoiceBox
     */
    private void loadEmpData() {
        occupationList.removeAll(occupationList);
        occupationList.addAll("Gazdasági referens", "Karbantartó", "Portás", "Takarító", "Telepvezető");
        empPosition.getItems().addAll(occupationList);
    }


    /**
     * Checks that the ZIP Code is only consists of digits
     * If yes, it return the ZIP Code as String, if not, it alerts the user about the failure
     * @param ZIP The ZIP Code which is a user's input
     * @return The ZIP Code as String
     */
    private String checkZIP(String ZIP) {
        if(StringUtils.isNumeric(ZIP)) {
            return ZIP;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Az irányítószámnak számjageykből kell állnia,\n szóköz nélkül!");
            alert.showAndWait();
            return "";
        }
    }


    /**
     * Clear all the input TextFields, DatePickers and ChoiceBox choices
     */
    private void clearFields() {
        empSurname.clear();
        empUsername.clear();
        empPassword.clear();
        empPasswordVal.clear();
        empFirstname.clear();
        empBirth.setValue(null);
        empPhone.clear();
        empCity.clear();
        empAddress.clear();
        empZIP.clear();
        empStart.setValue(null);
        empEnd.setValue(null);
        empPosition.setValue(null);
    }

}
