package com.biblioteca.api_sistema_biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiSistemaBibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiSistemaBibliotecaApplication.class, args);
	}

}
