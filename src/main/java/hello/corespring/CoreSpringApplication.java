package hello.corespring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

@SpringBootApplication //사실 이것만 있으면 돼. 해당 어노테이션에는 ComponentScan 과 Configuration 이 포함되어 있기 때문에야.
public class CoreSpringApplication {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class); //해당 클래스를 bean 설정으로 사용하겠다 선언.

		//application.xml 을 생성했으면 사용해야지.
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		System.out.println(Arrays.toString(beanDefinitionNames)); //bean 으로 등록된 이름을 가져옴.
		BookService bookService = (BookService) context.getBean("bookService"); //그냥은 Object로 나오니까 타입 캐스팅을 해줘야 해.
		System.out.println(bookService.bookRepository != null); //True

	}

}
