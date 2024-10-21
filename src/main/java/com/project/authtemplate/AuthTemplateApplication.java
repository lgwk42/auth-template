package com.project.authtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AuthTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthTemplateApplication.class, args);
    }

}
