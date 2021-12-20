package View;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MessagesView {
    @FXML
    private TextField searchField;
    @FXML
    private TextField messageField;

    private MessageViewListener listener;

    public void setListener(MessageViewListener messageViewListener) {
        this.listener = messageViewListener;
    }

    public void onSendButton() throws Exception {
        String message = messageField.getText();
        if (!message.equals(""))
            listener.onSendButton(message);
    }

    public void onSearchButton() throws Exception {
        String searched = searchField.getText();
        if (!searched.equals(""))
            listener.onSearchButton(searched);
    }

    public void onLogOutButton() throws Exception {
        listener.onLogoutButton();
    }

    public interface MessageViewListener {
        void onSendButton(String message) throws Exception;
        void onSearchButton(String searched) throws Exception;
        void onLogoutButton() throws Exception;
    }
}

