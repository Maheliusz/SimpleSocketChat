package containers;

import java.io.Serializable;

public class Message implements Serializable {
    public String message;
    public String name;
    public String howDelivered;

    public Message(){
        this.message="";
        this.name="";
        this.howDelivered="";
    }
}
