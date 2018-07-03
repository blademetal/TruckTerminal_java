package ui.signInEmployee;

import classes.BackgroundHelper;
import classes.Employee;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.addTruck.AddTruckController;

import javafx.event.ActionEvent;
import ui.main.MainController;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  This class is responsible for signing in a user who want to get into the system with an employee authorization.
 * To this the person has to give his/her username and password to the program, and if the authentication doesn't fail
 * the person, at this point user, will be able to get sign in the system.
 */
public class SignInEmployeeController implements Initializable{

    /**
     * The main pane which is used when the player wants to quit from the current window or wants to change it's background color.
     */
    @FXML private Pane rootPane;

    /**
     * If the user types in his/her username, this JFXTextField will hold it.
     */
    @FXML private JFXTextField empUser;

    /**
     * If the user types in his/her password, this JFXTextField will hold it.
     */
    @FXML private JFXPasswordField empPass;

    /**
     * This ArrayList holds the Employees objects which are loaded from the database.
     */
    private ArrayList<Employee> employeeList = new ArrayList<>();


    /**
     * A global DatabaseHandler object, which will be used in the initialization process.
     */
    private DatabaseHandler databaseHandler = null;

    /**
     * The global variable instantiate the BackgroundHelper class.
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * This method runs the initialization process.
     * @param location The URL which provides the path where the needed FXML file is.
     * @param resources Contains local specific objects.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            databaseHandler = DatabaseHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);

        loadEmployeeData();
    }


    /**
     * The method loads all employee data from the database, and puts it in the employeeList ArrayList.
     */
    private void loadEmployeeData() {
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Employee_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String empID = re.getString("id");
                String empUsername = re.getString("felhasznalonev");
                String empPassword = re.getString("jelszo");
                String empSurname = re.getString("vezeteknev");
                String empFirstname = re.getString("keresztnev");
                String empBirthDate = re.getString("szuletesi_datum");
                String empPhone = re.getString("telefon");
                String empCity = re.getString("lakhely");
                String empAddress = re.getString("cim");
                String empZIP = re.getString("iranyitoszam");
                String empStartDate = re.getString("kezdett");
                String empEndDate = re.getString("befejezte");


                employeeList.add(new Employee(
                        empID,
                        empUsername,
                        empPassword,
                        empSurname,
                        empFirstname,
                        empBirthDate,
                        empPhone,
                        empCity,
                        empAddress,
                        empZIP,
                        empStartDate,
                        empEndDate
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Checks taht the database contains the user as an employee, and that he/she is not the admin.
     * If the requirement is not fulfilled the method alerts the user about the problem.
     * @return true if the user passed the check, false in other cases
     */
    private Boolean checkUserAndPass() {
        String empUserTake = empUser.getText();
        String empPassTake = empPass.getText();
        for (Employee emp:employeeList
             ) {
            if(emp.getEmpUser().equals(empUserTake)
                    && emp.getEmpPass().equals(empPassTake)
                    && !emp.getEmpUser().equals("Admin123"))
            {
                return true;
            }
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Nem megfelelő a felhasználónév vagy a jelszó!");
        alert.showAndWait();
        return false;
    }


    /**
     * The method runs the checks and if the user is authorized for an entry, it will load the new window,
     * else it will clear all the fields.
     * @param event Listen to the clicking event.
     */
    @FXML
    private void signEmpIn(ActionEvent event) {
        if(checkUserAndPass()) {
            windowLoader("../main/Main.fxml", "Terminál Menedzsment Rendszer");
        }
        System.out.println("Nem lehet belépni!");
        clearFields();
    }

    /**
     * The method redirects the user to the SignInFull page.
     * @param event Listen to the clicking event.
     * @throws IOException To handle I/O actions.
     */
    @FXML
    private void goToSingInFull(ActionEvent event) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../signInFull/SignInFull.fxml"));
        rootPane.getChildren().setAll(pane);
    }


    /**
     * The method will create a new stage and change it with the old (current) one.
     * It also adds all the necessary decoration to the stage.
     * @param path The path where the needed FXML file is located.
     * @param title The title of the window, it will be displayed on the top side of the window..
     */
    public void windowLoader(String path, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainController.class.getResourceAsStream("../icons/delivery-truck.png")));
            stage.setMaximized(true);
            stage.setScene(new Scene(parent));
            stage.show();


            Stage oldStage = (Stage) rootPane.getScene().getWindow();
            oldStage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Clears the empUser and empPass fields.
     */
    private void clearFields() {
        empUser.clear();
        empPass.clear();
    }
}
