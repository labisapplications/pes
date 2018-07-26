package br.com.usp.labis.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication(scanBasePackages = { "br.com.usp.labis" })
//public class PesApplication extends SpringBootServletInitializer {  /TO RUN ON WILDFLY
public class PesApplication {


	public static void main(String[] args) {
		SpringApplication.run(PesApplication.class, args);
	}
	
	@Bean
    public MessageSource messageSource() {
    	ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    	messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

	//TO RUN ON WILDFLY 
	/*@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PesApplication.class);
	}*/

}
