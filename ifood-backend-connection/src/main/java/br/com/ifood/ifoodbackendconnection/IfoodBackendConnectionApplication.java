package br.com.ifood.ifoodbackendconnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IfoodBackendConnectionApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(IfoodBackendConnectionApplication.class);
		app.setBanner(new ServiceBanner());
		app.run(args);
	}
}