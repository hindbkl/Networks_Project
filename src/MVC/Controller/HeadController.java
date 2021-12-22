package MVC.Controller;

import MVC.Main;
import MVC.Model.ClientSideServer;
import MVC.Model.User;
import javafx.stage.Stage;

import java.io.IOException;

public class HeadController implements LoginController.LoginListener, RegisterController.RegisterListener, MessagesController.MessagesListener {
    private final HeadListener listener;
    private final Stage stage;
    private LoginController loginController;
    private RegisterController registerController;
    private MessagesController messagesController;
    private ClientSideServer server;

    public HeadController(HeadListener listener, Stage stage, ClientSideServer server) {
        this.listener = listener;
        this.stage = stage;
        this.server = server;
    }

    public void show() {
        loginController = new LoginController(this, stage, server);
        try {
            loginController.show();
        } catch (IOException e) {
            Main.showError("log in page");
        }
    }
    @Override
    public void onLoginAsked(User user) {
        messagesController = new MessagesController(this, stage, user, server);
        server.update(user, messagesController);
        try {
            messagesController.show();
        } catch (IOException e) {
        Main.showError("messages page");
        }
    }

    @Override
    public void onRegisterLinkAsked() {
        registerController = new RegisterController(this, stage, server);
        try {
            registerController.show();
        } catch (IOException e) {
            Main.showError("registration page");
        }
    }

    @Override
    public void onLogoutAsked() throws IOException {
        loginController.show();
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
    }
}
