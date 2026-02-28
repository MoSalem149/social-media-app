package com.socialmediaapp.Controller;

import com.socialmediaapp.SocialMediaApp;
import com.socialmediaapp.Model.Friend;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class ProfileController {
    @FXML private Label nameLabel, emailLabel, bioLabel;
    @FXML private ImageView profilePic;
    @FXML private ListView<Post> userPostsListView;
    @FXML private Button editProfileButton, addFriendButton, backButton;

    private SocialMediaApp mainApp;
    private UserService userService;
    private PostService postService;
    private FriendService friendService;
    private AuthService authService;
    private NotificationService notificationService;
    private User displayedUser;

    @FXML
    private void initialize() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.userService = registry.getUserService();
        this.postService = registry.getPostService();
        this.friendService = registry.getFriendService();
        this.authService = registry.getAuthService();
        this.notificationService = registry.getNotificationService();
    }

    public void setMainApp(SocialMediaApp mainApp) { this.mainApp = mainApp; }
    public void setUser(User user) {
        this.displayedUser = user;
        loadUserData();
        loadUserPosts();
        configureButtons();
    }

    private void loadUserData() {
        nameLabel.setText(displayedUser.getName());
        emailLabel.setText(displayedUser.getEmail());
        bioLabel.setText(displayedUser.getBio());
        if (displayedUser.getProfilePic() != null && !displayedUser.getProfilePic().isEmpty()) {
            profilePic.setImage(new Image(displayedUser.getProfilePic(), true));
        }
    }

    private void loadUserPosts() {
        var page = postService.getAllPostsAsPage(0, 20, "createdAt", "DESC", displayedUser.getId(), 0);
        userPostsListView.getItems().setAll(page.getContent());
        // Optionally set cell factory similar to MainController
    }

    private void configureButtons() {
        User current = authService.getCurrentUser();
        if (current.getId() == displayedUser.getId()) {
            editProfileButton.setVisible(true);
            addFriendButton.setVisible(false);
        } else {
            editProfileButton.setVisible(false);
            addFriendButton.setVisible(true);
            // Check if already friends (optional)
        }
    }

    @FXML
    private void handleEditProfile() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(displayedUser.getName());
        TextArea bioArea = new TextArea(displayedUser.getBio());
        Button chooseImageButton = new Button("Change Profile Picture");
        Label imageLabel = new Label("No new file chosen");
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
                new Label("Bio:"), bioArea,
                chooseImageButton, imageLabel
        ));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                displayedUser.setName(nameField.getText());
                displayedUser.setBio(bioArea.getText());
                return displayedUser;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedUser -> {
            try {
                userService.updateUser(updatedUser, Optional.ofNullable(selectedImage[0]));
                loadUserData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAddFriend() {
        Friend friend = new Friend();
        friend.setUserId(authService.getCurrentUser().getId());
        friend.setFriendId(displayedUser.getId());
        try {
            friendService.createFriend(friend);
            addFriendButton.setText("Request Sent");
            addFriendButton.setDisable(true);
        } catch (IllegalArgumentException e) {
            // Show alert
        }
    }

    @FXML
    private void goBack() {
        mainApp.showMainScene();
    }
}