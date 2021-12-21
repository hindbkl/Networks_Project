package MVC.Model;

public abstract class Signal {
    private User sender;
    private String type;

    public Signal (User sender, String type){
        this.sender = sender;
        this.type = type;
    }
}

//todo : ajouter les classes pour les autres types de signaux
