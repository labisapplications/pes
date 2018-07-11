package br.com.usp.labis.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = { "br.com.usp.labis" })
public class PesApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PesApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PesApplication.class);
	}

}
