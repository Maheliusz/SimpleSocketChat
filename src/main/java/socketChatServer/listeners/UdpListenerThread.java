package socketChatServer.listeners;

import containers.DatagramSocketInfo;
import containers.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UdpListenerThread extends Thread {
    private DatagramSocket udpSocket;
    private List<DatagramSocketInfo> addresses;

    public UdpListenerThread(DatagramSocket datagramSocket) {
        this.udpSocket = datagramSocket;
        addresses = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            byte[] receiveBuffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            for (DatagramSocketInfo info : addresses) {
                if (System.currentTimeMillis() - info.ttl >= 60000) {
                    addresses.remove(info);
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
                if (addresses.stream().noneMatch(info -> info.inetAddress.equals(packet.getAddress())
                        && info.port == packet.getPort())) {
                    addresses.add(new DatagramSocketInfo(packet.getPort(), packet.getAddress(), message.name,
                            System.currentTimeMillis()));
                } else {
                    for (DatagramSocketInfo info : addresses) {
                        if (info.inetAddress.equals(packet.getAddress()) && info.port == packet.getPort()) {
                            info.ttl = System.currentTimeMillis();
                            break;
                        }
                    }
                }
                if (!message.message.startsWith(String.valueOf((char) (0x0)))) {
                    for (DatagramSocketInfo info : addresses.stream().filter(element -> !(element.inetAddress.equals(packet.getAddress()) &&
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
