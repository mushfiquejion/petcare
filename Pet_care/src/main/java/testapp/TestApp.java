package testapp;

import dao.UserDAO;
import models.User;

public class TestApp {
    public static void main(String[] args) {
        // Add a user
        UserDAO.addUser("Alice", "alice@mail.com", "1234");

        // Try login
        User user = UserDAO.getUser("alice@mail.com", "1234");

        if (user != null) {
            System.out.println("🎉 Login successful: " + user);
        } else {
            System.out.println("❌ Login failed");
        }
    }
}