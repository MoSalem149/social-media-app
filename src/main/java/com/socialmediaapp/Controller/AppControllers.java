package com.socialmediaapp.Controller;

import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Enum.Type;
import com.socialmediaapp.Model.*;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.PasswordHashing;
import com.socialmediaapp.Util.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AppControllers {

    private final UserDAO userDAO = new UserDAO();
    private final PostDAO postDAO = new PostDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final LikeDAO likeDAO = new LikeDAO();
    private final FriendDAO friendDAO = new FriendDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    private final AuthService authService = new AuthService(UserSession.getInstance(), userDAO);
    private final UserService userService = new UserService(userDAO, friendDAO, postDAO, commentDAO, likeDAO, authService);
    private final PostService postService = new PostService(userDAO, postDAO, commentDAO, authService);
    private final CommentService commentService = new CommentService(userDAO, commentDAO, postDAO, authService);
    private final FriendService friendService = new FriendService(friendDAO, userDAO, authService);
    private final LikeService likeService = new LikeService(userDAO, postDAO, likeDAO, authService);

    @FXML private Label sessionLabel;

    @FXML private TextField registerNameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private TextArea registerBioField;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;

    @FXML private TextField profileNameField;
    @FXML private TextField profileEmailField;
    @FXML private PasswordField profilePasswordField;
    @FXML private TextArea profileBioField;

    @FXML private TextArea postContentField;
    @FXML private ComboBox<Privacy> privacyCombo;
    @FXML private ListView<String> feedList;
    @FXML private TextField commentPostIdField;
    @FXML private TextArea commentContentField;
    @FXML private TextField likePostIdField;

    @FXML private TextField friendIdField;
    @FXML private ListView<String> friendList;

    @FXML private ListView<String> notificationList;
    @FXML private TextField searchTermField;
    @FXML private ListView<String> searchList;

    @FXML
    public void initialize() {
        privacyCombo.setItems(FXCollections.observableArrayList(Privacy.values()));
        privacyCombo.setValue(Privacy.PUBLIC);
        refreshAllLists();
        refreshSession();
    }

    @FXML
    public void onRegister() {
        try {
            User user = User.builder()
                    .name(registerNameField.getText())
                    .email(registerEmailField.getText())
                    .password(registerPasswordField.getText())
                    .bio(registerBioField.getText())
                    .build();
            authService.register(user, Optional.empty());
            info("Registration completed successfully.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onLogin() {
        try {
            authService.login(loginEmailField.getText(), loginPasswordField.getText());
            refreshSession();
            loadProfileForm();
            refreshAllLists();
            info("Login successful.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onLogout() {
        authService.logout();
        refreshSession();
        refreshAllLists();
        info("Logged out.");
    }

    @FXML
    public void onUpdateProfile() {
        try {
            User current = requireCurrentUser();
            current.setName(profileNameField.getText());
            current.setEmail(profileEmailField.getText());
            current.setBio(profileBioField.getText());
            if (!profilePasswordField.getText().isBlank()) {
                current.setPassword(PasswordHashing.hashPassword(profilePasswordField.getText()));
            }
            userService.updateUser(current, Optional.empty());
            authService.setCurrentUser(userService.getUserById(current.getId()));
            refreshSession();
            info("Profile updated.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onCreatePost() {
        try {
            requireCurrentUser();
            Post post = Post.builder()
                    .content(postContentField.getText())
                    .privacy(privacyCombo.getValue())
                    .build();
            postService.createPost(post, Optional.empty());
            postContentField.clear();
            refreshFeed();
            info("Post created.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onCreateComment() {
        try {
            User current = requireCurrentUser();
            Comment comment = Comment.builder()
                    .userId(current.getId())
                    .postId(Integer.parseInt(commentPostIdField.getText()))
                    .content(commentContentField.getText())
                    .build();
            commentService.createComment(comment);
            notifyPostOwner(comment.getPostId(), Type.COMMENT);
            commentContentField.clear();
            refreshFeed();
            info("Comment created.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onLikePost() {
        try {
            User current = requireCurrentUser();
            Like like = Like.builder()
                    .userId(current.getId())
                    .postId(Integer.parseInt(likePostIdField.getText()))
                    .build();
            likeService.createLike(like);
            notifyPostOwner(like.getPostId(), Type.LIKE);
            refreshFeed();
            info("Post liked.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onSendFriendRequest() {
        try {
            User current = requireCurrentUser();
            int targetId = Integer.parseInt(friendIdField.getText());
            Friend friend = Friend.builder().userId(current.getId()).friendId(targetId).build();
            friendService.createFriend(friend);
            createNotification(targetId, Type.FRIEND_REQUEST, targetId);
            refreshFriends();
            info("Friend request sent.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onAcceptFirstPending() {
        try {
            User current = requireCurrentUser();
            List<Friend> all = friendService.getAllFriends();
            Friend pending = all.stream()
                    .filter(f -> f.getStatus() == Status.PENDING && (f.getFriendId() == current.getId() || f.getUserId() == current.getId()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("No pending friend request found."));

            pending.setStatus(Status.ACCEPTED);
            friendService.updateFriend(pending);
            int receiver = pending.getUserId() == current.getId() ? pending.getFriendId() : pending.getUserId();
            createNotification(receiver, Type.FRIEND_ACCEPT, pending.getId());
            refreshFriends();
            info("Friend request accepted.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    @FXML
    public void onSearch() {
        String term = searchTermField.getText().toLowerCase();
        List<String> users = userService.getAllUsers().stream()
                .filter(u -> u.getName().toLowerCase().contains(term) || u.getEmail().toLowerCase().contains(term))
                .map(u -> "User #" + u.getId() + " - " + u.getName() + " (" + u.getEmail() + ")")
                .toList();

        List<String> posts = postService.getAllPosts().stream()
                .filter(p -> p.getContent() != null && p.getContent().toLowerCase().contains(term))
                .map(p -> "Post #" + p.getId() + " by user #" + p.getUserId() + " => " + p.getContent())
                .toList();

        searchList.setItems(FXCollections.observableArrayList());
        searchList.getItems().addAll(users);
        searchList.getItems().addAll(posts);
    }

    @FXML
    public void onRefresh() {
        refreshAllLists();
    }

    private void refreshAllLists() {
        refreshFeed();
        refreshFriends();
        refreshNotifications();
    }

    private void refreshFeed() {
        List<String> items = postService.getAllPosts().stream().map(post -> {
            long likesCount = likeService.getAllLikesByPostId(post.getId()).size();
            long commentsCount = commentService.getAllComments().stream().filter(c -> c.getPostId() == post.getId()).count();
            return "Post #" + post.getId() + " | user #" + post.getUserId() + " | " + post.getPrivacy() + "\n"
                    + (post.getContent() == null ? "" : post.getContent())
                    + "\nLikes: " + likesCount + " | Comments: " + commentsCount;
        }).toList();
        feedList.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshFriends() {
        List<String> items = friendService.getAllFriends().stream()
                .map(f -> "Friendship #" + f.getId() + " - (" + f.getUserId() + " ↔ " + f.getFriendId() + ") status=" + f.getStatus())
                .toList();
        friendList.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshNotifications() {
        User current = authService.getCurrentUser();
        if (current == null) {
            notificationList.setItems(FXCollections.observableArrayList());
            return;
        }
        List<String> items = notificationDAO.findAllByUserId(current.getId()).stream()
                .map(n -> "Notification #" + n.getId() + " - " + n.getType() + " from user #" + n.getSenderId() + " (read=" + n.isRead() + ")")
                .toList();
        notificationList.setItems(FXCollections.observableArrayList(items));
    }

    private void createNotification(int targetUserId, Type type, int referenceId) {
        User current = requireCurrentUser();
        Notification notification = Notification.builder()
                .userId(targetUserId)
                .senderId(current.getId())
                .type(type)
                .referenceId(referenceId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationDAO.save(notification);
        refreshNotifications();
    }

    private void notifyPostOwner(int postId, Type type) {
        Post post = postService.getPostById(postId);
        if (post.getUserId() != requireCurrentUser().getId()) {
            createNotification(post.getUserId(), type, postId);
        }
    }

    private void refreshSession() {
        User current = authService.getCurrentUser();
        sessionLabel.setText(current == null ? "Session: not logged in" : "Session: " + current.getName() + " (#" + current.getId() + ")");
    }

    private void loadProfileForm() {
        User current = requireCurrentUser();
        profileNameField.setText(current.getName());
        profileEmailField.setText(current.getEmail());
        profileBioField.setText(current.getBio());
    }

    private User requireCurrentUser() {
        User current = authService.getCurrentUser();
        if (current == null) {
            throw new IllegalArgumentException("Please login first.");
        }
        return current;
    }

    private void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Operation failed");
        alert.showAndWait();
    }
}
