package Controllers.controller;

import ServerClient.*;
import Controllers.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.List;

public class ServerChat implements Controller{
  ScreenController sController = ScreenController.getInstance();
  @FXML
  AnchorPane mainAnchor;
  @FXML TextField inputText;
  @FXML JFXButton sendBtn;
  @FXML JFXListView<TextBubble> chatTable;
  @FXML Label userCount;
  @FXML JFXButton backBtn;
  NotificationSystem notif;

  Client client;

  @FXML
  public void initialize() {
    client = sController.getClient();
    sController.setController(this);
    notif = new NotificationSystem();
    mainAnchor.getChildren().add(notif);
    sController.setNotif(notif);
    client.sendMessage(new ClientMessage(ClientMessage.WHOISIN,""));
    populateChatTable();
  }

  public void btnClicked(ActionEvent actionEvent) throws IOException {
    if (actionEvent.getSource().equals(sendBtn)) {
      if(!inputText.getText().isEmpty()) {
        sendMessage();
      }
    } else if (actionEvent.getSource().equals(backBtn)) {
      saveChatTable();
      sController.changeScene("ClientList.fxml");
    }
  }

  @Override
  public void setTextBubble(ServerMessage msg) {
    String userFrom = msg.getUser();
    String message = msg.getMessage();
    String timeStamp = msg.getTimeStamp();
    ObservableList<TextBubble> textList = chatTable.getItems();
    TextBubble tb;

    // if last chat bubble was within 2 mins and from same person, we don't need to display that
    // info again.

    if (textList.size() != 0) {
      // wanted to be fancy and not display name and time if the last text was within the same minute and from the same user
      String lastTime = textList.get(textList.size() - 1).getTime();
      lastTime = lastTime.substring(lastTime.indexOf(":") + 1);
      lastTime = lastTime.substring(0, lastTime.indexOf(" "));
      String currTime = timeStamp;
      currTime = currTime.substring(currTime.indexOf(":") + 1);
      currTime = currTime.substring(0, currTime.indexOf(" "));

      String lastName = textList.get(textList.size() - 1).getUser();

      if (currTime.equals(lastTime) && lastName.equals(userFrom)) {
        if (client.getUsername().equals(userFrom)) {
          tb = new TextBubble(true, userFrom, timeStamp, message, true);
        } else {
          tb = new TextBubble(false, userFrom, timeStamp, message, true);
        }
      } else {
        if (client.getUsername().equals(userFrom)) {
          tb = new TextBubble(true, userFrom, timeStamp, message);
        } else {
          tb = new TextBubble(false, userFrom, timeStamp, message);
        }
      }
    } else {
      if (client.getUsername().equals(userFrom)) {
        tb = new TextBubble(true, userFrom, timeStamp, message);
      } else {
        tb = new TextBubble(false, userFrom, timeStamp, message);
      }
    }

    // JavaFx needs to be threadsafe
    Platform.runLater(
        new Runnable() {
          @Override
          public void run() {
            chatTable.getItems().add(tb);
            chatTable.scrollTo(chatTable.getItems().size());
          }
        });
  }

  public void actionKey(KeyEvent keyEvent) {
    if (keyEvent.getCode().equals(KeyCode.ENTER)) {
      sendMessage();
    }
  }

  // send message to server
  private void sendMessage() {
    String message;
    message = inputText.getText();
    //System.out.println(message);
    ClientMessage msg = new ClientMessage(1, message);
    inputText.clear();
    client.sendMessage(msg);
  }

  // update the count of users online
  @Override
  public void updateCount(ServerMessage msg) {
    String count = String.valueOf(msg.getOnlineUsers().size());

    if (sController.currentFXML.equals("ServerChat.fxml")) {
      Platform.runLater(
          new Runnable() {
            @Override
            public void run() {
              userCount.setText(count);
            }
          });
    }
  }

  // keeping chat persistent
  public void populateChatTable(){
    List<TextBubble> ls = sController.getMessages("Server");
    chatTable.getItems().clear();
    for(TextBubble x : ls){
      chatTable.getItems().add(x);
    }
  }

  public void saveChatTable(){
    List<TextBubble> texts = chatTable.getItems();
    sController.saveMessages("Server", texts);
  }
}
