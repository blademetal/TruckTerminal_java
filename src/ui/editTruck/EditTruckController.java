package ui.editTruck;

import classes.BackgroundHelper;
import classes.Utilities;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;

import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import classes.TransCompany;
import classes.Truck;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ui.main.MainController;

/**
 *  This class's responsibility is to handle the changes in the state of a Truck object, which still in the Terminal,
 * and if it wants to leave then alter it's state.
 */
public class EditTruckController implements Initializable{

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
     * If this CheckBox is checked, than the truck is leaving the terminal.
     */
    @FXML private JFXCheckBox isLeaving;

    /**
     * This object will store the Truck object whose state will be altered.
     */
    private Truck editTruck;

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

        bgHelper.changeBGColor(rootPane);

        loadTransCompName();
        loadTruckBrand();
    }

    /**
     * With a Truck object input, the method will get it's plate number and then run the loadEditTruckData() method.
     * @param truck The clocked Truck object from the Table.
     */
    public void editTruck(Truck truck) {
        String plateNum = truck.getPlateNumber();
        loadEditTruckData(plateNum);
    }


    /**
     * Redirect the user to the Main window
     */
    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    /**
     * Redirect the user to the Main window
     * @param event Listen to the click event
     */
    @FXML
    private void cancel(ActionEvent event) {
        closeWindow();
    }


    /**
     * The method loads the transportation companies names.
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
     * Puts all truck brand into a ChoiceBox.
     */
    private void loadTruckBrand() {
        truckBrandList.removeAll(truckBrandList);
        truckBrandList.addAll("Mercedes", "Man", "Iveco", "Renault", "Fiat", "Tatra", "Daf", "Scania", "Volvo");
        TruckBrand.getItems().addAll(truckBrandList);
    }


    /**
     * The method loads all TransCompany objects from the database and appends them to the transCompanyList ArrayList.
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
            Logger.getLogger(ui.addTruck.AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * When the window is opened, this method helps to display the clicked truck's data.
     * @param plateNum The plate number of the clicked truck.
     */
    private void loadEditTruckData(String plateNum) {
        ArrayList<Truck> truckList = new ArrayList<>();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Truck_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String truckID = re.getString("id");
                String transCompName = re.getString("cegnev");
                String plateNumber = re.getString("rendszam");
                String truckBrand = re.getString("marka");
                String truckStartDate = re.getString("beerkezes_datum");
                String truckEndDate = re.getString("kilepes_datum");
                double paid = re.getDouble("fizetett");

                truckList.add(new Truck(truckID,
                        transCompName,
                        plateNumber,
                        truckBrand,
                        truckStartDate,
                        truckEndDate,
                        paid
                ));
            }

            editTruck = findTruck(truckList, plateNum);

            PlateNumber.setText(editTruck.getPlateNumber());
            TransCompName.setValue(editTruck.getTransCompName());
            TruckBrand.setValue(editTruck.getTruckBrand());

        } catch (SQLException ex) {
            Logger.getLogger(ui.addTruck.AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method's aim is to find a particular truck by it's plate number.
     * @param TruckList It is and ArrayList which contains the Truck objects loaded from the database.
     * @param pn The plate number of the searched truck.
     * @return The method returns a Truck object if it finds that particular truck, otherwise null.
     */
    private Truck findTruck(ArrayList<Truck> TruckList, String pn) {
        for(Truck truck : TruckList) {
            if (pn.equals(truck.getPlateNumber())) {
                return truck;
            }
        }
        return null;
    }


    /**
     * If the button is clicked, the method will update the Truck_Data table in the database.
     * @param event Listens to the click event.
     */
    @FXML
    private void makeUpdateTruck(ActionEvent event) {
        updateTruck(editTruck);
    }


    /**
     * This method is responsible for updating the truck's state in the database, and after that refresh
     * the tables and revenues in the Main windiw.
     * @param truck That particular truck which state is altered.
     */
    private void updateTruck(Truck truck) {
        String addTruckID = truck.getTruckID();
        String addTruckStartDate = truck.getTruckStartDate();
        String addTransCompName = TransCompName.getValue();
        String addPlateNumber = PlateNumber.getText();
        String addTruckBrand = TruckBrand.getValue();
        String addTruckEndDate = truck.getTruckEndDate();
        double addPaid = truck.getPaid();

        Utilities utilities = new Utilities();

        if(isLeaving.isSelected()) {
            addTruckEndDate = databaseHandler.getCreationDate();
        }

        if(addPaid == 0) {
            try {
                addPaid = utilities.getPrice(addTruckStartDate, addTruckEndDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(addTransCompName.isEmpty()
                || addPlateNumber.isEmpty()
                || addTruckBrand.isEmpty()
                )
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A kamion rendszámát, a márkáját és a tulajdonos cég nevét kötelező megadni!");
            alert.showAndWait();
            return;
        }


        String act = "UPDATE Truck_Data SET " +
                "id = '" + addTruckID + "'," +
                " cegnev = '" + addTransCompName + "'," +
                " rendszam = '" + addPlateNumber +"'," +
                " marka = '" + addTruckBrand + "'," +
                " beerkezes_datum = '" + addTruckStartDate + "'," +
                " kilepes_datum = '" + addTruckEndDate + "'," +
                " fizetett = '" + addPaid + "'" +
                " WHERE id='" + addTruckID + "';";

        System.out.println(act);

        if (databaseHandler.execAction(act)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Az adatok sikeresen módosítva lettek az adatbázisban.");
            alert.showAndWait();

            closeWindow();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Az adatok módosítása az adatbázisban sikertelen volt!");
            alert.showAndWait();
        }


        MainController.MC.refreshTablesFunc();
        MainController.MC.refreshRevPrices();

    }
}
