package socketChatClient.threads;

import containers.Message;
import socketChatClient.controller.AppController;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Pinger extends Thread {
    private AppController appController;
    private String serverName;
    private int serverPort;

    public Pinger(AppController appController, String serverName, int serverPort) {
        this.appController = appController;
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
                appController.getUdpSocket().send(packet);
                byteStream.close();
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                System.out.println("Pinger sleep interrupted: " + e.getMessage());
            } catch (java.io.IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
