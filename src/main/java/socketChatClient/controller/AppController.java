package socketChatClient.controller;

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
import socketChatClient.threads.MulticastUdpListenerThread;
import socketChatClient.threads.Pinger;
import socketChatClient.threads.TcpListenerThread;
import socketChatClient.threads.UdpListenerThread;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Optional;

public class AppController {
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private MulticastSocket multicastSocket;
    private Stage primaryStage;
    private String serverName;
    private int serverPort;
    private String clientName;
    private int multicastPortNumber = 4446;
    private String groupAddressString = "234.234.234.234";
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
            multicastSocket = new MulticastSocket(multicastPortNumber);
            multicastSocket.setReuseAddress(true);
            multicastSocket.joinGroup(InetAddress.getByName(groupAddressString));
            this.primaryStage.setTitle("Chat Client");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("/socketChatClient/MainWindow.fxml"));
            Pane rootLayout = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setAppController(this);
            mainWindowController.setTcpSocket(tcpSocket);
            mainWindowController.setUdpSocket(udpSocket);
            mainWindowController.setMulticastSocket(multicastSocket);
            mainWindowController.setClientName(clientName);
            mainWindowController.setServerPort(serverPort);

            ObservableList<Message> messageList = FXCollections.observableArrayList();
            mainWindowController.initializeMessageList(messageList);

            udpListenerThread = new UdpListenerThread(messageList, udpSocket);
            udpListenerThread.start();

            tcpListenerThread = new TcpListenerThread(messageList, tcpSocket);
            tcpListenerThread.start();

            pinger = new Pinger(this, serverName, serverPort, udpSocket);
            pinger.start();

            MulticastUdpListenerThread multicastThread = new MulticastUdpListenerThread(multicastSocket, messageList);
            multicastThread.start();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
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
            tcpListenerThread.stop();
            udpListenerThread.stop();
            pinger.stop();
            primaryStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getServerName() {
        return serverName;
    }

    public String getClientName() {
        return clientName;
    }

    public int getMulticastPortNumber() {
        return multicastPortNumber;
    }
}
