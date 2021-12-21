package MVC.View;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class RegisterView {
    @FXML
    private Label errorMessage;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField pwField;
    @FXML
    private PasswordField confirmPwField;

    private RegisterViewListener listener;

    public void setListener(RegisterViewListener registerViewListener) {
        this.listener = registerViewListener;
    }

    public void onRegisterButton() throws Exception {
        String username = usernameField.getText();
        String pw = pwField.getText();
        String confirmpw = confirmPwField.getText();
        listener.onRegisterButton(username, pw, confirmpw);
    }

    public void onBackButton() throws IOException {
        listener.onBackButton();
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage.setText(errorMessage);
        this.errorMessage.setVisible(true);
    }

    public interface RegisterViewListener {
        void onRegisterButton(String username, String pw, String confirmpw) throws Exception;
        void onBackButton() throws IOException;
    }
}

