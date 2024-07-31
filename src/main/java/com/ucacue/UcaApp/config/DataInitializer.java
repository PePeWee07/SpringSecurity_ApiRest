package com.ucacue.UcaApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DataInitializer {

    // @Autowired
    // private DataSource dataSource;

    // @Bean
    // public ApplicationRunner initializer() {
    //     return args -> {
    //         ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    //         resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));
    //         resourceDatabasePopulator.execute(dataSource);
    //     };
    // }
}