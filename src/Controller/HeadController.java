package Controller;

import Model.Database;
import Model.User;
import javafx.stage.Stage;
import Main.Main;
import java.io.IOException;

public class HeadController implements LoginController.LoginListener, RegisterController.RegisterListener, MessagesController.MessagesListener {
    private final HeadListener listener;
    private final Stage stage;
    private LoginController loginController;
    private RegisterController registerController;
    private MessagesController messagesController;
    private Database database;

    public HeadController(HeadListener listener, Stage stage, Database database) {
        this.listener = listener;
        this.stage = stage;
        this.database = database;
    }

    public void show() {
        loginController = new LoginController(this, stage, database);
        try {
            loginController.show();
        } catch (IOException e) {
            Main.showError("log in page");
        }
    }
    @Override
    public void onLoginAsked(User user) throws IOException {
        listener.logIn(user);
        messagesController = new MessagesController(this, stage, user, database);
        try {
            messagesController.show();
        } catch (IOException e) {
        Main.showError("messages page");
        }
    }

    @Override
    public void onRegisterLinkAsked() {
        registerController = new RegisterController(this, stage, database);
        try {
            registerController.show();
        } catch (IOException e) {
            Main.showError("registration page");
        }

    }

    @Override
    public void onLogoutAsked() throws IOException {
        loginController.show();
        listener.logOut();
    }

    @Override
    public void onRegisterAsked() {
        try {
            loginController.show();
        } catch (IOException e) {
            Main.showError("log in page");
        }
    }

    @Override
    public void onBackToLoginAsked() {
        try {
            loginController.show();
        } catch (IOException e) {
            Main.showError("log in page");
        }
    }

    public interface HeadListener {
        void logIn(User user) throws IOException;
        void logOut() throws IOException;
    }
}
