package com.schemasync;

import com.schemasync.config.*;
import com.schemasync.core.MigrationCreator;
import com.schemasync.db.MigrationExecutor;
import com.schemasync.db.MigrationTracker;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ConfigLoader configLoader=new ConfigLoader();
        configLoader.load();
        MigrationTracker tracker = new MigrationTracker();
        MigrationExecutor executor = new MigrationExecutor(configLoader.getDbUrl(), configLoader.getUser(), configLoader.getPassword(), tracker);
        MigrationCreator creator = new MigrationCreator(tracker);

        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  --migrate                     Apply pending migrations");
            System.out.println("  --rollback                    Rollback the last applied migration");
            System.out.println("  --status                      Show status of all migrations");
            System.out.println("  --create \"Name\" [--up] [--down]   Create migration file(s)");
            return;
        }

        switch (args[0]) {
            case "--migrate":
                executor.applyMigrations("migrations");
                break;

            case "--rollback":
                executor.rollbackLast("rollback");
                break;

            case "--status":
                tracker.printStatus("migrations");
                break;

            case "--create":
                if (args.length < 2) {
                    System.out.println("Please provide a migration name.");
                    return;
                }

                String name = args[1];
                boolean up = Arrays.asList(args).contains("--up") || name.equalsIgnoreCase("init"); // init always implies up
                boolean down = Arrays.asList(args).contains("--down");

                if (!up && !down && !name.equalsIgnoreCase("init")) {
                    System.out.println("Specify --up, --down, or both.");
                    return;
                }

                creator.createMigration(name, up, down);
                break;

            default:
                System.out.println("Unknown command: " + args[0]);
        }
    }
}
