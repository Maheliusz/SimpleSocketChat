package socketChatClient.listeners;

import containers.Message;
import javafx.application.Platform;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class UdpListenerThread extends Thread {
    private DatagramSocket socket;
    private List<Message> list;

    public UdpListenerThread(DatagramSocket socket, List<Message> list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run() {
        ObjectInputStream is;
        while (true) {
            try {
                byte[] buffer = new byte[5000];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                socket.receive(dp);
                is = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
                Message receivedMessage = (Message) is.readObject();
                System.out.println("Received message by UDP from: " + receivedMessage.name);
                receivedMessage.howDelivered = "UDP";
                Platform.runLater(() -> list.add(receivedMessage));
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("UdpListenerThread ends work");
                return;
            }
        }
    }
}
