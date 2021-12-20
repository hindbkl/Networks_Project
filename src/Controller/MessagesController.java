package Controller;

import Model.Messages;
import Model.User;
import View.MessagesView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MessagesController implements MessagesView.MessageViewListener {

    private final MessagesListener listener;
    private final Stage stage;
    private User user;
    private MessagesView messagesView;

    public MessagesController(MessagesListener listener, Stage stage, User user) {
        this.stage = stage;
        this.listener = listener;
        this.user = user;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(MessagesView.class.getResource("MessagesView.fxml"));
        loader.load();
        messagesView = loader.getController();
        messagesView.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void hide() {
        stage.hide();
    }


    @Override
    public void onSendButton(String message) throws Exception {
        // va falloir changer pour que ça prenne message, sender, receiver, timestamp
        // save msg in db
        // reload view w/ msg in it
        System.out.println(message);
        listener.onSendAsked();
    }

    @Override
    public void onSearchButton(String searched) throws Exception {
        System.out.println(searched);
        listener.onSearchAsked();
    }

    @Override
    public void onLogoutButton() throws Exception {
        listener.onLogoutAsked();
        //log out user from db
    }

    public interface MessagesListener {
        void onSendAsked() throws IOException;
        void onSearchAsked() throws IOException;
        void onLogoutAsked() throws IOException;
    }
}
