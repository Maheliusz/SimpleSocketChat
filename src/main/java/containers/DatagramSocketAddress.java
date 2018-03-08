package containers;

import java.io.Serializable;
import java.net.InetAddress;

public class DatagramSocketAddress implements Serializable {
    public int port;
    public InetAddress inetAddress;
    public String nickname;

    public DatagramSocketAddress(int port, InetAddress inetAddress, String name) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.nickname=name;
    }
}
