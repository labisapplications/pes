package br.com.usp.labis.main;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"br.com.usp.labis"})
public class PesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PesApplication.class, args);
	}
}
