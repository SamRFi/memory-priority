package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcMemorySetRepositoryTest {

    public static final Logger LOGGER = Logger.getLogger(JdbcMemorySetRepositoryTest.class.getName());
    private static JdbcMemorySetRepository repository;
    private static Connection conn;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        repository = new JdbcMemorySetRepository(() -> conn);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE users (\n" +
                    "    username VARCHAR(255) PRIMARY KEY,\n" +
                    "    hashed_password VARCHAR(255) NOT NULL,\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP\n" +
                    ")");
            stmt.execute("CREATE TABLE memory_sets (\n" +
                    "    id INT AUTO_INCREMENT,\n" +
                    "    name VARCHAR(255),\n" +
                    "    priority_level VARCHAR(255),\n" +
                    "    last_time_rehearsed TIMESTAMP,\n" +
                    "    username VARCHAR(255),\n" +
                    "    FOREIGN KEY(username) REFERENCES users(username),\n" +
                    "    PRIMARY KEY(id)\n" +
                    ")");
            stmt.execute("CREATE TABLE memory_set_entries (\n" +
                    "    memory_set_id INT,\n" +
                    "    key_name VARCHAR(255),\n" +
                    "    value_name VARCHAR(255),\n" +
                    "    FOREIGN KEY(memory_set_id) REFERENCES memory_sets(id),\n" +
                    "    PRIMARY KEY(memory_set_id, key_name)\n" +
                    ")");
            stmt.execute("INSERT INTO users (username, hashed_password) VALUES ('user1', 'password')");
            stmt.execute("INSERT INTO memory_sets (name, priority_level, last_time_rehearsed, username) VALUES ('Set1', 'HIGH', CURRENT_TIMESTAMP, 'user1')");
            stmt.execute("INSERT INTO memory_set_entries (memory_set_id, key_name, value_name) VALUES (1, 'key1', 'value1')");
        }
        conn.close();
    }

    @BeforeEach
    void openDatabaseConnection() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        repository = new JdbcMemorySetRepository(() -> conn);
    }

    @AfterEach
    void closeDatabaseConnection() throws Exception {
        conn.close();
    }

    @Test
    void testGetMemoryCollectionOfUser() {
        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("user1");
        LOGGER.log(Level.INFO, memoryCollection.getMemorySets().toString());
        assertNotNull(memoryCollection);
        assertTrue(memoryCollection.getMemorySets().stream().anyMatch(memorySet -> memorySet.getName().equals("Set1")));
    }

    @Test
    void testGetMemoryCollectionOfNonExistentUser() {
        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("non_existent_user");
        LOGGER.log(Level.INFO, memoryCollection.getMemorySets().toString());
        assertNotNull(memoryCollection);
        assertTrue(memoryCollection.getMemorySets().isEmpty());
    }

    @Test
    void testGetMemoryCollectionOfUserWithNoMemorySets() throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO users (username, hashed_password) VALUES ('user2', 'password')");
        }
        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("user2");
        LOGGER.log(Level.INFO, memoryCollection.getMemorySets().toString());
        assertNotNull(memoryCollection);
        assertTrue(memoryCollection.getMemorySets().isEmpty());
    }

    @Test
    void testGetMemoryCollectionOfUserWithMemorySetNoEntries() throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO memory_sets (name, priority_level, last_time_rehearsed, username) VALUES ('Set2', 'HIGH', CURRENT_TIMESTAMP, 'user1')");
        }
        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("user1");
        LOGGER.log(Level.INFO, memoryCollection.getMemorySets().toString());
        assertNotNull(memoryCollection);
        assertTrue(memoryCollection.getMemorySets().stream().anyMatch(memorySet -> memorySet.getName().equals("Set2")));
        MemorySet set2 = memoryCollection.getMemorySets().stream().filter(memorySet -> memorySet.getName().equals("Set2")).findFirst().orElse(null);
        assertNotNull(set2);
        assertTrue(set2.getMemorySet().isEmpty());
    }

    @Test
    void testAddMemorySetToUser() throws Exception {
        Map<String, String> memorySetMap = new HashMap<>();
        memorySetMap.put("key1", "value1");
        memorySetMap.put("key2", "value2");
        MemorySet memorySet = new MemorySet("Set2", memorySetMap, PriorityLevel.HIGH);

        repository.addMemorySetToUser("user1", memorySet);

        openDatabaseConnection();

        MemoryCollection memoryCollection = repository.getMemoryCollectionOfUser("user1");
        LOGGER.log(Level.INFO, memoryCollection.getMemorySets().toString());

        assertNotNull(memoryCollection);
        assertTrue(memoryCollection.getMemorySets().stream().anyMatch(memorySetRetrieved -> memorySetRetrieved.getName().equals("Set2")));

        MemorySet set2 = memoryCollection.getMemorySets().stream().filter(memorySetRetrieved -> memorySetRetrieved.getName().equals("Set2")).findFirst().orElse(null);
        assertNotNull(set2);
        assertEquals(memorySet, set2);
    }



}
