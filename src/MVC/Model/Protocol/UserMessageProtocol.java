package MVC.Model.Protocol;

import MVC.Model.User;
import Utils.Crypto.AESUtils;
import Utils.Crypto.RSAUtils;
import Utils.Crypto.SignatureUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;

/**
 * Message constructors to be sent to the server. Form more information see where they are called.
 */

public class UserMessageProtocol {
    public static String symKeyMessage(String key, String iV) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String message = "SEC$";
        String eKeyAndIv = RSAUtils.encrypt(key + "$" + iV, RSAUtils.getPublicKeyServer());
        return message + eKeyAndIv;
    }

    public static String tryUserName(String usrName, String key, String iV) throws Exception {
        return "REG$" + AESUtils.encryption(usrName + "$" + new Date(), key, iV);
    }

    public static String register(String usrName, String password, String pk, String key, String iV) throws Exception {
        String message = usrName + "$" + password + "$" + pk + "$" + new Date();
        return "REG$" + AESUtils.encryption(message, key, iV);
    }

    public static String login(String usrName, String password, String key, String iV) throws Exception {
        String message = usrName + "$" + password + "$" + new Date();
        return "LOG$" + AESUtils.encryption(message, key, iV);
    }

    public static String queryMessages(User user, String userName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERY$MESSAGES$" + userName + "$" + new Date();
        String signature = SignatureUtils.signUser(message, user);
        return message + "$" + signature;
    }

    public static String queryUser(User user,String usrName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERY$USER$" + usrName + "$" + new Date();
        String signature = SignatureUtils.signUser(message,user);
        return message + "$" + signature;
    }

    public static String queryUserPk(User user,String usrName) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERY$PUBLIC$" + usrName + "$" + new Date();
        String signature = SignatureUtils.signUser(message,user);
        return message + "$" + signature;
    }

    public static String queryGetContacts(User user) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        String message = "QUERY$CONTACTS$" + new Date();
        String signature = SignatureUtils.signUser(message,user);
        return message + "$" + signature;
    }

    public static String sendMessage(User user, String usrName, String destPubKey, String key, String iV, String m) throws Exception {
        String usrEncKeyAndIv = RSAUtils.encrypt(key + "$" + iV, RSAUtils.getPublicKeyUser(user));
        String destEncKeyAndIv = RSAUtils.encrypt(key + "$" + iV,RSAUtils.loadPbK(destPubKey));
        String encMessage = AESUtils.encryption(m + "\n" + new Date(), key, iV);
        String message = "SEND$" + usrName + "$" + usrEncKeyAndIv + "$" + destEncKeyAndIv + "$" + encMessage + "$" + new Date();
        String signature = SignatureUtils.signUser(message, user);
        return message + "$" + signature;
    }
}
