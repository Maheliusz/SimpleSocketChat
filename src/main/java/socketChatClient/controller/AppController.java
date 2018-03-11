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
import socketChatClient.listeners.MulticastUdpListenerThread;
import socketChatClient.listeners.TcpListenerThread;
import socketChatClient.listeners.UdpListenerThread;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private String clientName;
    private List<DatagramSocketInfo> infoList;
    private int multicastPortNumber;

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

        TextInputDialog idDialog = new TextInputDialog("");
        idDialog.setTitle("Please enter your id");
        idDialog.setHeaderText("Please enter your id");
        idDialog.setContentText("Please enter your id:");
        result = idDialog.showAndWait();
        result.ifPresent(name -> clientName = name);

        try {
            tcpSocket = new Socket(serverName, 4444);
            udpSocket = new DatagramSocket();
            multicastPortNumber = 4445;
            multicastSocket = new MulticastSocket(multicastPortNumber);
            multicastSocket.joinGroup(InetAddress.getByName("233.233.233.233"));
            this.primaryStage.setTitle("Chat Client");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("/socketChatClient/MainWindow.fxml"));
            Pane rootLayout = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setAppController(this);
            mainWindowController.setClientName(clientName);
            mainWindowController.setMulticastPortNumber(multicastPortNumber);

            ObservableList<Message> messageList = FXCollections.observableArrayList();
            mainWindowController.initializeMessageList(messageList);

            UdpListenerThread udpListenerThread = new UdpListenerThread(udpSocket, messageList);
            udpListenerThread.start();

            TcpListenerThread tcpListenerThread = new TcpListenerThread(tcpSocket, messageList);
            tcpListenerThread.start();

            MulticastUdpListenerThread multicastThread = new MulticastUdpListenerThread(multicastSocket, messageList);
            multicastThread.start();

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
        multicastSocket.close();
        try {
            tcpSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        udpSocket.close();
        primaryStage.close();
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public String getServerName() {
        return serverName;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }
}
