package MVC.Model;

import Utils.Crypto.RSAUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class User {
    private String username, password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    // username must start by letter, be at least 8 char long, and only contain letters and digits
    public static boolean checkValidUsername(String username){
        if (username.length() >= 8){
            if (Character.isLetter(username.charAt(0))){
                for (int i=1; i<username.length(); i++){
                    if (!Character.isLetterOrDigit(username.charAt(i))){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean checkValidPw(String pw){
        boolean cap = false, dig = false, special = false;
        if (pw.length() >= 8){
            for (int i=0; i<pw.length(); i++){
                if (Character.isUpperCase(pw.charAt(i))){
                    cap = true;
                }
                else if (Character.isDigit(pw.charAt(i))){
                    dig = true;
                }
                else if (!Character.isLetterOrDigit(pw.charAt(i))){
                    special = true;
                }
            }
        }
        return (cap && dig && special);
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(RSAUtils.getPublicKeyUser(this).getEncoded());
    }

    public void generateNewKeys(){
        try {
            RSAUtils.generateKeys(this);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
