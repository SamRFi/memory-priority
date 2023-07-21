package memorypriority.domain;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class MemorySet {
    private String name;
    private Map<String, String> memorySet;
    private PriorityLevel priorityLevel;
    private Date lastTimeRehearsed;

    public MemorySet(String name, Map<String, String> memorySet, PriorityLevel priorityLevel) {
        this.name = name;
        this.memorySet = memorySet;
        this.priorityLevel = priorityLevel;
        lastTimeRehearsed = new Date(System.currentTimeMillis());
    }

    public void rehearsedNow() {
        lastTimeRehearsed = new Date(System.currentTimeMillis());
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
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

}

