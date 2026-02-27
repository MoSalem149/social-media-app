package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class PostDAO implements DAO<Post>{
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "user_id", "created_at"
    );
    @Override
    public Optional<Post> findById(int id) {
        String sql = "SELECT * FROM POSTS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Post post = Post.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .imagePath(resultset.getString("image_path"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(post);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Post> findAll() {
        String sql = "SELECT * FROM POSTS";
        List<Post> posts = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                posts.add(Post.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .imagePath(resultset.getString("image_path"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }
    public List<Post> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM POSTS WHERE user_id = ?";
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                posts.add(Post.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .imagePath(resultset.getString("image_path"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
        return posts;
    }

    /** Returns posts from given user IDs (e.g. self + friends) for news feed, newest first. */
    public List<Post> findFeedByUserIds(java.util.Collection<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) return new ArrayList<>();
        String placeholders = String.join(",", userIds.stream().map(id -> "?").toList());
        String sql = "SELECT * FROM posts WHERE user_id IN (" + placeholders + ") ORDER BY created_at DESC";
        List<Post> posts = new ArrayList<>();
        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (Integer id : userIds) {
                ps.setInt(i++, id);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(Post.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .content(rs.getString("content"))
                        .privacy(Privacy.valueOf(rs.getString("privacy").toUpperCase()))
                        .imagePath(rs.getString("image_path"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(int pageNumber, int pageSize, String sortBy, String sortDir,
                              Optional<String> searchTerm,
                              Optional<Integer> userId,
                              Optional<Integer> postId) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        sortDir = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        int offset = pageNumber * pageSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM posts");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM posts");
        List<Object> parameters = new ArrayList<>();
        boolean hasWhere = false; // track if WHERE is already added

        // searchTerm filter (search in post content)
        if(searchTerm.isPresent() && !searchTerm.get().isBlank()){
            sql.append(" WHERE LOWER(content) LIKE ?");
            countSql.append(" WHERE LOWER(content) LIKE ?");
            parameters.add("%" + searchTerm.get().toLowerCase() + "%");
            hasWhere = true;
        }

        // filter1 (e.g., user role or some id)
        if(userId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("user_id = ?");
            countSql.append("user_id = ?");
            parameters.add(userId.get());
            hasWhere = true;
        }

        // filter2 (e.g., another id or status)
        if(postId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("id = ?");
            countSql.append("id = ?");
            parameters.add(postId.get());
        }

        // Sorting and Pagination
        sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDir);
        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString());
             PreparedStatement countStmt = connection.prepareStatement(countSql.toString())) {

            // Set parameters for SELECT
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Post> posts = new ArrayList<>();
            while(rs.next()){
                posts.add(Post.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .content(rs.getString("content"))
                        .privacy(rs.getObject("privacy", Privacy.class))
                        .imagePath(rs.getString("image_path"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }

            // Set parameters for COUNT
            for(int i = 0; i < parameters.size() - 2; i++) { // exclude last 2 (LIMIT + OFFSET)
                countStmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet countRs = countStmt.executeQuery();
            int totalElements = countRs.next() ? countRs.getInt(1) : 0;

            return new Page<>(posts, pageNumber, pageSize, totalElements);

        } catch (SQLException e){
            e.printStackTrace();
            return new Page<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }
    @Override
    public boolean update(Post post) {
        String sql = "UPDATE POSTS SET content = ?, privacy = ?, image_path = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,post.getContent());
            preparedStatement.setString(2,post.getPrivacy().name().toLowerCase());
            preparedStatement.setString(3,post.getImagePath());
            preparedStatement.setInt(4,post.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Post Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Post post) {
        String sql = "DELETE FROM POSTS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,post.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Post Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Post post) {
        String sql = "INSERT INTO posts (user_id, content, privacy, image_path, created_at) VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,post.getUserId());
            preparedStatement.setString(2,post.getContent());
            preparedStatement.setString(3,post.getPrivacy().name().toLowerCase());
            preparedStatement.setString(4,post.getImagePath());
            preparedStatement.setObject(5,post.getCreatedAt());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Post Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
