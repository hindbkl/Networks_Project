package MVC.View;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginView {

    @FXML
    private Label errorMessage;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField pwField;

    private LoginViewListener listener;

    public void setListener(LoginViewListener listener) {
        this.listener = listener;
    }

    public void onLogInButton() throws Exception {
        String username = usernameField.getText();
        String password = pwField.getText();
        listener.onLogInButton(username, password);
    }

    public void onRegisterLink() throws IOException {
        listener.onRegisterLink();
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage.setText(errorMessage);
        this.errorMessage.setVisible(true);
    }

    public interface LoginViewListener {
        void onLogInButton(String username, String password) throws Exception;
        void onRegisterLink() throws IOException;
    }
}
