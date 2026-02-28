package com.socialmediaapp.Controller;

import com.socialmediaapp.SocialMediaApp;
import com.socialmediaapp.Model.Friend;
import com.socialmediaapp.Model.User;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Util.ServiceRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.stream.Collectors;

public class FriendsController {
    @FXML private ListView<User> friendsListView;
    @FXML private ListView<Friend> requestsListView;
    @FXML private TextField searchField;
    @FXML private Button backButton;

    private SocialMediaApp mainApp;
    private FriendService friendService;
    private UserService userService;
    private AuthService authService;
    private NotificationService notificationService;

    @FXML
    private void initialize() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.friendService = registry.getFriendService();
        this.userService = registry.getUserService();
        this.authService = registry.getAuthService();
        this.notificationService = registry.getNotificationService();
        loadFriends();
        loadRequests();
        setupCellFactories();
    }

    public void setMainApp(SocialMediaApp mainApp) { this.mainApp = mainApp; }

    private void setupCellFactories() {
        friendsListView.setCellFactory(lv -> new ListCell<User>() {
            private final ImageView profilePic = new ImageView();
            private final Label name = new Label();
            private final Button viewProfile = new Button("View Profile");
            private final HBox layout = new HBox(10, profilePic, name, viewProfile);

            {
                profilePic.setFitHeight(30);
                profilePic.setFitWidth(30);
                viewProfile.setOnAction(e -> mainApp.showProfileScene(getItem()));
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) setGraphic(null);
                else {
                    name.setText(user.getName());
                    if (user.getProfilePic() != null && !user.getProfilePic().isEmpty())
                        profilePic.setImage(new Image(user.getProfilePic(), true));
                    else
                        profilePic.setImage(null);
                    setGraphic(layout);
                }
            }
        });

        requestsListView.setCellFactory(lv -> new ListCell<Friend>() {
            private final Label fromUser = new Label();
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox layout = new HBox(10, fromUser, acceptButton, rejectButton);

            {
                acceptButton.setOnAction(e -> handleAccept(getItem()));
                rejectButton.setOnAction(e -> handleReject(getItem()));
            }

            @Override
            protected void updateItem(Friend request, boolean empty) {
                super.updateItem(request, empty);
                if (empty || request == null) setGraphic(null);
                else {
                    User sender = userService.getUserById(request.getUserId());
                    fromUser.setText(sender.getName() + " sent you a friend request");
                    setGraphic(layout);
                }
            }
        });
    }

    private void loadFriends() {
        List<Friend> friendships = friendService.getAllFriendshipsForCurrentUser();
        List<User> friends = friendships.stream()
                .filter(f -> f.getStatus() == Status.ACCEPTED)
                .map(f -> f.getUserId() == authService.getCurrentUser().getId() ? f.getFriendId() : f.getUserId())
                .map(id -> userService.getUserById(id))
                .collect(Collectors.toList());
        friendsListView.getItems().setAll(friends);
    }

    private void loadRequests() {
        List<Friend> requests = friendService.getPendingRequestsForCurrentUser();
        requestsListView.getItems().setAll(requests);
    }

    private void handleAccept(Friend request) {
        request.setStatus(Status.ACCEPTED);
        friendService.updateFriend(request);
        loadRequests();
        loadFriends();
    }

    private void handleReject(Friend request) {
        friendService.deleteFriend(request);
        loadRequests();
    }

    @FXML
    private void handleSearch() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) return;
        var page = userService.getAllUsersAsPage(0, 20, "name", "ASC", term);
        // Simple display
        StringBuilder sb = new StringBuilder("Search results:\n");
        page.getContent().forEach(u -> sb.append(u.getName()).append("\n"));
        Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString());
        alert.show();
    }

    @FXML
    private void goBack() {
        mainApp.showMainScene();
    }
}