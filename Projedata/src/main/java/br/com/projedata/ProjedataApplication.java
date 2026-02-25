package br.com.projedata;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = " Practical Test Projedata ",
				version = "1.0",
				description = "Application for managing inputs and optimizing industrial production.",
				contact = @Contact(name = "Carlos Roberto ribeiro Santos Junior", email = "crrsj1@gmail.com")
		)
)
public class ProjedataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjedataApplication.class, args);
	}

}
