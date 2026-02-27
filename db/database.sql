-- ================================================
-- Social Media Application - Database Schema
-- DEBI Internship Project
-- Team: Mohamed Salem & Mohamed Tarek
-- ================================================

CREATE DATABASE IF NOT EXISTS socialmedia_db;
USE socialmedia_db;

-- ================================================
-- Table 1: users
-- ================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    bio TEXT,
    profile_pic VARCHAR(255) DEFAULT 'default-avatar.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- Table 2: posts
-- ================================================
CREATE TABLE posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT,
    image_path VARCHAR(255),
    privacy ENUM('public', 'friends', 'private') DEFAULT 'public',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ================================================
-- Table 3: friends
-- ================================================
CREATE TABLE friends (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    status ENUM('pending', 'accepted') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_less_than_friend CHECK (user_id < friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ================================================
-- Table 4: likes
-- ================================================
CREATE TABLE likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_like (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- ================================================
-- Table 5: comments
-- ================================================
CREATE TABLE comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- ================================================
-- Table 6: notifications
-- ================================================
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    sender_id INT NOT NULL,
    type ENUM('like', 'comment', 'friend_request', 'friend_accept') NOT NULL,
    reference_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ================================================
-- Sample Data for Testing (optional)
-- Passwords here are plain text; for secure login use
-- the app's Register screen (passwords are hashed there).
-- ================================================
INSERT INTO users (name, email, password, bio) VALUES
('Mohamed Salem', 'salem@test.com', '123456', 'Java Developer'),
('Mohamed Tarek', 'tarek@test.com', '123456', 'UI Developer'),
('Test User', 'test@test.com', '123456', 'Test Account');

INSERT INTO posts (user_id, content, privacy) VALUES
(1, 'Hello from Mohamed Salem!', 'public'),
(2, 'Hello from Mohamed Tarek!', 'public'),
(1, 'This is a friends only post', 'friends');

INSERT INTO friends (user_id, friend_id, status) VALUES
(1, 2, 'accepted'),
(2, 1, 'accepted');

INSERT INTO likes (user_id, post_id) VALUES
(2, 1),
(1, 2);

INSERT INTO comments (user_id, post_id, content) VALUES
(2, 1, 'Nice post Salem!'),
(1, 2, 'Nice post Tarek!');
