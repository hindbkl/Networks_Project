package Main;

import Controller.HeadController;
import Model.Database;
import Model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application implements HeadController.HeadListener {

    private HeadController headController;
    private Stage stage;
    private User user;
    // Database
    private final String CONFIG_FILE = "src/Utils/postgresql-config.txt";
    private final String TABLES_FILE = "src/Utils/generateTables.sql" ;
    private Database database;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        loadDB();

        this.stage = stage;
        headController = new HeadController(this, stage);
        headController.show();
    }

    public static void showError(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error when trying to open " + type + ".");
        alert.setContentText("Sorry for the inconvenience. The program will now terminate.");
        alert.showAndWait();
        Platform.exit();
    }

    @Override
    public void logIn(User user) throws IOException {
        this.user = user;
    }

    @Override
    public void logOut() {
        System.out.println("log out user");
        //log out user from db
    }

    /* TODO
    @Override
    public void closePage() {
        if (user.isConnected()){
            user.setConnected(false);
        }
    }*/

    public void loadDB() {
        database = new Database(new File(CONFIG_FILE));
        String create_tables = database.getQuery(new File(TABLES_FILE));
        database.executeQuery(create_tables);
    }
}
