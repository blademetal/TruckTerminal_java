package classes;

import database.DatabaseHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import ui.main.MainController;

import java.sql.SQLException;
import java.util.*;

/**
 * The ShiftTimer class extends the TimerTask abstract class, and implements the Runnable interface.
 * It's aim is to initialize a new Shift every morning at 08:00 AM.
 * If the user exits from the program, there will remain a daemon thread which will run until 08:00 AM,
 * to initialize a new Shift. After it does this job, the software will shot down.
 */
public class ShiftTimer extends TimerTask {

    /**
     * A global DatabaseHandler object, which will be used in the run() method.
     */
    private DatabaseHandler databaseHandler = null;

    /**
     * This method creates a new thread which will create a new Shift object if the conditions indicates that it should doo that.
     */
    @Override
    public void run() {
        Platform.runLater(() -> {
            try {
                databaseHandler = DatabaseHandler.getInstance();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Utilities utilities = new Utilities();

            if(!utilities.isShiftDataAvailable()) {
                insertShiftData();
            } else if(utilities.isShiftDataAvailable() && utilities.shouldCreateShift()) {
                insertShiftData();
            }
        });
    }


    /**
     * This method creates the SQLite command to insert new Shift object into the database, and the method does th insertion
     * in an if condition where, if the insertion was successful the software shows confirmation, if no it alerts error.
     */
    private void insertShiftData() {
        String act = "INSERT INTO Shift_Data VALUES (" +
                "NULL, " +
                "'" + 0 + "'," +
                "'" + 0 + "'," +
                "'" + 0 + "'," +
                "'" + 0 + "'," +
                "'" + 0 + "'," +
                "'" + databaseHandler.getShiftStartDate() + " 08:00:00" + "'," +
                "'" + databaseHandler.getShiftTomorrowDate() + " 07:59:59" + "'" +
                ")";

        System.out.println(act);

        if (databaseHandler.execAction(act)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Sikeresen inicializáltuk a következő műszakot.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("A következő műszak inicializálása sikertelen!");
            alert.showAndWait();
        }

        System.out.println("Start Job");
        System.out.println("End Job" + System.currentTimeMillis());

        MainController.MC.refreshTablesFunc();
        MainController.MC.refreshRevPrices();
    }
}
