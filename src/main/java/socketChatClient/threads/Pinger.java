package socketChatClient.threads;

import containers.Message;
import socketChatClient.controller.AppController;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class Pinger extends Thread {
    private AppController appController;
    private DatagramSocket udpSocket;
    private String serverName;
    private int serverPort;

    public Pinger(AppController appController, String serverName, int serverPort, DatagramSocket udpSocket) {
        this.appController = appController;
        this.udpSocket = udpSocket;
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = new Message();
                message.message = String.valueOf((char) 0x0) + "HELLO";
                message.name = appController.getClientName();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(byteStream);
                os.writeObject(message);
                byte[] buffer = byteStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                        InetAddress.getByName(serverName), serverPort);
                udpSocket.send(packet);
                byteStream.close();
                Thread.sleep(TimeUnit.SECONDS.toMillis(55));
            } catch (InterruptedException e) {
                System.out.println("Pinger sleep interrupted: " + e.getMessage());
            } catch (java.io.IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
