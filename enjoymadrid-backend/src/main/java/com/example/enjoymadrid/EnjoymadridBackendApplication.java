package com.example.enjoymadrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class EnjoymadridBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnjoymadridBackendApplication.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedOrigins("http://localhost:8100", "capacitor://localhost", "ionic://localhost", "https://enjoy-madrid-d18ed.web.app")
				.allowedMethods("GET", "PUT", "POST", "DELETE");
			}
		};
	}

}
