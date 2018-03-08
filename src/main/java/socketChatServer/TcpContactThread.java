package socketChatServer;

import containers.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TcpContactThread extends Thread {
    private Socket client;
    private List<Socket> allClients;

    public TcpContactThread(Socket socket, List<Socket> allClients) {
        this.client = socket;
        this.allClients = allClients;
    }

    @Override
    public void run() {
        Message message;
        ObjectInputStream is;
        ObjectOutputStream os;
        while (true) {
            try {
                is = new ObjectInputStream(client.getInputStream());
                message = (Message) is.readObject();
                System.out.println("Received message from: " + message.name);
                for (Socket socket : allClients) {
                    if (!socket.equals(client)) {
                        os = new ObjectOutputStream(socket.getOutputStream());
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
                System.out.println(this.getName() + " ending work");
                return;
            }
        }
    }
}
