package MVC.Model;

import MVC.Model.Protocol.ServerMessageProtocol;
import Utils.Crypto.AESUtils;
import Utils.Crypto.PasswordUtils;
import Utils.Crypto.RSAUtils;
import Utils.Crypto.SignatureUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * Handles the communication between a user and the server in parallel with the other ones.
 *
 */

public class ClientHandler implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String userName = null;
    private Server server;
    private String publicKey;
    private boolean connected = true;

    ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.client = clientSocket;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            String[] symKey;
            // symmetric key shared by the user to start the login, in the same time the authenticity of the server is
            // verified.
            symKey = startCommunication();
            if (symKey == null) {
                closeConnection();
                return;
            }
            // login exchange messages
            login(symKey);
            publicKey = server.getPublicKey(userName);
            while (connected) {
                String request = in.readLine();
                if (verifySignature(request)) {
                    String[] parsedRequest = request.split("[$]");
                    if (Objects.equals(parsedRequest[0], "QUERY")) {
                        if (Objects.equals(parsedRequest[1], "MESSAGES") && parsedRequest.length == 5) {
                            returnMessages(parsedRequest[2]);
                        } else if (Objects.equals(parsedRequest[1], "USER") && parsedRequest.length == 5){
                            returnUser(parsedRequest[2]);
                        } else if (Objects.equals(parsedRequest[1], "PUBLIC") && parsedRequest.length == 5){
                            returnPublicKey(parsedRequest[2]);
                        } else if (Objects.equals(parsedRequest[1], "CONTACTS") && parsedRequest.length == 4){
                            returnContacts();
                        } else closeConnection();
                    } else if (Objects.equals(parsedRequest[0], "SEND") && parsedRequest.length == 7){
                        sendMessage(parsedRequest);
                    } else closeConnection();
                } else {
                    closeConnection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     *
     * Returns all the messages for a particular contact.
     *
     * @param userName
     * @throws Exception
     */

    private void returnMessages(String userName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        ArrayList<String> messages = server.getMessages(this.userName, userName);
        String message = ServerMessageProtocol.queryMessagesResponse(messages);
        writeMessage(message);
    }

    /**
     *
     * Returns true if a user is present in the database.
     *
     * @param userName
     * @throws Exception
     */

    private void returnUser(String userName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = ServerMessageProtocol.userQuery(server.findUser(userName));
        writeMessage(message);
    }

    /**
     *
     * Returns the public key of a user present in the database.
     *
     * @param userName
     * @throws Exception
     */

    private void returnPublicKey(String userName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String publicKey = server.getPublicKey(userName);
        String message = ServerMessageProtocol.publicKeyQuery(publicKey);
        writeMessage(message);
    }

    /**
     *
     * Sends a message to another user.
     *
     * @throws Exception
     */

    private void sendMessage(String[] parsedRequest) {
        server.writeMessage(parsedRequest, userName);
    }

    /**
     *
     * Returns the list of contacts.
     *
     * @throws Exception
     */

    private void returnContacts() throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = ServerMessageProtocol.sendContacts(server.getContacts(userName));
        writeMessage(message);
    }

    private boolean verifySignature(String request) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (request == null) return false;
        String[] parsedRequest = request.split("[$]");
        if (parsedRequest.length < 2) return false;
        String signature = parsedRequest[parsedRequest.length-1];
        String signed = request.substring(0,request.lastIndexOf("$"));

        return SignatureUtils.verify(signed,signature,RSAUtils.loadPbK(publicKey));
    }

    /**
     *
     * Handles the login/registration procedure.
     *
     * @param key
     * @throws Exception
     */
    private void login(String[] key) throws Exception {
        boolean registered = false;
        while (userName == null) {
            String inputText = in.readLine();
            if (inputText == null) return;
            String[] parsedIn = inputText.split("[$]");
            if (parsedIn.length != 2) return;
            String[] decryptedIn = AESUtils.decryption(parsedIn[1],key[0],key[1]).split("[$]");
            if (parsedIn[0].equals("REG") && decryptedIn.length == 2 && !registered) {
                handleRegistration1(decryptedIn[0], key);
            } else if (parsedIn[0].equals("REG") && decryptedIn.length == 4 && !registered) {
                registered = handleRegistration2(decryptedIn, key);
            } else if (parsedIn[0].equals("LOG") && decryptedIn.length == 3) {
                handleLogin(decryptedIn, key);
            } else return;

        }
    }

    /**
     *
     * Checks if the user gave the correct password
     *
     * @param decryptedIn decrypted message containing the userName and password
     * @param key keys used for decryption/encryption
     * @throws Exception
     */

    private void handleLogin(String[] decryptedIn, String[] key) throws Exception {
        boolean[] valid = server.checkUser(decryptedIn[0],decryptedIn[1]);
        if (!valid[0] && valid[1]) {
            userName = decryptedIn[0];
            server.updateUserStatus(userName,true);
        }
        String message = ServerMessageProtocol.loginValid(valid[0],valid[1],decryptedIn[0],key[0],key[1]);
        writeMessage(message);
    }

    /**
     *
     *  Response for the existence of a user query
     *
     * @param usrName user to be checked
     * @param key symmetric key and its initiated vector
     * @throws Exception
     */
    private void handleRegistration1(String usrName, String[] key) throws Exception {
        boolean validUserName = server.findUser(usrName);
        String message = ServerMessageProtocol.nameValidity(validUserName, usrName, key[0], key[1]);
        writeMessage(message);
    }
    /**
     *
     *  Response for a registration attempt
     *
     * @param decryptedIn decrypted message containing the userName and password
     * @param key symmetric key and its initiated vector
     * @throws Exception
     */
    private boolean handleRegistration2(String[] decryptedIn, String[] key) throws Exception {
        boolean validUserName = server.findUser(decryptedIn[0]);
        String message = ServerMessageProtocol.nameValidity(validUserName, decryptedIn[0], key[0], key[1]);
        server.newUser(decryptedIn[0], PasswordUtils.hashPassword(decryptedIn[1]),decryptedIn[2]);
        writeMessage(message);
        return true;
    }

    /**
     *
     *  Starts the authentication of the server and gets the symmetric key for further usage
     *
     * @return keyAndIv returns the symmetric key and the initiated vector shared by the user
     */

    private String[] startCommunication() {
        String[] symKey = null;
        try {
            String ip = getAddress();
            if (ip == null) return null;
            String message = ServerMessageProtocol.authMessage(ip);
            writeMessage(message);
            String inputText = in.readLine();
            if (inputText == null) return null;
            String[] parsedIn = inputText.split("[$]");
            if (parsedIn.length != 2 || !Objects.equals(parsedIn[0], "SEC")) return null;
            symKey = RSAUtils.decrypt(parsedIn[1],RSAUtils.getPrivateKeyServer()).split("[$]");
            if (symKey.length != 2) return null;
            writeMessage(ServerMessageProtocol.confSecret(symKey[0],symKey[1],ip));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return symKey;
    }

    /**
     *
     * Gets the Ip address
     *
     * @return ip The ip address of the user
     */

    private String getAddress() {
        SocketAddress socketAddress = client.getRemoteSocketAddress();

        if (socketAddress instanceof InetSocketAddress) {
            InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
            if (inetAddress instanceof Inet4Address)
                return inetAddress.toString();
            else if (inetAddress instanceof Inet6Address)
                return inetAddress.toString();
            else
                return null;
        } else {
            return null;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void writeMessage(String message) {
        out.println(message);
        out.flush();
    }

    public void closeConnection() {
        try {
            connected = false;
            if (userName != null) {
                server.updateUserStatus(userName,false);
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
