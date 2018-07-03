package ui.turnover;


import classes.*;
import com.jfoenix.controls.JFXButton;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import ui.main.MainController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  This class's responsibility to let the user to look up the financial and traffic data of
 *  the last shift, last week or last 30 days and after that generate an XLS file from it.
 */
public class TurnoverController implements Initializable {

    /**
     *
     */
    private ObservableList<TurnoverUploader> turnoverList = FXCollections.observableArrayList();

    /**
     * Create a global static instance of the TurnoverController class.
     */
    public static TurnoverController TC;

    /**
     * This is the window's rootPane.
     */
    @FXML Pane pane;

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();


    /**
     * Displays the period, which provides the data for the report.
     */
    @FXML private Label periodT;

    /**
     * Displays how many trucks moved in in that period.
     */
    @FXML private Label truckInT;

    /**
     * Displays how many trucks leftthe terminal in that period.
     */
    @FXML private Label truckOutT;

    /**
     * Displays the difference between the in and outgoing traffic.
     */
    @FXML private Label inAndOutTruckT;

    /**
     * Displays the revenues what was generated in the period.
     */
    @FXML private Label revT;

    /**
     * Displays the spent money in the period.
     */
    @FXML private Label expT;

    /**
     * Displays the cashflow in that period.
     */
    @FXML private Label cashflowT;


    /**
     * Redirects the user to the Main window.
     */
    @FXML private JFXButton backBtn;

    /**
     * If the user clicks it, the binded method will generate the report.
     */
    @FXML private JFXButton forwardBtn;

    /**
     * An instance of the Utilities class.
     */
    Utilities utilities = new Utilities();

    /**
     * This ArrayList will contain the loaded Truck objects.
     */
    private ArrayList<Truck> truckList = new ArrayList<>();

    /**
     * This ArrayList will contain the loaded Expenditure objects.
     */
    private ArrayList<Expenditure> expList = new ArrayList<>();


    /**
     * This variable holds the current time in milliseconds.
     */
    private long currentTime = System.currentTimeMillis();

    /**
     * This variable will hold the old date.
     */
    private long oldTime;

    /**
     * This variable holds a long value of a shift in milliseconds.
     */
    private long SHIFT = 24 * 60 * 60 * 1000;

    /**
     * This variable holds a long value of a weeks in milliseconds.
     */
    private long WEEK = 7 * 24 * 60 * 60 * 1000;

    /**
     * This variable holds a long value of 30 days in milliseconds.
     */
    private long DAYS30 = 30 * 24 * 60 * 60 * 1000L;


    /**
     * Instantiate database handler globally
     */
    DatabaseHandler databaseHandler;

    /**
     * Create a TrunoverUploader instance globally so it will be able to store the loaded object's data.
     */
    TurnoverUploader TU = null;


    /**
     * This method runs the initialization process.
     * @param url The URL which provides the path where the needed FXML file is.
     * @param rb Contains local specific objects.
     */
    public void initialize(URL url, ResourceBundle rb) {

        TC = this;

        if (Objects.equals(MainController.MC.turnoverControllerPeriodType, "SHIFT")) {
            oldTime = currentTime - SHIFT;
            initThings();
        } else if (Objects.equals(MainController.MC.turnoverControllerPeriodType, "WEEK")) {
            oldTime = currentTime - WEEK;
            initThings();
        } else if (Objects.equals(MainController.MC.turnoverControllerPeriodType, "DAYS30")) {
            oldTime = currentTime - DAYS30;
            initThings();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A program nem tudta végrehajtani a parancsot!");
            alert.showAndWait();

            Stage stage = (Stage) pane.getScene().getWindow();
            stage.close();
        }

        bgHelper.changeBGColor(pane);

    }

    /**
     * This method fills the Labels when the window is opened.
     */
    private void initThings() {
        loadTruckData();
        loadExpData();

        TU = createAndLoadTurnoverUploader();

        posToRight();

        periodT.setText(translate(MainController.MC.turnoverControllerPeriodType));
        truckInT.setText(String.valueOf(TU.getTruckInTurn()));
        truckOutT.setText(String.valueOf(TU.getTruckOutTurn()));
        inAndOutTruckT.setText(String.valueOf(TU.getTruckDiffTurn()));
        revT.setText(String.valueOf(TU.getRevTurn()));
        expT.setText(String.valueOf(TU.getExpTurn()));
        cashflowT.setText(String.valueOf(TU.getDiffRevExpTurn()));
    }


    /**
     * The method changes the text's position in all Labels in the GUI.
     */
    private void posToRight(){
        periodT.setAlignment(Pos.CENTER_RIGHT);
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

                System.out.println("asdfsajfhnaskjfnhsakjldfsand");
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
     * This method will create a TurnoverUploader object from the data what is in the ArrayList.
     * @return A TrunoverUploader object with the period data.
     */
    private TurnoverUploader createAndLoadTurnoverUploader() {

        turnoverList.clear();

        int truckInNum = 0;
        int truckOutNum = 0;
        int truckDiff = 0;
        double revenues = 0;
        double expenditures = 0;
        double cashflow = 0;

        Utilities utils = new Utilities();

        for (Truck truck : truckList
                ) {
            if (utils.wasInExaminedTimePeriodTruck(oldTime, currentTime, truck, 0)) {
                System.out.println(truck.getTruckEndDate());
                truckInNum++;
            }
        }

        for (Truck truck : truckList
                ) {
            if (utils.wasInExaminedTimePeriodTruck(oldTime, currentTime, truck, 1)) {
                truckOutNum++;
                revenues += truck.getPaid();
            }
        }

        // Are there more truck which moved in, or moved out?
        truckDiff = truckInNum - truckOutNum;

        for (Expenditure exp : expList
                ) {
            if (utils.wasInExaminedTimePeriodExp(oldTime, currentTime, exp)) {
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

        return turnUpload;
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
     * This method helps to convert the english period signing words into hungarian.
     * @param str An english word, which indicates how long the period is.
     * @return A hungarian word which denotes the same amount of time.
     */
    private String translate(String str) {
        switch (str){
            case "SHIFT":
                return "Utolsó műszak";
            case "WEEK":
                return "Utolsó hét";
            case "DAYS30":
                return "Elmúlt 30 nap";
        }
        return "";
    }


    /**
     * The method is a response to the user's click on the button which is responsible for the XLS creation.
     * If the creation was successful it confirms the user with an alert message, otherwise it throws an error alert.
     * @param event Listens to the click event.
     */
    @FXML
    private void generateReport(ActionEvent event){
        if(generateXLS(TU, translate(MainController.MC.turnoverControllerPeriodType))){
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
     * @param period The selected period.
     * @return If the XLS creation was successful the method returns with true, otherwise false.
     */
    private boolean generateXLS(TurnoverUploader tu, String period) {

        Date dt = new Date();
        String now = String.valueOf(dt.getTime());

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(("Riport_"+period));


        HSSFRow row1 = sheet.createRow(0);
        HSSFCell cell = row1.createCell(0);
        cell.setCellValue("Periódus: ");

        cell = row1.createCell(1);
        cell.setCellValue(period);


        HSSFRow row2 = sheet.createRow(1);
        cell = row2.createCell(0);
        cell.setCellValue("Behajtó kamionok száma: ");

        cell = row2.createCell(1);
        cell.setCellValue(tu.getTruckInTurn());


        HSSFRow row3 = sheet.createRow(2);
        cell = row3.createCell(0);
        cell.setCellValue("Kihajtó kaminok száma: ");

        cell = row3.createCell(1);
        cell.setCellValue(tu.getTruckOutTurn());


        HSSFRow row4 = sheet.createRow(3);
        cell = row4.createCell(0);
        cell.setCellValue("Bennmaradó kaminok száma: ");

        cell = row4.createCell(1);
        cell.setCellValue(tu.getTruckDiffTurn());


        HSSFRow row5 = sheet.createRow(4);
        cell = row5.createCell(0);
        cell.setCellValue("Bevételek forintban: ");

        cell = row5.createCell(1);
        cell.setCellValue(tu.getRevTurn());


        HSSFRow row6 = sheet.createRow(5);
        cell = row6.createCell(0);
        cell.setCellValue("Kiadások forintban: ");

        cell = row6.createCell(1);
        cell.setCellValue(tu.getExpTurn());


        HSSFRow row7 = sheet.createRow(6);
        cell = row7.createCell(0);
        cell.setCellValue("Cashflow: ");

        cell = row7.createCell(1);
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
