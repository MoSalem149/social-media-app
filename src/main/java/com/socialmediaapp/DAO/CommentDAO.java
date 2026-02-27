package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Util.DBConnection;
import com.socialmediaapp.Util.Page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class CommentDAO implements DAO<Comment>{
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "id", "user_id", "post_id", "created_at"
    );
    @Override
    public Optional<Comment> findById(int id) {
        String sql = "SELECT * FROM COMMENTS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Comment comment = Comment.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(comment);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT * FROM COMMENTS";
        List<Comment> comments = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                comments.add(Comment.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }
    @Override
    public Page<Comment> findAll(int pageNumber, int pageSize, String sortBy, String sortDir,
                              Optional<String> searchTerm,
                              Optional<Integer> userId,
                              Optional<Integer> postId) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "id";
        }
        sortDir = sortDir != null && sortDir.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        int offset = pageNumber * pageSize;

        StringBuilder sql = new StringBuilder("SELECT * FROM comments");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM comments");
        List<Object> parameters = new ArrayList<>();
        boolean hasWhere = false;

        if(searchTerm.isPresent() && !searchTerm.get().isBlank()){
            sql.append(" WHERE LOWER(name) LIKE ?");
            countSql.append(" WHERE LOWER(name) LIKE ?");
            parameters.add("%" + searchTerm.get().toLowerCase() + "%");
            hasWhere = true;
        }

        if(userId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("user_id = ?");
            countSql.append("user_id = ?");
            parameters.add(userId.get());
            hasWhere = true;
        }

        if(postId.isPresent()){
            sql.append(hasWhere ? " AND " : " WHERE ");
            countSql.append(hasWhere ? " AND " : " WHERE ");
            sql.append("post_id = ?");
            countSql.append("post_id = ?");
            parameters.add(postId.get());
        }

        sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDir);
        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(offset);

        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString());
             PreparedStatement countStmt = connection.prepareStatement(countSql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while(rs.next()){
                comments.add(Comment.builder()
                        .id(rs.getInt("id"))
                        .userId(rs.getInt("user_id"))
                        .postId(rs.getInt("post_id"))
                        .content(rs.getString("content"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .build());
            }

            for(int i = 0; i < parameters.size() - 2; i++) {
                countStmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet countRs = countStmt.executeQuery();
            int totalElements = countRs.next() ? countRs.getInt(1) : 0;

            return new Page<>(comments, pageNumber, pageSize, totalElements);

        } catch (SQLException e){
            e.printStackTrace();
            return new Page<>(Collections.emptyList(), pageNumber, pageSize, 0);
        }
    }

    public List<Comment> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM COMMENTS WHERE user_id = ?";
        List<Comment> comments = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,user_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                comments.add(Comment.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }
    public List<Comment> findAllByPostId(int post_id) {
        String sql = "SELECT * FROM COMMENTS WHERE post_id = ?";
        List<Comment> comments = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,post_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                comments.add(Comment.builder()
                        .id(resultset.getInt("id"))
                        .userId(resultset.getInt("user_id"))
                        .postId(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .createdAt(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public boolean update(Comment comment) {
        String sql = "UPDATE COMMENTS SET content = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,comment.getContent());
            preparedStatement.setInt(2,comment.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Comment Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Comment comment) {
        String sql = "DELETE FROM COMMENTS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,comment.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Comment Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Comment comment) {
        String sql = "INSERT INTO COMMENTS(user_id,post_id,content,created_at) VALUES(?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,comment.getUserId());
            preparedStatement.setInt(2,comment.getPostId());
            preparedStatement.setString(3,comment.getContent());
            preparedStatement.setObject(4,comment.getCreatedAt());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Comment Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
