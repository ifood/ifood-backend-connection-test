package com.ifood.ifoodclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ifood")
public class IfoodclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfoodclientApplication.class, args);
    }
}
