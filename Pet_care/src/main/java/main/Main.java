package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.StageManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(loader.load());

        StageManager.setStage(stage); // ✅ Register stage once
        StageManager.switchScene(scene, "🐾 Pet Care - Login");
    }

    public static void main(String[] args) {
        launch();
    }
}