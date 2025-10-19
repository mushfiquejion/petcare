package models;

public class Vet {
    private int id;
    private String name;

    public Vet(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    // Crucial for displaying name in ChoiceBox/ComboBox
    @Override
    public String toString() {
        return name;
    }
}