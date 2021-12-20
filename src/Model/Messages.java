package Model;

public class Messages extends Signal{
    private User receiver;
    private String content;
    private String SKsender, SKreceiver;

    public Messages(User sender, User receiver, String content){
        super(sender, "MESSAGE");
        this.receiver = receiver;
        this.content = content;
    }

    public void addMessageToDb(Database database){
        //TODO : ajout à la db
    }
}

//encrypter msg (each user has public + private key)
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


