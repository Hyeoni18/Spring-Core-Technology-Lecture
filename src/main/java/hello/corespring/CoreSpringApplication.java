package hello.corespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

}
