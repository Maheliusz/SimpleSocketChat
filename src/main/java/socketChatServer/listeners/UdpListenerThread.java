package socketChatServer.listeners;

import containers.DatagramSocketInfo;
import containers.Message;
import socketChatServer.Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.stream.Collectors;

public class UdpListenerThread extends Thread {
    private DatagramSocket udpSocket;
    private Server server;

    public UdpListenerThread(DatagramSocket datagramSocket, Server server) {
        this.udpSocket = datagramSocket;
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            byte[] receiveBuffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            for (DatagramSocketInfo info : server.getAddresses()) {
                if (System.currentTimeMillis() - info.ttl >= 60000) {
                    server.getAddresses().remove(info);
                    System.out.println(info.nickname + " timed out. Removing client.");
                }
            }
            DatagramSocketInfo sender = new DatagramSocketInfo(packet.getPort(),
                    packet.getAddress(), "sender", 0);
            try {
                udpSocket.receive(packet);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(receiveBuffer));
                Message message = (Message) is.readObject();
                System.out.println("Datagram from: " + message.name);
                if (server.getAddresses().stream().noneMatch(info -> info.inetAddress.equals(packet.getAddress())
                        && info.port == packet.getPort())) {
                    server.getAddresses().add(new DatagramSocketInfo(packet.getPort(), packet.getAddress(), message.name,
                            System.currentTimeMillis()));
                } else {
                    for (DatagramSocketInfo info : server.getAddresses()) {
                        if (info.inetAddress.equals(packet.getAddress()) && info.port == packet.getPort()) {
                            info.ttl = System.currentTimeMillis();
                            break;
                        }
                    }
                }
                if (!message.message.startsWith(String.valueOf((char) (0x0)))) {
                    for (DatagramSocketInfo info : server.getAddresses().stream().filter(element ->
                            !(element.inetAddress.equals(packet.getAddress()) &&
                            element.port == packet.getPort())).collect(Collectors.toList())) {
                        packet.setAddress(info.inetAddress);
                        packet.setPort(info.port);
                        udpSocket.send(packet);
                        System.out.println("Datagram sent");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
