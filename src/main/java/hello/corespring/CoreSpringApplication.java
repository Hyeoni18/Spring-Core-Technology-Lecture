package hello.corespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/app.properties") //app.properties 파일을 프로퍼티 소스에 놓겠다 선언.
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

}
