package ui.addTruck;

import classes.BackgroundHelper;
import classes.TransCompany;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import ui.main.MainController;

/**
 *  This class is responsible for adding new truck instances to the database. It will allow the user to choose
 *  the truck's brand, owning company and give the plate number.
 */
public class AddTruckController implements Initializable{

    /**
     * The main JavaFX pane of this window.
     */
    @FXML private Pane rootPane;

    /**
     * This ArrayList will help to load the transportation companies.
     */
    ObservableList transCompList = FXCollections.observableArrayList();

    /**
     * From this ChoiceBox can the user choose which company owns the truck.
     */
    @FXML private ChoiceBox<String> TransCompName;

    /**
     * This ObservableArrayList will be passed to the ChoiceBox and will contain the transportation companies.
     */
    ObservableList<TransCompany> transCompanyList = FXCollections.observableArrayList();


    /**
     * This ObservableArrayList will be passed to the ChoiceBox and will contain the truck brands.
     */
    ObservableList truckBrandList = FXCollections.observableArrayList();

    /**
     * From this ChoiceBox can the user choose what the truck's brand is.
     */
    @FXML private ChoiceBox<String> TruckBrand;

    /**
     * The TextField will hold the plate number of the truck.
     */
    @FXML private JFXTextField PlateNumber;

    /**
     * Instantiate database handler globally
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

        loadTransCompName();
        loadTruckBrand();
    }


    /**
     * Gets the texts from the TextFields and the ChoiceBox, and then after some validation, insert the Strings
     * into the database's Truck_Data table. If there was a success, it alerts the user, otherwise wanrs him/her.
     * At the end it clears all the TextFields and ChoiceBox.
     * @param event Listens to the event.
     */
    @FXML
    private void addTruck(ActionEvent event) {
        String addTruckID = databaseHandler.createID();
        String addTruckStartDate = databaseHandler.getCreationDate();
        String addTransCompName = TransCompName.getValue();
        String addPlateNumber = PlateNumber.getText();
        String addTruckBrand = TruckBrand.getValue();
        String addTruckEndDate = "";
        double addPaid = 0;

        if(addTransCompName == null
                || addPlateNumber.isEmpty()
                || addTruckBrand == null
                )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A kamion rendszámát, a márkáját és a tulajdonos cég nevét kötelező megadni!");
            alert.showAndWait();
            return;
        }


        String act = "INSERT INTO Truck_Data VALUES (" +
                "'" + addTruckID + "'," +
                "'" + addTransCompName + "'," +
                "'" + addPlateNumber +"'," +
                "'" + addTruckBrand + "'," +
                "'" + addTruckStartDate + "'," +
                "'" + null + "'," +
                "'" + addPaid + "'" +
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
     * The methods loads the AddTransCompany FXML file.
     */
    @FXML
    private void createTransCompany() {
        windowLoader("../addTransCompany/AddTransCompany.fxml", "Fuvarozási cég hozzáadás");
    }

    /**
     * Rfom the transCompany onbjects the method gets their names and pass it into the TransCompName ArrayList.
     */
    private void loadTransCompName() {
        loadTransCompData();
        transCompList.removeAll(transCompList);

        for (TransCompany comp: transCompanyList
             ) {
            transCompList.add(comp.getTransCompName());
        }

        TransCompName.getItems().addAll(transCompList);
    }


    /**
     * The method loads the most frequent truck brands into the TruckBrand ChoiceBox.
     */
    private void loadTruckBrand() {
        truckBrandList.removeAll(truckBrandList);
        truckBrandList.addAll("Mercedes", "Man", "Iveco", "Renault", "Fiat", "Tatra", "Daf", "Scania", "Volvo");
        TruckBrand.getItems().addAll(truckBrandList);
    }


    /**
     * Clear the input TextField and ChoiceBoxes choices.
     */
    private void clearFields() {
        TransCompName.setValue(null);
        TruckBrand.setValue(null);
        PlateNumber.clear();
    }


    /**
     * The method will load all records from the Trans_Company table in the database,
     * and append it to the transCompanyList ArrayList.
     */
    private void loadTransCompData() {
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Trans_Company";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String TransCompID = re.getString("id");
                String TransCompName = re.getString("cegnev");
                String TransCompNation = re.getString("orszag");
                String TransCompPhone = re.getString("telefon");
                String TransCompCity = re.getString("varos");
                String TransCompAddress = re.getString("cim");
                String TransCompZIP = re.getString("iranyitoszam");
                String TransCompDate = re.getString("datum");

                transCompanyList.add(new TransCompany(TransCompID,
                        TransCompName,
                        TransCompNation,
                        TransCompCity,
                        TransCompAddress,
                        TransCompPhone,
                        TransCompZIP,
                        TransCompDate));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * The method will create a new stage and change it with the old (current) one.
     * It also adds all the necessary decoration to the stage.
     * @param path The path where the needed FXML file is located.
     * @param title The title of the window, it will be displayed on the top side of the window..
     */
    private void windowLoader(String path, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainController.class.getResourceAsStream("../icons/delivery-truck.png")));
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
