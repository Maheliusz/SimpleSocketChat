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
import socketChatClient.listeners.TcpListenerThread;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

public class AppController {
    private Socket tcpsocket;
    private DatagramSocket udpsocket;
    private Stage primaryStage;
    private String serverName;
    private String clientName;

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

        dialog.setTitle("Please enter your id");
        dialog.setHeaderText("Please enter your id");
        dialog.setContentText("Please enter your id:");
        result = dialog.showAndWait();
        result.ifPresent(name -> clientName = name);
        try {
            tcpsocket = new Socket(serverName, 4444);
            udpsocket = new DatagramSocket(4445, InetAddress.getByName(serverName));
            this.primaryStage.setTitle("Chat Client");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("/socketChatClient/MainWindow.fxml"));
            Pane rootLayout = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setAppController(this);
            mainWindowController.setClientName(clientName);

            ObservableList<Message> messageList = FXCollections.observableArrayList();
            mainWindowController.initializeMessageList(messageList);

            TcpListenerThread tcpListenerThread = new TcpListenerThread(tcpsocket, messageList);
            tcpListenerThread.start();

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
        primaryStage.close();
    }

    public Socket getTcpsocket() {
        return tcpsocket;
    }

    public DatagramSocket getUdpsocket() {
        return udpsocket;
    }
}
