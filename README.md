# 📱 Social Media Application

A full-featured Social Media Application developed as part of the **Digital Egypt Builders Initiative (DEBI)** program.

Built with **Java**, **JavaFX**, and **MySQL**, applying OOP principles, Data Structures, and clean layered architecture.

---

## 👥 Team Members

| Name |
|---|
| Mohamed Salem |
| Mohamed Tarek |

---

## 🚀 Features

### Core Features

- 🔐 User Registration & Login with password hashing
- 👤 User Profile Management (bio, profile picture, personal info)
- 📝 Post Updates (text and images)
- 📰 News Feed with pagination
- ❤️ Likes & Comments on posts
- 👫 Friend System (send, accept, manage friends)

### Advanced Features

- 🔔 Notifications (likes, comments, friend requests)
- 🔍 Search for users and posts
- 🔒 Privacy Settings (public, friends-only, private)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| GUI | JavaFX 17+ |
| Database | MySQL |
| DB Connectivity | JDBC |
| Password Security | SHA-256 / BCrypt |
| Build Tool | Maven |
| UI Design | SceneBuilder (.fxml) |

---

## 🗂️ Project Structure

```
src/
├── models/ # OOP models (User, Post, Comment, etc.)
├── dao/ # Database Access Objects
├── controllers/ # JavaFX screen controllers
├── views/ # FXML UI files
└── utils/ # DBConnection, PasswordHashing
```

---

## 🗄️ Database Setup

1. Open MySQL and run the provided `.sql` file:

   ```bash
   mysql -u root -p < database.sql
   ```

2. Update DB credentials in `src/utils/DBConnection.java`.

---

## ▶️ How to Run

### Option 1: Run the JAR file

```bash
java -jar social-media-app.jar
```

### Option 2: Run from source

1. Clone the repository:

   ```bash
   git clone https://github.com/MoSalem149/social-media-app.git
   ```

2. Open in IntelliJ IDEA or Eclipse

3. Install dependencies via Maven

4. Run `Main.java`

---

## 📦 Submission Contents

- `README.md` — This file
- `social-media-app.jar` — Runnable JAR file
- `database.sql` — MySQL database schema and seed data
- `repo-link.txt` — GitHub repository link
- `team-members.txt` — Team names and IDs

---

## 📄 License

This project was developed for educational purposes as part of the DEBI program by the Ministry of Communications and Information Technology, Egypt.
