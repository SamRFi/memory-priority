package memorypriority.data;

import memorypriority.domain.MemoryCollection;
import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcMemorySetRepository implements MemorySetRepository {
    public static final Logger LOGGER = Logger.getLogger(JdbcMemorySetRepository.class.getName());
    private final Supplier<Connection> connectionSupplier;

    public JdbcMemorySetRepository(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public JdbcMemorySetRepository() throws SQLException {
        this.connectionSupplier = () -> {
            try {
                return JdbcConnection.getConnection();
            } catch (SQLException e) {
                throw new MemoryPriorityException("Failed to get connection to the database", e);
            }
        };
    }

    private List<Map.Entry<String, String>> getMemorySetEntries(Connection conn, int memorySetId) {
        String sql = "SELECT key_name, value_name FROM memory_set_entries WHERE memory_set_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memorySetId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Create a new list to store the entries
                List<Map.Entry<String, String>> entries = new ArrayList<>();
                while (rs.next()) {
                    // Create a new entry with the key and value from the result set
                    Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(rs.getString("key_name"), rs.getString("value_name"));
                    // Add the entry to the list
                    entries.add(entry);
                }
                return entries;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }



    private Set<MemorySet> getMemorySetsForUser(Connection conn, String username) {
        String sql = "SELECT * FROM memory_sets WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                Set<MemorySet> memorySets = new HashSet<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    PriorityLevel priorityLevel = PriorityLevel.valueOf(rs.getString("priority_level"));
                    Timestamp lastTimeRehearsed = new Timestamp(rs.getTimestamp("last_time_rehearsed").getTime());

                    List<Map.Entry<String, String>> entries = getMemorySetEntries(conn, id);

                    memorySets.add(new MemorySet(id, name, entries, priorityLevel, lastTimeRehearsed));
                }
                return memorySets;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error when getting memory sets for user", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }

    public MemoryCollection getMemoryCollectionOfUser(String username) {
        try (Connection conn = connectionSupplier.get()) {
            return new MemoryCollection(getMemorySetsForUser(conn, username));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }

    public void addMemorySetToUser(String username, MemorySet memorySet) {
        String insertMemorySetSql = "INSERT INTO memory_sets (name, priority_level, last_time_rehearsed, username) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionSupplier.get();
             PreparedStatement insertMemorySetStmt = conn.prepareStatement(insertMemorySetSql, Statement.RETURN_GENERATED_KEYS)) {

            insertMemorySetStmt.setString(1, memorySet.getName());
            insertMemorySetStmt.setString(2, memorySet.getPriorityLevel().toString());
            insertMemorySetStmt.setTimestamp(3, new Timestamp(memorySet.getLastTimeRehearsed().getTime()));
            insertMemorySetStmt.setString(4, username);

            int affectedRows = insertMemorySetStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MemoryPriorityException("Creating memory set failed, no rows affected.");
            }

            try (ResultSet generatedKeys = insertMemorySetStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int memorySetId = generatedKeys.getInt(1);

                    String insertEntrySql = "INSERT INTO memory_set_entries (memory_set_id, key_name, value_name) VALUES (?, ?, ?)";
                    for (Map.Entry<String, String> entry : memorySet.getPairList()) {
                        try (PreparedStatement insertEntryStmt = conn.prepareStatement(insertEntrySql)) {
                            insertEntryStmt.setInt(1, memorySetId);
                            insertEntryStmt.setString(2, entry.getKey());
                            insertEntryStmt.setString(3, entry.getValue());
                            insertEntryStmt.executeUpdate();
                        }
                    }

                } else {
                    throw new MemoryPriorityException("Creating memory set failed, no ID obtained.");
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }

    public void changePriority(MemorySet memorySet, PriorityLevel newPriority) {
        String sql = "UPDATE memory_sets SET priority_level = ? WHERE id = ?";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPriority.toString());
            stmt.setInt(2, memorySet.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new MemoryPriorityException("Changing priority failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error when changing priority", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }

    @Override
    public void removeMemorySet(int id) {
        throw new MemoryPriorityException("database memory set removal not yet implemented");
    }

    protected Connection getConnection() throws SQLException {
        return JdbcConnection.getConnection();
    }

}

