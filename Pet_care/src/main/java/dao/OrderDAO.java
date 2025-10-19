package dao;

import models.CartItem;
import utils.Cart;
import utils.DBConnection;
import java.sql.*;
import java.util.List;

public class OrderDAO {

    public static void createOrder(int userId, String address, String phone, String paymentMethod) {
        String insertOrderSQL = "INSERT INTO orders (user_id, total_price, delivery_address, phone, payment_method) VALUES (?, ?, ?, ?, ?)";
        String insertOrderItemSQL = "INSERT INTO order_items (order_id, accessory_id, quantity, price_per_item) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into 'orders' table
            PreparedStatement orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, Cart.getTotalPrice());
            orderStmt.setString(3, address);
            orderStmt.setString(4, phone);
            orderStmt.setString(5, paymentMethod);
            orderStmt.executeUpdate();

            // 2. Get the generated order ID
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);

                // 3. Insert each cart item into 'order_items' table
                PreparedStatement itemStmt = conn.prepareStatement(insertOrderItemSQL);
                List<CartItem> cartItems = Cart.getItems();
                for (CartItem item : cartItems) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getAccessory().getId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getAccessory().getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit(); // Finalize transaction
            System.out.println("✅ Order created successfully!");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}