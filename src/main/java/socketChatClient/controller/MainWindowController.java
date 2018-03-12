package socketChatClient.controller;

import containers.Message;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

public class MainWindowController {
    @FXML
    private TextArea textArea;
    @FXML
    private ListView<Message> messageListView;

    private AppController appController;
    private String clientName;
    private int multicastPortNumber;
    private List<Message> messageList;
    private int serverPort;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private MulticastSocket multicastSocket;

    public void initializeMessageList(ObservableList<Message> sourceList) {
        messageList = sourceList;
        messageListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            public void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? (item.name + " (" + item.howDelivered + ")"
                        + " wrote:\n" + item.message) : null);
            }
        });
        messageListView.setItems(sourceList);
    }

    public void handleSendByTcpAction(ActionEvent actionEvent) {
        if (textArea.getText().trim().equals("")) return;
        Message message = initializeMessage("TCP");
        try {
            ObjectOutputStream os = new ObjectOutputStream(tcpSocket.getOutputStream());
            os.writeObject(message);
            printMessage(message);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("TCP Send Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            appController.exit();
        }
        textArea.setText("");
    }

    public void handleMulticastSendAction(ActionEvent actionEvent) {
        if (textArea.getText().trim().equals("")) return;
        Message message = initializeMessage("M-UDP");
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(byteStream);
            os.writeObject(message);
            byte[] serializedMessage = byteStream.toByteArray();
            DatagramPacket dp = new DatagramPacket(serializedMessage, serializedMessage.length,
                    InetAddress.getByName(appController.getServerName()), appController.getMulticastPortNumber());
            multicastSocket.send(dp);
            byteStream.close();
            printMessage(message);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Multicast UDP Send Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
            appController.exit();
        }
        textArea.setText("");
    }

    public void handleSendByUdpAction(ActionEvent actionEvent) {
        if (textArea.getText().trim().equals("")) return;
        Message message = initializeMessage("UDP");
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(byteStream);
            os.writeObject(message);
            byte[] serializedMessage = byteStream.toByteArray();
            DatagramPacket dp = new DatagramPacket(serializedMessage, serializedMessage.length,
                    InetAddress.getByName(appController.getServerName()), serverPort);
            udpSocket.send(dp);
            byteStream.close();
            printMessage(message);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("UDP Send Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            appController.exit();
        }
        textArea.setText("");
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }


    private Message initializeMessage(String howDelivered) {
        Message message = new Message();
        message.name = clientName;
        message.message = textArea.getText();
        message.howDelivered = howDelivered;
        return message;
    }

    public void setMulticastPortNumber(int multicastPortNumber) {
        this.multicastPortNumber = multicastPortNumber;
    }

    public void printMessage(Message message) {
        message.howDelivered = "YOU - " + message.howDelivered;
        Platform.runLater(() -> messageList.add(message));
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        appController.exit();
    }

    public void setTcpSocket(Socket tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    public void setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    public void setMulticastSocket(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }
}
