package MVC.Controller;

import MVC.Model.Server;
import MVC.Model.User;
import MVC.View.MessagesView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class MessagesController implements MessagesView.MessageViewListener {

    private final MessagesListener listener;
    private final Stage stage;
    private User user;
    private MessagesView messagesView;
    private Server server;
    private ArrayList<String[]> contacts;
    private String cur_receiver = null;

    public MessagesController(MessagesListener listener, Stage stage, User user, Server server) {
        this.stage = stage;
        this.listener = listener;
        this.user = user;
        this.server = server;
        contacts = server.getContacts(user.getUsername());
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(MessagesView.class.getResource("MessagesView.fxml"));
        loader.load();
        messagesView = loader.getController();
        messagesView.showContacts(contacts);
        messagesView.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
        onCloseRequest();
    }

    public void hide() {
        stage.hide();
    }

    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            // disconnect user
            server.updateUserStatus(user.getUsername(), false);
        });
    }

    @Override
    public void onSendButton(String message) {
        if (cur_receiver != null){
            server.newMessage(user.getUsername(), cur_receiver, message);
            onContact(cur_receiver);
        }
    }

    @Override
    public void onSearchButton(String searched) {
        if (!searched.equals("")){
            ArrayList<String[]> result = new ArrayList<>();
            for (String[] c : contacts){
                if (c[0].contains(searched))
                    result.add(c);
            }
            messagesView.showContacts(result);
        }
        else
            messagesView.showContacts(contacts);
        stage.show();
        System.out.println(searched);
    }

    @Override
    public void onLogoutButton() throws Exception {
        server.updateUserStatus(user.getUsername(), false);
        listener.onLogoutAsked();
    }

    @Override
    public void onContact(String contact) {
        cur_receiver = contact;
        ArrayList<String> messages = server.getMessages(user.getUsername(), contact);
        messagesView.showMessages(messages);
        stage.show();
    }

    public interface MessagesListener {
        void onLogoutAsked() throws IOException;
    }
}
