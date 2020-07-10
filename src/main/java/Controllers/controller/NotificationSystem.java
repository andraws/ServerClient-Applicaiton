package Controllers.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationSystem extends HBox {
  Label notifLabel;
  Label bellLabel;

  // creating our notification gui
  public NotificationSystem() {
    this.setPrefSize(400,50);
    this.setStyle("-fx-background-color: #AAB8C2");
    this.setPadding(new Insets(0.0,0.0,0.0,10.0));
    this.setAlignment(Pos.CENTER);
    AnchorPane.setRightAnchor(this, 0.0);
    AnchorPane.setLeftAnchor(this, 0.0);
    AnchorPane.setTopAnchor(this,0.0);
    this.setVisible(false);

    bellLabel = new Label();
    bellLabel.setPrefSize(50,40);

    bellLabel.setText("\uD83D\uDD14");
    bellLabel.setTextFill(Paint.valueOf("#1DA1F2"));
    bellLabel.setFont(new Font(30));

    notifLabel = new Label();
    notifLabel.setPrefSize(350,30);
    notifLabel.setTextAlignment(TextAlignment.CENTER);
    notifLabel.setTextFill(Paint.valueOf("white"));
    notifLabel.setWrapText(true);
    notifLabel.setFont(new Font("Segoe UI Semibold", 16.0));
    notifLabel.setText("Notification System");



  this.getChildren().addAll(bellLabel, notifLabel);
  }

  // set our notification message
  public void setNotifLabel(String notif) {
    // threadsafe JavaFX
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        notifLabel.setText(notif);
      }
    });

    // scheduler to show for 2 seconds
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    this.setVisible(true);
    scheduler.schedule(new Runnable() {
      @Override
      public void run() {
        disable();
      }
    },
            2,
            TimeUnit.SECONDS);
  }

  public void disable(){
    this.setVisible(false);
  }
}
