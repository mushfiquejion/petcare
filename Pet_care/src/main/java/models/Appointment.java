package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int id;
    private int userId;
    private String petName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String type;

    public Appointment(int id, int userId, String petName, LocalDate appointmentDate, LocalTime appointmentTime, String type) {
        this.id = id;
        this.userId = userId;
        this.petName = petName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.type = type;
    }

    // getters & setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getPetName() { return petName; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public String getType() { return type; }
}