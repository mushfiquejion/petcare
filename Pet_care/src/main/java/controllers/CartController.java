package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // <-- THE MISSING IMPORT
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.CartItem;
import utils.Cart;
import utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    // FXML fields
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colItemName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colSubtotal;
    @FXML private TableColumn<CartItem, Void> colAction;
    @FXML private Label totalPriceLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup table columns to display cart item properties
        colItemName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccessory().getName()));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getAccessory().getPrice()).asObject());
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        // Add a "Remove" button to each row
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    Cart.removeItem(item);
                    loadCartItems(); // Refresh the table after removal
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        loadCartItems();
    }

    private void loadCartItems() {
        cartTable.setItems(FXCollections.observableArrayList(Cart.getItems()));
        totalPriceLabel.setText(String.format("Total: ৳%.2f", Cart.getTotalPrice()));
    }

    // --- NAVIGATION METHODS ---

    @FXML
    private void goBackToShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/shop.fxml"));
            Scene scene = new Scene(loader.load());
            StageManager.switchScene(scene, "🎁 Pet Accessories");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCheckout() {
        // Check if cart is empty before proceeding
        if (Cart.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Cart");
            alert.setHeaderText(null);
            alert.setContentText("Your cart is empty. Please add items before proceeding to checkout.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/checkout.fxml"));
            Scene scene = new Scene(loader.load());
            StageManager.switchScene(scene, "💳 Checkout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}