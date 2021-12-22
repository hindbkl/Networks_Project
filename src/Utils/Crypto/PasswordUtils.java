package Utils.Crypto;



public class PasswordUtils {

    public static String hashPassword(String pw){
        return PasswordAuthentication.hash(pw);
    }

    public static boolean authenticate(String password, String token) {
        return PasswordAuthentication.authenticate(password, token);
    }

}
