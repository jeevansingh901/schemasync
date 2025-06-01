package com.schemasync;
import com.schemasync.db.MigrationTracker;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SchemaTrackerTest {
    private MigrationTracker tracker;

    @BeforeEach
    void setupTrackerAndCleanDb() {
        tracker = new MigrationTracker("jdbc:sqlite:schema_version.db", true);
        try (Connection conn = tracker.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS schema_version");
            stmt.execute("""
                CREATE TABLE schema_version (
                    version TEXT PRIMARY KEY,
                    applied_at TEXT NOT NULL
                )
            """);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to set up schema_version table: " + e.getMessage());
        }
    }

    @Test
    public void testAddAndGetVersions() {
        tracker.markAsApplied("V1__init.sql");
        tracker.markAsApplied("V2__add_user.sql");
        List<String> versions = tracker.getAllAppliedVersions();
        assertEquals(2, versions.size(), "Expected 2 versions but got: " + versions);
        assertTrue(versions.contains("V1__init.sql"));
        assertTrue(versions.contains("V2__add_user.sql"));
    }

    @Test
    public void testRemoveVersion() {
        tracker.markAsApplied("V1__init.sql");
        tracker.markAsApplied("V2__add_user.sql");
        tracker.removeAppliedVersion("V2__add_user.sql");
        List<String> versions = tracker.getAllAppliedVersions();
        assertEquals(1, versions.size(), "Expected 1 version but got: " + versions);
        assertFalse(versions.contains("V2__add_user.sql"));
    }
}
