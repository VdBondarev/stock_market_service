package com.bond.config;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomMongoDbContainer extends MongoDBContainer {
    private static final String IMAGE_VERSION = "mongo:latest";
    private static CustomMongoDbContainer container;

    private final String username;
    private final String password;
    private final String databaseName;

    private CustomMongoDbContainer(String username, String password, String databaseName) {
        super(DockerImageName.parse(IMAGE_VERSION));
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;

        withEnv("MONGO_INITDB_ROOT_USERNAME", username);
        withEnv("MONGO_INITDB_ROOT_PASSWORD", password);
        addExposedPort(27017);
    }

    public static CustomMongoDbContainer getInstance(
            String username,
            String password,
            String databaseName
    ) {
        if (container == null) {
            container = new CustomMongoDbContainer(username, password, databaseName);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.data.mongodb.host", getHost());
        System.setProperty("spring.data.mongodb.port", getFirstMappedPort().toString());
        System.setProperty("spring.data.mongodb.username", username);
        System.setProperty("spring.data.mongodb.password", password);
        System.setProperty("spring.data.mongodb.database", databaseName);
    }
}
