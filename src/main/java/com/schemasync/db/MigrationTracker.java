package com.schemasync.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static javax.management.remote.JMXConnectorFactory.connect;

public class MigrationTracker {
    private final String dbUrl;
    private final boolean isInTest;

    public MigrationTracker() {
        this.dbUrl = "jdbc:sqlite:schema_version.db";
        this.isInTest = false;
        init();
    }
    public MigrationTracker(String dbUrl, boolean isInTest) {
        this.dbUrl = dbUrl;
        this.isInTest = isInTest;
        init();
    }

    private void init() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS schema_version (
                    version TEXT PRIMARY KEY,
                    applied_at TEXT NOT NULL
                );
            """);
        } catch (Exception e) {
            System.err.println("Error initializing schema_version DB: " + e.getMessage());
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public boolean isApplied(String version) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM schema_version WHERE version = ?")) {
            stmt.setString(1, version);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error checking version: " + e.getMessage());
            return false;
        }
    }

    public void markAsApplied(String version) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO schema_version (version, applied_at) VALUES (?, ?)"
             )) {
            stmt.setString(1, version);
            stmt.setString(2, LocalDateTime.now().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to mark migration: " + e.getMessage());
        }
    }

    public void printStatus(String migrationDir) {
        try (Stream<Path> files = Files.list(Paths.get(migrationDir))) {
            System.out.printf("%-30s | %-10s\n", "Migration", "Status");
            System.out.println("-------------------------------");
            files.filter(file -> file.toString().endsWith(".sql"))
                    .sorted()
                    .forEach(file -> {
                        String version = file.getFileName().toString();
                        boolean applied = isApplied(version);
                        System.out.printf("%-30s | %s\n", version, applied ? "APPLIED" : "PENDING");
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLastAppliedVersion() {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT version FROM schema_version ORDER BY applied_at DESC LIMIT 1")) {

            ResultSet rs=stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("version");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllAppliedVersions() {
        List<String> versions = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT version FROM schema_version ORDER BY applied_at")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                versions.add(rs.getString("version"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return versions;
    }


    public void removeAppliedVersion(String version) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM schema_version WHERE version = ?")) {
            stmt.setString(1, version);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void ensureVersionTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS schema_version (" +
                "version VARCHAR(255) PRIMARY KEY," +
                "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to ensure schema_version table: " + e.getMessage());
        }
    }
}
