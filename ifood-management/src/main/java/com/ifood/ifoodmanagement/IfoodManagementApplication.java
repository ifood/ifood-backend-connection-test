package com.ifood.ifoodmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class IfoodManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfoodManagementApplication.class, args);
    }
}
