package memorypriority.domain;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MemorySet {
    private UUID id;
    private String name;
    private PriorityLevel priorityLevel;
    private Timestamp lastTimeRehearsed;
    private List<Map.Entry<String, String>> pairList;
    private int currentIndex;

    public MemorySet(String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = new Timestamp(System.currentTimeMillis());
        this.pairList = pairList;
        this.currentIndex = 0;
    }

    public MemorySet(String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel, Timestamp lastTimeRehearsed) {
        this(name, pairList, priorityLevel);
        this.lastTimeRehearsed = lastTimeRehearsed;
    }

    public MemorySet(UUID id, String name, List<Map.Entry<String, String>> pairList, PriorityLevel priorityLevel, Timestamp lastTimeRehearsed) {
        this(name, pairList, priorityLevel, lastTimeRehearsed);
        this.id = id;
    }

    public void rehearsedNow() {
        lastTimeRehearsed = new Timestamp(System.currentTimeMillis());
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public UUID getId() {
        return id;
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

    public void shuffle() {
        Collections.shuffle(pairList);
        currentIndex = 0;
    }

    // Removed getRandomPair method

    public Map.Entry<String, String> getNextPair() {
        if (currentIndex >= pairList.size()) {
            currentIndex = 0;
        }
        Map.Entry<String, String> nextPair = pairList.get(currentIndex);
        currentIndex++;
        return nextPair;
    }

    public Map.Entry<String, String> getFirstPair() {
        currentIndex = 0;
        return pairList.get(0);
    }

    public void resetCurrentIndex() {
        this.currentIndex = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemorySet memorySet1 = (MemorySet) o;
        return Objects.equals(name, memorySet1.name) &&
                Objects.equals(pairList, memorySet1.pairList) &&
                priorityLevel == memorySet1.priorityLevel &&
                Objects.equals(lastTimeRehearsed, memorySet1.lastTimeRehearsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pairList, priorityLevel, lastTimeRehearsed);
    }
}
