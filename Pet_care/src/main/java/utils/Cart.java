package utils;

import models.Accessory;
import models.CartItem;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static final List<CartItem> items = new ArrayList<>();

    public static void addItem(Accessory accessory) {
        // Check if item already exists in cart
        for (CartItem item : items) {
            if (item.getAccessory().getId() == accessory.getId()) {
                item.setQuantity(item.getQuantity() + 1); // Increase quantity
                return;
            }
        }
        // If not, add new item
        items.add(new CartItem(accessory));
    }

    public static List<CartItem> getItems() {
        return items;
    }

    public static double getTotalPrice() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public static void clearCart() {
        items.clear();
    }

    public static void removeItem(CartItem cartItem) {
        items.remove(cartItem);
    }
}