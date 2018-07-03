package ui.signInFull;

import classes.BackgroundHelper;
import classes.Employee;
import com.jfoenix.controls.JFXButton;
import database.DatabaseHandler;
import javafx.event.ActionEvent;
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
import ui.settings.SettingsController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class is the opening window of this software. The person, who runs it, has to decide if he/she wants,
 * wants to log in as an employee or as the administrator.
 */
public class SignInFullController implements Initializable  {


    /**
     * This ArrayList will hold the loaded Employee objects.
     */
    ArrayList<Employee> empList = new ArrayList<>();

    /**
     * The main pane which is used when the player wants to quit from the current window or wants to change it's background color.
     */
    @FXML private Pane rootPane;

    /**
     * If this button is clicked, it will directs to the SignInCompany window.
     */
    @FXML private JFXButton companyBtn;

    /**
     * If this button is clicked, it will directs to the SignInEmployee window.
     */
    @FXML private JFXButton employeeBtn;

    /**
     * A global DatabaseHandler object, which will be used in the initialization process.
     */
    DatabaseHandler databaseHandler = null;

    /**
     * The global variable instantiate the BackgroundHelper class.
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * It will create a new Properties instance.
     */
    private Properties props = new Properties();

    /**
     * Holds a boolean value about the Properties state.
     */
    private Boolean propFlag = false;

    /**
     * This will hold the type data form the loaded Properties file.
     */
    private String type;

    /**
     * This will hold the numOfHoue data form the loaded Properties file.
     */
    private String numOfHour;

    /**
     * This will hold the priceOfHour data form the loaded Properties file.
     */
    private String priceOfHour;

    /**
     * This will hold the priceOfDay data form the loaded Properties file.
     */
    private String priceOfDay;

    /**
     * This will hold the Color data form the loaded Properties file.
     */
    private String color;


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

        try {
            props.load(new FileInputStream("TeMeReSettings.properties"));
            if (props.stringPropertyNames().isEmpty()) {
                props.setProperty("ExpTypes", "");
                props.setProperty("Type", "perHour");
                props.setProperty("NumOfHour", "1");
                props.setProperty("PriceOfHour", "100");
                props.setProperty("PriceOfDay", "0");
                props.setProperty("Color", "#343538");

                try{
                    props.store(new FileOutputStream("TeMeReSettings.properties"), null);
                } catch (Exception ex) {
                    Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
                };
            }
            type = props.getProperty("Type");
            numOfHour = props.getProperty("NumOfHour");
            priceOfHour = props.getProperty("PriceOfHour");
            priceOfDay = props.getProperty("PriceOfDay");
            color = props.getProperty("Color");
            propFlag = true;
        } catch (IOException e) {
            propFlag = false;
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);

        loadEmployeeData();

        if(!checkAdmin()) {
            createAdmin();
        }
    }


    /**
     * The method directs the user to the SignInCompany window.
     * @param event Listen to the clicking event.
     * @throws IOException To handle I/O actions.
     */
    @FXML
    private void goToSignInCompany(ActionEvent event) throws IOException {
        windowLoader("../signInCompany/SignInCompany.fxml", "Adminisztrációs bejelentkezés");
    }


    /**
     * The method directs the user to the SignInEmployee window.
     * @param event Listen to the clicking event.
     * @throws IOException To handle I/O actions.
     */
    @FXML
    private void goToSignInEmployee(ActionEvent event) throws IOException {
        windowLoader("../signInEmployee/SignInEmployee.fxml", "Munkatársak bejelentkezése");
    }


    /**
     * This method loads the employees data into an ArrayList which name is empList.
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


                empList.add(new Employee(
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
     * This method checks whether the admin exists or not.
     * @return If the admin does exists it returns true, otherwise false.
     */
    private Boolean checkAdmin() {
        for (Employee emp:empList
                ) {
            if(emp.getEmpUser().equals("Admin123"))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * If the admin is not created yet, then this method will do it.
     */
    private void createAdmin() {
        String act = "INSERT INTO Employee_Data VALUES (" +
                "'" + "ADMIN123" + "'," +
                "'" + "Admin123" + "'," +
                "'" + "AdPass10" + "'," +
                "'" + "Kovács" + "'," +
                "'" + "Tibor" + "'," +
                "'" + "1993" +"'," +
                "'" + "06305555" + "'," +
                "'" + "Budapest" + "'," +
                "'" + "K10" + "'," +
                "'" + "12311" + "'," +
                "'" + "151515" + "'," +
                "'" + "161616" + "'" +
                ")";

        System.out.println(act);

        if (databaseHandler.execAction(act)) {
            System.out.println("Az adminisztrátor regisztrálva!");
        } else {
            System.out.println("Az adminisztrátor regisztrációja sikertelen volt!");
        }
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
            stage.setScene(new Scene(parent));
            stage.show();

            Stage oldStage = (Stage) rootPane.getScene().getWindow();
            oldStage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
