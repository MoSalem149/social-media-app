package com.socialmediaapp.DAO;

import com.socialmediaapp.Model.User;
import com.socialmediaapp.Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User>{
    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                User user = User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profile_pic(resultset.getString("profile_pic"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(user);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
                e.printStackTrace();
        }
        return Optional.empty();
    }
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,email);
            ResultSet resultset = preparedStatement.executeQuery();
            if(resultset.next()){
                User user = User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profile_pic(resultset.getString("profile_pic"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build();
                return Optional.of(user);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        List<User> users = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                users.add(User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profile_pic(resultset.getString("profile_pic"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    public List<User> findByName(String name) {
        String sql = "SELECT * FROM USERS WHERE name Like CONCAT('%',?,'%')";
        List<User> users = new ArrayList<>();
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                users.add(User.builder()
                        .id(resultset.getInt("id"))
                        .name(resultset.getString("name"))
                        .email(resultset.getString("email"))
                        .password(resultset.getString("password"))
                        .bio(resultset.getString("bio"))
                        .profile_pic(resultset.getString("profile_pic"))
                        .created_at(resultset.getObject("created_at", LocalDateTime.class))
                        .build());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public boolean update(User user) {
        String sql = "UPDATE USERS SET name = ?, email = ?, password = ?, bio = ?, profile_pic = ? WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getBio());
            preparedStatement.setString(5,user.getProfile_pic());
            preparedStatement.setInt(6,user.getId());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("User Updated!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(User user) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
        preparedStatement.setInt(1,user.getId());
        boolean res = preparedStatement.executeUpdate() > 0;
        System.out.println("User Deleted!");
        return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO USERS(name,email,password,bio,profile_pic,created_at) VALUES(?,?,?,?,?,?)";
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getBio());
            preparedStatement.setString(5,user.getPassword());
            preparedStatement.setObject(6,user.getCreated_at());
            boolean res = preparedStatement.executeUpdate() > 0;
            System.out.println("User Created!");
            return res;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
