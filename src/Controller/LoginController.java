package Controller;

import Model.Database;
import Model.User;
import View.LoginView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class LoginController implements LoginView.LoginViewListener{
    private final LoginListener listener;
    private final Stage stage;
    private LoginView loginView;
    private Database database;

    public LoginController(LoginListener listener, Stage stage, Database database){
        this.stage = stage;
        this.listener = listener;
        this.database = database;
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
        ArrayList<String> userFound = database.executeQuery("SELECT publickey FROM users WHERE username = '" + username + "' AND password = '" + password + "';");
        if (userFound.size() != 0){
            String connected = database.executeQuery("SELECT connected FROM users WHERE username = '" + username + "';").get(0);
            if (connected.replace("\t", "").equals("f")){
                String pk = userFound.get(0).replace("\t", "");
                User user = new User(username, password, pk);
                database.executeQuery("UPDATE users SET connected = true WHERE username = '" + user.getUsername() + "';");
                listener.onLoginAsked(user);
            }
            else
                loginView.setErrorMessage("User already connected");
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
