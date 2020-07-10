package Controllers;

import Controllers.controller.*;
import ServerClient.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ScreenController {

  // this is a singleton object for every client
  private static ScreenController single_instance = null;
  // save what FXML we are at
  public String currentFXML;
  // our client thread
  Client client;
  // our stage for switching scenes
  Stage main;
  //Controller to update fields
  Controller guiController;
  // BlankChat Controller, when we talk with other users
  BlankChat blankChat;
  // our notification icon to show events happening
  NotificationSystem notif;
  // for private messages we want to specify who the we are receiving the dm while in our BlankChat GUI
  private String sender;

  // do we create the client? Only if the username hasn't been taken
  private Boolean createUser = null;
  // list containing all users for testing
  private List<String> testerList;
  // Username we want to make sure isn't in server
  private String testUserName;

  // a way to persist the messages, not the best way but for this minimal application it works
  private HashMap<String,List<TextBubble>> persistentMessages = new HashMap<String,List<TextBubble>>();

  private ScreenController() {}

  public static ScreenController getInstance() {
    if (single_instance == null) {
      single_instance = new ScreenController();
    }
    return single_instance;
  }

  // setting our client thread
  public void setClient(Client th) {
    this.client = th;
    ClientMessage whosIn = new ClientMessage(ClientMessage.WHOISIN, "");
    client.sendMessage(whosIn);
  }

  // save messages, persisting while we switch FXML
  public void saveMessages(String user, List<TextBubble> texts){
    persistentMessages.put(user,texts);
  }

  // getting the needed messages
  public List<TextBubble> getMessages(String user){
    List<TextBubble> ls = persistentMessages.get(user);
    if(ls == null){
      return new LinkedList<>();
    }else
      return ls;
  }

  // setting our notification object
  public void setNotif(NotificationSystem notif){
    this.notif = notif;
  }

  // return the client, used by FXML controllers
  public Client getClient() {
    return client;
  }

  // setting primaryStage, in order to change scenes
  public void setPrimaryStage(Stage primeStage) {
    this.main = primeStage;
  }

  // change scene
  public void changeScene(String fxmlFile) throws IOException {
    String fxmlpath = "FXML/" + fxmlFile;
    currentFXML = fxmlFile;
    Parent root = FXMLLoader.load(getClass().getResource(fxmlpath));
    Scene scene = new Scene(root);
    main.setTitle("Server Client JavaFx.Controllers.App");
    main.setScene(scene);

    // add eventfilter for when we exit we can disconnect the client
    main.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closedWindowEvent);
    main.show();
  }

  // EventFilter for when we close the window, we also want to disconnect our client
  private void closedWindowEvent(WindowEvent event){
    if(!(client == null))
        client.disconnect();
    System.exit(0);
  }

  // setting controller
  public void setController(Controller serverChat) {
    this.guiController = serverChat;
  }

  public void setDirectChat(BlankChat blankChat) {
    this.blankChat = blankChat;
  }


  public void setTestUserName(String userName) {
    this.testUserName = userName;
    createUser = null;
  }

  // Displaying ServerMessage, either display chat bubble, update counter and list, or send a notification
  public void displayMessage(ServerMessage msg) {
      // if the msg is from "Tester" dont display anything, I could've def worked this out better
    if(!msg.getUser().equals("Tester")) {
      // if it is a regular message and we are in ServerChat, just set our textbubbles
      if (msg.getType() == ServerMessage.MESSAGE) {
        if (currentFXML.equals("ServerChat.fxml")) {
          guiController.setTextBubble(msg);
        }else{
          // else we need to save what was sent so when we go back to our server room we can see everything that was sent
          List<TextBubble> dm = getMessages("Server");
          dm.add(new TextBubble(false, msg.getUser(), msg.getTimeStamp(), msg.getMessage()));
          saveMessages("Server", dm);
        }
        // if msg is who joined, all we want to do it notify who is joined
      } else if (msg.getType() == ServerMessage.WHOJOINED) {
        notif.setNotifLabel(msg.getMessage());
        // were also gonna send a ClientMessage asking WHOISIN so we can update our list and counters
        client.sendMessage(new ClientMessage(ClientMessage.WHOISIN, ""));
        // same deal with who left
      } else if (msg.getType() == ServerMessage.WHOLEFT) {
        notif.setNotifLabel(msg.getMessage());
        client.sendMessage(new ClientMessage(ClientMessage.WHOISIN, ""));
        // if we receive who is in, we are going to update our counter or list
      } else if (msg.getType() == ServerMessage.WHOISIN) {
        upDateCounter(msg);
        // if message is dirrect
      } else if (msg.getType() == ServerMessage.DIRECT) {
        // if we are in our BlankChat
        if (currentFXML.equals("BlankChat.fxml")) {
          // if we arent in the chatroom with our sender, then just send a notification that we got a DM
          if(!msg.getUser().equals(sender)){
            notif.setNotifLabel(msg.getUser() + " sent you a private message");
            List<TextBubble> dm = getMessages(msg.getUser());
            dm.add(new TextBubble(false, msg.getUser(), msg.getTimeStamp(), msg.getMessage()));
            saveMessages(msg.getUser(), dm);
          }else
            blankChat.setTextBubble(msg);
          // if we arent in a chatroom, send a notification
        } else {
          notif.setNotifLabel(msg.getUser() + " sent you a private message");
          List<TextBubble> dm = getMessages(msg.getUser());
          dm.add(new TextBubble(false, msg.getUser(), msg.getTimeStamp(), msg.getMessage()));
          saveMessages(msg.getUser(), dm);
        }
      }
    }
  }

  //update counter, or list depending what FXML we are in
  public void upDateCounter(ServerMessage msg) {
    Platform.runLater(
        new Runnable() {
          @Override
          public void run() {
            guiController.updateCount(msg);
          }
        });
  }

  // with our Test thread, check if name is free
  public void getTestMsg(ServerMessage msg) {
    createUser = null;
    List<String> ls = msg.getOnlineUsers();
    for (String x : ls) {
      if (x.equals(testUserName)) {
        //System.out.println("Don't create client");
        createUser = false;
        break;
      }
    }
    if (createUser == null) {
      createUser = true;
    }
  }

  public Boolean getcreateUser() {
    return createUser;
  }

  public void setReceiverver(String user) {
    this.sender = user;
  }

  public String getReceiver() {
    return sender;
  }
}
