package memorypriority.data;

import java.util.List;

public interface ProfileRepository {

    List<String> getAllUsernames();

    void removeUsername(String usernameToRemove);

    void addProfile(String username);
}
