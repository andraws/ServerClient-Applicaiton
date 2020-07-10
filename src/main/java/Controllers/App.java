package Controllers;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

  @Override
  public void start(Stage primaryStage) throws IOException {
      // create our ScreenController and jump into startpage
    ScreenController sController = ScreenController.getInstance();
    sController.setPrimaryStage(primaryStage);
    sController.changeScene("ClientStartPage.fxml");
  }
}
