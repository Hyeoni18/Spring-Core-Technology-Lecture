<h3>스프링 프레임워크 핵심 기술</h3>

2강 , ApplicationContext와 다양한 빈 설정 방법

SpringBoot Project 생성 후 BookService, BookRepository 생성

```java
public class BookRepository {
}
```

```java
public class BookService {

    BookRepository bookRepository;

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

}
```

xml 태그 이용

resources 폴더에 application.xml 생성

```xml
<beans>
<bean id="bookService" class="hello.corespring.book.BookService">
        <!--이 상태로는 bookService 가 bookRepository 를 주입받지 못해.-->
        <!-- ref(레퍼런스)를 빈에 주입을 직접 해줘야 해. name의 bookRepository 는 세터에서 가져온거고, ref 는 다른 빈을 참조한다는 의미야. 그래서 ref 뒤에는 다른 빈의 아이디가 와야 해.-->
        <property name="bookRepository" ref="bookRepository" />
    </bean>
    <bean id="bookRepository" class="hello.corespring.book.BookRepository">
    </bean>
</beans>
```

main 메소드에서 등록된 bean 확인

```java
public static void main(String[] args) {
		//application.xml 을 생성했으면 사용해야지.
		ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		System.out.println(Arrays.toString(beanDefinitionNames)); //bean 으로 등록된 이름을 가져옴.
		BookService bookService = (BookService) context.getBean("bookService"); //그냥은 Object로 나오니까 타입 캐스팅을 해줘야 해.
		System.out.println(bookService.bookRepository != null); //True
    //의존성이 주입됐는지 확인
//근데 이거 package 가 다를 때는 못찾던데 왜 그럴까. book 패키지에 있던 service랑 repository를 main 메소드랑 같은 위치에 두니까 돌아갔음. 밑에서 작성한 component-scan 도 안되는건 같았음.
	}
```

위와 같은 방법은 일일이 bean 을 등록하는게 굉장히 번거로움.

```xml
<beans xmlns:context="http://www.springframework.org/schema/context">
	<context:component-scan base-package="hello.corespring"/>
    <!--나는 해당 패키지부터 bean 을 스캐닝 해서 등록을 하겠다. 스캐닝을 할 때는 기본적으로 component 라는 어노테이션을 사용해서 등록할 수 있음.-->
</beans>
```

```java
@Repository
public class BookRepository {
}
@Service
public class BookService {
}
```

Repository, Service 둘 다 Component 어노테이션을 확장한 어노테이션이다. 어노테이션을 달아주면 bean 으로 등록'만' 됨.

의존성 주입은 Autowired 라는 어노테이션을 사용하거나 Inject 라는 어노테이션을 사용해야 함. 근데 여기서 Inject 는 또 다른 의존성을 필요로 하기 때문에 Autowired 를 사용하기로 함.

```java
@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

}
```

이렇게 설정하고 나면 의존성 주입이 된 것을 확인할 수 있다. 아까와는 다르게 xml 에 등록된 component-scan 기능을 사용해서 bean 들을 패키지 이하에서 어노테이션 스캐닝 후 등록함. 스프링 2.5 부터 가능하게 된 기능. 어노테이션 기반의 빈 등록 후 설정하는 방법임.

근데 bean 설정 파일을 xml 말고 java 로 만들 수 없을까. 하고 만들어진 java bean 설정파일임.

ApplicationConfig 파일 생성

```java
@Configuration //이건 bean 설정파일이다. 라는 것을 알려주는 어노테이션임.
public class ApplicationConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepository();
    }

    @Bean
    public BookService bookService(BookRepository bookRepository) { //(2) 또는 메소드 파라미터로 주입받을 수도 있음.
        BookService bookService = new BookService();
        bookService.setBookRepository(bookRepository()); //의존성 주입을 해준거야. setter 가 있었기에 가능함. (1) 의존성 주입에 필요한 인스턴스는 메소드를 호출해서 가져올 수 있고.
        return bookService;
    }
}
```

main 메소드로 돌아가

```java
public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class); //해당 클래스를 bean 설정으로 사용하겠다 선언. 
}
```

ApplicationContext 를 ClassPathXmlApplicationContext 에서 AnnotationConfigApplicationContext 로 바꾸어 설정해준다.

여기서 의존성 주입만 Autowired 를 쓰겠다, 하면

```java
public class ApplicationConfig {
       @Bean
    public BookService bookService(BookRepository bookRepository) { 
        return new BookService();
    }
}
//로 바꿔주고
public class BookService {

    @Autowired //를 추가해준다.
    BookRepository bookRepository;
}

```

위와 같은 상황은 우리가 setter 를 사용했기에 가능했지만 만약 생성자를 사용했을 경우에는 Autowired 를 해줄 수가 없고 BootService 를 생성할 때 Repository 가 불가피하게 필요하기 때문에 사용할 수 없다. 

근데 이제 java 에서도 bean 을 일일이 적어주는거 대신 component-scan 처럼 변경할 수 있다.

```java
@Configuration 
@ComponentScan(basePackageClasses = CoreSpringApplication.class) //basePackage 로 패키지명을 설정해도 되지만, basePackageClasses로 해당 클래스가 위치한 곳부터 컴포넌트 스캐닝을 해줘. 라는 의미.
public class ApplicationConfig {
}
```

그리고 service 와 repository 에 @Service , @Repository 를 추가해주고 service 클래스의 Autowired 로 의존성을 추가해주면 됨.

```java
@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;
}
@Repository
public class BookRepository {
}
```

그렇지만 이 과정을 Spring Boot 가 해주기 때문에 설정파일을 만들 필요가 없다.

```java
@SpringBootApplication //사실 이것만 있으면 돼. 해당 어노테이션에는 ComponentScan 과 Configuration 이 포함되어 있기 때문에야.
public class CoreSpringApplication {
}
```

프로젝트 생성했을 때, main 메소드가 들어있는 클래스 자체가 bean 설정 파일이다.

