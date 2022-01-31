package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ResourceBanner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(resourceLoader.getClass()); //class org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext

        Resource resource = resourceLoader.getResource("classpath:test.txt");
        System.out.println(resource.getClass()); //class org.springframework.core.io.ClassPathResource
        System.out.println(resource.exists()); //true
        System.out.println(resource.getDescription()); //class path resource [test.txt]
        System.out.println(Files.readString(Path.of(resource.getURI()))); //hello spring

        Resource resource2 = resourceLoader.getResource("test.txt"); //classpath 라는 문자열을 지우면, 위에서 WebServerApplicationContext 나왔으니까 기본적으로 ServletContextResource 가 되어야 해.
        //그러면 ServletContextResource 는 WebApplicationRoot 즉 컨텍스트 루트, 컨텍스트 패스부터 찾게되는거야. 그 리소스를. (Tomcat started on port(s): 8080 (http) with context path '')
        //그런데 스프링부트가 띄워주는 내장형 톰캣에는 컨텍스트 패스가 지정되어 있지 않음. 따라서 리소스를 찾을 수 없음.
        System.out.println(resource2.getClass()); //class org.springframework.web.context.support.ServletContextResource
        System.out.println(resource2.exists()); // 그러니까 false 가 찍히겠지

        System.out.println(resource2.getDescription()); //ServletContext resource [/test.txt]
        System.out.println(Files.readString(Path.of(resource2.getURI()))); //그리고 여기서 오류가 날거야. 없는 파일을 읽으려고 했으니까.

        //이렇게 스프링부트 기반의 애플리케이션을 작성할 때, 보통(특히 jsp 를 사용하지 않을 때) 클래스 패스 기준으로 많은 리소스를 사용함.
        //따라서 클래스패스 접두어를 사용하는 것을 추천함. 그냥 리소스 이름만 적으면 ServletContextResource로 리졸빙이 된다는 것을 이해 해야 해.
    }
}
