package socketChatClient.threads;

import containers.Message;
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class UdpListenerThread extends Thread {
    private List<Message> list;
    private DatagramSocket udpSocket;

    public UdpListenerThread(List<Message> list, DatagramSocket udpSocket) {
        this.list = list;
        this.udpSocket = udpSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[5000];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(dp);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(buffer));
                Message receivedMessage = (Message) is.readObject();
                System.out.println("Received message by UDP from: " + receivedMessage.name);
                Platform.runLater(() -> list.add(receivedMessage));
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("UdpListenerThread ends work");
                return;
            }
        }
    }
}
