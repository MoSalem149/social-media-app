package com.socialmediaapp.Controller;

import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.AuthService;
import com.socialmediaapp.SocialMediaApp;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private SocialMediaApp mainApp;
    private AuthService authService;

    public void setMainApp(SocialMediaApp mainApp) { this.mainApp = mainApp; }

    @FXML
    private void initialize() {
        authService = ServiceRegistry.getInstance().getAuthService();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        try {
            authService.login(email, password);
            mainApp.showMainScene();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register");
        dialog.setHeaderText("Create a new account");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        TextArea bioArea = new TextArea();
        bioArea.setPromptText("Bio");
        Button chooseImageButton = new Button("Choose Profile Picture");
        Label imageLabel = new Label("No file chosen");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File[] selectedImage = new File[1];
        chooseImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedImage[0] = file;
                imageLabel.setText(file.getName());
            }
        });

        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(8,
                new Label("Name:"), nameField,
                new Label("Email:"), emailField,
                new Label("Password:"), passField,
                new Label("Bio:"), bioArea,
                chooseImageButton, imageLabel
        ));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                User user = new User();
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setPassword(passField.getText());
                user.setBio(bioArea.getText());
                user.setCreatedAt(LocalDateTime.now());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                authService.register(user, Optional.ofNullable(selectedImage[0]));
                errorLabel.setText("Registration successful! Please log in.");
            } catch (Exception e) {
                errorLabel.setText("Registration failed: " + e.getMessage());
            }
        });
    }
}