package hello.corespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreSpringApplication {

	//펑션을 사용한 빈 등록
	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

}
