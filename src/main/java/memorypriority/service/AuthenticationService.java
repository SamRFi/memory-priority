package memorypriority.service;

import memorypriority.data.AuthorizationRepository;
import memorypriority.data.JdbcAuthorizationRepository;
import memorypriority.data.Repositories;
import memorypriority.domain.User;
import memorypriority.util.MemoryPriorityException;


public class AuthenticationService {

    private final AuthorizationRepository authRepo;

    public AuthenticationService() {
        this.authRepo = Repositories.getAuthorizationRepository();
    }

    public User login(String username, String password) {
        User user = authRepo.authenticateUser(username, password);
        if (user == null) {
            throw new MemoryPriorityException("Invalid username or password");
        }
        return user;
    }

}
