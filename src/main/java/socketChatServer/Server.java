package socketChatServer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket tcpSocket;
    private DatagramSocket udpSocket;
    private List<Socket> tcpClients;
    private final int portNumber = 4444;
    private UdpListenerThread udpListenerThread;

    public static void main(String args[]) {
        Server server = new Server();
//        server.setupUdpListener();
        server.listenLoop();
    }

    public Server() {
        try {
            System.out.println("HOSTNAME: " + InetAddress.getLocalHost().getHostName());
            System.out.println("PORT: " + portNumber);
            this.tcpSocket = new ServerSocket(portNumber);
            this.udpSocket = new DatagramSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        tcpClients = new ArrayList<>();
    }

    private void listenLoop() {
        Socket client;
        TcpContactThread tcpContactThread;
        while (true) {
            client = null;
            try {
                client = tcpSocket.accept();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (client != null) {
                tcpClients.add(client);
                tcpContactThread = new TcpContactThread(client, tcpClients);
                tcpContactThread.start();
            }
        }
    }

    private void setupUdpListener() {
        udpListenerThread = new UdpListenerThread(udpSocket);
        udpListenerThread.start();
    }

}
