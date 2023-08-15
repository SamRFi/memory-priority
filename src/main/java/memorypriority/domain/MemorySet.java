package memorypriority.domain;

import java.sql.Date;
import java.util.Map;
import java.util.Objects;

public class MemorySet {

    private int Id;
    private String name;
    private Map<String, String> memorySet;
    private PriorityLevel priorityLevel;
    private Date lastTimeRehearsed;

    public MemorySet(String name, Map<String, String> memorySet, PriorityLevel priorityLevel) {
        this.name = name;
        this.memorySet = memorySet;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = new Date(System.currentTimeMillis());
    }

    public MemorySet(String name, Map<String, String> memorySet, PriorityLevel priorityLevel, Date lastTimeRehearsed) {
        this.name = name;
        this.memorySet = memorySet;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = lastTimeRehearsed;
    }

    public MemorySet(int id, String name, Map<String, String> memorySet, PriorityLevel priorityLevel, Date lastTimeRehearsed) {
        this.Id = id;
        this.name = name;
        this.memorySet = memorySet;
        this.priorityLevel = priorityLevel;
        this.lastTimeRehearsed = lastTimeRehearsed;
    }

    public void rehearsedNow() {
        lastTimeRehearsed = new Date(System.currentTimeMillis());
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

    public Map<String, String> getMemorySet() {
        return memorySet;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public Date getLastTimeRehearsed() {
        return lastTimeRehearsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemorySet memorySet1 = (MemorySet) o;
        return Objects.equals(name, memorySet1.name) && Objects.equals(memorySet, memorySet1.memorySet) && priorityLevel == memorySet1.priorityLevel && Objects.equals(lastTimeRehearsed, memorySet1.lastTimeRehearsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, memorySet, priorityLevel, lastTimeRehearsed);
    }


}

