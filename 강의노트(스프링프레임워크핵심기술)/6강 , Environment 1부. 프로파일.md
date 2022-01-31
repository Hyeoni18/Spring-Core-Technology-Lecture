<h3>스프링 프레임워크 핵심 기술</h3>

6강 , Environment 1부. 프로파일

ApplicationContext는 빈팩토리 기능만 하는건 아님.

ApplicationContext가 상속받고 있는 인터페이스 중 EnvironmentCapable. EnvironmentCapable 이 제공하는 기능이 크게 2가지 있음. 그 중 프로파일을 살펴보겠음.

우선 프로파일은 빈들의 묶음임. 메이븐에도 프로파일 기능이 있고 스프링에 있는 프로파일도 마찬가지임. 

어떤 환경임. 테스트 환경에서는 이런 빈을 쓰겠다. 실제 프로덕션에선 이런거, 알파나 베타 에서는 이런 빈을 쓰겠다. 각각의 환경에서 다른 빈을 쓰고, 특정 환경에서만 어떤 빈을 등록해야 하는 경우. 그런 요구사항을 충족시키기 위해 프로파일 기능이 추가됨. 그리고 그 기능은 스프링 Environment 라는 인터페이스를 통해 우리가 쓸 수 있음.

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext ctx;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //ApplicationContext가 EnvironmentCapable를 상속받았기 떄문에 우리는 EnvironmentCapable에 온 Environment를 쓸 수 있는거지.
        Environment environment = ctx.getEnvironment();
        System.out.println(Arrays.toString(environment.getActiveProfiles())); //현재 액티브 되어있는 프로파일이 뭔지. //결과 []
        System.out.println(Arrays.toString(environment.getDefaultProfiles())); //결과 [default]
    }
}
```

default 는 아무런 프로파일을 설정하지 않아도 적용되는거야. @Component 같은 것들도 어떻게 보면 기본에 속하는거지.

그러면 이제 Congifuration 클래스를 따로 정의하는 방법을 알아볼거야.

TestConfiguration, TestBookRepository, BookRepository 생성

```java
@Configuration
@Profile("test") //test 프로파일일 때만 사용이 되는 빈 설정 파일이 됨.
public class TestConfiguration {

    @Bean
    public BookRepository bookRepository() {
        return new TestBookRepository();
    }
}
```

```java
public class TestBookRepository implements BookRepository{
}
```

```java
public interface BookRepository {
}
```

이렇게 생성하고 

```
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    BookRepository bookRepository;
}
```

BookRepository 를 찾으려 하면 찾을 수 없다고 오류가 발생함.

Field bookRepository in hello.corespring.AppRunner required a bean of type 'hello.corespring.BookRepository' that could not be found.

왜냐면 test 프로파일일 때만 실행하라고 했으니까.

프로파일을 지정하는 방법은

-Dspring.profiles.active=test 를 VM Option에 설정해주면 됨.

다시 프로파일을 정의하는 방법으로 돌아가면,

```java
@Configuration
public class TestConfiguration {

    @Bean
    @Profile("test") //그냥 메소드에 지정할 수도 있음.
    public BookRepository bookRepository() {
        return new TestBookRepository();
    }
}
```

그리고 컴포넌트 스캔으로 등록되는 빈에도 프로파일을 지정할 수 있음.

```java
@Repository
@Profile("test")
public class TestBookRepository implements BookRepository{
}
```

그리고 @Profile("!prod") 이렇게도 설정할 수 있음.

prod 가 아닌 경우에 등록해줘. 외에도 !(not), &(and), |(or) 을 쓸 수 있다.

@Profile("!prod & test") 이렇게도 쓸 수 있지만 최대한 단순하게 쓰는게 좋다.