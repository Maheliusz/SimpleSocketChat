package containers;

import java.io.Serializable;
import java.net.InetAddress;

public class DatagramSocketInfo implements Serializable {
    public int port;
    public InetAddress inetAddress;
    public String nickname;
    public long ttl;

    public DatagramSocketInfo(int port, InetAddress inetAddress, String name, long ttl) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.nickname=name;
        this.ttl = ttl;
    }
}
