package Controller;

import Model.User;
import View.LoginView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController implements LoginView.LoginViewListener{
    private final LoginListener listener;
    private final Stage stage;
    private LoginView loginView;

    public LoginController(LoginListener listener, Stage stage){
        this.stage = stage;
        this.listener = listener;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginView.class.getResource("LoginView.fxml"));
        loader.load();
        loginView = loader.getController();
        loginView.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.setTitle("Messenger app"); //TODO : change name
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    @Override
    public void onLogInButton(String username, String password) throws Exception {
        // if user in db -> connect
        if (1 == 1){
            User user = new User("hind", "hindpw");
            listener.onLoginAsked(user);
        }
        else {
            loginView.setErrorMessage("Username or password incorrect");
        }
    }

    @Override
    public void onRegisterLink() throws IOException {
        listener.onRegisterLinkAsked();
    }

    public interface LoginListener {
        void onLoginAsked(User user) throws IOException;
        void onRegisterLinkAsked() throws IOException;
    }
}
