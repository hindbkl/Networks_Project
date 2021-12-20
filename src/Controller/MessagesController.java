package Controller;

import Model.Database;
import Model.User;
import View.MessagesView;
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
    private Database database;
    private ArrayList<String[]> contacts;
    private String cur_receiver = null;

    public MessagesController(MessagesListener listener, Stage stage, User user, Database database) {
        this.stage = stage;
        this.listener = listener;
        this.user = user;
        this.database = database;
        contacts = database.getContacts(user.getUsername());
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
            database.executeQuery("UPDATE users SET connected = false WHERE username = '" + user.getUsername() + "';");
        });
    }

    @Override
    public void onSendButton(String message) {
        System.out.println(message);
        if (cur_receiver != null){
            ArrayList<String> res = database.executeQuery("SELECT sender, sksender, skreceiver FROM messages WHERE (sender = '" + user
                    + "' AND receiver = '" + cur_receiver + "') OR (receiver = '" + user + "' AND sender = '" + cur_receiver + "')");
            if (res.size() != 0){ //if both already communicated once -> keep same symmetric key
                String sender, sksender, skreceiver;
                String[] s = res.get(0).split("\t");
                sender = s[0]; sksender = s[1]; skreceiver = s[2];
                String SKS,SKR;
                if (sender.equals(user.getUsername())) {
                    SKS = sksender; SKR = skreceiver; }
                else {
                    SKS = skreceiver; SKR = sksender; }

                database.executeQuery("INSERT INTO messages (sender, receiver, content, timestamp, sksender, skreceiver) values ('" + user.getUsername() + "','" + cur_receiver + "','" + message + "',CURRENT_TIMESTAMP(1),'" + SKS + "','" + SKR + "')");
            }
            else{ //else -> create new symmetric key
                //TODO : générer nouvelles SKS et SKR
                database.executeQuery("INSERT INTO messages (sender, receiver, content, timestamp, sksender, skreceiver) values ('" + user.getUsername() + "','" + cur_receiver + "','" + message + "',CURRENT_TIMESTAMP(1),'183742774372','84357457943')");
            }
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
        database.executeQuery("UPDATE users SET connected = false WHERE username = '" + user.getUsername() + "';");
        listener.onLogoutAsked();
    }

    @Override
    public void onContact(String contact) {
        cur_receiver = contact;
        ArrayList<String> messages = database.getMessages(user.getUsername(), contact);
        messagesView.showMessages(messages);
        stage.show();
    }

    public interface MessagesListener {
        void onLogoutAsked() throws IOException;
    }
}
