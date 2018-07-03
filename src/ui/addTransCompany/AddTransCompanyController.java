package ui.addTransCompany;

import classes.BackgroundHelper;
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
import org.apache.commons.lang3.math.NumberUtils;


import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This class is responsible for adding new Transportation Companies to the database.
 * It implements the Initializable interface.
 */
public class AddTransCompanyController implements Initializable{

    /**
     * This is the window's rootPane.
     */
    @FXML private Pane rootPane;

    /**
     * The TextField will hold the company's name.
     */
    @FXML private JFXTextField TransCompName;

    /**
     * The TextField will hold the company's city.
     */
    @FXML private JFXTextField TransCompCity;

    /**
     * The TextField will hold the company's address in the city.
     */
    @FXML private JFXTextField TransCompAddress;

    /**
     * The TextField will hold the company's phone number.
     */
    @FXML private JFXTextField TransCompPhone;

    /**
     * The TextField will hold the company's addresses ZIP Code.
     */
    @FXML private JFXTextField TransCompZIP;

    ObservableList countryList = FXCollections.observableArrayList();
    @FXML private ChoiceBox<String> TransCompNation;


    /**
     * Instantiate database handler globally.
     */
    DatabaseHandler databaseHandler;

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

        loadTransCompNation();
    }


    /**
     * The method gets the texts from all TextFields and ChoiceBoxes, then validates them, and after that
     * inserts a new TransCompany object into the Trans_Company table.
     * If the insertin was successful, it alerts the user, otherwise warns him/her.
     * At last it clears all TextFields and the ChoiceBox.
     * @param event Listen to a click event.
     */
    @FXML
    private void addTransCompany(ActionEvent event) {
        String addTransCompID = databaseHandler.createID();
        String addTransCompDate = databaseHandler.getCreationDate();
        String addTransCompName = TransCompName.getText();
        String addTransCompCity = TransCompCity.getText();
        String addTransCompAddress = TransCompAddress.getText();
        String addTransCompPhone = TransCompPhone.getText();
        String addTransCompZIP = null;

        addTransCompZIP = checkZIP(TransCompZIP.getText());

        String addTransCompNation = TransCompNation.getValue();

        if(addTransCompName.isEmpty()
                || addTransCompNation == null
                || addTransCompZIP.isEmpty()
                || addTransCompPhone.isEmpty()
                || !NumberUtils.isCreatable(addTransCompPhone)
                )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A cég nevét, származási országát, központjának irányítószámát és telefoszámát (csak szám) kötelező megadni!");
            alert.showAndWait();
            return;
        }


        String act = "INSERT INTO Trans_Company VALUES (" +
                "'" + addTransCompID + "'," +
                "'" + addTransCompName + "'," +
                "'" + addTransCompNation + "'," +
                "'" + addTransCompPhone +"'," +
                "'" + addTransCompCity + "'," +
                "'" + addTransCompAddress + "'," +
                "'" + addTransCompZIP + "'," +
                "'" + addTransCompDate + "'" +
                ")";

        System.out.println(act);

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
     * The method loads the most frequent county names in the ChoiceBox.
     */
    private void loadTransCompNation() {
        countryList.removeAll(countryList);
        countryList.addAll("Magyarország", "Ukrajna", "Oroszország", "Románia", "Szlovákia",
                "Lengyelország","Fehéroroszország", "Egyéb");
        TransCompNation.getItems().addAll(countryList);
    }


    /**
     * The method checks that the ZIP Code is consisted only by digits, if none it will alert the user.
     * @param ZIP The transportation company's ZIP Code.
     * @return The ZIP Code if it's in adequate, else ''.
     */
    private String checkZIP(String ZIP) {
        if(StringUtils.isNumeric(ZIP)) {
            String addTransCompZIP;
            addTransCompZIP = ZIP;
            return addTransCompZIP;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Az irányítószámnak számjageykből kell állnia,\n szóköz nélkül!");
            alert.showAndWait();
            return "";
        }
    }


    /**
     * The method clears all TextFields and set the ChoiceBox's value to null.
     */
    private void clearFields() {
        TransCompName.clear();
        TransCompCity.clear();
        TransCompNation.setValue(null);
        TransCompAddress.clear();
        TransCompPhone.clear();
        TransCompZIP.clear();
    }
}
