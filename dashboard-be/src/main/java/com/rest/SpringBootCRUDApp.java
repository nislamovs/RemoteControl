package com.rest;

import com.rest.configuration.H2ServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import com.rest.configuration.JpaConfiguration;


@Import({JpaConfiguration.class, H2ServerConfiguration.class})
@SpringBootApplication(scanBasePackages={"com.rest"})
public class SpringBootCRUDApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCRUDApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SpringBootCRUDApp.class);
    }
}