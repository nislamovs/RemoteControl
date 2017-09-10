package com.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.rest.configuration.JpaConfiguration;


@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages={"com.rest"})
public class SpringBootCRUDApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCRUDApp.class, args);
    }
}
