package classes;

import javafx.scene.layout.Pane;
import ui.main.MainController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *  This class is in charge with changing the background color of a window's root pane, is settings are changed.
 * The color data is obtainable from a properties file.
 */
public class BackgroundHelper {

    /**
     * Instantiate a new Properties object.
     */
    private Properties loadSettings = new Properties();

    /**
     * This String will hold globally the background color's hexadecimal code.
     */
    private String bgColor;

    /**
     * The construction method of this class
     */
    public BackgroundHelper() {
        try {
            loadSettings.load(new FileInputStream("TeMeReSettings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBgColor() {
        return loadSettings.getProperty("Color");
    }

    /**
     * Changes the background color of the root pane
     * @param pane The Main window's root pane which color will be altered
     */
    public void changeBGColor(Pane pane) {
        pane.setStyle("-fx-background-color: " + loadSettings.getProperty("Color") + ";");
    }

    /**
     * Changes the background color of the window's own pane
     * @param pane The used window's pane which color will be altered
     * @param color The new color which will be used
     */
    public void changeBGColorOwn(Pane pane, String color) {
        pane.setStyle("-fx-background-color: " + color + ";");
    }
}
