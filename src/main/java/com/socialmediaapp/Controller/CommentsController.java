package com.socialmediaapp.Controller;

import com.socialmediaapp.SocialMediaApp;
import com.socialmediaapp.Model.Comment;
import com.socialmediaapp.Model.Post;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CommentsController {
    @FXML private ListView<Comment> commentListView;
    @FXML private TextArea newCommentText;
    @FXML private Button backButton;

    private SocialMediaApp mainApp;
    private CommentService commentService;
    private AuthService authService;
    private UserService userService;
    private Post post;

    @FXML
    private void initialize() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.commentService = registry.getCommentService();
        this.authService = registry.getAuthService();
        this.userService = registry.getUserService();
    }

    public void setMainApp(SocialMediaApp mainApp) { this.mainApp = mainApp; }
    public void setPost(Post post) {
        this.post = post;
        loadComments();
        setupCellFactory();
    }

    private void setupCellFactory() {
        commentListView.setCellFactory(lv -> new ListCell<Comment>() {
            private final ImageView profilePic = new ImageView();
            private final Label userName = new Label();
            private final Label content = new Label();
            private final Label createdAt = new Label();
            private final HBox layout = new HBox(10, profilePic, new VBox(5, userName, content, createdAt));

            {
                profilePic.setFitHeight(30);
                profilePic.setFitWidth(30);
            }

            @Override
            protected void updateItem(Comment comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) setGraphic(null);
                else {
                    User user = userService.getUserById(comment.getUserId());
                    userName.setText(user.getName());
                    content.setText(comment.getContent());
                    createdAt.setText(comment.getCreatedAt().toString());
                    if (user.getProfilePic() != null && !user.getProfilePic().isEmpty())
                        profilePic.setImage(new Image(user.getProfilePic(), true));
                    else
                        profilePic.setImage(null);
                    setGraphic(layout);
                }
            }
        });
    }

    private void loadComments() {
        var page = commentService.getAllCommentsAsPage(0, 50, "createdAt", "ASC", 0, post.getId());
        commentListView.getItems().setAll(page.getContent());
    }

    @FXML
    private void handleAddComment() {
        String text = newCommentText.getText().trim();
        if (text.isEmpty()) return;
        Comment comment = new Comment();
        comment.setContent(text);
        comment.setPostId(post.getId());
        comment.setUserId(authService.getCurrentUser().getId());
        try {
            commentService.createComment(comment);
            newCommentText.clear();
            loadComments();
        } catch (IllegalArgumentException e) {
            // show error
        }
    }

    @FXML
    private void goBack() {
        mainApp.showMainScene();
    }
}