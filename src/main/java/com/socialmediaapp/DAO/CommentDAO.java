package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentDAO implements DAO<Comment>{
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
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
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
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }
    public List<Comment> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM COMMENTS WHERE user_id = ?";
        List<Comment> comments = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                comments.add(Comment.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
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
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                comments.add(Comment.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .content(resultset.getString("content"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
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
            preparedStatement.setInt(1,comment.getUser_id());
            preparedStatement.setInt(2,comment.getPost_id());
            preparedStatement.setString(3,comment.getContent());
            preparedStatement.setObject(4,comment.getCreated_at());
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
