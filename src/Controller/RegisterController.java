package Controller;

import Model.Database;
import Model.User;
import View.RegisterView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class RegisterController implements RegisterView.RegisterViewListener {
    private final RegisterListener listener;
    private final Stage stage;
    private RegisterView registerView;
    private Database database;

    public RegisterController(RegisterListener listener, Stage stage, Database database) {
        this.stage = stage;
        this.listener = listener;
        this.database = database;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(RegisterView.class.getResource("RegisterView.fxml"));
        loader.load();
        registerView = loader.getController();
        registerView.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    @Override
    public void onRegisterButton(String username, String pw, String confirmpw) throws Exception {
        if (!User.checkValidUsername(username)){
            registerView.setErrorMessage("Username must :\n- start by a letter\n- only contain letters and digits\n- contain at least 8 characters");
        }
        else if (!User.checkValidPw(pw)){
            registerView.setErrorMessage("Password must contain at least:\n- 8 characters\n- 1 digit\n- 1 capital letter\n- 1 special character");
        }
        else if (!pw.equals(confirmpw)){
            registerView.setErrorMessage("Passwords should be the same");
        }
        else {
            ArrayList<String> userFound = database.executeQuery("SELECT username FROM users WHERE username = '" + username + "';");
            if (userFound.size() != 0){
                registerView.setErrorMessage("Username already exists.");
            }
            else {
                System.out.println("create user : " + username);
                User user = new User(username, pw);
                database.executeQuery("INSERT into users (username, password, publickey, connected) values ('" + username + "','" + pw + "','" + user.getPublickey() + "', false);");
                listener.onRegisterAsked();
            }
        }
    }

    @Override
    public void onBackButton() throws IOException {
        listener.onBackToLoginAsked();
    }

    public interface RegisterListener {
        void onRegisterAsked() throws IOException;
        void onBackToLoginAsked() throws IOException;
    }
}
