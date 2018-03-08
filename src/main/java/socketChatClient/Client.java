package socketChatClient;

import javafx.application.Application;
import javafx.stage.Stage;
import socketChatClient.controller.AppController;

public class Client extends Application {
    private Stage primaryStage;
    private AppController appController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Chat Client");

        this.appController = new AppController(primaryStage);
        this.appController.init();
    }
}
