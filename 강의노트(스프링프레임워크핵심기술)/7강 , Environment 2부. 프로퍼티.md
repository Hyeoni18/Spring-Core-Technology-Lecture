<h3>스프링 프레임워크 핵심 기술</h3>

7강 , Environment 2부. 프로퍼티

Environment 제공하는 두 번째 기능 프로퍼티.

애플리케이션에 등록되는 여러가지 키,밸류 쌍으로 제공되는 그런 프로퍼티에 접근할 수 있는 기능임.

계층형으로 접근함. 프로퍼티는 여러가지 소스가 있는데

StandardServletEnvironment의 우선순위
○ ServletConfig 매개변수
○ ServletContext 매개변수
○ JNDI (java:comp/env/)
○ JVM 시스템 프로퍼티 (-Dkey=”value”) , 자바 애플리케이션이 실행할 때 넘겨주는 프로퍼티들.
○ JVM 시스템 환경 변수 (운영 체제 환경 변수)

예를 들어, 기본적으로 VM Option에

-Dapp.name=spring5 설정했을 때

내부적으로는 계층형으로 정리를 하고, 우리는 getProperty 를 쓰면 된다.

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext ctx;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Environment environment = ctx.getEnvironment();
        System.out.println(environment.getProperty("app.name")); //결과 spring5
    }
}
```

이런 방법 말고 좀 더 체계적으로 값을 전달하고 싶을때

app.properties 생성 

```properties
app.name=spring
```

```java
@SpringBootApplication
@PropertySource("classpath:/app.properties") //app.properties 파일을 프로퍼티 소스에 놓겠다 선언.
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext ctx;
    
    //이런것도 가능함
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
```

외에도 프로퍼티 파일을 쉽게 사용할 수 있게 스프링 부트가 지원해주는 기능이 존재함. 다른 스프링 부트 강좌를 참고. 

이게 기본 프로퍼티 소스를 지원하는거고 이거를 기반으로 스프링 부트가 기능들을 제공해주는거야.