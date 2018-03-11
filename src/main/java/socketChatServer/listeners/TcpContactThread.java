package socketChatServer.listeners;

import containers.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TcpContactThread implements Runnable {
    private Socket client;
    private List<Socket> allClients;

    public TcpContactThread(Socket socket, List<Socket> allClients) {
        this.client = socket;
        this.allClients = allClients;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream is = new ObjectInputStream(client.getInputStream());
                Message message = (Message) is.readObject();
                System.out.println("Received message from: " + message.name);
                for (Socket socket : allClients) {
                    if (!socket.equals(client)) {
                        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                        os.writeObject(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                allClients.remove(client);
                System.out.println(e.getMessage());
                try {
                    client.close();
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
                System.out.println("Thread responsible for " + client.getInetAddress().toString() + " ending work");
                return;
            }
        }
    }
}
