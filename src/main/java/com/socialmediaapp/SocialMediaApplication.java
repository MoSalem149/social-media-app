package com.socialmediaapp;

import com.socialmediaapp.Util.DDL;
import com.socialmediaapp.Util.AppContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SocialMediaApplication extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Social Media App");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        showLoginScene();
        primaryStage.show();
    }

    public static void showLoginScene() {
        Scene scene = new Scene(new com.socialmediaapp.View.LoginView().getRoot(), 500, 400);
        primaryStage.setScene(scene);
    }

    public static void showRegisterScene() {
        Scene scene = new Scene(new com.socialmediaapp.View.RegisterView().getRoot(), 500, 520);
        primaryStage.setScene(scene);
    }

    public static void showMainScene() {
        Scene scene = new Scene(new com.socialmediaapp.View.MainView().getRoot(), 900, 650);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        DDL.createDatabase();
        DDL.createTables();
        AppContext.init();
        Application.launch(args);
    }
}
