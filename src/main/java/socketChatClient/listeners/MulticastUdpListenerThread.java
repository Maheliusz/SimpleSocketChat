package socketChatClient.listeners;

import containers.Message;
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.List;

public class MulticastUdpListenerThread extends Thread {
    private MulticastSocket socket;
    private List<Message> list;

    public MulticastUdpListenerThread(MulticastSocket multicastSocket, List<Message> list) {
        this.socket = multicastSocket;
        this.list=list;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[5000];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                socket.receive(dp);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(buffer));
                Message receivedMessage = (Message) is.readObject();
                System.out.println("Received message by M-UDP from: " + receivedMessage.name);
                receivedMessage.howDelivered = "M-UDP";
                Platform.runLater(() -> list.add(receivedMessage));
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("MulticastUdpListenerThread ends work");
                return;
            }
        }
    }
}
