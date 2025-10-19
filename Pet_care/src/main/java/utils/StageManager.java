package utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static void switchScene(Scene scene, String title) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);

        // 🔥 Always keep fullscreen
        primaryStage.setMaximized(true);  // keep window maximized
        // OR if you want real fullscreen (like F11 in browsers):
        // primaryStage.setFullScreen(true);

        primaryStage.show();
    }
}