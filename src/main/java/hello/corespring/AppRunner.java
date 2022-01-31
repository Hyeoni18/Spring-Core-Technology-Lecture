package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //ApplicationContext가 EnvironmentCapable를 상속받았기 떄문에 우리는 EnvironmentCapable에 온 Environment를 쓸 수 있는거지.
        Environment environment = ctx.getEnvironment();
        System.out.println(Arrays.toString(environment.getActiveProfiles())); //현재 액티브 되어있는 프로파일이 뭔지.
        System.out.println(Arrays.toString(environment.getDefaultProfiles()));
    }
}
