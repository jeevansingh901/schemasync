package com.schemasync.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Comparator;
import java.util.stream.Stream;

public class MigrationExecutor {
    private final String url;
    private final String user;
    private final String password;
    private final MigrationTracker tracker;

    public MigrationExecutor(String url, String user, String password,MigrationTracker tracker) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.tracker=tracker;
    }


    public void applyMigrations(String migrationDir) {
        try(Connection conn = DriverManager.getConnection(url,user,password)){

            try (Stream<Path> paths = Files.list(Paths.get(migrationDir))) {
                paths.filter(path -> path.toString().endsWith(".sql"))
                        .sorted(Comparator.comparing(Path::toString))
                        .forEach(path -> {
                            String version = path.getFileName().toString();
                            if (!tracker.isApplied(version)) {
                                applyMigration(conn, path);
                                tracker.markAsApplied(version);
                            }
                            else {
                                System.out.println("Skipped (already applied): " + version);
                            }
                            });

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    private  void applyMigration(Connection conn, Path sqlFilePath) {
        System.out.println("Applying migration: " + sqlFilePath.getFileName());


        try {
            String sql = Files.readString(sqlFilePath);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Applied: " + sqlFilePath.getFileName());
            }
        } catch (IOException e) {
            System.err.println("Failed to read: " + sqlFilePath);
        } catch (Exception e) {
            System.err.println("Migration error: " + e.getMessage());
        }

    }
    public void rollbackLast(String rollbackDir) {
        String lastVersion = tracker.getLastAppliedVersion();
        if (lastVersion == null) {
            System.out.println("No applied migrations to rollback.");
            return;
        }
        String rollbackFile = rollbackDir + "/" + lastVersion.replace(".sql", ".down.sql");
        Path path = Paths.get(rollbackFile);
        if (!Files.exists(path)) {
            System.out.println("No rollback script found for " + lastVersion);
            return;
        }
        try (Connection conn = DriverManager.getConnection(url,user,password)) {
            String sql = Files.readString(path, StandardCharsets.UTF_8);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                tracker.removeAppliedVersion(lastVersion);
                System.out.println("Rolled back: " + lastVersion);
            }
        } catch (Exception e) {
            System.err.println("Rollback failed: " + e.getMessage());
        }
    }

}
