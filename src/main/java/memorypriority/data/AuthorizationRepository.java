package memorypriority.data;

import memorypriority.domain.User;

public interface AuthorizationRepository {
    User authenticateUser(String username, String hashedPassword);
}
