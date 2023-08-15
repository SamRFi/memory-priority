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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public MemoryCollection getMemoryCollectionOfUser(String username) {
        Set<MemorySet> memorySets = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("===") && username.equals(reader.readLine())) {
                    int id = Integer.parseInt(reader.readLine().split(":")[1].trim());
                    String name = reader.readLine().split(":")[1].trim();
                    PriorityLevel priorityLevel = PriorityLevel.valueOf(reader.readLine().split(":")[1].trim());
                    java.util.Date utilDate = format.parse(reader.readLine().split(":")[1].trim());
                    java.sql.Date lastTimeRehearsed = new java.sql.Date(utilDate.getTime());

                    Map<String, String> entries = new HashMap<>();
                    while (!(line = reader.readLine()).equals("---")) {
                        String[] parts = line.split(":");
                        entries.put(parts[0].trim(), parts[1].trim());
                    }
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
            for (Map.Entry<String, String> entry : memorySet.getMemorySet().entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
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


}
