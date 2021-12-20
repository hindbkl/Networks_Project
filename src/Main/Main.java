package Main;

import Controller.HeadController;
import Model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application implements HeadController.HeadListener {

    private HeadController headController;
    private Stage stage;
    private User user;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //TODO : load DBs
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
    public void logIn(User user) {
        this.user = user;
        headController.show();
    }

    //TODO : rajouter onClose()

}
