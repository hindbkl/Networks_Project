package Main;

import Controller.HeadController;
import Controller.MessagesController;
import Model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application implements HeadController.HeadListener {

    private HeadController headController;
    private MessagesController messagesController;
    private Stage stage;
    private User user;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
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
    public void logIn(User user) throws IOException {
        this.user = user;
    }

    @Override
    public void logOut() {
        System.out.println("log out user");
        //log out user from db
    }

    //TODO : rajouter onClose()

}
