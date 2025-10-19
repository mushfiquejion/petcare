package dao;

import models.Accessory;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoryDAO {

    public static List<Accessory> getAllAccessories() {
        List<Accessory> accessories = new ArrayList<>();
        String sql = "SELECT * FROM accessories";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accessories.add(new Accessory(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessories;
    }
}