package ui.turnoverOptional;


import classes.*;
import com.jfoenix.controls.JFXButton;
import database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ui.addTruck.AddTruckController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class's responsibility to let the user to look up the financial and traffic data for a desired period,
 * and after that generate an XLS file from it.
 */
public class TurnoverOptionalController implements Initializable {

    /**
     * This is the window's rootPane.
     */
    @FXML Pane pane;

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * If this button is clicked, it will run the filtering method.
     */
    @FXML private JFXButton filterPeriodBtn;

    /**
     * The user can pick the period's starting date.
     */
    @FXML private DatePicker periodStartPicker;

    /**
     * The user can pick the period's ending date.
     */
    @FXML private DatePicker periodEndPicker;

    /**
     * These Labels will display the period's information about the traffic and cashflow.
     */
    @FXML private Label truckInT;
    @FXML private Label truckOutT;
    @FXML private Label inAndOutTruckT;
    @FXML private Label revT;
    @FXML private Label expT;
    @FXML private Label cashflowT;


    /**
     * This ArrayList will hold all Truck objects what it finds in the database.
     */
    private ArrayList<Truck> truckList = new ArrayList<>();

    /**
     * This ArrayList will hold all Expenditure objects what it finds in the database.
     */
    private ArrayList<Expenditure> expList = new ArrayList<>();

    /**
     * Instantiate database handler globally
     */
    DatabaseHandler databaseHandler;

    /**
     * Create a TrunoverUploader instance globally so it will be able to store the loaded object's data.
     */
    private TurnoverUploader TU = null;


    /**
     * This method runs the initialization process.
     * @param url The URL which provides the path where the needed FXML file is.
     * @param rb Contains local specific objects.
     */
    public void initialize(URL url, ResourceBundle rb) {

        loadTruckData();
        loadExpData();

        bgHelper.changeBGColor(pane);

    }


    /**
     * The method changes the text's position in all Labels in the GUI.
     */
    private void posToRight(){
        truckInT.setAlignment(Pos.CENTER_RIGHT);
        truckOutT.setAlignment(Pos.CENTER_RIGHT);
        inAndOutTruckT.setAlignment(Pos.CENTER_RIGHT);
        revT.setAlignment(Pos.CENTER_RIGHT);
        expT.setAlignment(Pos.CENTER_RIGHT);
        cashflowT.setAlignment(Pos.CENTER_RIGHT);
    }



    /**
     * The method reads all Truck objects from the database's
     * Truck_Data table and appends them to the truckList ArrayList.
     */
    private void loadTruckData() {
        truckList.clear();
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
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * The method reads all Expenditure objects from the database's
     * Expenditure_Data table and appends them to the expList ArrayList.
     */
    private void loadExpData() {
        expList.clear();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Expenditure_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String expID = re.getString("id");
                String expType = re.getString("tipus");
                double expPricePerPiece = re.getDouble("egysegar");
                int expAmount = re.getInt("darabszam");
                double expPaid = re.getDouble("osszeg");
                String expDate = re.getString("datum");

                expList.add(new Expenditure(expID,
                        expType,
                        expPricePerPiece,
                        expAmount,
                        expPaid,
                        expDate
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * From the provided global ArrayLists and LocalDate objects this method generates a new TurnoverUploader object
     * what contains the main infos about the examined period.
     */
    @FXML
    private void createAndLoadTurnoverUploader() {

        if(DatePickerUtil.validatePeriodDates(periodStartPicker, periodEndPicker)) {

            int truckInNum = 0;
            int truckOutNum = 0;
            int truckDiff = 0;
            double revenues = 0;
            double expenditures = 0;
            double cashflow = 0;

            Utilities utils = new Utilities();

            for (Truck truck : truckList
                    ) {
                if (utils.wasInExaminedTimePeriodTruck(dateFomatter(periodStartPicker.getValue()),
                        dateFomatter(periodEndPicker.getValue()), truck, 0)) {
                    System.out.println(truck.getTruckEndDate());
                    truckInNum++;
                }
            }

            for (Truck truck : truckList
                    ) {
                if (utils.wasInExaminedTimePeriodTruck(dateFomatter(periodStartPicker.getValue()),
                        dateFomatter(periodEndPicker.getValue()), truck, 1)) {
                    truckOutNum++;
                    revenues += truck.getPaid();
                }
            }

            // Are there more truck which moved in, or moved out?
            truckDiff = truckInNum - truckOutNum;

            for (Expenditure exp : expList
                    ) {
                if (utils.wasInExaminedTimePeriodExp(dateFomatter(periodStartPicker.getValue()),
                        dateFomatter(periodEndPicker.getValue()), exp)) {
                    expenditures += exp.getExpPaid();
                }

            }

            // Is there more in flowing money, than expenditure?
            cashflow = revenues - expenditures;


            TurnoverUploader turnUpload = new TurnoverUploader(truckInNum,
                    truckOutNum,
                    truckDiff,
                    revenues,
                    expenditures,
                    cashflow);

            TU = turnUpload;

            posToRight();

            truckInT.setText(String.valueOf(turnUpload.getTruckInTurn()));
            truckOutT.setText(String.valueOf(turnUpload.getTruckOutTurn()));
            inAndOutTruckT.setText(String.valueOf(turnUpload.getTruckDiffTurn()));
            revT.setText(String.valueOf(turnUpload.getRevTurn()));
            expT.setText(String.valueOf(turnUpload.getExpTurn()));
            cashflowT.setText(String.valueOf(turnUpload.getDiffRevExpTurn()));
        }
    }

    /**
     * The method convert a LocalDate object into a String object if it's value in not-null.
     * @param ld The LocalDate object which will be converted.
     * @return The converted String, with the value of the input LocalDate object.
     */
    private String dateFomatter(LocalDate ld) {
        if(ld == null) {
            return "null";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return ld.format(formatter);
    }


    /**
     * Redirect the user to the Main window
     * @param event Listen to the click event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }


    /**
     * The method is a response to the user's click on the button which is responsible for the XLS creation.
     * If the creation was successful it confirms the user with an alert message, otherwise it throws an error alert.
     * @param event Listens to the click event.
     */
    @FXML
    private void generateReport(ActionEvent event){
        if(generateXLS(TU, periodStartPicker, periodEndPicker)){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setContentText("A program sikeresen legenerálta a jelentést!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A program nem tudta legenerálni a jelentést!");
            alert.showAndWait();
        }
    }


    /**
     * The method generates an XLS file and fills it with informations about the examined period.
     * After it created and filler the file, the method will save it in the project's directory.
     * @param tu The examined period.
     * @param picker1 The starting date of the period.
     * @param picker2 The ending date of the period.
     * @return If the XLS creation was successful the method returns with true, otherwise false.
     */
    private boolean generateXLS(TurnoverUploader tu, DatePicker picker1, DatePicker picker2) {

        Date dt = new Date();
        String now = String.valueOf(dt.getTime());

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(("Riport_egyéni_"));


        HSSFRow row1 = sheet.createRow(0);
        HSSFCell cell = row1.createCell(0);
        cell.setCellValue("Időszak kezdete: ");

        cell = row1.createCell(1);
        cell.setCellValue(picker1.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        HSSFRow row2 = sheet.createRow(1);
        cell = row2.createCell(0);
        cell.setCellValue("Időszak kezdete: ");

        cell = row2.createCell(1);
        cell.setCellValue(picker2.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        HSSFRow row3 = sheet.createRow(2);
        cell = row3.createCell(0);
        cell.setCellValue("Behajtó kamionok száma: ");

        cell = row3.createCell(1);
        cell.setCellValue(tu.getTruckInTurn());


        HSSFRow row4 = sheet.createRow(3);
        cell = row4.createCell(0);
        cell.setCellValue("Kihajtó kaminok száma: ");

        cell = row4.createCell(1);
        cell.setCellValue(tu.getTruckOutTurn());


        HSSFRow row5 = sheet.createRow(4);
        cell = row5.createCell(0);
        cell.setCellValue("Bennmaradó kaminok száma: ");

        cell = row5.createCell(1);
        cell.setCellValue(tu.getTruckDiffTurn());


        HSSFRow row6 = sheet.createRow(5);
        cell = row6.createCell(0);
        cell.setCellValue("Bevételek forintban: ");

        cell = row6.createCell(1);
        cell.setCellValue(tu.getRevTurn());


        HSSFRow row7 = sheet.createRow(6);
        cell = row7.createCell(0);
        cell.setCellValue("Kiadások forintban: ");

        cell = row7.createCell(1);
        cell.setCellValue(tu.getExpTurn());


        HSSFRow row8 = sheet.createRow(7);
        cell = row8.createCell(0);
        cell.setCellValue("Cashflow: ");

        cell = row8.createCell(1);
        cell.setCellValue(tu.getDiffRevExpTurn());

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        try {
            workbook.write(new FileOutputStream(("Riport_"+ now +".xls")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
