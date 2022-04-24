package com.harifrizki.crimemapsappsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan("com.harifrizki.crimemapsappsapi")
public class CrimeMapsAppsApiDevApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrimeMapsAppsApiDevApplication.class, args);
	}
}