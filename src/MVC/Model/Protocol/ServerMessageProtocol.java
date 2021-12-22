package MVC.Model.Protocol;

import Utils.Crypto.AESUtils;
import Utils.Crypto.SignatureUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Message constructors to be sent to the user. Form more information see where they are called.
 */

public class ServerMessageProtocol {
    public static String authMessage(String ip) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "CON$" + new Date() + "$" + ip + "$";
        String signature = SignatureUtils.signServer(message);
        message += signature;
        return message;
    }

    public static String confSecret(String key, String iV, String ip) throws Exception {
        String message = new Date() + "$" + ip;
        String encryptedM = AESUtils.encryption(message, key, iV);
        return "CON$" + encryptedM;
    }

    public static String nameValidity(boolean valid, String login, String key, String iV) throws Exception {
        String message = valid + "$" + login + "$" + new Date() ;
        String encryptedM =  AESUtils.encryption(message, key, iV);
        return "REGR$" + encryptedM;
    }

    public static String loginValid(boolean logged,boolean valid, String login, String key, String iV) throws Exception {
        String message = logged + "$" + valid + "$" + login + "$" + new Date();
        String encryptedM =  AESUtils.encryption(message, key, iV);
        return "LOGR$" + encryptedM;
    }

    public static String publicKeyQuery(String pk) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERYR$PUBLIC$" + pk + "$" + new Date();
        String signature = SignatureUtils.signServer(message);
        return message + "$" + signature;
    }

    public static String userQuery(boolean exist) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERYR$USER$" + exist + "$" + new Date();
        String signature = SignatureUtils.signServer(message);
        return message + "$" + signature;
    }

    public static String queryMessagesResponse(ArrayList<String> messages) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERYR$MESSAGE";
        if (messages.size() == 0) {
            message += "$NO_MESSAGE";
        } else {
            for (String m : messages) {
                message += "$" + m;
            }
        }
        String signature = SignatureUtils.signServer(message);
        return message + "$" + signature;
    }

    public static String sendMessage(String sourceUsr) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "SEND$";
        message += sourceUsr;
        String signature = SignatureUtils.signServer(message);
        return message + "$" + signature;
    }

    public static String sendContacts(String contacts) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERY$CONTACTS";
        message += contacts;
        String signature = SignatureUtils.signServer(message);
        return message + "$" + signature;
    }
}
