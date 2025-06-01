package com.schemasync.core;



import com.schemasync.db.MigrationTracker;
import com.schemasync.utils.FilesUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MigrationCreator {

    private final MigrationTracker tracker;

    public MigrationCreator(MigrationTracker tracker) {
        this.tracker = tracker;
    }

    public void createMigration(String name, boolean up, boolean down) {
        String version = getNextVersion(); // e.g., "1", "2", etc.
        String baseName = "V" + version + "__" + name.replace(" ", "_");

        String upFile = "migrations/" + baseName + ".sql";
        String downFile = "rollback/" + baseName + ".down.sql";

        // Special handling for V1__init.sql — only allow up file
        boolean isInitMigration = version.equals("1") && name.equalsIgnoreCase("init");

        if (!isInitMigration) {
            // Block if this file version already exists and is not the last
            String latestVersion = tracker.getLastAppliedVersion();
            boolean isAlreadyApplied = tracker.isApplied(baseName + ".sql");

            if (isAlreadyApplied && !baseName.concat(".sql").equals(latestVersion)) {
                System.out.println("Cannot modify migration '" + baseName + "'. It has already been applied and is not the latest.");
                return;
            }
        }

        // Always create up file for V1__init
        if (up || isInitMigration) {
            createFile(upFile, "-- Migration: " + name);
        }

        // Create rollback only if requested and not V1
        if (down && !isInitMigration) {
            createFile(downFile, "-- Rollback: " + name);
        }

        System.out.println("Created migration files:");
        if (up || isInitMigration) System.out.println("  → " + upFile);
        if (down && !isInitMigration) System.out.println("  → " + downFile);
    }


    private String getNextVersion() {
        File migrationDir = new File("migrations");
        if (!migrationDir.exists()) migrationDir.mkdirs();

        File[] files = migrationDir.listFiles((dir, name) -> name.startsWith("V") && name.endsWith(".sql"));

        if (files == null || files.length == 0) return "1";

        return String.valueOf(
                FilesUtils.getMaxVersion(files) + 1
        );
    }

    private void createFile(String path, String content) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (file.exists()) {
                System.out.println("File already exists: " + path);
                return;
            }
            FileWriter fw = new FileWriter(file);
            fw.write(content + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Failed to create file: " + path);
            e.printStackTrace();
        }
    }
}
