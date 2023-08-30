package memorypriority.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class MemorySet {

    private int Id;
    private String name;
    private PriorityLevel priorityLevel;
    private Timestamp lastTimeRehearsed;

    // Add a field to store the list of key-value pairs
    private List<Map.Entry<String, String>> pairList;

    // Add a field to store the current index of the pair list
    private int currentIndex;

    public MemorySet(String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel) {
        this.name = name;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = new Timestamp(System.currentTimeMillis());
        // Initialize the pair list and the current index
        this.pairList = pairList;
        this.currentIndex = 0;
    }

    public MemorySet(String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel, Timestamp lastTimeRehearsed) {
        this.name = name;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = lastTimeRehearsed;
        this.pairList = pairList;
        this.currentIndex = 0;
    }

    public MemorySet(int id, String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel, Timestamp lastTimeRehearsed) {
        this.Id = id;
        this.name = name;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = lastTimeRehearsed;
        // Initialize the pair list and the current index
        this.pairList = pairList;
        this.currentIndex = 0;
    }

    public void rehearsedNow() {
        lastTimeRehearsed = new Timestamp(System.currentTimeMillis());
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public List<Map.Entry<String, String>> getPairList() {
        return pairList;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public Timestamp getLastTimeRehearsed() {
        return lastTimeRehearsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemorySet memorySet1 = (MemorySet) o;
        return Objects.equals(name, memorySet1.name) && Objects.equals(pairList, memorySet1.pairList) && priorityLevel == memorySet1.priorityLevel && Objects.equals(lastTimeRehearsed, memorySet1.lastTimeRehearsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pairList, priorityLevel, lastTimeRehearsed);
    }

    // Add a method to shuffle the pair list
    public void shuffle() {
        Collections.shuffle(pairList); // Use the Collections utility class to randomize the order of pairs
        currentIndex = 0; // Reset the current index to zero
    }

    // Add a method to get a random pair from the pair list
    public Map.Entry<String, String> getRandomPair() {
        int randomIndex = (int) (Math.random() * pairList.size()); // Generate a random index between 0 and the size of the pair list
        return pairList.get(randomIndex); // Return the pair at that index
    }

    // Add a method to get the next pair in sequence from the pair list
    public Map.Entry<String, String> getNextPair() {
        if (currentIndex >= pairList.size()) { // If the current index is out of bounds
            currentIndex = 0; // Wrap around to zero
        }
        Map.Entry<String, String> nextPair = pairList.get(currentIndex); // Get the pair at the current index
        currentIndex++; // Increment the current index by one
        return nextPair; // Return the next pair
    }

    // Add a method to get the first pair from the pair list
    public Map.Entry<String, String> getFirstPair() {
        currentIndex = 0; // Reset the current index to zero
        return pairList.get(0); // Return the first pair in the list
    }

}

