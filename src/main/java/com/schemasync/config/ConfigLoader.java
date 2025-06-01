package com.schemasync.config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private String dbUrl;
    private String user;
    private String password;

    public void load() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("application.properties")) {
            props.load(fis);
            this.dbUrl = props.getProperty("url");
            this.user = props.getProperty("user");
            this.password = props.getProperty("password");

            System.out.println("Loaded DB Config: " + props);
        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
            System.exit(1);
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
