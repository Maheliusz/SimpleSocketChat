package socketChatServer;

import socketChatServer.listeners.TcpContactThread;
import socketChatServer.listeners.UdpListenerThread;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket tcpSocket;
    private DatagramSocket udpSocket;
    private List<Socket> tcpClients;
    private final int portNumber = 4444;
    private UdpListenerThread udpListenerThread;
    private ExecutorService threadPool;

    public static void main(String args[]) {
        Server server = new Server();
        server.setupUdpListener();
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
        threadPool = Executors.newFixedThreadPool(20);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            tcpClients.forEach(socket -> {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });
            try {
                tcpSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            udpSocket.close();
            threadPool.shutdown();
            udpListenerThread.interrupt();
        }));
    }

    private void listenLoop() {
        Socket client;
        while (true) {
            client = null;
            try {
                client = tcpSocket.accept();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            if (client != null) {
                tcpClients.add(client);
                threadPool.submit(new TcpContactThread(client, tcpClients));
            }
        }

    }

    private void setupUdpListener() {
        udpListenerThread = new UdpListenerThread(udpSocket);
        udpListenerThread.start();
    }

}
