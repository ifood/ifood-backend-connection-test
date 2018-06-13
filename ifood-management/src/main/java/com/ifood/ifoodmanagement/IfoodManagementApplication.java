package com.ifood.ifoodmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@ComponentScan(basePackages = "com.ifood")
public class IfoodManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfoodManagementApplication.class, args);
    }
}
