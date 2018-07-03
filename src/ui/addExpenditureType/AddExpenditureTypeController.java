package ui.addExpenditureType;

import classes.BackgroundHelper;
import database.DatabaseHandler;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;


import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.stage.Stage;
import ui.settings.SettingsController;


/**
 *  This class is in charge with control the AddExpenditureType window, and handle all the background operations
 * what takes place in the background. The class implements the Initializable interface.
 */
public class AddExpenditureTypeController implements Initializable{

    /**
     * This is the window's rootPane.
     */
    @FXML private Pane rootPane;

    /**
     * The user can write in this TextField to create a new Expenditure type.
     */
    @FXML private JFXTextField newExpType;

    /**
     * Instantiate database handler globally
     */
    DatabaseHandler databaseHandler = null;

    /**
     * Instantiate a Properties class globally.
     */
    private Properties props = new Properties();

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * The ArrayList holds the default expenditure types.
     */
    private ArrayList<String> baseExpTypes = new ArrayList<>(Arrays.asList("Festék", "Szerszám", "Tisztítószer", "Karbantartás"));

    /**
     * This is a helper String to concatenate the added expenditure type and the old ones.
     */
    private String fullListInString = "";


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

        try {
            props.load(new FileInputStream("TeMeReSettings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!Objects.equals(props.getProperty("ExpTypes"), "")) {
            baseExpTypes = splitExpType(props.getProperty("ExpTypes"));
            props.setProperty("ExpTypes", "");
        }

        bgHelper.changeBGColor(rootPane);
    }


    /**
     * Takes the text from the TextField newExpType and checks if it's not in the list already.
     * If it is, or the TextField is empty the program alerts the user.
     * Otherwise the method saves the new type into a properties file.
     * At the end it clears the TextField.
     * @param event Listen to a click event.
     */
    @FXML
    private void addNewExpType(ActionEvent event) {

        String newType = newExpType.getText();

        if(newType.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Meg kell adni az új típus nevét!");
            alert.showAndWait();
            return;
        }

        if (addToExpTypeList(baseExpTypes, newType)) {
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

        fullListInString = createFullList(baseExpTypes);
        props.setProperty("ExpTypes", fullListInString);

        try{
            props.store(new FileOutputStream("TeMeReSettings.properties"), null);
        } catch (Exception ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        newExpType.clear();
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
     * This is a helper method, to create a String from an ArrayList of String objects.
     * @param list An ArrayList which contains String objects.
     * @return All the String from the input ArrayList concatenated into one String.
     */
    private String createFullList(ArrayList<String> list) {
        String word = "";
        for (int i = 0; i < list.size(); i++) {
           if(i == 0) {
               word = list.get(i);
           } else {
               word = word + "," + list.get(i);
           }
        }
        return word;
    }

    /**
     * If list not contains the input String, it adds that to the input ArrayList.
     * @param list An ArrayList consisting of String objects.
     * @param newType A String which holds a new expenditure type.
     * @return True if there was not such an element in the list yet, otherwise false.
     */
    private Boolean addToExpTypeList(ArrayList<String> list, String newType) {
        if(list.contains(newType)) {
            return false;
        } else {
            list.add(newType);
            return true;
        }
    }

    /**
     * The method splits a comma separated String into a series of String objects.
     * @param expType A comma separated String object.
     * @return An ArrayList of the separated String objects.
     */
    public static ArrayList<String> splitExpType(String expType) {
        String[] typeList = expType.split(",");
        return new ArrayList<String>(Arrays.asList(typeList));
    }

}
