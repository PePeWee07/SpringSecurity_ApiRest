package com.ucacue.UcaApp;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Habilita las tareas programadas
public class UcaAppApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Guayaquil"));
		SpringApplication.run(UcaAppApplication.class, args);


		// ----- En caso de olvidar contraseña rempalzar por el hash -----
		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // String clavePlana = "admin";
        // String hashGenerado = encoder.encode(clavePlana);
        
        // System.out.println("Clave original: " + clavePlana);
        // System.out.println("Hash para tu SQL: " + hashGenerado);
	}

}
