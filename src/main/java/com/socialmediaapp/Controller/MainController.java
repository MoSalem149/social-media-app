package com.socialmediaapp.Controller;

import com.socialmediaapp.SocialMediaApp;
import com.socialmediaapp.Model.Like;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.Page;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class MainController {
    @FXML private ListView<Post> postListView;
    @FXML private TextArea newPostContent;
    @FXML private Button chooseImageButton;
    @FXML private Label imageFileName;
    @FXML private Button prevPageButton, nextPageButton;
    @FXML private Label pageInfoLabel;

    private SocialMediaApp mainApp;
    private PostService postService;
    private LikeService likeService;
    private CommentService commentService;
    private AuthService authService;
    private NotificationService notificationService;
    private UserService userService;
    private File selectedImage;
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private Page<Post> currentPageData;

    @FXML
    private void initialize() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.postService = registry.getPostService();
        this.likeService = registry.getLikeService();
        this.commentService = registry.getCommentService();
        this.authService = registry.getAuthService();
        this.notificationService = registry.getNotificationService();
        this.userService = registry.getUserService();
        loadPosts();
        setupPostCellFactory();
    }

    public void setMainApp(SocialMediaApp mainApp) { this.mainApp = mainApp; }

    private void setupPostCellFactory() {
        postListView.setCellFactory(lv -> new ListCell<Post>() {
            private final ImageView profilePic = new ImageView();
            private final Label userName = new Label();
            private final Label content = new Label();
            private final ImageView postImage = new ImageView();
            private final Button likeButton = new Button("Like");
            private final Button commentButton = new Button("Comment");
            private final Label likesCount = new Label();
            private final VBox layout = new VBox(5);

            {
                profilePic.setFitHeight(30);
                profilePic.setFitWidth(30);
                HBox userBox = new HBox(5, profilePic, userName);
                likeButton.setOnAction(e -> handleLike(getItem()));
                commentButton.setOnAction(e -> handleComment(getItem()));
                HBox actions = new HBox(10, likeButton, commentButton, likesCount);
                layout.getChildren().addAll(userBox, content, postImage, actions);
                setGraphic(layout);
            }

            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    User author = userService.getUserById(post.getUserId());
                    userName.setText(author.getName());
                    content.setText(post.getContent());
                    if (author.getProfilePic() != null && !author.getProfilePic().isEmpty()) {
                        profilePic.setImage(new Image(author.getProfilePic(), true));
                    } else {
                        profilePic.setImage(null);
                    }
                    if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
                        postImage.setImage(new Image(post.getImagePath(), true));
                        postImage.setFitHeight(200);
                        postImage.setPreserveRatio(true);
                    } else {
                        postImage.setImage(null);
                    }
                    long likeCount = likeService.getAllLikesByPostId(post.getId()).size();
                    likesCount.setText(likeCount + " likes");
                    setGraphic(layout);
                }
            }
        });
    }

    private void handleLike(Post post) {
        Like like = new Like();
        like.setPostId(post.getId());
        like.setUserId(authService.getCurrentUser().getId());
        try {
            likeService.createLike(like);
            loadPosts(); // refresh to update count
        } catch (IllegalArgumentException e) {
            // maybe already liked
        }
    }

    private void handleComment(Post post) {
        mainApp.showCommentsScene(post);
    }

    private void loadPosts() {
        currentPageData = postService.getAllPostsAsPage(currentPage, PAGE_SIZE, "createdAt", "DESC", 0, 0);
        postListView.getItems().setAll(currentPageData.getContent());
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        prevPageButton.setDisable(currentPageData.isFirst());
        nextPageButton.setDisable(currentPageData.isLast());
        pageInfoLabel.setText("Page " + (currentPage + 1) + " of " + currentPageData.getTotalPages());
    }

    @FXML
    private void nextPage() {
        if (!currentPageData.isLast()) {
            currentPage++;
            loadPosts();
        }
    }

    @FXML
    private void prevPage() {
        if (!currentPageData.isFirst()) {
            currentPage--;
            loadPosts();
        }
    }

    @FXML
    private void handleCreatePost() {
        String content = newPostContent.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        Post post = new Post();
        post.setContent(content);
        post.setPrivacy(com.socialmediaapp.Enum.Privacy.PUBLIC);
        try {
            postService.createPost(post, Optional.ofNullable(selectedImage));
            newPostContent.clear();
            selectedImage = null;
            imageFileName.setText("");
            loadPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImage = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (selectedImage != null) imageFileName.setText(selectedImage.getName());
    }

    @FXML
    private void goToProfile() {
        mainApp.showProfileScene(authService.getCurrentUser());
    }

    @FXML
    private void goToFriends() {
        mainApp.showFriendsScene();
    }

    @FXML
    private void logout() {
        authService.logout();
        mainApp.showLoginScene();
    }
}