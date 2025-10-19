package models;

public class Post {
    private int id;
    private int userId;
    private String userName; // We need this to display who posted
    private String content;
    private String imagePath;

    // Constructor, Getters, and Setters below
    public Post(int id, int userId, String userName, String content, String imagePath) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }

    // Add necessary setters if needed, but getters are crucial for display
}