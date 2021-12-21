package MVC.Model;

import java.io.File;
import java.util.ArrayList;

public class Server {
    private Database database;

    public Server(){
        database = new Database(new File("src/Utils/postgresql-config.txt"));
        String create_tables = database.getQuery(new File("src/Utils/generateTables.sql"));
        database.executeQuery(create_tables);
    }

    // -------------------------------------------- Manipulate users --------------------------------------------//
    public boolean findUser(String username){
        ArrayList<String> res = database.executeQuery("SELECT * FROM users WHERE username = '" + username + "';");
        if (res.size() != 0){
            return true;
        }
        return false;
    }

    public String[] checkUser(String username, String password){
        ArrayList<String> res = database.executeQuery("SELECT connected, publickey FROM users WHERE username = '" + username + "' AND password = '" + password + "';");
        String[] conn_pk = null;
        if (res.size() != 0){
            conn_pk = res.get(0).split("\t");
        }
        return conn_pk;
    }

    public void updateUserStatus(String username, boolean connected){
        if (connected){
            database.executeQuery("UPDATE users SET connected = true WHERE username = '" + username + "';");
        }
        else {
            database.executeQuery("UPDATE users SET connected = false WHERE username = '" + username + "';");
        }
    }

    public void newUser(String username, String password, String publickey){
        database.executeQuery("INSERT into users (username, password, publickey, connected) values ('" + username + "','" + password + "','" + publickey + "', false);");
    }


    //-------------------------------------- Manipulate messages and contacts --------------------------------------//
    public ArrayList<String[]> getContacts(String user){
        ArrayList<String> contacts = database.executeQuery("SELECT username, connected FROM users WHERE username <> '" + user + "'");
        ArrayList<String[]> formatted = new ArrayList<>();
        for (String res : contacts){
            String[] s = res.split("\t");
            formatted.add(s);
        }

        return formatted;
    }

    public ArrayList<String> getMessages(String user, String contact) {
        ArrayList<String> result = database.executeQuery("SELECT sender, timestamp, content FROM messages WHERE (sender = '" + user
                + "' AND receiver = '" + contact + "') OR (receiver = '" + user + "' AND sender = '" + contact + "')"); //TODO : décrypter
        ArrayList<String> messages = new ArrayList<>();
        for (String s1 : result){
            String s2 = s1.replace("\t","\n");
            messages.add(s2);
        }
        return messages;
    }

    public void newMessage(String user, String receiver, String message){
        ArrayList<String> res = database.executeQuery("SELECT sender, sksender, skreceiver FROM messages WHERE (sender = '" + user
                + "' AND receiver = '" + receiver + "') OR (receiver = '" + user + "' AND sender = '" + receiver + "')");
        if (res.size() != 0){ //if both already communicated once -> keep same symmetric key
            String sender, sksender, skreceiver;
            String[] s = res.get(0).split("\t");
            sender = s[0]; sksender = s[1]; skreceiver = s[2];
            String SKS,SKR;
            if (sender.equals(user)) {
                SKS = sksender; SKR = skreceiver; }
            else {
                SKS = skreceiver; SKR = sksender; }

            database.executeQuery("INSERT INTO messages (sender, receiver, content, timestamp, sksender, skreceiver) values ('" + user + "','" + receiver + "','" + message + "',CURRENT_TIMESTAMP(1),'" + SKS + "','" + SKR + "')");
        }
        else{ //else -> create new symmetric key
            //TODO : générer nouvelles SKS et SKR
            database.executeQuery("INSERT INTO messages (sender, receiver, content, timestamp, sksender, skreceiver) values ('" + user + "','" + receiver + "','" + message + "',CURRENT_TIMESTAMP(1),'183742774372','84357457943')");
        }
    }

}
//  TODO : effacer tout ça
// encrypter msg (each user has public + private key)
// convo encrypted w/ symmetric key
/*
1) sender : encrypter msg avec clé sym qu'il connait
2) sender : encrypter clé sym (avec la clé publique du receiver)
3) receiver : decrypter clé sym (avec sa clé privée)
4) receiver : decrypter msg (avec clé sym qu'il vient de décrypter)

sender | receiver | message        | timestamp | sym key as sender | sym key as receiver
----------------------------------------------------------------------------------------
maciej | hind     | encr_sym(msg)  | ts        | sym key pk sender | sym key pk receiver
hind   | maciej   |


username | pw | public key | connected
--------------------------------------

et chaque username connait sa propre private key

*/
