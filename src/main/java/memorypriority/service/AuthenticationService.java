package memorypriority.service;


import memorypriority.data.ProfileRepository;
import memorypriority.data.Repositories;
import memorypriority.util.MemoryPriorityException;

import java.util.List;


public class AuthenticationService {

    private final ProfileRepository profileRepository;

    public AuthenticationService() {
        this.profileRepository = Repositories.getProfileRepository();
    }

    public String login(String username) {
        List<String> usernames = profileRepository.getAllUsernames();
        if (!usernames.contains(username)) {
            throw new MemoryPriorityException("Trying to log in to invalid username");
        }
        return username;
    }

    public void addProfile(String usernameToAdd) {
        profileRepository.addProfile(usernameToAdd);
    }

    public void removeProfile(String usernameToRemove) {
        profileRepository.removeUsername(usernameToRemove);
    }

    public List<String> getAllUsernames() {
        return profileRepository.getAllUsernames();
    }

}
