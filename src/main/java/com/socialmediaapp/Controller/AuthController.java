package com.socialmediaapp.Controller;

import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.AuthService;
import com.socialmediaapp.SocialMediaApplication;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.io.File;
import java.util.Optional;

public class AuthController {

    private AuthService authService;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;

    @FXML private TextField registerNameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;

    @FXML private Label statusLabel;
    @FXML private Label registerImageLabel;

    private File registerImageFile;

    @FXML
    private void initialize() {
        authService = ServiceRegistry.getInstance().getAuthService();
    }

    @FXML
    private void onChooseRegisterImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select profile picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(statusLabel.getScene().getWindow());
        if (file != null) {
            registerImageFile = file;
            registerImageLabel.setText(file.getName());
        } else {
            registerImageFile = null;
            registerImageLabel.setText("No image selected");
        }
    }

    @FXML
    private void onLogin() {
        try {
            authService.login(loginEmailField.getText().trim(), loginPasswordField.getText());
            setStatus("Login successful. Loading your feed...");
            openMainView();
        } catch (Exception e) {
            setStatus("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        try {
            User user = User.builder()
                    .name(registerNameField.getText().trim())
                    .email(registerEmailField.getText().trim())
                    .password(registerPasswordField.getText())
                    .bio("New user")
                    .createdAt(LocalDateTime.now())
                    .build();
            authService.register(user, Optional.ofNullable(registerImageFile));
            setStatus("Registration successful. You can sign in now.");
        } catch (Exception e) {
            setStatus("Register failed: " + e.getMessage());
        }
    }

    private void openMainView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    SocialMediaApplication.class.getResource("/com/socialmediaapp/View/AppViews.fxml")
            );
            Scene scene = new Scene(fxmlLoader.load(), 1150, 760);
            scene.getStylesheets().add(
                    SocialMediaApplication.class
                            .getResource("/com/socialmediaapp/Style/AppStyle.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setTitle("Social Media Application");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            setStatus("Failed to open main view: " + msg);
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}

