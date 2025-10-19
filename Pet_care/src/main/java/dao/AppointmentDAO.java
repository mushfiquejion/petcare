package dao;

import models.Appointment;
import models.Vet; // NEW IMPORT
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // --- MODIFIED: Insert new appointment (now requires vetId) ---
    public static void addAppointment(int userId, String petName, LocalDate date, LocalTime time, String type, int vetId) {
        // Updated SQL query to include vet_id
        String sql = "INSERT INTO appointments (user_id, pet_name, appointment_date, appointment_time, type, vet_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, petName);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setTime(4, Time.valueOf(time));
            stmt.setString(5, type);
            stmt.setInt(6, vetId); // NEW: Set Vet ID
            stmt.executeUpdate();
            System.out.println("✅ Appointment booked!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Retrieve all appointments for a user (no change needed here) ---
    public static List<Appointment> getAppointmentsByUser(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("pet_name"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("type")
                        // NOTE: If you update Appointment model to include vet name, update this constructor
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // --- NEW: Retrieve all Vets ---
    public static List<Vet> getAllVets() {
        List<Vet> vets = new ArrayList<>();
        String sql = "SELECT id, name FROM vets ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vets.add(new Vet(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vets;
    }

    // --- NEW: Check booked slots for a specific Vet on a specific Date ---
    public static List<LocalTime> getBookedSlots(int vetId, LocalDate date) {
        List<LocalTime> bookedSlots = new ArrayList<>();
        String sql = "SELECT appointment_time FROM appointments WHERE vet_id = ? AND appointment_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vetId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookedSlots.add(rs.getTime("appointment_time").toLocalTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookedSlots;
    }
}