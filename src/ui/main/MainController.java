package ui.main;

import classes.*;
import com.jfoenix.controls.JFXButton;
import database.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.addTruck.AddTruckController;
import ui.editTruck.EditTruckController;
import ui.signInCompany.SignInCompanyController;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainController implements Initializable{

    /**
     * This ObservableArrayList will hold the needed Truck data.
     */
    private ObservableList<Truck> truckList = FXCollections.observableArrayList();

    /**
     * This ObservableArrayList will hold the needed Expenditure data.
     */
    private ObservableList<Expenditure> expList = FXCollections.observableArrayList();

    /**
     * This ObservableArrayList will hold the needed Employee data.
     */
    private ObservableList<Employee> empList = FXCollections.observableArrayList();

    /**
     * This is the window's rootPane.
     */
    @FXML public AnchorPane rootPane;

    /**
     * Create a global instance of this class.
     */
    public static MainController MC;

    /**
     * The tab what contains the TableViews.
     */
    @FXML private Tab employeesTab;

    /**
     * These are the TableViews aiming to display the datas of the objects.
     */
    @FXML private TableView inTableView;
    @FXML private TableView outTableView;
    @FXML private TableView filterTableView;
    @FXML private TableView expTableView;
    @FXML private TableView empTableView;

    /**
     * These are the columns for the truck data.
     */
    @FXML private TableColumn<Truck, String> inTruckIDCol;
    @FXML private TableColumn<Truck, String> outTruckIDCol;
    @FXML private TableColumn<Truck, String> inPlateNumberCol;
    @FXML private TableColumn<Truck, String> outPlateNumberCol;
    @FXML private TableColumn<Truck, String> inTransCompNameCol;
    @FXML private TableColumn<Truck, String> outTransCompNameCol;
    @FXML private TableColumn<Truck, String> inTruckBrandCol;
    @FXML private TableColumn<Truck, String> outTruckBrandCol;
    @FXML private TableColumn<Truck, String> inTruckStartDateCol;
    @FXML private TableColumn<Truck, String> outTruckStartDateCol;
    @FXML private TableColumn<Truck, String> outTruckEndDateCol;
    @FXML private TableColumn<Truck, Double> outPaidCol;

    /**
     * These are the columns for the truck data which will be filtered.
     */
    @FXML private TableColumn<Truck, String> filterTruckIDCol;
    @FXML private TableColumn<Truck, String> filterPlateNumberCol;
    @FXML private TableColumn<Truck, String> filterTransCompNameCol;
    @FXML private TableColumn<Truck, String> filterTruckBrandCol;
    @FXML private TableColumn<Truck, String> filterTruckStartDateCol;
    @FXML private TableColumn<Truck, String> filterTruckEndDateCol;
    @FXML private TableColumn<Truck, Double> filterPaidCol;
    @FXML private TextField filterField;

    /**
     * These are the columns for the expenditure data.
     */
    @FXML private TableColumn<Shift, String> expIDCol;
    @FXML private TableColumn<Shift, String> expTypeCol;
    @FXML private TableColumn<Shift, Double> expPriceByPieceCol;
    @FXML private TableColumn<Shift, Integer> expAmountCol;
    @FXML private TableColumn<Shift, Double> expPaidCol;
    @FXML private TableColumn<Shift, String> expDateCol;

    /**
     * These are the columns for the employee data.
     */
    @FXML private TableColumn<Employee, String> empIDCol;
    @FXML private TableColumn<Employee, String> empUserCol;
    @FXML private TableColumn<Employee, String> empPassCol;
    @FXML private TableColumn<Employee, String> empLastNCol;
    @FXML private TableColumn<Employee, String> empFirstNCol;
    @FXML private TableColumn<Employee, String> empBirthCol;
    @FXML private TableColumn<Employee, String> empPhoneCol;
    @FXML private TableColumn<Employee, String> empPlaceCol;
    @FXML private TableColumn<Employee, String> empAddressCol;
    @FXML private TableColumn<Employee, String> empZIPCol;
    @FXML private TableColumn<Employee, String> empStartCol;
    @FXML private TableColumn<Employee, String> empEndCol;

    /**
     * If the button is clicked, the program opens the AddEmployee window.
     */
    @FXML private JFXButton loadAddEmployeeBtn;

    /**
     * * The Text field displays the current number of parking truck in the terminal.
     */
    @FXML private Text numOfTruckIn;

    /**
     * The Text field displays the current summed revenue in the shift.
     */
    @FXML private Text shiftRevYet;

    /**
     * The Text field displays the current summed expenditure in the shift.
     */
    @FXML private Text shiftExpYet;

    /**
     * Buttons used for open a new window, and the generate report about a period.
     */
    @FXML private JFXButton turnoverPerShiftBtn;
    @FXML private JFXButton turnoverPerWeekBtn;
    @FXML private JFXButton turnoverPer30DaysBtn;
    @FXML private JFXButton turnoverOptionalBtn;


    /**
     * This ObservableArrayList helps in the filtering cycle.
     */
    private ObservableList<Truck> masterData = FXCollections.observableArrayList();

    /**
     *  This FilteredList hold the filtered objects.
     */
    FilteredList<Truck> filteredData = new FilteredList<>(masterData, p -> true);

    /**
     * Instantiate BackgroundHelper object to change the background color if it is altered
     */
    private BackgroundHelper bgHelper = new BackgroundHelper();

    /**
     * A regex String variable for validation.
     */
    private final String digitPattern = "[0-9]+";

    /**
     * This variable will be usable from other classes to get the clicked period type.
     */
    public String turnoverControllerPeriodType;

    /**
     * Creates a Utility class's instance.
     */
    Utilities utilities = new Utilities();


    /**
     * This method runs the initialization process.
     * @param url The URL which provides the path where the needed FXML file is.
     * @param rb Contains local specific objects.
     */
    public void initialize(URL url, ResourceBundle rb) {

        MC = this;

        inTruckIDCol.prefWidthProperty().bind(inTableView.widthProperty().divide(5));
        inPlateNumberCol.prefWidthProperty().bind(inTableView.widthProperty().divide(5));
        inTransCompNameCol.prefWidthProperty().bind(inTableView.widthProperty().divide(5));
        inTruckBrandCol.prefWidthProperty().bind(inTableView.widthProperty().divide(5));
        inTruckStartDateCol.prefWidthProperty().bind(inTableView.widthProperty().divide(5));

        outTruckIDCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outPlateNumberCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outTransCompNameCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outTruckBrandCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outTruckStartDateCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outTruckEndDateCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));
        outPaidCol.prefWidthProperty().bind(outTableView.widthProperty().divide(7));

        filterTruckIDCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterPlateNumberCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterTransCompNameCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterTruckBrandCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterTruckStartDateCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterTruckEndDateCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));
        filterPaidCol.prefWidthProperty().bind(filterTableView.widthProperty().divide(7));

        expIDCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));
        expTypeCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));
        expPriceByPieceCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));
        expAmountCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));
        expPaidCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));
        expDateCol.prefWidthProperty().bind(expTableView.widthProperty().divide(6));

        empIDCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empUserCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empPassCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empLastNCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empFirstNCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empBirthCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empPhoneCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empPlaceCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empAddressCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empZIPCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empStartCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));
        empEndCol.prefWidthProperty().bind(empTableView.widthProperty().divide(12));


        initInCol();
        initOutCol();
        initFilterCol();
        initExpCol();
        initEmpCol();


        loadTruckData(inTableView);
        loadTruckData(outTableView);
        loadTruckData(filterTableView);
        loadExpData();
        loadEmpData();

        filterTextType();


        if(!SignInCompanyController.isLogInCompany) {
            employeesTab.setDisable(true);
            loadAddEmployeeBtn.setDisable(true);
            turnoverPerShiftBtn.setDisable(true);
            turnoverPerWeekBtn.setDisable(true);
            turnoverPer30DaysBtn.setDisable(true);
            turnoverOptionalBtn.setDisable(true);
        }


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new ShiftTimer(), 0,
                1, TimeUnit.MINUTES);


        inTableView.setRowFactory( tv -> {
            TableRow<Truck> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Truck truck = row.getItem();
                    windowLoaderEditTruck("../editTruck/EditTruck.fxml", "Hozzáadott kamion szerkesztése", truck);
                }
            });
            return row ;
        });

        bgHelper.changeBGColor(rootPane);

        numOfTruckIn.setText(String.valueOf(inTableView.getItems().size()));
        shiftRevYet.setText(utilities.revInLatestShiftSum + " HUF");
        shiftExpYet.setText(utilities.expInLatestShiftSum + " HUF");

    }

    /**
     * This method will refresh all important table and label in the program's Main window.
     */
    public void refreshTablesFunc() {
        initInCol();
        initOutCol();
        initFilterCol();
        initExpCol();
        initEmpCol();

        loadTruckData(inTableView);
        loadTruckData(outTableView);
        loadTruckData(filterTableView);
        loadExpData();
        loadEmpData();

        filterTextType();

        numOfTruckIn.setText(String.valueOf(inTableView.getItems().size()));
        shiftRevYet.setText(utilities.revInLatestShiftSum + " HUF");
        shiftExpYet.setText(utilities.expInLatestShiftSum + " HUF");
    }


    /**
     * The method refreshes the Labels with the current revenue and expenditure.
     */
    public void refreshRevPrices() {
        utilities.refreshDatabasePrices();
        numOfTruckIn.setText(String.valueOf(inTableView.getItems().size()));
        shiftRevYet.setText(utilities.revInLatestShiftSum + " HUF");
        shiftExpYet.setText(utilities.expInLatestShiftSum + " HUF");
    }


    /**
     * This method will set the column values in the TableView through binding an Truck object to it.
     * This method will fill up the table for truck which are in the terminal at the moment.
     */
    private void initInCol() {
        inTruckIDCol.setCellValueFactory(new PropertyValueFactory<>("truckID"));
        inPlateNumberCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        inTransCompNameCol.setCellValueFactory(new PropertyValueFactory<>("transCompName"));
        inTruckBrandCol.setCellValueFactory(new PropertyValueFactory<>("truckBrand"));
        inTruckStartDateCol.setCellValueFactory(new PropertyValueFactory<>("truckStartDate"));
    }


    /**
     * This method will set the column values in the TableView through binding an Truck object to it.
     * This method will fill up the outgoing trucks table.
     */
    private void initOutCol() {
        outTruckIDCol.setCellValueFactory(new PropertyValueFactory<>("truckID"));
        outPlateNumberCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        outTransCompNameCol.setCellValueFactory(new PropertyValueFactory<>("transCompName"));
        outTruckBrandCol.setCellValueFactory(new PropertyValueFactory<>("truckBrand"));
        outTruckStartDateCol.setCellValueFactory(new PropertyValueFactory<>("truckStartDate"));
        outTruckEndDateCol.setCellValueFactory(new PropertyValueFactory<>("truckEndDate"));
        outPaidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
    }


    /**
     * This method will set the column values in the TableView through binding an Truck object to it.
     * This method will fill up the tables for filtering the trucks's data.
     */
    private void initFilterCol() {
        filterTruckIDCol.setCellValueFactory(new PropertyValueFactory<>("truckID"));
        filterPlateNumberCol.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        filterTransCompNameCol.setCellValueFactory(new PropertyValueFactory<>("transCompName"));
        filterTruckBrandCol.setCellValueFactory(new PropertyValueFactory<>("truckBrand"));
        filterTruckStartDateCol.setCellValueFactory(new PropertyValueFactory<>("truckStartDate"));
        filterTruckEndDateCol.setCellValueFactory(new PropertyValueFactory<>("truckEndDate"));
        filterPaidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
    }


    /**
     * This method will set the column values in the TableView through binding an Expenditure object to it.
     */
    private void initExpCol() {
        expIDCol.setCellValueFactory(new PropertyValueFactory<>("expID"));
        expTypeCol.setCellValueFactory(new PropertyValueFactory<>("expType"));
        expPriceByPieceCol.setCellValueFactory(new PropertyValueFactory<>("expPricePerPiece"));
        expAmountCol.setCellValueFactory(new PropertyValueFactory<>("expAmount"));
        expPaidCol.setCellValueFactory(new PropertyValueFactory<>("expPaid"));
        expDateCol.setCellValueFactory(new PropertyValueFactory<>("expDate"));
    }


    /**
     * This method will set the column values in the TableView through binding an Employee object to it.
     */
    private void initEmpCol() {
        empIDCol.setCellValueFactory(new PropertyValueFactory<>("empID"));
        empUserCol.setCellValueFactory(new PropertyValueFactory<>("empUser"));
        empPassCol.setCellValueFactory(new PropertyValueFactory<>("empPass"));
        empFirstNCol.setCellValueFactory(new PropertyValueFactory<>("empFirstN"));
        empLastNCol.setCellValueFactory(new PropertyValueFactory<>("empLastN"));
        empBirthCol.setCellValueFactory(new PropertyValueFactory<>("empBirth"));
        empPhoneCol.setCellValueFactory(new PropertyValueFactory<>("empPhone"));
        empPlaceCol.setCellValueFactory(new PropertyValueFactory<>("empPlace"));
        empAddressCol.setCellValueFactory(new PropertyValueFactory<>("empAddress"));
        empZIPCol.setCellValueFactory(new PropertyValueFactory<>("empZIP"));
        empStartCol.setCellValueFactory(new PropertyValueFactory<>("empStart"));
        empEndCol.setCellValueFactory(new PropertyValueFactory<>("empEnd"));
    }


    /**
     * The method loads the Truck objects from the database, and appends the to a selected TableView
     * which can be controlled with the input argument. It will separate the Truck according the fact
     * that they are still in the terminal or they have already left it.
     * @param TABLEVIEW The inTableView or the outTableView Tableviews.
     */
    private void loadTruckData(TableView TABLEVIEW) {
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

                if(TABLEVIEW == inTableView) {
                    if (Objects.equals(truckEndDate,null)
                            || Objects.equals(truckEndDate, "null")
                            || Objects.equals(truckEndDate, "")) {
                        truckList.add(new Truck(truckID,
                                transCompName,
                                plateNumber,
                                truckBrand,
                                truckStartDate,
                                "",
                                0
                        ));
                    }
                } else if (TABLEVIEW == outTableView) {
                    if (!Objects.equals(truckEndDate,null)
                            && !Objects.equals(truckEndDate, "null")
                            && !Objects.equals(truckEndDate, "")) {
                        truckList.add(new Truck(truckID,
                                transCompName,
                                plateNumber,
                                truckBrand,
                                truckStartDate,
                                truckEndDate,
                                paid
                        ));
                    }
                } else {
                    truckList.add(new Truck(truckID,
                            transCompName,
                            plateNumber,
                            truckBrand,
                            truckStartDate,
                            truckEndDate,
                            paid
                    ));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(TABLEVIEW == filterTableView) {
            masterData.clear();
            masterData.addAll(truckList);
        } else {
            TABLEVIEW.getItems().setAll(truckList);
        }
    }


    /**
     * The method loads the Expenditure objects from the database, and appends the to a TableView.
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

        expTableView.getItems().setAll(expList);
    }


    /**
     * The method loads the Employee objects from the database, and appends the to a TableView.
     */
    private void loadEmpData() {
        empList.clear();
        DatabaseHandler handler = null;
        try {
            handler = DatabaseHandler.getInstance();
            String query = "SELECT * FROM Employee_Data";
            ResultSet re = handler.execQuery(query);
            while (re.next()) {
                String empID = re.getString("id");
                String empUser = re.getString("felhasznalonev");
                String empPass = re.getString("jelszo");
                String empLastN = re.getString("vezeteknev");
                String empFirstN = re.getString("keresztnev");
                String empBirth = re.getString("szuletesi_datum");
                String empPhone = re.getString("telefon");
                String empPlace = re.getString("lakhely");
                String empAddress = re.getString("cim");
                String empZIP = re.getString("iranyitoszam");
                String empStart = re.getString("kezdett");
                String empEnd = re.getString("befejezte");



                empList.add(new Employee(empID,
                                    empUser,
                                    empPass,
                                    empLastN,
                                    empFirstN,
                                    empBirth,
                                    empPhone,
                                    empPlace,
                                    empAddress,
                                    empZIP,
                                    empStart,
                                    empEnd
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }

        empTableView.getItems().setAll(empList);
    }


    /**
     * The method will load the AddTruck window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddTruck(ActionEvent event){
        windowLoader("../addTruck/AddTruck.fxml", "Kamion hozzáadása");
    }

    /**
     * The method will load the AddTransCompany window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddTransCompany(ActionEvent event){
        windowLoader("../addTransCompany/AddTransCompany.fxml", "Fuvarozási cég hozzáadás");
    }

    /**
     * The method will load the AddEmployee window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddEmployee(ActionEvent event){
        windowLoader("../addEmployee/AddEmployee.fxml", "Munkatárs hozzáadása");
    }

    /**
     * The method will load the AddExpenditure window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddExpenditure(ActionEvent event){
        windowLoader("../addExpenditure/AddExpenditure.fxml", "Kiadás hozzáadása");
    }

    /**
     * The method will load the AddRevenue window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddRevenue(ActionEvent event){
        windowLoader("../addRevenue/AddRevenue.fxml", "Bevétel hozzáadása");
    }

    /**
     * The method will load the AddExpenditureType window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadAddExpenditureType(ActionEvent event){
        windowLoader("../addExpenditureType/AddExpenditureType.fxml", "Kiadás típus hozzáadása");
    }

    /**
     * The method will load the Settings window, if a responsible button is clicked.
     * @param event Listens to the click event.
     */
    @FXML
    private void loadSettings(ActionEvent event){
        windowLoader("../settings/Settings.fxml", "Beállítások");
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

    /**
     * This method loads the editing windows of a Truck object.
     * @param path A String path to the needed FXML.
     * @param title The title of the new window.
     * @param actTruck The Truck object which will be edited.
     */
    @FXML
    private void windowLoaderEditTruck(String path, String title, Truck actTruck) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(rootPane.getScene().getWindow());
            EditTruckController editTruckController = loader.getController();
            editTruckController.editTruck(actTruck);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.getIcons().add(new Image(MainController.class.getResourceAsStream("../icons/delivery-truck.png")));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(AddTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method is responsible for the filtering.
     */
    private void filterTextType() {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(truck -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (truck.getTruckID().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches trans. company.
                } else if (truck.getTransCompName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches trans. company.
                } else if (truck.getPlateNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches plate number.
                } else if (truck.getTruckBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (lowerCaseFilter.matches(digitPattern)
                        && truck.getPaid() == Double.parseDouble(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<Truck> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(filterTableView.comparatorProperty());

        filterTableView.setItems(sortedData);
    }


    /**
     * The user exits from the program.
     */
    @FXML
    private void exit() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    /**
     * The function loads the SignInFull window which means, that the user signed out.
     * @param event Listens to the click event.
     */
    @FXML
    private void signOut(ActionEvent event) {
        SignInCompanyController.isLogInCompany = false;
         windowSignOutLoader("../signInFull/SignInFull.fxml", "Bejelentkezés");
    }

    /**
     * The method will create a new stage and change it with the old (current) one.
     * It also adds all the necessary decoration to the stage. This one is only used for redirect to the sign in Stage.
     * @param path The path where the needed FXML file is located.
     * @param title The title of the window, it will be displayed on the top side of the window..
     */
    public void windowSignOutLoader(String path, String title) {
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


    /**
     * The function loads the Turnover window with the state parameter SHIFT.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleTurnoverPerShift(ActionEvent event) {
        turnoverControllerPeriodType = "SHIFT";
        windowLoader("../turnover/Turnover.fxml", "Forgalom az utolsó műszak");
    }


    /**
     * The function loads the Turnover window with the state parameter WEEK.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleTurnoverPerWeek(ActionEvent event) {
        turnoverControllerPeriodType = "WEEK";
        windowLoader("../turnover/Turnover.fxml", "Forgalom az utolsó héten");
    }


    /**
     * The function loads the Turnover window with the state parameter DAYS30.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleTurnoverPer30Days(ActionEvent event) {
        turnoverControllerPeriodType = "DAYS30";
        windowLoader("../turnover/Turnover.fxml", "Forgalom az utolsó 30 nap");
    }


    /**
     * The function loads the TurnoverOptional window.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleTurnoverOptional(ActionEvent event) {
        windowLoader("../turnoverOptional/TurnoverOptional.fxml", "Forgalom egyéni");
    }

}
