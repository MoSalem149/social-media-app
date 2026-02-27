package com.socialmediaapp.View;

import com.socialmediaapp.Model.User;
import com.socialmediaapp.SocialMediaApplication;
import com.socialmediaapp.Util.AppContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class RegisterView {
    private final VBox root;
    private final TextField nameField;
    private final TextField emailField;
    private final PasswordField passwordField;
    private final TextArea bioArea;
    private final Label errorLabel;
    private final Label profilePicLabel;
    private File selectedProfilePic;

    public RegisterView() {
        Label title = new Label("Create Account");
        title.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #1a1a2e;");

        nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setPrefWidth(280);
        nameField.setMaxWidth(280);
        nameField.setStyle("-fx-text-fill: #1a1a2e;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(280);
        emailField.setMaxWidth(280);
        emailField.setStyle("-fx-text-fill: #1a1a2e;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(280);
        passwordField.setMaxWidth(280);
        passwordField.setStyle("-fx-text-fill: #1a1a2e;");

        bioArea = new TextArea();
        bioArea.setPromptText("Bio (optional)");
        bioArea.setPrefRowCount(2);
        bioArea.setPrefWidth(280);
        bioArea.setMaxWidth(280);
        bioArea.setWrapText(true);
        bioArea.setStyle("-fx-text-fill: #1a1a2e;");

        profilePicLabel = new Label("No profile picture chosen");
        profilePicLabel.setStyle("-fx-text-fill: #444;");
        Button uploadProfilePicBtn = new Button("Upload profile picture");
        uploadProfilePicBtn.setStyle("-fx-cursor: hand; -fx-text-fill: #1a1a2e;");
        uploadProfilePicBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            Stage stage = (Stage) uploadProfilePicBtn.getScene().getWindow();
            File f = fc.showOpenDialog(stage);
            if (f != null) {
                selectedProfilePic = f;
                profilePicLabel.setText(f.getName());
            }
        });

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        Button registerBtn = new Button("Register");
        registerBtn.setDefaultButton(true);
        registerBtn.setPrefWidth(120);
        registerBtn.setStyle("-fx-text-fill: #1a1a2e;");
        registerBtn.setOnAction(e -> doRegister());

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle("-fx-text-fill: #1877f2;");
        backLink.setOnAction(e -> SocialMediaApplication.showLoginScene());

        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.getChildren().addAll(title, nameField, emailField, passwordField, bioArea,
                profilePicLabel, uploadProfilePicBtn, errorLabel, registerBtn, backLink);
        form.setPadding(new Insets(30));

        root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f2f5;");
    }

    private void doRegister() {
        errorLabel.setText("");
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String bio = bioArea.getText() == null ? "" : bioArea.getText().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Name, email and password are required.");
            return;
        }
        try {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .bio(bio.isEmpty() ? null : bio)
                    .build();
            AppContext.getAuthService().register(user, Optional.ofNullable(selectedProfilePic));
            SocialMediaApplication.showMainScene();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    public VBox getRoot() {
        return root;
    }
}
