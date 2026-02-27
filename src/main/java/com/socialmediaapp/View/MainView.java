package com.socialmediaapp.View;

import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Model.*;
import com.socialmediaapp.SocialMediaApplication;
import com.socialmediaapp.Util.AppContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainView {
    private static final String TEXT_DARK = "-fx-text-fill: #1c1e21;";
    private static final String TEXT_MUTED = "-fx-text-fill: #65676b;";

    private final BorderPane root;
    private final StackPane centerStack;
    private final Button notificationsBtn;
    private final ScrollPane feedPane;
    private final ScrollPane profilePane;
    private final ScrollPane friendsPane;
    private final ScrollPane notificationsPane;
    private final ScrollPane searchPane;

    public MainView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #e4e6eb;");

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setStyle("-fx-background-color: #1877f2;");

        Label appTitle = new Label("Social Media");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        appTitle.setTextFill(Color.WHITE);

        Button homeBtn = new Button("Home");
        Button profileBtn = new Button("Profile");
        Button friendsBtn = new Button("Friends");
        notificationsBtn = new Button("Notifications");
        Button searchBtn = new Button("Search");
        Button logoutBtn = new Button("Logout");

        styleNavButton(homeBtn);
        styleNavButton(profileBtn);
        styleNavButton(friendsBtn);
        styleNavButton(notificationsBtn);
        styleNavButton(searchBtn);
        logoutBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand;");

        topBar.getChildren().addAll(appTitle, homeBtn, profileBtn, friendsBtn, notificationsBtn, searchBtn, new Region(), logoutBtn);
        HBox.setHgrow(topBar.getChildren().get(topBar.getChildren().size() - 2), Priority.ALWAYS);

        logoutBtn.setOnAction(e -> {
            AppContext.getAuthService().logout();
            SocialMediaApplication.showLoginScene();
        });

        centerStack = new StackPane();
        feedPane = buildFeedPane();
        profilePane = buildProfilePane();
        friendsPane = buildFriendsPane();
        notificationsPane = buildNotificationsPane();
        searchPane = buildSearchPane();

        centerStack.getChildren().add(feedPane);
        homeBtn.setOnAction(e -> switchCenter(feedPane));
        profileBtn.setOnAction(e -> { refreshProfile(); switchCenter(profilePane); });
        friendsBtn.setOnAction(e -> { refreshFriends(); switchCenter(friendsPane); });
        notificationsBtn.setOnAction(e -> { refreshNotifications(); switchCenter(notificationsPane); });
        searchBtn.setOnAction(e -> { refreshSearch(); switchCenter(searchPane); });

        root.setTop(topBar);
        root.setCenter(centerStack);
        refreshNotificationBadge();
    }

    private Label darkLabel(String text) {
        Label l = new Label(text);
        l.setStyle(TEXT_DARK);
        return l;
    }

    private void styleNavButton(Button b) {
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;"));
    }

    private void switchCenter(ScrollPane pane) {
        centerStack.getChildren().clear();
        centerStack.getChildren().add(pane);
    }

    private void refreshNotificationBadge() {
        long unread = AppContext.getNotificationService().getUnreadCount();
        notificationsBtn.setText(unread > 0 ? "Notifications (" + unread + ")" : "Notifications");
    }

    private ScrollPane buildFeedPane() {
        VBox feed = new VBox(16);
        feed.setPadding(new Insets(20));
        feed.setStyle("-fx-background-color: #e4e6eb;");
        refreshFeed(feed);
        ScrollPane sp = new ScrollPane(feed);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #e4e6eb; -fx-background-color: #e4e6eb;");
        return sp;
    }

    private void refreshFeed(VBox feed) {
        feed.getChildren().clear();
        try {
            VBox createBox = new VBox(10);
            createBox.setPadding(new Insets(12));
            createBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            TextArea newPostContent = new TextArea();
            newPostContent.setPromptText("What's on your mind?");
            newPostContent.setPrefRowCount(2);
            newPostContent.setWrapText(true);
            newPostContent.setStyle(TEXT_DARK);
            ComboBox<String> privacyCombo = new ComboBox<>();
            privacyCombo.getItems().addAll("Public", "Friends only", "Private");
            privacyCombo.setValue("Public");
            privacyCombo.setStyle(TEXT_DARK);
            Label imageLabel = new Label("No image");
            imageLabel.setStyle(TEXT_MUTED);
            File[] postImageRef = new File[1];
            Button attachImageBtn = new Button("Attach image");
            attachImageBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            attachImageBtn.setOnAction(ev -> {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                Stage stage = (Stage) feed.getScene().getWindow();
                File f = fc.showOpenDialog(stage);
                if (f != null) {
                    postImageRef[0] = f;
                    imageLabel.setText(f.getName());
                }
            });
            Button postBtn = new Button("Post");
            postBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            postBtn.setOnAction(e -> {
                String content = newPostContent.getText();
                if (content != null && !content.trim().isEmpty()) {
                    try {
                        Privacy pr = privacyCombo.getValue().startsWith("Friends") ? Privacy.FRIENDS : privacyCombo.getValue().equals("Private") ? Privacy.PRIVATE : Privacy.PUBLIC;
                        Post post = Post.builder().content(content.trim()).privacy(pr).build();
                        AppContext.getPostService().createPost(post, Optional.ofNullable(postImageRef[0]));
                        newPostContent.clear();
                        postImageRef[0] = null;
                        imageLabel.setText("No image");
                        refreshFeed(feed);
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, ex.getMessage());
                    }
                }
            });
            createBox.getChildren().addAll(newPostContent, new HBox(10, privacyCombo, imageLabel, attachImageBtn), postBtn);
            feed.getChildren().add(createBox);

            List<Post> posts = AppContext.getPostService().getFeedForCurrentUser();
            User currentUser = AppContext.getAuthService().getCurrentUser();
            for (Post post : posts) {
                User author = AppContext.getUserService().getUserById(post.getUserId());
                VBox card = buildPostCard(post, author, currentUser.getId(), true, () -> refreshFeed(feed));
                feed.getChildren().add(card);
            }
            if (posts.isEmpty()) {
                Label empty = darkLabel("No posts in your feed. Add friends or create a post above.");
                empty.setStyle(TEXT_MUTED);
                feed.getChildren().add(empty);
            }
        } catch (Exception e) {
            Label err = darkLabel("Error: " + e.getMessage());
            err.setStyle("-fx-text-fill: #c0392b;");
            feed.getChildren().add(err);
        }
    }

    private VBox buildPostCard(Post post, User author, int currentUserId, boolean showInFeed, Runnable onRefresh) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label(author.getName());
        authorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        authorLabel.setStyle(TEXT_DARK);
        Label privacyLabel = new Label(" • " + post.getPrivacy().name().toLowerCase());
        privacyLabel.setStyle(TEXT_MUTED);
        topRow.getChildren().add(authorLabel);
        topRow.getChildren().add(privacyLabel);
        boolean isOwnPost = author.getId() == currentUserId;
        if (isOwnPost) {
            Button editPostBtn = new Button("Edit");
            editPostBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            editPostBtn.setOnAction(ev -> {
                TextInputDialog d = new TextInputDialog(post.getContent());
                d.setTitle("Edit post");
                d.setHeaderText("Edit content");
                d.showAndWait().ifPresent(newContent -> {
                    try {
                        post.setContent(newContent);
                        AppContext.getPostService().updatePost(post, Optional.empty());
                        onRefresh.run();
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, ex.getMessage());
                    }
                });
            });
            Button deletePostBtn = new Button("Delete");
            deletePostBtn.setStyle("-fx-cursor: hand; -fx-text-fill: #c0392b;");
            deletePostBtn.setOnAction(ev -> {
                if (showConfirm("Delete this post?")) {
                    try {
                        AppContext.getPostService().deletePost(post);
                        onRefresh.run();
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, ex.getMessage());
                    }
                }
            });
            topRow.getChildren().add(new Region());
            HBox.setHgrow(topRow.getChildren().get(topRow.getChildren().size() - 1), Priority.ALWAYS);
            topRow.getChildren().addAll(editPostBtn, deletePostBtn);
        }
        card.getChildren().add(topRow);

        Label contentLabel = new Label(post.getContent() == null ? "" : post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle(TEXT_DARK);
        card.getChildren().add(contentLabel);

        List<Like> likes = AppContext.getLikeService().getAllLikesByPostId(post.getId());
        List<Comment> comments = AppContext.getCommentService().getCommentsByPostId(post.getId());
        boolean currentUserLiked = likes.stream().anyMatch(l -> l.getUserId() == currentUserId);

        HBox actions = new HBox(16);
        Button likeBtn = new Button(currentUserLiked ? "Liked (" + likes.size() + ")" : "Like (" + likes.size() + ")");
        likeBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
        Label commentCount = new Label(comments.size() + " comments");
        commentCount.setStyle(TEXT_DARK);
        TextField commentInput = new TextField();
        commentInput.setPromptText("Write a comment...");
        commentInput.setPrefWidth(250);
        commentInput.setStyle(TEXT_DARK);
        Button postCommentBtn = new Button("Comment");
        postCommentBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);

        likeBtn.setOnAction(e -> {
            try {
                if (currentUserLiked) {
                    Like toRemove = likes.stream().filter(l -> l.getUserId() == currentUserId).findFirst().orElse(null);
                    if (toRemove != null) AppContext.getLikeService().deleteLike(toRemove);
                } else {
                    Like like = Like.builder().userId(currentUserId).postId(post.getId()).build();
                    AppContext.getLikeService().createLike(like);
                }
                onRefresh.run();
                refreshNotificationBadge();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });

        postCommentBtn.setOnAction(ev -> {
            String text = commentInput.getText();
            if (text == null || text.isBlank()) return;
            try {
                Comment c = Comment.builder().userId(currentUserId).postId(post.getId()).content(text.trim()).build();
                AppContext.getCommentService().createComment(c);
                commentInput.clear();
                onRefresh.run();
                refreshNotificationBadge();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });

        actions.getChildren().addAll(likeBtn, commentCount, commentInput, postCommentBtn);
        card.getChildren().add(actions);

        VBox commentsList = new VBox(4);
        for (Comment c : comments) {
            User commenter = AppContext.getUserService().getUserById(c.getUserId());
            HBox commentRow = new HBox(6);
            Label cl = new Label(commenter.getName() + ": " + c.getContent());
            cl.setStyle(TEXT_DARK);
            cl.setWrapText(true);
            commentRow.getChildren().add(cl);
            if (c.getUserId() == currentUserId) {
                Button delComment = new Button("Delete");
                delComment.setStyle("-fx-cursor: hand; -fx-font-size: 10; -fx-text-fill: #c0392b;");
                delComment.setOnAction(e -> {
                    try {
                        AppContext.getCommentService().deleteComment(c);
                        onRefresh.run();
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, ex.getMessage());
                    }
                });
                commentRow.getChildren().add(delComment);
            }
            commentsList.getChildren().add(commentRow);
        }
        card.getChildren().add(commentsList);
        return card;
    }

    private boolean showConfirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText(message);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private ScrollPane buildProfilePane() {
        VBox profile = new VBox(16);
        profile.setPadding(new Insets(20));
        profile.setStyle("-fx-background-color: #e4e6eb;");
        ScrollPane sp = new ScrollPane(profile);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #e4e6eb; -fx-background-color: #e4e6eb;");
        return sp;
    }

    private void refreshProfile() {
        VBox profile = (VBox) profilePane.getContent();
        profile.getChildren().clear();
        try {
            User user = AppContext.getAuthService().getCurrentUser();
            VBox editBox = new VBox(10);
            editBox.setPadding(new Insets(16));
            editBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            Label editTitle = darkLabel("Edit profile");
            editTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
            TextField nameField = new TextField(user.getName());
            nameField.setPromptText("Name");
            nameField.setStyle(TEXT_DARK);
            TextArea bioField = new TextArea(user.getBio() == null ? "" : user.getBio());
            bioField.setPromptText("Bio");
            bioField.setPrefRowCount(2);
            bioField.setWrapText(true);
            bioField.setStyle(TEXT_DARK);
            Label profilePicLabel = darkLabel(user.getProfilePic() != null ? "Photo: set" : "No profile picture");
            profilePicLabel.setStyle(TEXT_MUTED);
            File[] profilePicRef = new File[1];
            Button uploadProfilePic = new Button("Upload profile picture");
            uploadProfilePic.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            uploadProfilePic.setOnAction(e -> {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                Stage stage = (Stage) profile.getScene().getWindow();
                File f = fc.showOpenDialog(stage);
                if (f != null) {
                    profilePicRef[0] = f;
                    profilePicLabel.setText(f.getName());
                }
            });
            Button saveProfileBtn = new Button("Save profile");
            saveProfileBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            saveProfileBtn.setOnAction(e -> {
                try {
                    user.setName(nameField.getText().trim());
                    user.setBio(bioField.getText().trim().isEmpty() ? null : bioField.getText().trim());
                    AppContext.getUserService().updateUser(user, Optional.ofNullable(profilePicRef[0]));
                    showAlert(Alert.AlertType.INFORMATION, "Profile updated.");
                    refreshProfile();
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, ex.getMessage());
                }
            });
            editBox.getChildren().addAll(editTitle, nameField, bioField, profilePicLabel, uploadProfilePic, saveProfileBtn);
            profile.getChildren().add(editBox);

            Label nameLabel = darkLabel(user.getName());
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            Label emailLabel = darkLabel(user.getEmail());
            emailLabel.setStyle(TEXT_MUTED);
            Label bioLabel = darkLabel(user.getBio() == null ? "No bio" : user.getBio());
            bioLabel.setWrapText(true);
            profile.getChildren().addAll(nameLabel, emailLabel, bioLabel);

            List<Post> myPosts = AppContext.getPostService().getAllPosts().stream()
                    .filter(p -> p.getUserId() == user.getId()).toList();
            Label postsTitle = darkLabel("My Posts (" + myPosts.size() + ")");
            postsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            profile.getChildren().add(postsTitle);
            for (Post post : myPosts) {
                VBox card = buildPostCard(post, user, user.getId(), false, this::refreshProfile);
                profile.getChildren().add(card);
            }
        } catch (Exception e) {
            Label err = darkLabel("Error: " + e.getMessage());
            err.setStyle("-fx-text-fill: #c0392b;");
            profile.getChildren().add(err);
        }
    }

    private ScrollPane buildFriendsPane() {
        VBox friends = new VBox(12);
        friends.setPadding(new Insets(20));
        friends.setStyle("-fx-background-color: #e4e6eb;");
        ScrollPane sp = new ScrollPane(friends);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #e4e6eb; -fx-background-color: #e4e6eb;");
        return sp;
    }

    private void refreshFriends() {
        VBox friends = (VBox) friendsPane.getContent();
        friends.getChildren().clear();
        try {
            int currentId = AppContext.getAuthService().getCurrentUser().getId();
            List<Friend> list = AppContext.getFriendService().getAllFriendsByUserId(currentId);
            Label title = darkLabel("Friends");
            title.setFont(Font.font("System", FontWeight.BOLD, 16));
            friends.getChildren().add(title);

            Label addLabel = darkLabel("Add friend by email:");
            TextField emailField = new TextField();
            emailField.setPromptText("Friend's email");
            emailField.setPrefWidth(250);
            emailField.setStyle(TEXT_DARK);
            Button addBtn = new Button("Add Friend");
            addBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            addBtn.setOnAction(e -> {
                String email = emailField.getText();
                if (email == null || email.isBlank()) return;
                try {
                    User other = AppContext.getUserService().getUserByEmail(email.trim());
                    Friend friend = Friend.builder().userId(Math.min(currentId, other.getId())).friendId(Math.max(currentId, other.getId())).build();
                    AppContext.getFriendService().createFriend(friend);
                    emailField.clear();
                    refreshFriends();
                    refreshNotificationBadge();
                    showAlert(Alert.AlertType.INFORMATION, "Friend request sent.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, ex.getMessage());
                }
            });
            friends.getChildren().addAll(addLabel, new HBox(8, emailField, addBtn));

            for (Friend f : list) {
                int otherId = f.getUserId() == currentId ? f.getFriendId() : f.getUserId();
                User other = AppContext.getUserService().getUserById(otherId);
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label lbl = darkLabel(other.getName() + " (" + other.getEmail() + ") — " + f.getStatus().name().toLowerCase());
                row.getChildren().add(lbl);
                boolean iAmReceiver = f.getFriendId() == currentId;
                if (f.getStatus() == Status.PENDING && iAmReceiver) {
                    Button acceptBtn = new Button("Accept");
                    acceptBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
                    acceptBtn.setOnAction(ev -> {
                        try {
                            f.setStatus(Status.ACCEPTED);
                            AppContext.getFriendService().updateFriend(f);
                            refreshFriends();
                            refreshNotificationBadge();
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, ex.getMessage());
                        }
                    });
                    Button rejectBtn = new Button("Reject");
                    rejectBtn.setStyle("-fx-cursor: hand; -fx-text-fill: #c0392b;");
                    rejectBtn.setOnAction(ev -> {
                        try {
                            AppContext.getFriendService().deleteFriend(f);
                            refreshFriends();
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, ex.getMessage());
                        }
                    });
                    row.getChildren().addAll(acceptBtn, rejectBtn);
                }
                Button removeBtn = new Button("Remove");
                removeBtn.setStyle("-fx-cursor: hand; -fx-text-fill: #c0392b;");
                removeBtn.setOnAction(ev -> {
                    if (showConfirm("Remove this friend?")) {
                        try {
                            AppContext.getFriendService().deleteFriend(f);
                            refreshFriends();
                        } catch (Exception ex) {
                            showAlert(Alert.AlertType.ERROR, ex.getMessage());
                        }
                    }
                });
                row.getChildren().add(removeBtn);
                friends.getChildren().add(row);
            }
        } catch (Exception e) {
            Label err = darkLabel("Error: " + e.getMessage());
            err.setStyle("-fx-text-fill: #c0392b;");
            friends.getChildren().add(err);
        }
    }

    private ScrollPane buildNotificationsPane() {
        VBox list = new VBox(8);
        list.setPadding(new Insets(20));
        list.setStyle("-fx-background-color: #e4e6eb;");
        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #e4e6eb; -fx-background-color: #e4e6eb;");
        return sp;
    }

    private void refreshNotifications() {
        VBox list = (VBox) notificationsPane.getContent();
        list.getChildren().clear();
        try {
            List<Notification> notifications = AppContext.getNotificationService().getNotificationsForCurrentUser();
            Button markAllRead = new Button("Mark all as read");
            markAllRead.setStyle("-fx-cursor: hand; " + TEXT_DARK);
            markAllRead.setOnAction(e -> {
                AppContext.getNotificationService().markAllAsRead();
                refreshNotifications();
                refreshNotificationBadge();
            });
            list.getChildren().add(markAllRead);
            for (Notification n : notifications) {
                User sender = AppContext.getUserService().getUserById(n.getSenderId());
                String text = formatNotification(n, sender);
                Label lbl = darkLabel(text);
                lbl.setWrapText(true);
                if (!n.isRead()) lbl.setStyle(TEXT_DARK + " -fx-font-weight: bold;");
                list.getChildren().add(lbl);
            }
            if (notifications.isEmpty()) {
                Label empty = darkLabel("No notifications.");
                empty.setStyle(TEXT_MUTED);
                list.getChildren().add(empty);
            }
            refreshNotificationBadge();
        } catch (Exception e) {
            Label err = darkLabel("Error: " + e.getMessage());
            err.setStyle("-fx-text-fill: #c0392b;");
            list.getChildren().add(err);
        }
    }

    private ScrollPane buildSearchPane() {
        VBox searchContent = new VBox(12);
        searchContent.setPadding(new Insets(20));
        searchContent.setStyle("-fx-background-color: #e4e6eb;");
        ScrollPane sp = new ScrollPane(searchContent);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #e4e6eb; -fx-background-color: #e4e6eb;");
        return sp;
    }

    private void refreshSearch() {
        VBox searchContent = (VBox) searchPane.getContent();
        searchContent.getChildren().clear();
        Label hint = darkLabel("Search users by name or email, or search posts by content.");
        hint.setStyle(TEXT_MUTED);
        TextField searchField = new TextField();
        searchField.setPromptText("Type to search...");
        searchField.setPrefWidth(300);
        searchField.setStyle(TEXT_DARK);
        Button searchUsersBtn = new Button("Search users");
        searchUsersBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
        Button searchPostsBtn = new Button("Search posts");
        searchPostsBtn.setStyle("-fx-cursor: hand; " + TEXT_DARK);
        VBox resultsBox = new VBox(8);
        searchUsersBtn.setOnAction(e -> {
            String q = searchField.getText() == null ? "" : searchField.getText().trim();
            resultsBox.getChildren().clear();
            if (q.isEmpty()) {
                resultsBox.getChildren().add(darkLabel("Enter a search term."));
                return;
            }
            try {
                List<User> users = AppContext.getUserService().getAllUsers().stream()
                        .filter(u -> u.getName().toLowerCase().contains(q.toLowerCase()) || u.getEmail().toLowerCase().contains(q.toLowerCase()))
                        .toList();
                resultsBox.getChildren().add(darkLabel("Users (" + users.size() + "):"));
                for (User u : users) {
                    Label l = darkLabel(u.getName() + " — " + u.getEmail());
                    resultsBox.getChildren().add(l);
                }
                if (users.isEmpty()) resultsBox.getChildren().add(darkLabel("No users found."));
            } catch (Exception ex) {
                resultsBox.getChildren().add(darkLabel("Error: " + ex.getMessage()));
            }
        });
        searchPostsBtn.setOnAction(e -> {
            String q = searchField.getText() == null ? "" : searchField.getText().trim();
            resultsBox.getChildren().clear();
            if (q.isEmpty()) {
                resultsBox.getChildren().add(darkLabel("Enter a search term."));
                return;
            }
            try {
                List<Post> posts = AppContext.getPostService().getAllPosts().stream()
                        .filter(p -> p.getContent() != null && p.getContent().toLowerCase().contains(q.toLowerCase()))
                        .toList();
                resultsBox.getChildren().add(darkLabel("Posts (" + posts.size() + "):"));
                User currentUser = AppContext.getAuthService().getCurrentUser();
                for (Post post : posts) {
                    User author = AppContext.getUserService().getUserById(post.getUserId());
                    VBox card = buildPostCard(post, author, currentUser.getId(), true, () -> refreshSearch());
                    resultsBox.getChildren().add(card);
                }
                if (posts.isEmpty()) resultsBox.getChildren().add(darkLabel("No posts found."));
            } catch (Exception ex) {
                resultsBox.getChildren().add(darkLabel("Error: " + ex.getMessage()));
            }
        });
        searchContent.getChildren().addAll(hint, new HBox(10, searchField, searchUsersBtn, searchPostsBtn), resultsBox);
    }

    private String formatNotification(Notification n, User sender) {
        return switch (n.getType()) {
            case LIKE -> sender.getName() + " liked your post.";
            case COMMENT -> sender.getName() + " commented on your post.";
            case FRIEND_REQUEST -> sender.getName() + " sent you a friend request.";
            case FRIEND_ACCEPT -> sender.getName() + " accepted your friend request.";
        };
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert a = new Alert(type);
        a.setContentText(message);
        a.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }
}
