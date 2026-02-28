package com.socialmediaapp;

import com.socialmediaapp.Controller.CommentsController;
import com.socialmediaapp.Controller.FriendsController;
import com.socialmediaapp.Controller.LoginController;
import com.socialmediaapp.Controller.MainController;
import com.socialmediaapp.Controller.ProfileController;
import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.DDL;
import com.socialmediaapp.Util.ServiceRegistry;
import com.socialmediaapp.Util.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SocialMediaApp extends Application {
    private Stage primaryStage;

    // DAOs
    private UserDAO userDAO = new UserDAO();
    private PostDAO postDAO = new PostDAO();
    private CommentDAO commentDAO = new CommentDAO();
    private LikeDAO likeDAO = new LikeDAO();
    private FriendDAO friendDAO = new FriendDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Session
    private UserSession userSession = UserSession.getInstance();

    // Services
    private AuthService authService = new AuthService(userSession, userDAO);
    private UserService userService = new UserService(userDAO, friendDAO, postDAO, commentDAO, likeDAO, authService);
    private PostService postService = new PostService(userDAO, postDAO, commentDAO, authService);
    private CommentService commentService = new CommentService(userDAO, commentDAO, postDAO, authService);
    private LikeService likeService = new LikeService(userDAO, postDAO, likeDAO, authService);
    private FriendService friendService = new FriendService(friendDAO, userDAO, authService);
    private NotificationService notificationService = new NotificationService(notificationDAO, authService);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize ServiceRegistry
        ServiceRegistry.initialize(authService, userService, postService, commentService, friendService, likeService, notificationService);

        showLoginScene();
    }

    public void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialmediaapp/View/login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.setTitle("Social Media App - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialmediaapp/View/main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Social Media App - Feed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProfileScene(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialmediaapp/View/profile.fxml"));
            Parent root = loader.load();
            ProfileController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUser(user);
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Profile - " + user.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFriendsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialmediaapp/View/friends.fxml"));
            Parent root = loader.load();
            FriendsController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Friends");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCommentsScene(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/socialmediaapp/View/comments.fxml"));
            Parent root = loader.load();
            CommentsController controller = loader.getController();
            controller.setMainApp(this);
            controller.setPost(post);
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Comments");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        DDL.createDatabase();
        DDL.createTables();
        launch(args);
    }
}