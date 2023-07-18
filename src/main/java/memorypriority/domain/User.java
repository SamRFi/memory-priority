package memorypriority.domain;

import java.util.UUID;

public class User {
    private final String id;
    private final String username;
    private final String hashedPassword;

    public User(String username, String hashedPassword) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
