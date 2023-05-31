package com.xogito.project.user.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
//@ComponentScan(basePackages = "com.xogito.project.user.management.mapper")
public class ProjectUserManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectUserManagementApplication.class, args);
    }

}
