package socketChatClient.threads;

import containers.Message;
import javafx.application.Platform;
import socketChatClient.controller.AppController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class TcpListenerThread extends Thread {
    private AppController appController;
    private List<Message> list;
    private Socket tcpSocket;

    public TcpListenerThread(AppController appController, List<Message> list, Socket tcpSocket) {
        this.appController = appController;
        this.list = list;
        this.tcpSocket = tcpSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream is = new ObjectInputStream(tcpSocket.getInputStream());
                Message receivedMessage = (Message) is.readObject();
                System.out.println("Received message by TCP from: " + receivedMessage.name);
                Platform.runLater(() -> list.add(receivedMessage));
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("TcpListenerThread ends work");
                return;
            }
        }
    }
}
