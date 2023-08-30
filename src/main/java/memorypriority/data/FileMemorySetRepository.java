package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class FileMemorySetRepository implements MemorySetRepository {

    private final String filePath = "./data/memory_data.txt";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override public MemoryCollection getMemoryCollectionOfUser(String username) { Set<MemorySet> memorySets = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("===") && username.equals(reader.readLine())) {
                    int id = Integer.parseInt(reader.readLine().split(":")[1].trim());
                    String name = reader.readLine().split(":")[1].trim();
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
                    while (!(line = reader.readLine()).equals("---")) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("===\n");
            writer.write(username + "\n");
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
    public void changePriority(MemorySet memorySet, PriorityLevel newPriority) {
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);

                if (line.equals("===")) {
                    String usernameLine = reader.readLine();
                    fileContent.add(usernameLine);

                    String[] idParts = reader.readLine().split(":");
                    if (idParts.length > 1 && Integer.parseInt(idParts[1].trim()) == memorySet.getId()) {
                        fileContent.add("ID: " + memorySet.getId());
                        fileContent.add(reader.readLine()); // Name
                        fileContent.add("Priority: " + newPriority);
                        reader.readLine(); // skipping the old priority level
                        fileContent.add(reader.readLine()); // Last Rehearsed
                    } else {
                        fileContent.add("ID: " + idParts[1].trim());
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String contentLine : fileContent) {
                    writer.write(contentLine + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MemoryPriorityException("Error modifying file", e);
        }
    }

    public void removeMemorySet(int id) {
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
            if (line.equals("===")) {
                try {
                    String idLine = fileContent.get(i + 2);
                    String[] idParts = idLine.split(":");
                    if (idParts.length > 1 && Integer.parseInt(idParts[1].trim()) == id) {
                        start = i;
                        while (i < fileContent.size() && !fileContent.get(i).equals("---")) {
                            i++;
                        }
                        end = i;
                        break;
                    }
                } catch (NumberFormatException e) {
                    continue;
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

}
