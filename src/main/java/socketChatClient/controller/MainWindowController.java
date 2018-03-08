package socketChatClient.controller;

import containers.Message;
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
import java.net.DatagramPacket;
import java.util.ArrayList;

public class MainWindowController {
    @FXML
    private TextArea textArea;
    @FXML
    private ListView<Message> messageList;

    private AppController appController;
    private String clientName;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void initializeMessageList(ObservableList<Message> sourceList) {
        messageList.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            public void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? (item.name + " (" + item.howDelivered + ")"
                        + " wrote:\n" + item.message) : null);
            }
        });
        messageList.setItems(sourceList);
    }

    public void handleSendByTcpAction(ActionEvent actionEvent) {
        Message message = initializeMessage();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(appController.getTcpsocket().getOutputStream());
            os.writeObject(message);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("TCP Send Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
        textArea.setText("");
    }

    public void handleMulticastSendAction(ActionEvent actionEvent) {

        textArea.setText("");
    }

    public void handleSendByUdpAction(ActionEvent actionEvent) {
        Message message = initializeMessage();
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(byteStream);
            os.writeObject(message);
            byte[] serializedMessage = byteStream.toByteArray();
            DatagramPacket dp = new DatagramPacket(serializedMessage, serializedMessage.length,
                    appController.getUdpsocket().getInetAddress(), appController.getUdpsocket().getPort());
            appController.getUdpsocket().send(dp);
            byteStream.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("UDP Send Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
        textArea.setText("");
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private Message initializeMessage() {
        Message message = new Message();
        message.name = clientName;
        message.message = textArea.getText();
        message.clients = new ArrayList<>();
        message.howDelivered = "";
        return message;
    }
}