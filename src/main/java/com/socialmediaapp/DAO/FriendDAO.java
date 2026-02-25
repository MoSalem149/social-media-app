package com.socialmediaapp.DAO;

import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Model.Friend;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendDAO implements DAO<Friend>{
    @Override
    public Optional<Friend> findById(int id) {
        String sql = "SELECT * FROM FRIENDS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                Friend friend = Friend.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .friend_id(resultset.getInt("friend_id"))
                        .status(resultset.getObject("status", Status.class))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(friend);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Friend> findAll() {
        String sql = "SELECT * FROM FRIENDS";
        List<Friend> friends = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                friends.add(Friend.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .friend_id(resultset.getInt("friend_id"))
                        .status(resultset.getObject("status", Status.class))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return friends;
    }
    public List<Friend> findAllByUserId(int user_id) {
        String sql = "SELECT * FROM FRIENDS WHERE user_id = ?";
        List<Friend> friends = new ArrayList<>();
        try (Connection connection = DBConnection.getAppDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                friends.add(Friend.builder()
                        .id(resultset.getInt("id"))
                        .user_id(resultset.getInt("user_id"))
                        .friend_id(resultset.getInt("friend_id"))
                        .status(resultset.getObject("status", Status.class))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return friends;
    }
    @Override
    public boolean update(Friend friend) {
        String sql = "UPDATE FRIENDS SET friend_id = ?, status = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getFriend_id());
            preparedStatement.setString(2,friend.getStatus().name());
            preparedStatement.setInt(3,friend.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Friend friend) {
        String sql = "DELETE FROM FRIENDS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Deleted!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(Friend friend) {
        String sql = "INSERT INTO FRIENDS(user_id,friend_id,status,created_at) VALUES(?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,friend.getUser_id());
            preparedStatement.setInt(2,friend.getFriend_id());
            preparedStatement.setString(3,friend.getStatus().name());
            preparedStatement.setObject(5,friend.getCreated_at());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("Friend Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
