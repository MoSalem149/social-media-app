package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostDAO implements DAO<Post>{
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
                        .user_id(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .image_path(resultset.getString("image_path"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
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
                        .user_id(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .image_path(resultset.getString("image_path"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
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
                        .user_id(resultset.getInt("user_id"))
                        .content(resultset.getString("content"))
                        .privacy(resultset.getObject("privacy", Privacy.class))
                        .image_path(resultset.getString("image_path"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }
    @Override
    public boolean update(Post post) {
        String sql = "UPDATE POSTS SET content = ?, privacy = ?, image_path = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,post.getContent());
            preparedStatement.setString(2,post.getPrivacy().name());
            preparedStatement.setString(3,post.getImage_path());
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
        String sql = "INSERT INTO POSTS(user_id,content,privacy,image_path,created_at) VALUES(?,?,?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,post.getUser_id());
            preparedStatement.setString(2,post.getContent());
            preparedStatement.setString(3,post.getPrivacy().name());
            preparedStatement.setString(4,post.getImage_path());
            preparedStatement.setObject(5,post.getCreated_at());
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
