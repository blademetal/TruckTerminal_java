package ui.addExpenditure;

import classes.*;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import com.jfoenix.controls.JFXTextField;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.addExpenditureType.AddExpenditureTypeController;
import ui.main.MainController;

import java.sql.SQLException;
import java.util.*;


/**
 *  This class is in charge with handle the background operations behind the AddExpenditure class.
 * It's aim is to add new Expenditure objects to the database. The class implements the Initializable interface.
 */
public class AddExpenditureController implements Initializable{

    /**
     * This is the window's rootPane.
     */
    @FXML private Pane rootPane;

    /**
     * This ArrayList will hold all types that are in the Properties file.
     */
    private ArrayList<String> expTypesEditable = new ArrayList<>();

    /**
     * Instantiate a Properties class globally.
     */
    private Properties props = new Properties();

    /**
     * This object helps to create new Expenditure types.
     */
    private AddExpenditureTypeController typeCont = new AddExpenditureTypeController();

    /**
     * The ObservableArrayList helps to fill the ChoiceBox for the types.
     */
    private ObservableList expTypeList = FXCollections.observableArrayList();

    /**
     * This ChoiceBox contains the selectable Expenditure types.
     */
    @FXML private ChoiceBox<String> expType;

    /**
     * This TextField should be filled with the price per unit.
     */
    @FXML private JFXTextField expPricePerItem;

    /**
     * This TextField needs the amont of units.
     */
    @FXML private JFXTextField expNumberOfItem;

    /**
     * The full money spent on the item in hungarian HUF.
     */
    @FXML private Text expTotal;

    /**
     * A helper global double to store financial data.
     */
    private double expSum;

    /**
     * A global DatabaseHandler object, which will be used in the initialization process.
     */
    DatabaseHandler databaseHandler;

    /**
     * The global variable instantiate the BackgroundHelper class.
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

        try {
            props.load(new FileInputStream("TeMeReSettings.properties"));
            expTypesEditable = typeCont.splitExpType(props.getProperty("ExpTypes"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);

        loadExpType();
    }


    /**
     * This method adds the Expenditure object to the database. The object is created from using the text
     * available by the TextFields and ChoiceBox.
     * The fields will undergo some validation and if the fail it, an alert will show up.
     * If no problem occurred, the methos saves the object into the database, and clears all TextFields.
     * At the end it also refresh the tables and the revPrices label in the Main window.
     * @param event Listen to a click event.
     */
    @FXML
    private void addExpenditure(ActionEvent event) {
        String addExpID = databaseHandler.createID();
        String addExpDate = databaseHandler.getCreationDate();
        String addExpPricePerItem = expPricePerItem.getText();
        String addExpNumberOfItem = expNumberOfItem.getText();
        String addExpType = expType.getValue();


        if(expPricePerItem.getText().isEmpty()
                || expNumberOfItem.getText().isEmpty()
                || !isConvertableToDouble(addExpPricePerItem)
                || !isConvertableToDouble(addExpNumberOfItem)
                || addExpType == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A kiadásra vonatkozó összes adatot kötelező megadni!");
            alert.showAndWait();
            return;
        }

        Double addExpPricePerItemDouble = Double.parseDouble(addExpPricePerItem);
        Double addExpNumberOfItemDouble = Double.parseDouble(addExpNumberOfItem);

        expSum = addExpPricePerItemDouble * addExpNumberOfItemDouble;

        String act = "INSERT INTO Expenditure_Data VALUES (" +
                "'" + addExpID + "'," +
                "'" + addExpType+ "'," +
                "'" + addExpPricePerItemDouble+ "'," +
                "'" + addExpNumberOfItemDouble +"'," +
                "'" + expSum + "'," +
                "'" + addExpDate + "'" +
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

        MainController.MC.refreshTablesFunc();
        MainController.MC.refreshRevPrices();

    }


    /**
     * Helps to check if the field only contains digits.
     * @param d Is a String parameter what needs to be checked.
     * @return True if the String is convertable to digit, false otherwise.
     */
    private boolean isConvertableToDouble(String d) {
        if(!org.apache.commons.lang3.math.NumberUtils.isCreatable(d)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A beírt áraknak és mennyiségeknek számoknak kell lenniük!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Redirect the user to the Main window.
     * @param event Listen to the click event.
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    /**
     * The method clears all TextFields, Labal and also set the ChoiceBox's value to null.
     */
    private void clearFields() {
        expType.setValue(null);
        expNumberOfItem.clear();
        expPricePerItem.clear();
        expTotal.setText("");
    }

    /**
     * The methos fill loads all Expenditure Type.
     */
    private void loadExpType() {
        expTypeList.removeAll(expTypeList);
        expTypeList.addAll(expTypesEditable);
        expType.getItems().addAll(expTypeList);
    }


    /**
     * This method calculates the amont of money the user have to pay for an expenditure.
     * @param event Listen to a click event.
     */
    @FXML
    private void calcExp(ActionEvent event) {
        if(!expPricePerItem.getText().isEmpty()
                && !expNumberOfItem.getText().isEmpty()
            && org.apache.commons.lang3.math.NumberUtils.isCreatable(expPricePerItem.getText())
            && org.apache.commons.lang3.math.NumberUtils.isCreatable(expNumberOfItem.getText())) {
            double ppi = Double.parseDouble(expPricePerItem.getText());
            double noi = Double.parseDouble(expNumberOfItem.getText());

            expTotal.setText("");

            expSum = ppi*noi;

            expTotal.setText(expSum + " HUF");
        }
    }

}
