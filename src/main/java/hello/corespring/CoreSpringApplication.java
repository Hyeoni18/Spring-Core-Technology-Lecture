package hello.corespring;

import hello.out.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Supplier;

@SpringBootApplication
public class CoreSpringApplication {

	@Autowired
	MyService myService; //빈 주입이 안됐을 경우 error 발생.

	//펑션을 사용한 빈 등록
	public static void main(String[] args) {
		//인스턴스를 만들어서 사용하는 방법
		var app = new SpringApplication(CoreSpringApplication.class);
		//중간에 뭔가를 하고 싶다.
		app.addInitializers((ApplicationContextInitializer<GenericApplicationContext>) ctx -> {
			if(true)
			//직접 빈을 등록할 수 있어.
			ctx.registerBean(MyService.class); //컴포넌트 스캔 범위 밖에 생성한 클래스를 등록
			ctx.registerBean(ApplicationRunner.class, () -> args1 -> System.out.println("Functional Bean Definition!!"));
		});
		app.run(args);
				//빌더를 사용하는 방법
//		new SpringApplicationBuilder()
//				.sources(CoreSpringApplication.class)
//				.initializers((ApplicationContextInitializer<GenericApplicationContext>)
//						applicationContext -> {
//							applicationContext.registerBean(MyService.class);
//						})
//				.run(args);
	}

}
