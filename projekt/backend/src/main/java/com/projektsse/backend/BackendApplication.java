package com.projektsse.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.projektsse.backend.repository.entities")
@EnableJpaRepositories("com.projektsse.backend.repository")
public class BackendApplication {

    public static void main(String[] args) {
        setDotenvVariables();
        SpringApplication.run(BackendApplication.class, args);
    }

    public static void setDotenvVariables() {
        // Load .env variables into application.properties
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach((entry) -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
