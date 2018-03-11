package socketChatClient.listeners;

import containers.Message;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class TcpListenerThread extends Thread {
    private Socket socket;
    private List<Message> list;

    public TcpListenerThread(Socket socket, List<Message> list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                Message receivedMessage = (Message) is.readObject();
                System.out.println("Received message by TCP from: " + receivedMessage.name);
                receivedMessage.howDelivered = "TCP";
                Platform.runLater(() -> list.add(receivedMessage));
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("TcpListenerThread ends work");
                return;
            }
        }
    }
}
