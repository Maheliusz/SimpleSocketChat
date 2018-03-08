package containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    public String message;
    public String name;
    public String howDelivered;
    public List<DatagramSocketAddress> clients;

    public Message() {
        clients = new ArrayList<>();
    }
}
