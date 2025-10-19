package controllers;

import dao.UserDAO;
import models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.StageManager;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = UserDAO.getUser(email, password);

        if (user != null) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                Scene scene = new Scene(loader.load());
                StageManager.switchScene(scene, "🐾 Pet Care - Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Login Failed", "Invalid email or password.");
        }
    }

    @FXML
    private void goToSignup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/signup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            StageManager.switchScene(scene, "🐾 Sign Up");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}