package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Model.Like;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LikeDAO implements DAO<Like>{
    @Override
    public Optional<Like> findById(int id) {
        String sql = "SELECT * FROM LIKES WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Like like = Like.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(like);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findAll() {
        String sql = "SELECT * FROM LIKES";
        List<Like> likes = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                likes.add(Like.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return likes;
    }
    public List<Like> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM Likes WHERE user_id = ?";
        List<Like> likes = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                likes.add(Like.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return likes;
    }
    public List<Like> findAllByPostId(int post_id) {
        String sql = "SELECT * FROM LIKES WHERE post_id = ?";
        List<Like> likes = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                likes.add(Like.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .post_id(resultset.getInt("post_id"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return likes;
    }

    @Override
    public boolean update(Like like) {
        return false;
    }

    @Override
    public boolean delete(Like like) {
        String sql = "DELETE FROM LIKES WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,like.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Like Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Like like) {
        String sql = "INSERT INTO LIKES(user_id,post_id,created_at) VALUES(?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,like.getUser_id());
            preparedStatement.setInt(2,like.getPost_id());
            preparedStatement.setObject(3,like.getCreated_at());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Like Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
