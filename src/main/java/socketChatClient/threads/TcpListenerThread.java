package socketChatClient.threads;

import containers.Message;
import javafx.application.Platform;
import socketChatClient.controller.AppController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class TcpListenerThread extends Thread {
    private AppController appController;
    private List<Message> list;

    public TcpListenerThread(AppController appController, List<Message> list) {
        this.appController = appController;
        this.list = list;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream is = new ObjectInputStream(appController.getTcpSocket().getInputStream());
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
