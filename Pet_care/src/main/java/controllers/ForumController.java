package controllers;

import dao.ForumDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority; // <-- Crucial Import for the layout fix
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Comment;
import models.Post;
import utils.StageManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class ForumController implements Initializable {

    @FXML private TextArea postTextArea;
    @FXML private Label photoPathLabel;
    @FXML private VBox postsContainer;

    private File selectedImageFile;
    private final int CURRENT_USER_ID = 1;
    private final String IMAGE_DIR = "pet_images/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ensure image directory exists
        new File(IMAGE_DIR).mkdirs();
        loadPosts();
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

    // --- PHOTO ATTACHMENT & POSTING ---
    @FXML
    private void handleAttachPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) postsContainer.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            photoPathLabel.setText("Photo attached: " + selectedImageFile.getName());
        } else {
            photoPathLabel.setText("No photo attached.");
        }
    }

    @FXML
    private void handlePost() {
        String content = postTextArea.getText();
        String imagePath = null;

        if (content.trim().isEmpty() && selectedImageFile == null) {
            System.out.println("Post cannot be empty.");
            return;
        }

        if (selectedImageFile != null) {
            try {
                imagePath = saveImageLocally(selectedImageFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        ForumDAO.addPost(CURRENT_USER_ID, content, imagePath);

        postTextArea.clear();
        selectedImageFile = null;
        photoPathLabel.setText("No photo attached.");
        loadPosts();
    }

    private String saveImageLocally(File sourceFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        Path targetPath = Paths.get(IMAGE_DIR + fileName);
        Files.copy(sourceFile.toPath(), targetPath);
        return IMAGE_DIR + fileName;
    }

    // --- RENDERING POSTS ---
    private void loadPosts() {
        postsContainer.getChildren().clear();
        List<Post> posts = ForumDAO.getAllPosts();

        for (Post post : posts) {
            VBox postView = createPostView(post);
            postsContainer.getChildren().add(postView);
        }
    }

    private VBox createPostView(Post post) {
        VBox postBox = new VBox(8);
        postBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: white; -fx-max-width: 600; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label userLabel = new Label("Posted by: " + post.getUserName());
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #0984e3;");

        Label contentLabel = new Label(post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 13px;");

        postBox.getChildren().addAll(userLabel, new Separator(), contentLabel);

        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                File imageFile = new File(post.getImagePath());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(570);
                    imageView.setPreserveRatio(true);
                    postBox.getChildren().add(imageView);
                }
            } catch (Exception e) {
                System.err.println("Error loading image for post ID " + post.getId());
            }
        }

        // --- Action Bar ---
        HBox interactionBar = createInteractionBar(post);
        postBox.getChildren().add(interactionBar);

        // Comments
        VBox commentsSection = loadCommentsSection(post.getId());
        postBox.getChildren().add(commentsSection);

        VBox commentInput = createCommentInput(post.getId());
        postBox.getChildren().add(commentInput);

        return postBox;
    }

    private HBox createInteractionBar(Post post) {
        HBox box = new HBox(150);

        // 1. Reaction Count
        Label reactionCount = new Label(ForumDAO.getReactionCount(post.getId()) + " Likes");
        reactionCount.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        // 2. Like Button
        Button likeButton = new Button("👍 Like");
        likeButton.setStyle("-fx-background-color: #f0f2f5; -fx-border-radius: 5;");
        likeButton.setOnAction(e -> {
            ForumDAO.addReaction(post.getId(), CURRENT_USER_ID, "LIKE");
            loadPosts();
        });

        HBox likeArea = new HBox(5, likeButton);
        HBox metrics = new HBox(5, reactionCount);

        box.getChildren().addAll(metrics, likeArea);
        return box;
    }

    // --- COMMENT LOGIC ---
    private VBox loadCommentsSection(int postId) {
        VBox commentsBox = new VBox(3);
        commentsBox.setStyle("-fx-padding: 5 0 5 15;");
        List<Comment> comments = ForumDAO.getCommentsForPost(postId);

        for (Comment comment : comments) {
            Label commentLabel = new Label("↳ " + comment.getUserName() + ": " + comment.getContent());
            commentLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
            commentsBox.getChildren().add(commentLabel);
        }
        return commentsBox;
    }

    private VBox createCommentInput(int postId) {
        VBox inputBox = new VBox(5);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write a comment...");
        commentArea.setPrefRowCount(1);

        Button submitButton = new Button("Comment");
        submitButton.setStyle("-fx-background-color:#0984e3; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 8;");

        // HBox holds the text area and button side-by-side
        HBox inputRow = new HBox(5, commentArea, submitButton);
        HBox.setHgrow(commentArea, Priority.ALWAYS);

        submitButton.setOnAction(e -> {
            String commentContent = commentArea.getText();
            if (!commentContent.trim().isEmpty()) {
                ForumDAO.addComment(postId, CURRENT_USER_ID, commentContent);
                // Clear the input area
                commentArea.clear();
                loadPosts();
            }
        });

        inputBox.getChildren().add(inputRow);
        return inputBox;
    }
}