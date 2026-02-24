package com.socialmediaapp.Util;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
public class DDL {
    public static void createDatabase(){
        try(Connection connection = DBConnection.getBootstrapDataSource().getConnection();
            Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE IF NOT EXISTS "+DBConnection.getDATABASE_NAME();
            statement.executeUpdate(sql);
            System.out.println("Database Created Successfully!");
        } catch (SQLException e) {
            System.out.println("Database not created\n"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTables()  {
        try(Connection connection = DBConnection.getAppDataSource().getConnection();
        Statement statement = connection.createStatement();){
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    name VARCHAR(100) NOT NULL,\n" +
                            "    email VARCHAR(100) UNIQUE NOT NULL,\n" +
                            "    password VARCHAR(255) NOT NULL,\n" +
                            "    bio TEXT,\n" +
                            "    profile_pic VARCHAR(255) DEFAULT 'default-avatar.png',\n" +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                            ");"
            );
            System.out.println("Users Table Created Successfully!");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS posts (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    user_id INT NOT NULL,\n" +
                            "    content TEXT,\n" +
                            "    image_path VARCHAR(255),\n" +
                            "    privacy ENUM('public', 'friends', 'private') DEFAULT 'public',\n" +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                            "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                            ");"
            );
            System.out.println("Posts Table Created Successfully!");

            statement.executeUpdate(
              "CREATE TABLE IF NOT EXISTS friends (\n" +
                      "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                      "    user_id INT NOT NULL,\n" +
                      "    friend_id INT NOT NULL,\n" +
                      "    status ENUM('pending', 'accepted') DEFAULT 'pending',\n" +
                      "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                      "    CONSTRAINT chk_user_less_than_friend CHECK (user_id < friend_id),\n" +
                      "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                      "    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                      ");"
            );
            System.out.println("Friends Table Created Successfully!");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS likes (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    user_id INT NOT NULL,\n" +
                            "    post_id INT NOT NULL,\n" +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                            "    UNIQUE KEY unique_like (user_id, post_id),\n" +
                            "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                            "    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE\n" +
                            ");"
            );
            System.out.println("Likes Table Created Successfully!");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS comments (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    user_id INT NOT NULL,\n" +
                            "    post_id INT NOT NULL,\n" +
                            "    content TEXT NOT NULL,\n" +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                            "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                            "    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE\n" +
                            ");"
            );
            System.out.println("Comments Table Created Successfully!");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS notifications (\n" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                            "    user_id INT NOT NULL,\n" +
                            "    sender_id INT NOT NULL,\n" +
                            "    type ENUM('like', 'comment', 'friend_request', 'friend_accept') NOT NULL,\n" +
                            "    reference_id INT,\n" +
                            "    is_read BOOLEAN DEFAULT FALSE,\n" +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                            "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,\n" +
                            "    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                            ");"
            );
            System.out.println("Notifications Table Created Successfully!");
            System.out.println("Tables Created Successfully!");
        }catch (SQLException e){
            System.out.println("Tables not created\n"+e.getMessage());
            e.printStackTrace();
        }
    }
}
