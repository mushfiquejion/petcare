package controllers;

import dao.AppointmentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Appointment;
import models.Vet;
import utils.StageManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentController implements Initializable {

    // FXML fields linked to the FXML file
    @FXML private TextField petNameField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> timeSlotChoiceBox;
    @FXML private ComboBox<Vet> vetChoiceBox;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> colPetName;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colType;

    // Availability Settings
    private final LocalTime START_TIME = LocalTime.of(10, 0);
    private final LocalTime END_TIME   = LocalTime.of(17, 0);
    private final int SLOT_MINUTES = 60;
    private int userId = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // --- Filter Dates to Working Days (Sat-Thu) ---
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Load types
        typeChoice.getItems().addAll("online", "offline");

        // --- Load Vets only once, after clearing to prevent duplication ---
        vetChoiceBox.getItems().clear();
        List<Vet> vets = AppointmentDAO.getAllVets();
        vetChoiceBox.setItems(FXCollections.observableArrayList(vets));

        // Setup Listeners
        vetChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeSlots());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeSlots());

        // --- Table Column Setup ---
        colPetName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPetName()));
        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentDate().toString()));
        colTime.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAppointmentTime().toString()));
        colType.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        loadAppointments();
    }

    // --- NAVIGATION ---


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


    // --- DYNAMIC SLOT LOGIC ---
    private void updateTimeSlots() {
        Vet selectedVet = vetChoiceBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        timeSlotChoiceBox.getItems().clear();
        timeSlotChoiceBox.setPromptText("Select Time Slot");

        if (selectedVet == null || selectedDate == null || selectedDate.isBefore(LocalDate.now())
                || selectedDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
            timeSlotChoiceBox.setPromptText("Select Vet and a valid Date");
            return;
        }

        List<LocalTime> allSlots = generateAllTimeSlots();
        List<LocalTime> bookedSlots = AppointmentDAO.getBookedSlots(selectedVet.getId(), selectedDate);
        List<LocalTime> availableSlots =
                allSlots.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());

        timeSlotChoiceBox.setItems(FXCollections.observableArrayList(availableSlots));
        timeSlotChoiceBox.setPromptText(availableSlots.isEmpty() ? "No slots available" : "Select Time Slot");
    }

    private List<LocalTime> generateAllTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime time = START_TIME;
        while (time.isBefore(END_TIME)) {
            slots.add(time);
            time = time.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    // --- BOOKING LOGIC ---
    @FXML
    private void handleAppointment() {
        Vet selectedVet = vetChoiceBox.getValue();
        LocalTime selectedTime = timeSlotChoiceBox.getValue();
        String petName = petNameField.getText();
        LocalDate date = datePicker.getValue();
        String type = typeChoice.getValue();

        if (selectedVet == null || selectedTime == null || petName.isEmpty() || date == null || type == null) {
            showAlert("Error", "Please fill all fields and select a valid Time Slot.");
            return;
        }

        AppointmentDAO.addAppointment(userId, petName, date, selectedTime, type, selectedVet.getId());

        showAlert("Success", "Appointment with " + selectedVet.getName() + " booked successfully!");
        updateTimeSlots();
        loadAppointments();
    }

    // --- UTILITIES ---
    private void loadAppointments() {
        List<Appointment> list = AppointmentDAO.getAppointmentsByUser(userId);
        ObservableList<Appointment> data = FXCollections.observableArrayList(list);
        appointmentTable.setItems(data);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}