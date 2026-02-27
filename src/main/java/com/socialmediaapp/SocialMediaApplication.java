package com.socialmediaapp;

import com.socialmediaapp.Util.DDL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SocialMediaApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DDL.createDatabase();
        DDL.createTables();

        FXMLLoader loader = new FXMLLoader(SocialMediaApplication.class.getResource("/com/socialmediaapp/View/AppViews.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(SocialMediaApplication.class.getResource("/com/socialmediaapp/Style/AppStyle.css").toExternalForm());

        stage.setTitle("Social Media Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
