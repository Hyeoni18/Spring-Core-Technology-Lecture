package hello.corespring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration //이건 bean 설정파일이다. 라는 것을 알려주는 어노테이션임.
@ComponentScan(basePackageClasses = CoreSpringApplication.class) //basePackage 로 패키지명을 설정해도 되지만, basePackageClasses로 해당 클래스가 위치한 곳부터 컴포넌트 스캐닝을 해줘. 라는 의미.
public class ApplicationConfig {

}
