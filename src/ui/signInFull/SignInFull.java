package ui.signInFull;

import database.DatabaseHandler;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import ui.main.MainController;

import java.sql.SQLException;

/**
 *  This class is the starting point of the program. It creates the first connection with the database.
 * The class also holds the informations connected to it's appearance.
 */
public class SignInFull extends Application {

    /**
     * This is the starting method of the software.
     * @param primaryStage It requires a Stage object as input.
     * @throws Exception If some Exception happens, it handles it on a broad range.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SignInFull.fxml"));
        primaryStage.setTitle("Bejelentkezés");
        primaryStage.getIcons().add(new Image(MainController.class.getResourceAsStream("../icons/delivery-truck.png")));
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseHandler.getInstance();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
