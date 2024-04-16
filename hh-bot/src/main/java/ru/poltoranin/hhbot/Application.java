package ru.poltoranin.hhbot;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.poltoranin.hhbot.service.BotService;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

	public static void main(String[] args) throws IOException, InterruptedException {
		var contex = SpringApplication.run(Application.class, args);
		var botService = contex.getBean(BotService.class);
		botService.run();
		
		contex.close();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
