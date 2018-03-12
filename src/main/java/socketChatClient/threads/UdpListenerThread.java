package socketChatClient.threads;

import containers.Message;
import javafx.application.Platform;
import socketChatClient.controller.AppController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.List;

public class UdpListenerThread extends Thread {
    private AppController appController;
    private List<Message> list;
    private Thread pinger;

    public UdpListenerThread(AppController appController, List<Message> list) {
        this.appController = appController;
        this.list = list;
    }

    @Override
    public void run() {
        pinger = new Thread(() -> {

        });
        while (true) {
            try {
                byte[] buffer = new byte[5000];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                appController.getUdpSocket().receive(dp);
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
