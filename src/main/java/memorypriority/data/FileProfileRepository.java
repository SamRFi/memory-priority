package memorypriority.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileProfileRepository implements ProfileRepository {

    private final String directoryPath = "./data/";

    public void addProfile(String username) {
        File file = new File(directoryPath + username + ".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                String username = file.getName().replace(".txt", "");
                usernames.add(username);
            }
        }

        return usernames;
    }

    public void removeUsername(String usernameToRemove) {
        File file = new File(directoryPath + usernameToRemove + ".txt");
        if (file.exists()) {
            file.delete();
        }
    }
}
