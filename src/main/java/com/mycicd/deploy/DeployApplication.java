package com.mycicd.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class DeployApplication {



	public static void main(String[] args) {
		SpringApplication.run(DeployApplication.class, args);
	}

}
