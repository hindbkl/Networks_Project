package View;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;

public class MessagesView {
    @FXML
    private TextField searchField, messageField;
    @FXML
    private ListView<String> messageDisplay, contactsView;

    private MessageViewListener listener;

    public void setListener(MessageViewListener messageViewListener) {
        this.listener = messageViewListener;
    }

    public void showContacts(ArrayList<String[]> contacts){
        contactsView.getItems().remove(0, contactsView.getItems().size()); //clean ListView
        for (String[] i : contacts) //refill ListView
            contactsView.getItems().add(i[0] + " (" + i[1] + ")");
    }

    public void showMessages(ArrayList<String> messages){
        messageDisplay.getItems().remove(0, messageDisplay.getItems().size()); //clean ListView
        for (String i : messages){ //refill ListView
            messageDisplay.getItems().add(i);
        }
    }

    public String selectedContact(){
        String contact = contactsView.getSelectionModel().getSelectedItem();
        if (contact != null)
            contact = contact.split(" ")[0]; //keep the user without the (online/offline)
            listener.onContact(contact);
        return contact;
    }

    public void onSendButton() throws Exception {
        String message = messageField.getText();
        if (!message.equals(""))
            listener.onSendButton(message);
        messageField.setText("");
    }

    public void onSearchButton() throws Exception {
        String searched = searchField.getText();
        listener.onSearchButton(searched);
    }

    public void onLogOutButton() throws Exception {
        listener.onLogoutButton();
    }

    public interface MessageViewListener {
        void onSendButton(String message) throws Exception;
        void onSearchButton(String searched) throws Exception;
        void onLogoutButton() throws Exception;
        void onContact(String contact);
    }
}

