package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    BookRepository bookRepository;

    @Value("${app.name}")
    String appName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //ApplicationContext가 EnvironmentCapable를 상속받았기 떄문에 우리는 EnvironmentCapable에 온 Environment를 쓸 수 있는거지.
        Environment environment = ctx.getEnvironment();
        System.out.println(environment.getProperty("app.name"));
        //프로퍼티 소스가 environment에 들어오면 꺼내서 사용할 수 있음.
        //이럴 때 둘의 우선순위가 어떻게 될까. 프로퍼티 소스로 넣은게 높을까, JVM 시스템 프로퍼티에 넘겨준게 높을까
        //JVM 옵션이 더 높다.
        System.out.println(appName);
    }
}
