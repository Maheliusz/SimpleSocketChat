package socketChatServer;

import containers.DatagramSocketAddress;
import containers.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UdpListenerThread extends Thread {
    private DatagramSocket udpSocket;
    private List<DatagramSocket> clients;
    private List<DatagramSocketAddress> addresses;

    public UdpListenerThread(DatagramSocket datagramSocket) {
        this.udpSocket = datagramSocket;
        clients = new ArrayList<>();
        addresses = new ArrayList<>();
    }

    @Override
    public void run() {
        ObjectInputStream is;
        byte[] receiveBuffer;
//        DatagramPacket packet;
        while (true) {
            receiveBuffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
//            for (DatagramSocket socket : clients) {
//                if (socket.isClosed()) clients.remove(socket);
//            }
            try {
                udpSocket.receive(packet);
                is = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(receiveBuffer)));
                Message message = (Message) is.readObject();
                message.clients = addresses.stream().filter(element -> !(element.inetAddress.equals(packet.getAddress()) &&
                        element.port == packet.getPort())).collect(Collectors.toList());
                System.out.println("Datagram from: " + message.name);
                if (clients.stream().noneMatch(socket -> socket.getInetAddress().equals(packet.getAddress()))) try {
                    clients.add(new DatagramSocket(packet.getPort(), packet.getAddress()));
                    addresses.add(new DatagramSocketAddress(packet.getPort(), packet.getAddress(), message.name));
                } catch (SocketException e) {
                    System.out.println(e.getMessage());
                }
                for (DatagramSocket socket : clients) {
                    if (!socket.getRemoteSocketAddress().equals(packet.getSocketAddress())) {
                        socket.send(packet);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
