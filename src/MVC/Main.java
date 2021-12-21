package MVC;

import MVC.Controller.HeadController;
import MVC.Model.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application implements HeadController.HeadListener {

    private HeadController headController;
    private Server server;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        server = new Server();
        headController = new HeadController(this, stage, server);
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
}
