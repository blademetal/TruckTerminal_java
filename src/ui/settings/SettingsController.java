package ui.settings;


import java.io.*;
import java.net.URL;

import classes.BackgroundHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.main.MainController;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  This class's purpose to handle the changes in settings which means it has to load the existing settings,
 *  and if there are any changes it also has to save the new ones in a Properties file.
 */
public class SettingsController implements Initializable{

    /**
     * The main JavaFX pane of this window.
     */
    @FXML private Pane rootPane;

    /**
     * This Radio Button will be clicked, if 'Hourly' stste is selected.
     */
    @FXML private JFXRadioButton perHourRadio;

    /**
     * This ChoiceBox will contain the even number which can be selected for a charging unit.
     */
    @FXML private ChoiceBox<Integer> numOfHoursPerHour;

    /**
     * The field will contain the hourly price if the state is set to 'Hourly'.
     */
    @FXML private TextField pricePerHour;

    /**
     * This Radio Button will be clicked, if 'Daily' stste is selected.
     */
    @FXML private JFXRadioButton perDayRadio;

    /**
     * The field will contain the daily price if the state is set to 'Daily'.
     */
    @FXML private TextField pricePerDay;

    /**
     * This Radio Button will be clicked, if 'Hibrid' stste is selected.
     */
    @FXML private JFXRadioButton hibridRadio;

    /**
     * The number of hours for an hourly unit in 'Hibrid' state.
     */
    @FXML private ChoiceBox<Integer> numOfHoursHibrid;

    /**
     * The field will contain the price of hourly charging in a 'Hibrid' paying state.
     */
    @FXML private TextField pricePerHourHibrid;

    /**
     * The field will contain the price of daily charging in a 'Hibrid' paying state.
     */
    @FXML private TextField pricePerDayHibrid;

    /**
     * A new ColorPicker object to select new background color.
     */
    @FXML private ColorPicker colorPalette;

    /**
     * If this button will be clicked, the new settings will be set and the user will return to Main window.
     */
    @FXML private JFXButton OKBtn;


    /**
     * If this button will be clicked, the new settings will be set.
     */
    @FXML private JFXButton applyBtn;

    /**
     * A list of even numbers to hold options for hourly charging.
     */
    private ObservableList<Integer> listOfHours = FXCollections.observableArrayList();

    /**
     * A regex pattern to filter out only numbers.
     */
    private String pattern = "[0-9]+";

    /**
     * Creating a new Properties object to handle the settings.
     */
    private Properties settings = new Properties();

    /**
     * Creating a new Properties object to load the Settings file.
     */
    private Properties loadSettings = new Properties();

    /**
     * Instantiate database handler globally
     */
    private String expType;

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
            loadSettings.load(new FileInputStream("TeMeReSettings.properties"));
            expType = loadSettings.getProperty("ExpTypes");
        } catch (IOException e) {
            e.printStackTrace();
        }

        bgHelper.changeBGColor(rootPane);

        listOfHours.addAll(1, 2, 3, 4, 6, 8, 12);
        colorPalette.setValue(Color.web("#343538"));

        OKBtn.setDisable(true);
        applyBtn.setDisable(true);
        numOfHoursPerHour.setItems(listOfHours);
        numOfHoursHibrid.setItems(listOfHours);

        numOfHoursPerHour.setDisable(true);
        pricePerHour.setDisable(true);
        pricePerDay.setDisable(true);
        numOfHoursHibrid.setDisable(true);
        pricePerHourHibrid.setDisable(true);
        pricePerDayHibrid.setDisable(true);

        pricePerHour.textProperty().addListener(listenerHour);
        pricePerDay.textProperty().addListener(listenerDay);
        pricePerDayHibrid.textProperty().addListener(listenerHibrid);

        initValues();
    }

    /**
     * This method changes the availability of the fields and buttons in the window if 'Hourly' state is selected.
     * @param event Listens to the click event.
     */
    @FXML
    private void selectHour(ActionEvent event) {
        if(perHourRadio.isSelected()) {
            perDayRadio.setDisable(true);
            hibridRadio.setDisable(true);
            numOfHoursPerHour.setDisable(false);
            pricePerHour.setDisable(false);
            pricePerDay.setDisable(true);
            numOfHoursHibrid.setDisable(true);
            pricePerHourHibrid.setDisable(true);
            pricePerDayHibrid.setDisable(true);
        } else {
            perDayRadio.setDisable(false);
            hibridRadio.setDisable(false);

            numOfHoursPerHour.setDisable(true);
            pricePerHour.setDisable(true);

            numOfHoursPerHour.setValue(null);
            pricePerHour.clear();
        }
    }

    /**
     * This method changes the availability of the fields and buttons in the window if 'Daily' state is selected.
     * @param event Listens to the click event.
     */
    @FXML
    private void selectDay(ActionEvent event) {
        if(perDayRadio.isSelected()) {
            perHourRadio.setDisable(true);
            hibridRadio.setDisable(true);
            pricePerDay.setDisable(false);
            pricePerHour.setDisable(true);
            numOfHoursPerHour.setDisable(true);
            numOfHoursHibrid.setDisable(true);
            pricePerHourHibrid.setDisable(true);
            pricePerDayHibrid.setDisable(true);
        } else {
            perHourRadio.setDisable(false);
            hibridRadio.setDisable(false);

            pricePerDay.setDisable(true);

            pricePerDay.clear();
        }
    }


    /**
     * This method changes the availability of the fields and buttons in the window if 'Hibrid' state is selected.
     * @param event Listens to the click event.
     */
    @FXML
    private void selectHibrid(ActionEvent event) {
        if(hibridRadio.isSelected()) {
            perHourRadio.setDisable(true);
            perDayRadio.setDisable(true);
            numOfHoursHibrid.setDisable(false);
            pricePerHourHibrid.setDisable(false);
            pricePerDayHibrid.setDisable(false);
            pricePerHour.setDisable(true);
            numOfHoursPerHour.setDisable(true);
            pricePerDay.setDisable(true);
        } else {
            perHourRadio.setDisable(false);
            perDayRadio.setDisable(false);

            numOfHoursHibrid.setDisable(true);
            pricePerHourHibrid.setDisable(true);
            pricePerDayHibrid.setDisable(true);

            numOfHoursHibrid.setValue(null);
            pricePerHourHibrid.clear();
            pricePerDayHibrid.clear();
        }
    }


    /**
     * The created object will be in charge with listening to changes at 'Hourly' state.
     */
    ChangeListener listenerHour = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            boolean able = true;

            if (!pricePerHour.getText().trim().isEmpty()
                    && pricePerHour.getText().matches(pattern)
                    && !(numOfHoursPerHour.getValue() == null)) {
                able = false;
            }

            OKBtn.setDisable(able);
            applyBtn.setDisable(able);
        }
    };


    /**
     * The created object will be in charge with listening to changes at 'Daily' state.
     */
    ChangeListener listenerDay = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            boolean able = true;

            if (!pricePerDay.getText().trim().isEmpty()
                    && pricePerDay.getText().matches(pattern)) {
                able = false;
            }

            OKBtn.setDisable(able);
            applyBtn.setDisable(able);
        }
    };


    /**
     * The created object will be in charge with listening to changes at 'Hibrid' state.
     */
    ChangeListener listenerHibrid = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            boolean able = true;

            if (!pricePerHourHibrid.getText().trim().isEmpty()
                    && pricePerHourHibrid.getText().matches(pattern)
                    && !pricePerDayHibrid.getText().trim().isEmpty()
                    && pricePerDayHibrid.getText().matches(pattern)
                    && !(numOfHoursHibrid.getValue() == null)) {
                able = false;
            }

            OKBtn.setDisable(able);
            applyBtn.setDisable(able);
        }
    };


    /**
     * This method will execute the apply function and also close the window and return to the Main window.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleOK(ActionEvent event) {
        applyFunc();
        cancelFunc();
    }


    /**
     * This method will handel the application of new settings.
     * @param event Listens to the click event.
     */
    @FXML
    private void handleApply(ActionEvent event) {
        applyFunc();
    }


    /**
     * This method sets the new settings into action. That means it saves them in the Properties file,
     * end also applies them on the system.
     */
    private void applyFunc() {
        Color bgColor = colorPalette.getValue();
        if(perHourRadio.isSelected()) {
            int perHour = numOfHoursPerHour.getValue();
            int perHourPrice = Integer.parseInt(pricePerHour.getText());
            fillSettings("perHour", perHour, perHourPrice, 0, bgColor);
        }
        else if (perDayRadio.isSelected()) {
            int perDayPrice = Integer.parseInt(pricePerDay.getText());
            fillSettings("perDay", 0, 0, perDayPrice, bgColor);
        }
        else if (hibridRadio.isSelected()) {
            int perHibridHour = numOfHoursHibrid.getValue();
            int perHibridHourPrice = Integer.parseInt(pricePerHourHibrid.getText());
            int perHibridDayPrice = Integer.parseInt(pricePerDayHibrid.getText());
            fillSettings("Hibrid", perHibridHour, perHibridHourPrice, perHibridDayPrice, bgColor);
        }

        try{
            settings.store(new FileOutputStream("TeMeReSettings.properties"), null);
        } catch (Exception ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        bgHelper.changeBGColorOwn(rootPane, toRGBCode(bgColor));
        bgHelper.changeBGColorOwn(MainController.MC.rootPane, toRGBCode(bgColor));
        applyBtn.setDisable(true);
    }


    /**
     * This method will fill up the globally created Properties object with the new settings.
     * @param type The type of paying system.
     * @param nOH The number of hours which are used to calculate prices in rounds.
     * @param pOH Price of an hourly unit in HUF.
     * @param pOD Price of a daily unit in HUF.
     * @param BGColor The background color of the windows.
     */
    private void fillSettings(String type, int nOH, int pOH, int pOD, Color BGColor){
        settings.setProperty("Type", type);
        settings.setProperty("NumOfHour", String.valueOf(nOH));
        settings.setProperty("PriceOfHour", String.valueOf(pOH));
        settings.setProperty("PriceOfDay", String.valueOf(pOD));
        settings.setProperty("Color", toRGBCode(BGColor));
        settings.setProperty("ExpTypes", expType);
    }


    /**
     * Converts the Color object into a String.
     * @param color A Color object from the ColorPicker.
     * @return A newly created String object from the Color object.
     */
    private String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }


    /**
     * Redirect the user to the Main window
     * @param event Listens to the click event
     */
    @FXML
    private void cancel(ActionEvent event) {
        cancelFunc();
    }


    /**
     * Redirect the user to the Main window.
     */
    private void cancelFunc() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    /**
     * This method sets up the initial arrangement of the Settings window. And also it initialize the values.
     */
    private void initValues(){
        String type = loadSettings.getProperty("Type");
        if(Objects.equals(type, "perHour")) {
            perHourRadio.setSelected(true);
            perDayRadio.setDisable(true);
            hibridRadio.setDisable(true);
            numOfHoursPerHour.setValue(Integer.parseInt(loadSettings.getProperty("NumOfHour")));
            numOfHoursPerHour.setDisable(false);
            pricePerHour.setText(loadSettings.getProperty("PriceOfHour"));
            pricePerHour.setDisable(false);
            colorPalette.setValue(Color.web(loadSettings.getProperty("Color")));
        } else if (Objects.equals(type, "perDay")) {
            perDayRadio.setSelected(true);
            perHourRadio.setDisable(true);
            hibridRadio.setDisable(true);
            pricePerDay.setText(loadSettings.getProperty("PriceOfDay"));
            pricePerDay.setDisable(false);
            colorPalette.setValue(Color.web(loadSettings.getProperty("Color")));
        } else if (Objects.equals(type, "Hibrid")) {
            hibridRadio.setSelected(true);
            perHourRadio.setDisable(true);
            perDayRadio.setDisable(true);
            numOfHoursHibrid.setValue(Integer.parseInt(loadSettings.getProperty("NumOfHour")));
            numOfHoursHibrid.setDisable(false);
            pricePerHourHibrid.setText(loadSettings.getProperty("PriceOfHour"));
            pricePerHourHibrid.setDisable(false);
            pricePerDayHibrid.setText(loadSettings.getProperty("PriceOfDay"));
            pricePerDayHibrid.setDisable(false);
            colorPalette.setValue(Color.web(loadSettings.getProperty("Color")));
        }
    }

}
