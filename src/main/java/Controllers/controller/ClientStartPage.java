package Controllers.controller;

import ServerClient.*;
import Controllers.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collections;

public class ClientStartPage {

  ScreenController sController = ScreenController.getInstance();
  @FXML JFXTextField userName;
  @FXML JFXTextField portNum;
  @FXML JFXTextField serverPort;
  @FXML JFXButton clientBtn;
  @FXML JFXButton serverBtn;
  @FXML VBox clientVBox;
  @FXML VBox serverVBox;
  @FXML VBox mainVBox;
  @FXML JFXTextArea clientErrorLbl;
  @FXML JFXTextArea serverErrorLbl;
  @FXML JFXButton createClient;
  @FXML JFXButton createServer;
  Output outPut = new Output();
  ObservableList<Node> workingCollection;
  boolean isClientVsb = true;

  @FXML
  public void initialize() {
    clientErrorLbl.setStyle("-fx-text-alignment: center");
    serverErrorLbl.setStyle("-fx-text-alignment: center");
    clientBtn.setStyle("-fx-background-color: #AAB8C2; -fx-text-fill: #FFFFFF");
    serverBtn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black");
    serverVBox.setVisible(false);
    clientVBox.setVisible(true);
    workingCollection = FXCollections.observableArrayList(mainVBox.getChildren());
  }

  public void btnAction(ActionEvent actionEvent) throws IOException, InterruptedException {

    if (actionEvent.getSource().equals(clientBtn)) {
      clientBtn.setStyle("-fx-background-color: #AAB8C2; -fx-text-fill: #FFFFFF");
      serverBtn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black");

      // show info to create a client
      if (!isClientVsb) {
        Collections.swap(workingCollection, 2, 3);
        mainVBox.getChildren().clear();
        mainVBox.getChildren().addAll(workingCollection);
        clientVBox.setVisible(true);
        serverVBox.setVisible(false);
        isClientVsb = true;
      }

    } else if (actionEvent.getSource().equals(serverBtn)) {
      clientBtn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black");
      serverBtn.setStyle("-fx-background-color: #AAB8C2; -fx-text-fill: #FFFFFF");

      // show info to create a server
      if (isClientVsb) {
        Collections.swap(workingCollection, 3, 2);
        mainVBox.getChildren().clear();
        mainVBox.getChildren().addAll(workingCollection);
        clientVBox.setVisible(false);
        serverVBox.setVisible(true);
        isClientVsb = false;
      }
    }

    // if actionEvent is createClient
    else if (actionEvent.getSource().equals(createClient)) {
      int portNumber = 0;
      String usrName = "";
      boolean gotPortNum = false;
      boolean createClientb = false;

      try {
        // make sure we got an integer for portNumber
        portNumber = Integer.parseInt(portNum.getText());
        // we also need a username
        if (userName.getText().isEmpty()) {
          clientErrorLbl.setVisible(true);
          clientErrorLbl.setText("Error: Enter UserName");
        } else {
          usrName = userName.getText();
          createClientb = true;
        }
      } catch (NumberFormatException e) {
        clientErrorLbl.setVisible(true);
        clientErrorLbl.setText("Error: Port # must be an Integer");
      }

      // we got the right fields
      if (createClientb) {
        sController.setTestUserName(usrName);
        // check if name has been taken
        Client tester = new Client(portNumber, "Tester", outPut);
        if(!tester.getConnected()){
          clientErrorLbl.setVisible(true);
          clientErrorLbl.setText("No server in that port");
        }
        try {
          tester.sendMessage(new ClientMessage(ClientMessage.WHOISIN, ""));
        }catch (Exception e){
          e.printStackTrace();
        }
        // we gotta wait for the thread to update if we can create the user or not
        while (sController.getcreateUser() == null) {
          createClient.setDisable(true);
        }
        createClient.setDisable(false);
        if (!sController.getcreateUser()) {
          clientErrorLbl.setVisible(true);
          clientErrorLbl.setText("UserName is taken");
          createClientb = false;
        }
        tester.disconnect();
      }

      // username hasn't been taken, we can create our client
      if (createClientb) {

        Client mClient = new Client(portNumber, usrName, outPut);
        sController.changeScene("ServerChat.fxml");
      }
    }

    // if actionEvent is createServer
    else if (actionEvent.getSource().equals(createServer)) {
      int portNumber = 0;
      boolean createServer = false;
      try {
        // make sure portNumb is a integer
        portNumber = Integer.parseInt(serverPort.getText());
        createServer = true;
      } catch (NumberFormatException e) {
        serverErrorLbl.setVisible(true);
        serverErrorLbl.setText("Error: Port # must be an Integer");
      }

      if (createServer) {
        // Creating Thread to make Server, current thread is handling GUI
        Runnable startServer = new ThreadHelper(portNumber, outPut);
        new Thread(startServer).start();
        System.out.println("Started Thread");
        String feedback = "";
        while (feedback.equals("")) {
          feedback = outPut.getInput();
        }
        if (feedback.contains("Exception")) {
          serverErrorLbl.setVisible(true);
          serverErrorLbl.setText(feedback);
        }
      }
    }
  }

  // thread that creates our server
  private class ThreadHelper implements Runnable {
    int portNum;
    Output out;
    String username;
    boolean createServer;

    public ThreadHelper(int portNum, Output out) {
      this.portNum = portNum;
      this.out = out;
      createServer = true;
    }

    @Override
    public void run() {
      if (createServer) {
        System.out.println("Creating Server");
        Server mServer = new Server(portNum, outPut);
        mServer.start();
      } else {
        try {
          // make sure server works
          Client mClient = new Client(portNum, username, out);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
