package Model;

public class User {
    private String username, pw, publickey; //à générer (pt àpd username + pw)

    public User(String username, String pw){
        this.username = username;
        this.pw = pw;
        this.publickey = "PK_test"; //TODO : generate public key
    }

    public User(String username, String pw, String publickey){
        this.username = username;
        this.pw = pw;
        this.publickey = publickey;
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

    public String toString(){
        return username;
    }

    public String getPublickey() {
        return publickey;
    }

    public String getUsername() {
        return username;
    }
}
