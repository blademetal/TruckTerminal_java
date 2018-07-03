package ui.signInCompany;

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
import ui.main.MainController;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  This class is responsible for the authentication of the administrator. Only the administrator can get into the system,
 *  with a predefined username and password.
 */
public class SignInCompanyController implements Initializable{

    /**
     * This ArrayList will contain all the Employee objects which are available in the database.
     */
    ArrayList<Employee> employeeList= new ArrayList<>();

    /**
     * This global boolean will denote the authorization of the user.
     */
    public static Boolean isLogInCompany = false;

    /**
     * The main JavaFX pane of this window.
     */
    @FXML
    private Pane rootPane;

    /**
     * This TextField will contain the users username.
     */
    @FXML
    private JFXTextField compUser;

    /**
     * This PasswordField will contains the users password.
     */
    @FXML
    private JFXPasswordField compPass;

    /**
     * Instantiate database handler globally
     */
    private DatabaseHandler databaseHandler = null;

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * This method runs the initialization process.
     * @param url The URL which provides the path where the needed FXML file is.
     * @param rb Contains local specific objects.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            databaseHandler = DatabaseHandler.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);
        loadEmployeeData();
    }


    /**
     * Loads all Employee objects from the database, and appends them to the employeeList ArrayList.
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
     * This method checks if the username and password authorizes the user to go into the system az an administrator.
     * If the user can pass, it will return true, otherwise false, and alert an error.
     * @return True if user is authorized, otherwise false.
     */
    private Boolean checkUserAndPass() {
        String empUserTake = compUser.getText();
        String empPassTake = compPass.getText();
        for (Employee emp:employeeList
                ) {
            if(emp.getEmpUser().equals(empUserTake)
                    && emp.getEmpPass().equals(empPassTake)
                    && emp.getEmpUser().equals("Admin123"))
            {
                isLogInCompany = true;
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
     * The method checks if the user can get into the system, if he/she can, it redirects him/her to Main window.
     * Otherwise it will show the hurdle which occurred. At the end it will clear all TextFields.
     * @param event Listens to the click event.
     */
    @FXML
    private void signCompIn(ActionEvent event) {
        if(checkUserAndPass()) {
            windowLoader("../main/Main.fxml", "Terminál Menedzsment Rendszer");
        }
        System.out.println("Nem lehet belépni!");
        clearFields();
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
     * This method redirects the user to the SignInFull window.
     * @param event Listens to the click event.
     * @throws IOException Throws an I/O Exception if it occurs.
     */
    @FXML
    private void goToSingInFull(ActionEvent event) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../signInFull/SignInFull.fxml"));
        rootPane.getChildren().setAll(pane);
    }

    /**
     * This method will clear the compUser and compPass TextFields.
     */
    private void clearFields() {
        compUser.clear();
        compPass.clear();
    }

}