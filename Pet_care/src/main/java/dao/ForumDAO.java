package dao;

import models.Post;
import models.Comment;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumDAO {

    // --- 1. POSTING ---
    public static void addPost(int userId, String content, String imagePath) {
        // post_content can be empty if only an image is posted
        String sql = "INSERT INTO forum_posts (user_id, post_content, image_path) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, content);
            stmt.setString(3, imagePath); // Path to image file, can be null
            stmt.executeUpdate();

            System.out.println("✅ Post added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 2. RETRIEVING POSTS ---
    public static List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        // Join users table to get the userName who created the post
        String sql = "SELECT fp.id, fp.user_id, fp.post_content, fp.image_path, u.name as user_name " +
                "FROM forum_posts fp JOIN users u ON fp.user_id = u.id ORDER BY fp.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("post_content"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    // --- 3. ADDING COMMENTS ---
    public static void addComment(int postId, int userId, String content) {
        String sql = "INSERT INTO forum_comments (post_id, user_id, comment_content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.setString(3, content);
            stmt.executeUpdate();

            System.out.println("✅ Comment added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 4. RETRIEVING COMMENTS FOR A POST ---
    public static List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        // Join users table to get the userName who wrote the comment
        String sql = "SELECT fc.id, fc.post_id, fc.comment_content, u.name as user_name " +
                "FROM forum_comments fc JOIN users u ON fc.user_id = u.id WHERE fc.post_id = ? ORDER BY fc.created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getString("user_name"),
                        rs.getString("comment_content")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    // --- 5. ADDING/UPDATING REACTIONS (LIKE) ---
    public static void addReaction(int postId, int userId, String type) {
        // REPLACE INTO will update the row if (post_id, user_id) already exists, or insert new
        String sql = "REPLACE INTO forum_reactions (post_id, user_id, reaction_type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            stmt.setString(3, type); // Example: 'LIKE'
            stmt.executeUpdate();

            System.out.println("✅ Reaction added: " + type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 6. GETTING REACTION COUNT ---
    public static int getReactionCount(int postId) {
        String sql = "SELECT COUNT(*) FROM forum_reactions WHERE post_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}