package MVC.Controller;

import MVC.Model.ClientSideServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Listens for the incoming messages from the server
 */

public class ServerListener implements Runnable {
    private String[] queryResponse = null;
    private ClientSideServer server;
    private MessagesController messagesController;
    private boolean running = true;
    private BufferedReader in;

    public ServerListener(ClientSideServer server, MessagesController messagesController) {
        this.server = server;
        this.messagesController = messagesController;
        this.in = new BufferedReader( new InputStreamReader(server.getStreamReader()));
    }

    public String[] getQueryResponse() {
        String[] message = queryResponse;
        if (message == null) {
            setQuery(null);
        }
        return message;
    }

    private synchronized void setQuery(String[] value){
        queryResponse = null;
    }


    @Override
    public void run() {
        while (running) {
            try {
                String input = in.readLine();
                if (in == null) return;
                if (server.verifySignature(input)) {
                    String[] parsedIn = input.split("[$]");
                    // if the message starts with a "SEND", the messages Controler is notified
                    if (Objects.equals(parsedIn[0], "SEND") && parsedIn.length == 3) {
                        messagesController.notify(parsedIn[1]);
                        // if the message starts with a "QUERY" it is stored in a variable
                    } else if (Objects.equals(parsedIn[0], "QUERY")) {
                        setQuery(parsedIn);
                    } else {
                        running = false;
                    }
                } else {
                    running = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                running = false;
            }
        }
    }
}
