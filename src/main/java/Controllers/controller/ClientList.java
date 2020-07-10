package Controllers.controller;

import ServerClient.*;
import Controllers.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ClientList implements Controller {
    ScreenController sController = ScreenController.getInstance();
    @FXML JFXButton serverChatBtn;
    @FXML JFXListView<String> clientList;
    @FXML AnchorPane mainAnchor;
    Client client;
    NotificationSystem notif;


    @FXML
    public void initialize(){
        client = sController.getClient();
        sController.setController(this);
        notif = new NotificationSystem();
        mainAnchor.getChildren().add(notif);
        sController.setNotif(notif);
        client.sendMessage(new ClientMessage(ClientMessage.WHOISIN,""));



        clientList.setOnMouseClicked(
                new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent click) {

                        if (click.getClickCount() == 2) {
                            // Use ListView's getSelected Item
                            String user = clientList.getSelectionModel().getSelectedItem();
                            sController.setReceiverver(user);
                            try {
                                sController.changeScene("BlankChat.fxml");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void btnClicked(ActionEvent actionEvent) throws IOException {
        if(actionEvent.getSource().equals(serverChatBtn)){
            sController.changeScene("ServerChat.fxml");
            client.sendMessage(new ClientMessage(ClientMessage.WHOISIN,""));
        }
    }

    // update our list with clients
    @Override
    public void updateCount(ServerMessage msg){
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        clientList.getItems().clear();
                        for (String user : msg.getOnlineUsers()) {
                            if (!user.equals(client.getUsername())) {
                                clientList.getItems().add(user);
                            }
                        }
                    }
                });
    }
}
