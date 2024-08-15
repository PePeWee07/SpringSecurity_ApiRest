package com.ucacue.UcaApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Habilita las tareas programadas
public class UcaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(UcaAppApplication.class, args);
	}

}
