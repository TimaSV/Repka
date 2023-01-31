package main;

import main.apiResponses.StatisticsInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//TODO:

@SpringBootApplication
public class SearchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineApplication.class, args);
	}

	@Bean(initMethod="init")
	public StatisticsInitializer getBean() {
		return new StatisticsInitializer();
	}
}
