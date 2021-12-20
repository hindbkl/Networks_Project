package View;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MessagesView {
    @FXML
    private TextField searchField;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button searchButton;

    private MessageViewListener listener;

    public void setListener(MessageViewListener messageViewListener) {
        this.listener = messageViewListener;
    }

    public void onSendButton() throws Exception {
        String message = messageField.getText();
        listener.onSendButton(message);
    }

    public void onSearchButton() throws Exception {
        String searched = searchField.getText();
        listener.onSearchButton(searched);
    }

    public interface MessageViewListener {
        void onSendButton(String message) throws Exception;
        void onSearchButton(String searched) throws Exception;
    }
}

