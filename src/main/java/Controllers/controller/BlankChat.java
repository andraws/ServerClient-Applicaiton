package Controllers.controller;

import ServerClient.*;
import Controllers.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlankChat {
  // very similar to serverchat, I should've made this first then just extended serverchat
  ScreenController sController = ScreenController.getInstance();
  @FXML TextField inputText;
  @FXML JFXButton sendBtn;
  @FXML JFXListView<TextBubble> chatTable;
  @FXML JFXButton backBtn;
  @FXML Label headingLabel;
  @FXML AnchorPane mainAnchor;
  private SimpleDateFormat sdf;
  public Client client;
  public String receiver;
  NotificationSystem notif;


  @FXML
  public void initialize() {
    sController.setDirectChat(this);
    client = sController.getClient();
    receiver = sController.getReceiver();
    headingLabel.setText(receiver);
    sdf = new SimpleDateFormat("hh:mm a");
    notif = new NotificationSystem();
    mainAnchor.getChildren().add(notif);
    sController.setNotif(notif);
    populateChatTable();
  }

  public void btnClicked(ActionEvent actionEvent) throws IOException {
    if (actionEvent.getSource().equals(sendBtn)) {
      sendMessage();
    } else if (actionEvent.getSource().equals(backBtn)) {
      saveChatTable();
      sController.changeScene("ClientList.fxml");
      client.sendMessage(new ClientMessage(ClientMessage.WHOISIN, ""));
    }
  }

  public void setTextBubble(ServerMessage msg) {
    String userFrom = msg.getUser();
    String message = msg.getMessage();
    String timeStamp = sdf.format(new Date());
      TextBubble tb;

      if (client.getUsername().equals(userFrom)) {
        tb = new TextBubble(true, userFrom, timeStamp, message);
      } else {
          tb = new TextBubble(false, userFrom, timeStamp, message);
      }

      if(client.getUsername().equals(userFrom) || userFrom.equals(receiver)) {
        Platform.runLater(
                new Runnable() {
                  @Override
                  public void run() {
                    chatTable.getItems().add(tb);
                    chatTable.scrollTo(chatTable.getItems().size());
                  }
                });
      }
  }

  public void actionKey(KeyEvent keyEvent) {
    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
      sendMessage();
    }
  }

  private void sendMessage() {
    String message;
    message = inputText.getText();
    //System.out.println(message);
    ClientMessage msg =
        new ClientMessage(ClientMessage.DIRECT, client.getUsername(), receiver, message);
    inputText.clear();
    setTextBubble(new ServerMessage(ServerMessage.DIRECT, client.getUsername(), message));
    client.sendMessage(msg);
  }

  public void populateChatTable(){
    List<TextBubble> ls = sController.getMessages(receiver);
    chatTable.getItems().clear();
    for(TextBubble x : ls){
      chatTable.getItems().add(x);
    }
  }

  public void saveChatTable(){
    List<TextBubble> texts = chatTable.getItems();
    sController.saveMessages(receiver, texts);
  }

}
