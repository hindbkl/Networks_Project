package MVC.Model;

import MVC.Model.Protocol.ServerMessageProtocol;
import Utils.Crypto.PasswordUtils;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT = 9090;
    private final int MAX_USR = 4;
    private Database database;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private ExecutorService pool ;
    private ServerSocket listener;

    public static void main(String[] args) {
        launch(args);
    }

    private static void launch(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() throws IOException {
        database = new Database(new File("src/Utils/postgresql-config.txt"));
        String create_tables = database.getQuery(new File("src/Utils/generateTables.sql"));
        database.executeQuery(create_tables);
        listener = new ServerSocket(PORT);
        pool = Executors.newFixedThreadPool(MAX_USR);
    }

    private void closeConnections() {
        pool.shutdownNow();
    }

    public void writeMessage(String[] parsedRequest, String username) {
        addNewMessage(username,parsedRequest[1],parsedRequest[4], parsedRequest[2],parsedRequest[3]);
        if (isConnected(parsedRequest[1])) {
            for (ClientHandler users : clients) {
                if (users.getUserName() != null && users.getUserName().equals(parsedRequest[1])) {
                    String message = null;
                    try {
                        message = ServerMessageProtocol.sendMessage(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        users.closeConnection();
                    }
                    users.writeMessage(message);
                }
            }
        }
    }

    public void start() throws IOException {
        try {
            while (true) {
                System.out.println("[SERVER] Waiting for client connection...");
                Socket client = listener.accept();
                System.out.println("[SERVER] Connected to client!");
                ClientHandler clientThread = new ClientHandler(client,this);
                clients.add(clientThread);
                pool.execute(clientThread);
                PrintWriter out = new PrintWriter(client.getOutputStream());
                out.println("Connected");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
            listener.close();
        }
    }

    // -------------------------------------------- Manipulate users --------------------------------------------//
    public boolean findUser(String username){
        ArrayList<String> res = database.executeQuery("SELECT * FROM users WHERE username = '" + username + "';");
        if (res.size() != 0){
            return true;
        }
        return false;
    }

    public boolean isConnected(String username) {
        ArrayList<String> res = database.executeQuery("SELECT connected FROM users WHERE username = '" + username + "';");
        if (res.size() != 0){
            return Boolean.parseBoolean(res.get(0).split("\t")[0]);
        } else {
            return false;
        }
    }

    public boolean[] checkUser(String username, String password){
        ArrayList<String> res = database.executeQuery("SELECT connected, password FROM users WHERE username = '" + username + "';");
        String[] conn_pk = null;
        if (res.size() != 0){
            conn_pk = res.get(0).split("\t");
        } else {
            return null;
        }
        boolean[] result = new boolean[2];
        result[0] = Boolean.parseBoolean(conn_pk[0]);
        result[1] = PasswordUtils.authenticate(password,conn_pk[1]);
        return result;
    }

    public String getPublicKey(String username) {
        ArrayList<String> res = database.executeQuery("SELECT publickey FROM users WHERE username = '" +
                username + "';");
        if (res.size() != 0)
            return res.get(0);
        else
            return null;
    }

    public synchronized void updateUserStatus(String username, boolean connected){
        if (connected){
            database.executeQuery("UPDATE users SET connected = true WHERE username = '" + username + "';");
        }
        else {
            database.executeQuery("UPDATE users SET connected = false WHERE username = '" + username + "';");
        }
    }

    public synchronized void newUser(String username, String password, String publickey){
        database.executeQuery("INSERT into users (username, password, publickey, connected) values ('" + username + "','" + password + "','" + publickey + "', false);");
    }


    //-------------------------------------- Manipulate messages and contacts --------------------------------------//
    public String getContacts(String user){
        ArrayList<String> contacts = database.executeQuery("SELECT username, connected FROM users WHERE username <> '" + user + "'");
        String formatted = "";
        for (String res : contacts){
            String s = res.replace("\t","$");
            formatted += "$" + s;
        }

        return formatted;
    }

    public ArrayList<String> getMessages(String user, String contact) {
        /*ArrayList<String> result = database.executeQuery("SELECT sender, timestamp, content FROM messages WHERE (sender = '" + user
                + "' AND receiver = '" + contact + "') OR (receiver = '" + user + "' AND sender = '" + contact + "')"); //TODO : décrypter
        ArrayList<String> messages = new ArrayList<>();
        for (String s1 : result){
            String s2 = s1.replace("\t","\n");
            messages.add(s2);
        }
        return messages;*/
        ArrayList<String> result = database.executeQuery("SELECT sender, timestamp, content, sksender, skreceiver FROM messages WHERE (sender = '" + user
                + "' AND receiver = '" + contact + "') OR (receiver = '" + user + "' AND sender = '" + contact + "')");
        ArrayList<String> messages = new ArrayList<>();
        for (String s1 : result){
            String m = "";
            String[] s2 = s1.split("\t");
            m += s2[0] + "$" + s2[1] + "$" + s2[2] + "$";
            if (user.equals(s2[0])) {
                m += s2[3];
            } else {
                m += s2[4];
            }
            messages.add(m);
        }
        return messages;
    }



    public void newMessage(String user, String receiver, String message, String symKeySender, String symKeyReceiver){
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

    public synchronized void addNewMessage(String user, String receiver, String message, String SKS, String SKR){
        database.executeQuery("INSERT INTO messages (sender, receiver, content, timestamp, sksender, skreceiver) values ('" + user + "','" + receiver + "','" + message + "',CURRENT_TIMESTAMP(1),'" + SKS + "','" + SKR + "')");
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
