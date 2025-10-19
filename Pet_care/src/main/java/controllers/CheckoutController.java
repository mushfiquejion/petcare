package controllers;

import dao.OrderDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import utils.Cart;
import utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class CheckoutController implements Initializable {

    @FXML private TextField emailField;
    @FXML private ToggleGroup paymentToggleGroup;
    @FXML private Label totalPriceLabel;

    private static final String PREF_KEY_EMAIL = "checkout_email";
    private static final int DEFAULT_USER_ID = 1; // If your DB requires user_id

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cart total
        totalPriceLabel.setText(String.format("Total to Pay: ৳%.2f", Cart.getTotalPrice()));
        // Prefill last used email (optional)
        String savedEmail = Preferences.userNodeForPackage(CheckoutController.class)
                .get(PREF_KEY_EMAIL, "");
        if (!savedEmail.isBlank()) {
            emailField.setText(savedEmail);
        }
    }

    @FXML
    private void handlePlaceOrder() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";

        // Validate payment choice
        RadioButton rb = (RadioButton) paymentToggleGroup.getSelectedToggle();
        if (rb == null) {
            showAlert("Error", "Please choose a payment method.");
            return;
        }
        String paymentMethod = String.valueOf(rb.getUserData());

        // Validate email (basic)
        if (email.isBlank() || !email.contains("@")) {
            showAlert("Error", "Please enter a valid email.");
            return;
        }

        // Persist order (address/phone omitted as requested)
        OrderDAO.createOrder(DEFAULT_USER_ID, "", "", paymentMethod);

        // Save email for next time
        Preferences.userNodeForPackage(CheckoutController.class)
                .put(PREF_KEY_EMAIL, email);

        // Clear cart and confirm
        Cart.clearCart();
        showAlert("Success", "Your order has been placed!");
        goBackToDashboard();
    }

    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            StageManager.switchScene(scene, "🐾 Pet Care - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}