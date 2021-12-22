package MVC.Controller;

import MVC.Model.ClientSideServer;
import MVC.Model.User;
import MVC.View.LoginView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class LoginController implements LoginView.LoginViewListener{
    private final LoginListener listener;
    private final Stage stage;
    private LoginView loginView;
    private ClientSideServer server;

    public LoginController(LoginListener listener, Stage stage, ClientSideServer server){
        this.stage = stage;
        this.listener = listener;
        this.server = server;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginView.class.getResource("LoginView.fxml"));
        loader.load();
        loginView = loader.getController();
        loginView.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.setTitle("Messenger app");
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    @Override
    public void onLogInButton(String username, String password) throws Exception {
        String[] conn_pk = server.checkUser(username, password);
        if (Objects.equals(conn_pk[1], "true")){
            if (conn_pk[0].equals("false")){
                User user = new User(username, password);
                listener.onLoginAsked(user);
            }
            else {
                loginView.setErrorMessage("User already connected");
            }
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
