package memorypriority.data;

import memorypriority.domain.MemorySet;
import memorypriority.domain.PriorityLevel;
import memorypriority.util.MemoryPriorityException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcMemorySetRepository {
    public static final Logger LOGGER = Logger.getLogger(JdbcMemorySetRepository.class.getName());

    private Map<String, String> getMemorySetEntries(int memorySetId) {
        String sql = "SELECT key_name, value_name FROM memory_set_entries WHERE memory_set_id = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memorySetId);

            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, String> entries = new HashMap<>();
                while (rs.next()) {
                    entries.put(rs.getString("key_name"), rs.getString("value_name"));
                }
                return entries;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }

    private List<MemorySet> getMemorySetsForUser(String username) {
        String sql = "SELECT * FROM memory_sets WHERE username = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                List<MemorySet> memorySets = new ArrayList<>();
                while (rs.next()) {
                    Map<String, String> entries = getMemorySetEntries(rs.getInt("id"));
                    memorySets.add(new MemorySet(
                            rs.getString("name"),
                            entries,
                            PriorityLevel.valueOf(rs.getString("priority_level")),
                            new Date(rs.getTimestamp("last_time_rehearsed").getTime())));
                }
                return memorySets;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }


}

