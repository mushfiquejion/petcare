package controllers;

import dao.AccessoryDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.Accessory;
import utils.Cart;
import utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ShopController implements Initializable {

    @FXML private GridPane accessoryGrid;
    private final int NUM_COLUMNS = 4; // Number of cards per row

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAccessories();
    }

    private void loadAccessories() {
        accessoryGrid.getChildren().clear();
        List<Accessory> accessories = AccessoryDAO.getAllAccessories();
        int row = 0, col = 0;

        for (Accessory accessory : accessories) {
            VBox card = createAccessoryCard(accessory);
            accessoryGrid.add(card, col, row);

            col++;
            if (col == NUM_COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createAccessoryCard(Accessory accessory) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        ImageView imageView = new ImageView(new Image(accessory.getImagePath(), 150, 150, true, true));
        Label nameLabel = new Label(accessory.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label priceLabel = new Label("৳" + String.format("%.2f", accessory.getPrice()));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> {
            Cart.addItem(accessory);
            System.out.println(accessory.getName() + " added to cart.");

            // Optional: Show a confirmation alert
            showAlert("Added to Cart", accessory.getName() + " has been added to your cart!");
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, addToCartButton);
        return card;
    }

    // --- NAVIGATION METHODS ---

    @FXML
    private void goToCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cart.fxml"));
            Scene scene = new Scene(loader.load());
            StageManager.switchScene(scene, "🛒 Your Shopping Cart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            StageManager.switchScene(scene, "🐾 Pet Care - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}