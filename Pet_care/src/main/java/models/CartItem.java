package models;

public class CartItem {
    private final Accessory accessory;
    private int quantity;

    public CartItem(Accessory accessory) {
        this.accessory = accessory;
        this.quantity = 1; // Default quantity is 1
    }

    public Accessory getAccessory() { return accessory; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return accessory.getPrice() * quantity; }
}