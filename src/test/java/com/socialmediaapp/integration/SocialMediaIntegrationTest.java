package com.socialmediaapp.integration;

import com.socialmediaapp.DAO.*;
import com.socialmediaapp.Enum.Privacy;
import com.socialmediaapp.Enum.Status;
import com.socialmediaapp.Model.*;
import com.socialmediaapp.Service.*;
import com.socialmediaapp.Util.UserSession;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SocialMediaIntegrationTest {

    private static AuthService authService;
    private static UserService userService;
    private static PostService postService;
    private static CommentService commentService;
    private static LikeService likeService;
    private static FriendService friendService;
    private static NotificationService notificationService;

    private static UserDAO userDAO;
    private static PostDAO postDAO;
    private static CommentDAO commentDAO;
    private static LikeDAO likeDAO;
    private static FriendDAO friendDAO;
    private static NotificationDAO notificationDAO;

    @BeforeAll
    static void setup() throws Exception {
        System.setProperty("app.db.url", "jdbc:h2:mem:social;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        System.setProperty("app.db.bootstrapUrl", "jdbc:h2:mem:social;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        System.setProperty("app.db.user", "sa");
        System.setProperty("app.db.password", "");
        System.setProperty("app.imageUploader.localOnly", "true");

        createSchema();

        userDAO = new UserDAO();
        postDAO = new PostDAO();
        commentDAO = new CommentDAO();
        likeDAO = new LikeDAO();
        friendDAO = new FriendDAO();
        notificationDAO = new NotificationDAO();

        authService = new AuthService(UserSession.getInstance(), userDAO);
        notificationService = new NotificationService(notificationDAO, authService);
        userService = new UserService(userDAO, friendDAO, postDAO, commentDAO, likeDAO, authService);
        postService = new PostService(userDAO, postDAO, commentDAO, authService, friendDAO);
        commentService = new CommentService(userDAO, commentDAO, postDAO, authService, notificationService);
        likeService = new LikeService(userDAO, postDAO, likeDAO, authService, notificationService);
        friendService = new FriendService(friendDAO, userDAO, authService, notificationService);
    }

    private static void createSchema() throws Exception {
        try (Connection c = com.socialmediaapp.Util.DBConnection.getAppDataSource().getConnection();
             Statement st = c.createStatement()) {
            st.execute("CREATE TABLE users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(100) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, bio TEXT, profile_pic VARCHAR(255), created_at TIMESTAMP)");
            st.execute("CREATE TABLE posts (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, content TEXT, image_path VARCHAR(255), privacy VARCHAR(20) DEFAULT 'public', created_at TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE friends (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, friend_id INT NOT NULL, status VARCHAR(20) DEFAULT 'pending', created_at TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE likes (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, post_id INT NOT NULL, created_at TIMESTAMP, UNIQUE (user_id, post_id), FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE comments (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, post_id INT NOT NULL, content TEXT NOT NULL, created_at TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE)");
            st.execute("CREATE TABLE notifications (id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, sender_id INT NOT NULL, type VARCHAR(40) NOT NULL, reference_id INT, is_read BOOLEAN DEFAULT FALSE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE)");
        }
    }

    @Test
    @Order(1)
    void fullFlow_register_login_friend_post_comment_like_and_notifications() throws Exception {
        Path profileImage = Files.createTempFile("profile", ".png");
        Files.writeString(profileImage, "img");

        User u1 = User.builder().name("Alice").email("alice@test.com").password("123456").bio("hello").build();
        assertTrue(authService.register(u1, Optional.of(profileImage.toFile())));
        assertNotNull(authService.getCurrentUser());

        User u2 = User.builder().name("Bob").email("bob@test.com").password("123456").bio("dev").build();
        assertTrue(authService.register(u2, Optional.empty()));

        authService.logout();
        assertTrue(authService.login("alice@test.com", "123456"));
        User alice = authService.getCurrentUser();

        Path postImage = Files.createTempFile("post", ".png");
        Files.writeString(postImage, "img");
        Post post = Post.builder().content("Hello world").privacy(Privacy.PUBLIC).build();
        assertTrue(postService.createPost(post, Optional.of(postImage.toFile())));
        List<Post> alicePosts = postDAO.findAllByUserId(alice.getId());
        assertFalse(alicePosts.isEmpty());
        int postId = alicePosts.get(0).getId();

        authService.logout();
        assertTrue(authService.login("bob@test.com", "123456"));
        User bob = authService.getCurrentUser();
        Friend request = Friend.builder().userId(bob.getId()).friendId(alice.getId()).build();
        assertTrue(friendService.createFriend(request));

        authService.logout();
        assertTrue(authService.login("alice@test.com", "123456"));
        Friend pending = friendDAO.findAllByUserId(alice.getId()).stream()
                .filter(f -> f.getStatus() == Status.PENDING)
                .findFirst().orElseThrow();
        pending.setStatus(Status.ACCEPTED);
        assertTrue(friendService.updateFriend(pending));

        authService.logout();
        assertTrue(authService.login("bob@test.com", "123456"));
        Comment comment = Comment.builder().userId(bob.getId()).postId(postId).content("Nice post").build();
        assertTrue(commentService.createComment(comment));

        Like like = Like.builder().userId(bob.getId()).postId(postId).build();
        assertTrue(likeService.createLike(like));

        List<Post> feed = postService.getFeedForCurrentUser();
        assertTrue(feed.stream().anyMatch(p -> p.getId() == postId));

        authService.logout();
        assertTrue(authService.login("alice@test.com", "123456"));
        List<Notification> notes = notificationService.getNotificationsForCurrentUser();
        assertTrue(notes.size() >= 3, "expected friend request + comment + like notifications");
        assertTrue(notificationService.markAllAsRead());
        assertEquals(0, notificationService.getUnreadCount());
    }

    @Test
    @Order(2)
    void can_reject_friend_request() throws Exception {
        User u3 = User.builder().name("Carol").email("carol@test.com").password("123456").build();
        authService.logout();
        assertTrue(authService.register(u3, Optional.empty()));

        User alice = userDAO.findByEmail("alice@test.com").orElseThrow();
        User carol = userDAO.findByEmail("carol@test.com").orElseThrow();

        authService.logout();
        assertTrue(authService.login("carol@test.com", "123456"));
        assertTrue(friendService.createFriend(Friend.builder().userId(carol.getId()).friendId(alice.getId()).build()));

        authService.logout();
        assertTrue(authService.login("alice@test.com", "123456"));
        Friend pending = friendDAO.findAllByUserId(alice.getId()).stream()
                .filter(f -> f.getStatus() == Status.PENDING &&
                        ((f.getUserId() == carol.getId() && f.getFriendId() == alice.getId()) ||
                                (f.getUserId() == alice.getId() && f.getFriendId() == carol.getId())))
                .findFirst().orElseThrow();

        assertTrue(friendService.deleteFriend(pending));
        assertTrue(friendDAO.findById(pending.getId()).isEmpty());
    }
}
