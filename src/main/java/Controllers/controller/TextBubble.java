package Controllers.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TextBubble extends VBox {

  Label nameLabel;
  Label timeLabel;
  HBox stampHBox;
  VBox msgVbox;
  Label msgLabel;

  /*
   Create TextBubble GUI Object for out ChatPage
   Sender - True if sender is current client
   User - Username
   Time - Time Stamp
   Message - Message
  */
  public TextBubble(Boolean sender, String user, String time, String message) {
    // file path for our cssFile
    String cssFile = "/CSSFiles/TextBubble.css";
    // Setting up our main VBox holding the txt message
    this.getStylesheets().add(cssFile);
    this.spacingProperty().set(1);
    // Setting up our nameLabel on Receiver Name
    nameLabel = new Label(user);
    nameLabel.getStylesheets().clear();
    nameLabel.getStyleClass().add("nameLabel");

    // Setting up our timeLabel for time
    timeLabel = new Label(time);
    timeLabel.getStyleClass().add("timeLabel");

    // Setting our HBox to hold name and time
    stampHBox = new HBox();
    stampHBox.setAlignment(Pos.CENTER_LEFT);
    stampHBox.setPrefSize(350, 17.0);
    stampHBox.spacingProperty().set(10);
    stampHBox.setCenterShape(true);
    stampHBox.setScaleShape(true);
    stampHBox.setVisible(true);
    stampHBox.setCacheShape(true);
    AnchorPane.setTopAnchor(stampHBox, 0.0);
    AnchorPane.setRightAnchor(stampHBox, 0.0);
    AnchorPane.setLeftAnchor(stampHBox, 0.0);
    stampHBox.getChildren().addAll(nameLabel, timeLabel);

    // Setting our Vbox to hold our message
    msgVbox = new VBox();
    AnchorPane.setTopAnchor(msgVbox, 20.0);
    AnchorPane.setLeftAnchor(msgVbox, 0.0);
    AnchorPane.setRightAnchor(msgVbox, 0.0);

    // Setting up Message Label
    msgLabel = new Label(message);
    msgLabel.setWrapText(true);

    // Setting up msgAnchor

    msgVbox = new VBox();
    msgVbox.setAlignment(Pos.CENTER);
    msgVbox.setPrefSize(350, msgLabel.getPrefHeight());
    msgVbox.setVisible(true);
    msgVbox.setCacheShape(true);
    msgVbox.setCenterShape(true);
    msgVbox.setScaleShape(true);
    msgVbox.setPadding(new Insets(15.0, 15.0, 15.0, 15.0));
    AnchorPane.setTopAnchor(msgVbox, 20.0);
    AnchorPane.setBottomAnchor(msgVbox, 0.0);
    AnchorPane.setLeftAnchor(msgVbox, 0.0);
    AnchorPane.setRightAnchor(msgVbox, 0.0);

    // adding msgLable to msgVBox
    msgVbox.getChildren().add(msgLabel);

    if (sender) {
      msgVbox.getStyleClass().add("sender");
      msgLabel.getStyleClass().add("sender");
    } else {
      msgVbox.getStyleClass().add("receiver");
      msgLabel.getStyleClass().add("receiver");
    }

    // this.setPrefWidth(380);
    this.setPrefHeight(msgLabel.getPrefHeight());
    this.getChildren().addAll(stampHBox, msgVbox);
  }

  public TextBubble(Boolean sender, String User, String time, String message, boolean t) {
    nameLabel = new Label();
    timeLabel = new Label();
    nameLabel.setText(User);
    timeLabel.setText(time);

    // file path for our cssFile
    String cssFile = "/CSSFiles/TextBubble.css";
    // Setting up our main VBox holding the txt message
    this.getStylesheets().add(cssFile);
    this.spacingProperty().set(1);

    // Setting our Vbox to hold our message
    msgVbox = new VBox();
    AnchorPane.setTopAnchor(msgVbox, 20.0);
    AnchorPane.setLeftAnchor(msgVbox, 0.0);
    AnchorPane.setRightAnchor(msgVbox, 0.0);

    // Setting up Message Label
    msgLabel = new Label(message);
    msgLabel.setWrapText(true);

    // Setting up msgAnchor

    msgVbox = new VBox();
    msgVbox.setAlignment(Pos.CENTER);
    msgVbox.setPrefSize(350, msgLabel.getPrefHeight());
    msgVbox.setVisible(true);
    msgVbox.setCacheShape(true);
    msgVbox.setCenterShape(true);
    msgVbox.setScaleShape(true);
    msgVbox.setPadding(new Insets(15.0, 15.0, 15.0, 15.0));
    AnchorPane.setTopAnchor(msgVbox, 20.0);
    AnchorPane.setBottomAnchor(msgVbox, 0.0);
    AnchorPane.setLeftAnchor(msgVbox, 0.0);
    AnchorPane.setRightAnchor(msgVbox, 0.0);

    // adding msgLable to msgVBox
    msgVbox.getChildren().add(msgLabel);

    if (sender) {
      msgVbox.getStyleClass().add("sender");
      msgLabel.getStyleClass().add("sender");
    } else {
      msgVbox.getStyleClass().add("receiver");
      msgLabel.getStyleClass().add("receiver");
    }

    // this.setPrefWidth(380);
    this.setPrefHeight(msgLabel.getPrefHeight());
    this.getChildren().addAll(msgVbox);
  }

  public String getTime() {
    return timeLabel.getText();
  }

  public String getUser() {
    return nameLabel.getText();
  }
}
