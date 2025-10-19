package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import utils.StageManager;

public class DashboardController {

    @FXML
    private void goToHealthCare() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/appointment.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            StageManager.switchScene(scene, "🐾 Pet Care - Health Care");  // ✅ fullscreen
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToForum() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/forum.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            StageManager.switchScene(scene, "👥 Pet Friendly Forum");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToShop() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/shop.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            StageManager.switchScene(scene, "🎁 Pet Accessories Shop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}