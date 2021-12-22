package MVC.Model;

import MVC.Controller.MessagesController;
import MVC.Controller.ServerListener;
import MVC.Model.Protocol.UserMessageProtocol;
import Utils.Crypto.AESUtils;
import Utils.Crypto.RSAUtils;
import Utils.Crypto.SignatureUtils;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientSideServer {
    private Socket connection;
    private PrintWriter out;
    private BufferedReader in;
    private ServerListener sv;
    private ExecutorService threadExecutor;
    private User user;
    private String key;
    private String iV;

    public ClientSideServer(String ip, int port) {
        try {
            this.connection = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.out = new PrintWriter(connection.getOutputStream(), true);
            this.in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            makeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void update(User usr, MessagesController mc) {
        threadExecutor = Executors.newSingleThreadExecutor();
        ServerListener serverHandler = new ServerListener(this, mc);
        threadExecutor.execute(serverHandler);
        this.user = usr;
        this.sv = serverHandler;
    }

    public void makeConnection() {
        try {
            String inputText = in.readLine();
            if (inputText == null) return;
            String[] parsedIn = inputText.split("[$]");
            if (parsedIn.length != 4 || !Objects.equals(parsedIn[0], "CON") || verifySignature(inputText)) return;
            this.key = AESUtils.generateKey();
            this.iV = AESUtils.generateIv();
            String message = UserMessageProtocol.symKeyMessage(key,iV);
            writeMessage(message);
            inputText = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    public void writeMessage(String message) {
        out.println(message);
        out.flush();
    }

    public boolean findUser(String username) {
        try {
            String message = UserMessageProtocol.tryUserName(username,key,iV);
            writeMessage(message);
            String inputText = in.readLine();
            if (inputText == null) return false;
            String[] parsedIn = inputText.split("[$]");
            String decryptedMSG = AESUtils.decryption(parsedIn[1],key,iV);
            parsedIn = decryptedMSG.split("[$]");
            return Boolean.parseBoolean(parsedIn[0]);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return false;
    }

    public String[] checkUser(String username, String password) {
        try {
            String message = UserMessageProtocol.login(username, password, key, iV);
            writeMessage(message);
            String inputText = in.readLine();
            if (inputText == null) return new String[]{"false","false"};
            String[] parsedIn = inputText.split("[$]");
            String decryptedMSG = AESUtils.decryption(parsedIn[1],key,iV);
            parsedIn = decryptedMSG.split("[$]");
            return new String[]{parsedIn[0],parsedIn[1]};
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return new String[]{"false","false"};
    }


    public ArrayList<String[]> getContacts() {
        try {
            String message = UserMessageProtocol.queryGetContacts(user);
            writeMessage(message);
            String[] response = sv.getQueryResponse();
            while (response == null) {
                TimeUnit.MILLISECONDS.sleep(10);
                response = sv.getQueryResponse();
            }
            ArrayList<String[]> res = new ArrayList<>();
            if (response.length < 4) return res;
            for (int i = 2; i < response.length-1; i += 2) {
                String[] newStr = new String[2];
                newStr[0] = response[i];
                newStr[1] = response[i+1];
                res.add(newStr);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return new ArrayList<>();
    }

    public void newMessage(String cur_receiver, String message) {
        try {
            String query = UserMessageProtocol.queryUserPk(user,cur_receiver);
            writeMessage(query);
            String[] response = sv.getQueryResponse();
            while (response == null) {
                TimeUnit.MILLISECONDS.sleep(10);
                response = sv.getQueryResponse();
            }
            String pk = response[2];
            String key = AESUtils.generateKey();
            String iV = AESUtils.generateIv();
            String newMessage = UserMessageProtocol.sendMessage(user,cur_receiver,pk,key,iV,message);
            writeMessage(newMessage);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public ArrayList<String> getMessages(String contact) {
        try {
            String query = UserMessageProtocol.queryMessages(user, contact);
            writeMessage(query);
            String[] response = sv.getQueryResponse();
            while (response == null) {
                TimeUnit.MILLISECONDS.sleep(10);
                response = sv.getQueryResponse();
            }
            ArrayList<String> res = new ArrayList<>();
            if (response[2].equals("NO_MESSAGE")) return res;
            for (int i=2; i < response.length - 1; i += 4) {
                String newMes = response[i] + "\n" + response[i+1] + "\n";
                String[] keyAndIv = RSAUtils.decrypt(response[i+3], RSAUtils.getPrivateKeyUser(user)).split("[$]");
                newMes += AESUtils.decryption(response[i+2],keyAndIv[0],keyAndIv[1]);
                res.add(newMes);
            }
            return res;

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return new ArrayList<>();
    }

    public void newUser(String username, String pw, String publickey) {
        try {
            String message = UserMessageProtocol.register(username, pw, publickey, key, iV);
            writeMessage(message);
            String inputText = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

    }

    public InputStream getStreamReader() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    public void closeConnection() {
        try {
            threadExecutor.shutdownNow();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verifySignature(String request) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (request == null) return false;
        String[] parsedRequest = request.split("[$]");
        if (parsedRequest.length < 2) return false;
        String signature = parsedRequest[parsedRequest.length-1];
        String signed = request.substring(0,request.lastIndexOf("$"));

        return SignatureUtils.verify(signed,signature, RSAUtils.getPublicKeyServer());
    }
}
