package hello.corespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		var messagesSource = new ReloadableResourceBundleMessageSource();
		messagesSource.setBasename("classpath:/messages");
		messagesSource.setDefaultEncoding("UTF-8");
		messagesSource.setCacheSeconds(3);
		return messagesSource;
	}

}
