package memorypriority.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileProfileRepository implements ProfileRepository {

    private final String filePath = "./data/users.txt";

    public void addProfile(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("===\n");
            writer.write("Username: " + username + "\n");
            writer.write("---\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("===")) {
                    String usernameLine = reader.readLine();
                    if (usernameLine != null && usernameLine.startsWith("Username: ")) {
                        String username = usernameLine.split(":")[1].trim();
                        usernames.add(username);
                    }
                    reader.readLine(); // Skip past the separator "---"
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    public void removeUsername(String usernameToRemove) {
        List<String> usernames = getAllUsernames();
        usernames.remove(usernameToRemove);

        // Now, re-write the file without the removed username
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String username : usernames) {
                writer.write("===\n");
                writer.write("Username: " + username + "\n");
                writer.write("---\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // You can add more methods to manage profiles here
}

