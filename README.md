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

### Prerequisites

- **Java 21** (with JavaFX included or use Maven/IDE)
- **MySQL** server running; create the database using `db/database.sql` or let the app create tables on first run.

Update DB credentials in `com.socialmediaapp.Util.DBConnection` (user, password, port if not 3306).

### Option 1: Run the JAR file

```bash
java -jar social-media-app.jar
```

### Option 2: Run from IntelliJ IDEA (recommended)

JavaFX is not included in the standard JDK (Java 11+), so running the main class directly can cause:  
*"JavaFX runtime components are missing, and are required to run this application"*.

**Use one of these ways:**

**A) Use the included run configuration (easiest)**  
1. In the top-right run configurations dropdown, select **"Run JavaFX App"**.  
2. Click the green **Run** button.  
   (This runs Maven’s `javafx:run`, which starts the app with JavaFX.)

**B) Run via Maven tool window**  
1. Open **View → Tool Windows → Maven**.  
2. Expand **social-media-app → Plugins → javafx**.  
3. Double-click **javafx:run**.

**C) Run the main class with VM options**  
1. **Run → Edit Configurations**.  
2. Select or create an **Application** config with main class: `com.socialmediaapp.SocialMediaApplication`.  
3. In **Modify options**, enable **"Add VM options"**.  
4. In **VM options**, paste (adjust the path to your Maven repo if needed):

   **Windows (PowerShell / CMD):**
   ```
   --module-path "%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21\javafx-base-21-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21\javafx-controls-21-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21\javafx-graphics-21-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\21\javafx-fxml-21-win.jar" --add-modules javafx.controls,javafx.fxml
   ```

   **macOS / Linux:**
   ```
   --module-path "$HOME/.m2/repository/org/openjfx/javafx-base/21/javafx-base-21.jar:$HOME/.m2/repository/org/openjfx/javafx-controls/21/javafx-controls-21.jar:$HOME/.m2/repository/org/openjfx/javafx-graphics/21/javafx-graphics-21.jar:$HOME/.m2/repository/org/openjfx/javafx-fxml/21/javafx-fxml-21.jar" --add-modules javafx.controls,javafx.fxml
   ```

   Use the correct platform suffix in the JAR names: `-win` (Windows), `-mac` (macOS), or none (Linux). Run `mvn dependency:resolve` once so the JARs exist in your local repo.

### Option 3: Maven from terminal

```bash
mvn clean javafx:run
```

**Note:** If you use the sample data from `database.sql`, those user passwords are stored in plain text. For secure login, use the app’s **Register** screen to create an account (passwords are hashed).

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
