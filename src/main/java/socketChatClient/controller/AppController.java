package socketChatClient.controller;

import containers.DatagramSocketInfo;
import containers.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socketChatClient.Client;
import socketChatClient.threads.Pinger;
import socketChatClient.threads.TcpListenerThread;
import socketChatClient.threads.UdpListenerThread;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class AppController {
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private MulticastSocket multicastSocket;
    private Stage primaryStage;
    private String serverName;
    private int serverPort;
    private String clientName;
    private List<DatagramSocketInfo> infoList;
    private int multicastPortNumber = 14445;
    private String groupAddressString = "233.233.233.233";
    private TcpListenerThread tcpListenerThread;
    private UdpListenerThread udpListenerThread;
    private Pinger pinger;

    public AppController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void init() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Please enter server name");
        dialog.setHeaderText("Please enter server name");
        dialog.setContentText("Please enter server name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> serverName = name);

        TextInputDialog portDialog = new TextInputDialog("");
        portDialog.setTitle("Please enter server port");
        portDialog.setHeaderText("Please enter server port");
        portDialog.setContentText("Please enter server port:");
        result = portDialog.showAndWait();
        try {
            result.ifPresent(port -> serverPort = Integer.parseInt(port));
        } catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText("String isn't number");
            alert.showAndWait();
            exit();
            return;
        }

        TextInputDialog idDialog = new TextInputDialog("");
        idDialog.setTitle("Please enter your id");
        idDialog.setHeaderText("Please enter your id");
        idDialog.setContentText("Please enter your id:");
        result = idDialog.showAndWait();
        result.ifPresent(name -> clientName = name);

        try {
            tcpSocket = new Socket(serverName, serverPort);
            udpSocket = new DatagramSocket(tcpSocket.getLocalPort());
//            multicastSocket = new MulticastSocket();
//            multicastSocket.setReuseAddress(true);
//            multicastSocket.bind(new InetSocketAddress(multicastPortNumber));
//            multicastSocket.joinGroup(InetAddress.getByName(groupAddressString));
            this.primaryStage.setTitle("Chat Client");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("/socketChatClient/MainWindow.fxml"));
            Pane rootLayout = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setAppController(this);
            mainWindowController.setClientName(clientName);
            mainWindowController.setMulticastPortNumber(multicastPortNumber);
            mainWindowController.setServerPort(serverPort);

            ObservableList<Message> messageList = FXCollections.observableArrayList();
            mainWindowController.initializeMessageList(messageList);

            udpListenerThread = new UdpListenerThread(this, messageList);
            udpListenerThread.start();

            tcpListenerThread = new TcpListenerThread(this, messageList);
            tcpListenerThread.start();

            pinger = new Pinger(this, serverName, serverPort);
            pinger.start();

//            MulticastUdpListenerThread multicastThread = new MulticastUdpListenerThread(multicastSocket, messageList);
//            multicastThread.start();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText("Try again later");
            alert.showAndWait();
            exit();
        }
    }

    public void exit() {
        try {
            tcpSocket.close();
            multicastSocket.close();
            udpSocket.close();
            tcpListenerThread.interrupt();
            udpListenerThread.interrupt();
            pinger.interrupt();
            primaryStage.close();
        } catch (IOException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    public synchronized Socket getTcpSocket() {
        return tcpSocket;
    }

    public synchronized DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public String getServerName() {
        return serverName;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public String getClientName() {
        return clientName;
    }
}
