package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class FileMemorySetRepository implements MemorySetRepository {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public MemoryCollection getMemoryCollectionOfUser(String username) {
        Set<MemorySet> memorySets = new HashSet<>();
        String filePath = getFilePath(username);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID: ")) {
                    UUID id = UUID.fromString(line.split(":")[1].trim());

                    // Read the entire name line
                    String nameLine = reader.readLine();
                    // Extract name, keeping everything after the first colon
                    String name = nameLine.substring(nameLine.indexOf(":") + 1).trim();

                    PriorityLevel priorityLevel = PriorityLevel.valueOf(reader.readLine().split(":")[1].trim());

                    // Parse the date and time string using the format object
                    // Read the line and extract the date and time string
                    line = reader.readLine();
                    String[] dateTimeParts = line.split(": ")[1].split(" ");
                    String dateString = dateTimeParts[0];
                    String timeString = dateTimeParts[1];

                    // Combine date and time for parsing
                    String dateTimeString = dateString + " " + timeString;

                    java.util.Date utilDate = format.parse(dateTimeString);
                    java.sql.Timestamp lastTimeRehearsed = new java.sql.Timestamp(utilDate.getTime());

                    // Change the map to a list of entries
                    List<Map.Entry<String, String>> entries = new ArrayList<>();
                    while ((line = reader.readLine()) != null && !line.equals("---")) {
                        String[] parts = line.split("=:::=");
                        // Create a new entry with the key and value from the parts
                        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(parts[0].trim(), parts[1].trim());
                        // Add the entry to the list
                        entries.add(entry);
                    }
                    // Use the list instead of the map to create a new MemorySet object
                    memorySets.add(new MemorySet(id, name, entries, priorityLevel, lastTimeRehearsed));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error reading from file", e);
        }
        return new MemoryCollection(memorySets);
    }


    @Override
    public void addMemorySetToUser(String username, MemorySet memorySet) {
        String filePath = getFilePath(username);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("ID: " + memorySet.getId() + "\n");
            writer.write("Name: " + memorySet.getName() + "\n");
            writer.write("Priority: " + memorySet.getPriorityLevel() + "\n");
            writer.write("Last Rehearsed: " + format.format(memorySet.getLastTimeRehearsed()) + "\n");

            // Change the loop to iterate over the list of entries instead of the map
            for (Map.Entry<String, String> entry : memorySet.getPairList()) {
                // Write the key and value of each entry as before
                writer.write(entry.getKey() + " =:::= " + entry.getValue() + "\n");
            }

            writer.write("---\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error writing to file", e);
        }
    }


    @Override
    public void changePriority(String username, MemorySet memorySet, PriorityLevel newPriority) {
        String filePath = getFilePath(username);
        List<String> fileContent = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);

                if (line.startsWith("ID: ")) {
                    UUID id = UUID.fromString(line.split(": ")[1].trim());
                    if (id.equals(memorySet.getId())) {
                        // Read and store the name
                        fileContent.add(reader.readLine());
                        // Skip the old priority line and add the new priority
                        reader.readLine();
                        fileContent.add("Priority: " + newPriority);
                        // Read and store the last rehearsed time
                        fileContent.add(reader.readLine());
                        found = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error reading from file", e);
        }

        if (!found) {
            throw new MemoryPriorityException("Memory set not found");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String contentLine : fileContent) {
                writer.write(contentLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error writing to file", e);
        }
    }



    @Override
    public void removeMemorySet(String username, UUID id) {
        String filePath = getFilePath(username);
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error reading from file", e);
        }

        int start = -1;
        int end = -1;

        for (int i = 0; i < fileContent.size(); i++) {
            String line = fileContent.get(i);
            if (line.startsWith("ID: ")) {
                UUID currentId = UUID.fromString(line.split(":")[1].trim());
                if (currentId.equals(id)) {
                    start = i;
                    while (i < fileContent.size() && !fileContent.get(i).equals("---")) {
                        i++;
                    }
                    end = i;
                    break;
                }
            }
        }

        if (start != -1 && end != -1) {
            fileContent.subList(start, end + 1).clear();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String contentLine : fileContent) {
                    writer.write(contentLine + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new MemoryPriorityException("Error writing to file", e);
            }
        } else {
            throw new MemoryPriorityException("Memory set not found");
        }
    }

    @Override
    public void updateKeyValuePair(String username, UUID memorySetId, String originalKey, String originalValue, String newKey, String newValue) {
        String filePath = getFilePath(username);
        List<String> fileContent = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID: " + memorySetId.toString())) {
                    found = true;
                    fileContent.add(line);
                    while (!(line = reader.readLine()).equals("---")) {
                        if (line.startsWith(originalKey + " =:::= " + originalValue)) {
                            line = newKey + " =:::= " + newValue;
                        }
                        fileContent.add(line);
                    }
                    fileContent.add(line); // Add the "---" line
                } else {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            throw new MemoryPriorityException("Error reading from file", e);
        }

        if (!found) {
            throw new MemoryPriorityException("Memory set not found");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String contentLine : fileContent) {
                writer.write(contentLine + "\n");
            }
        } catch (IOException e) {
            throw new MemoryPriorityException("Error writing to file", e);
        }
    }


    private String getFilePath(String username) {
        return "./data/" + username + ".txt";
    }
}
