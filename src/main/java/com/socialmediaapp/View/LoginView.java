package com.socialmediaapp.View;

import com.socialmediaapp.SocialMediaApplication;
import com.socialmediaapp.Util.AppContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginView {
    private final VBox root;
    private final TextField emailField;
    private final PasswordField passwordField;
    private final Label errorLabel;

    public LoginView() {
        Label title = new Label("Social Media App");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #1a1a2e;");

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

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        loginBtn.setPrefWidth(120);
        loginBtn.setStyle("-fx-text-fill: #1a1a2e;");
        loginBtn.setOnAction(e -> doLogin());

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setStyle("-fx-text-fill: #1877f2;");
        registerLink.setOnAction(e -> SocialMediaApplication.showRegisterScene());

        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.getChildren().addAll(title, emailField, passwordField, errorLabel, loginBtn, registerLink);
        form.setPadding(new Insets(40));

        root = new VBox(form);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f2f5;");
    }

    private void doLogin() {
        errorLabel.setText("");
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }
        try {
            AppContext.getAuthService().login(email, password);
            SocialMediaApplication.showMainScene();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    public VBox getRoot() {
        return root;
    }
}
