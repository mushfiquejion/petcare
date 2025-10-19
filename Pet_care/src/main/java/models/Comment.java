package models;

public class Comment {
    private int id;
    private int postId;
    private String userName;
    private String content;

    // Constructor, Getters, and Setters below
    public Comment(int id, int postId, String userName, String content) {
        this.id = id;
        this.postId = postId;
        this.userName = userName;
        this.content = content;
    }

    public int getId() { return id; }
    public int getPostId() { return postId; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
}