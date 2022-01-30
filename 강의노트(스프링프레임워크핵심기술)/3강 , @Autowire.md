<h3>스프링 프레임워크 핵심 기술</h3>

3강 , @Autowire

Autowired 를 사용할 수 있는 위치.

1. BookService, BookRepository 생성

```java
//Service 에는 어노테이션도 붙여주고, 생성자도 생성한 후 Autowired로 의존성을 주입해 봄.
@Service
public class BookService {

    BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}

//Repository는 그냥 생성만 된 상태
public class BookRepository {
}
```

해당 빈을 찾을 수 없는 error 가 발생

Parameter 0 of constructor in hello.corespring.BookService required a bean of type 'hello.corespring.BookRepository' that could not be found.

그러니까 빈으로 정의해 줘

Consider defining a bean of type 'hello.corespring.BookRepository' in your configuration.

```java
//정의해주면 error가 사라짐.
@Repository
public class BookRepository {
}
```

2. 이번에는 생성자가 아니라 setter 에다가 해볼거야.

```java
@Service
public class BookService {

    BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```

똑같이 error 메세지가 나오게 됨.

Description:

Parameter 0 of method setBookRepository in hello.corespring.BookService required a bean of type 'hello.corespring.BookRepository' that could not be found.


Action:

Consider defining a bean of type 'hello.corespring.BookRepository' in your configuration.

근데 여기서 다른 점은,

생성자 주입은 bean 을 만들다가 필요한 다른 의존성이었던 repository 를 못 찾아서 실패했구나. 알 수 있지만.

Setter 로 했을 경우에는 그래도 bean 은 만들 수 있지 않을까? bookService 자체의 인스턴스는 생성 가능함. 

근데 Autowired 라는 어노테이션 때문에 의존성을 주입하려고 시도하게 됨. bean 을 생성할 때. 근데 이 과정이 실패하는거야. 그래서 인스턴스 자체는 repository 없이도 만들 수 있지만 autowired 를 하라고 했기에 실패하는거야.

그러니까 이럴 때는,

```java
//autowired 에 의존성을 옵션으로 설정해주면. 꼭 필요하지 않다는 옵션.
@Autowired(required = false)
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
```

service 인스턴스만 만들어서 bean 으로 등록은 됐지만 의존성은 주입이 되지 않는다.

3. 또 다른 방법으로는 필드에도 붙여줄 수 있다.

```java
@Service
public class BookService {

    @Autowired(required = false)
    BookRepository bookRepository;

}
```

이렇게 되면 생성자를 통한 의존성 주입과는 조금 달라.

생성자는 bean 을 만들 때에도 개입이 돼. 생성자에 전달받아야 되는 bean 이 없으면 인스턴스를 만들지 못하고 service 도 bean 등록이 안됨.

근데 setter 나 field inject 을 사용하게 될 때에는 이렇게 옵션을 설정해서 service 가 해당하는 의존성 없이도 bean 으로 등록되게 할 수 있다.

위의 예시들은 해당 타입의 빈이 없거나 한 개인 경우를 살펴본 거고 이제 빈이 여러 개일 경우를 볼 거야.

기존에 만들었던 BookRepository 를 interface 로 변경하고, MyBookRepository, HelloRepository 를 생성

```java
//BookRepository
public interface BookRepository {
}

//MyBookRepository
@Repository
public class MyBookRepository implements BookRepository{
    //BookRepository 의 구현체야.
}

//HelloRepository
@Repository
public class HelloBookRepository implements BookRepository{
    //BookRepository 의 구현체야.
}
```

이러면 BookRepository 타입의 bean 이 2개가 생긴거야.

그런데 service 에서 의존성을 주입하려 한다면

```java
@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;
}
```

error 가 발생하게 됨. 어떤 repository 를 써야 할지 모른다는 거지.

Field bookRepository in hello.corespring.BookService required a single bean, but 2 were found:

그러니까 1. @Primary 어노테이션을 붙여서 사용하고 싶은 bean 이 뭔지 marking 해. 2. 모든 bean 을 다 받거나, 3. @Qualifier 어노테이션으로 뭘 원하는지 marking 해

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed

@Primary  사용하기

```java
@Repository @Primary //여러가지 BookRepository 가 있지만, 나는 그 중에서 얘를 주로 사용할거야. 얘를 반드시 주입할거야. marking.
public class HelloBookRepository implements BookRepository{
    //BookRepository 의 구현체야.
}
```

하면 됨.

근데 이렇게 하면 실제로 어떤 bean 이 주입된건지 모르겠어. Spring Boot 에서는 그걸 알 수 있는 방법이 있어.

BookServiceRunner 생성

```java
@Component
public class BookServiceRunner implements ApplicationRunner{
	//1. bean 을 주입받아.
    @Autowired
    BookService bookService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //3. 실행
        bookService.printBookRepository();
        //4. 결과 class hello.corespring.HelloBookRepository
    }
}
```

```java
@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
	//2. 현재 어떤 bean 주입되었는지 출력하는 메소드
    public void printBookRepository() {
  System.out.println(bookRepository.getClass()); 
    }
}
```

위의 내용은 Spring Boot 강의를 참고하기.

@Qualifier 사용하기

Qualifier 는 bean 의 아이디를 적어줘야해, 기본적으로 Service, Repository 어노테이션을 쓰면 등록된 빈의 아이디는 스몰케이스로 시작하는 클래스 이름과 동일하게 지어짐.

```java
@Service
public class BookService {
    @Autowired @Qualifier("helloBookRepository")
    BookRepository bookRepository;
}
```

모든 bean 을 다 받기

```java
@Service
public class BookService {

    @Autowired
    List<BookRepository> bookRepositories;
	//list 로 그냥 다 받으면 돼.
    public void printBookRepository() {
        this.bookRepositories.forEach(System.out::println);
        //결과 :  hello.corespring.HelloBookRepository@3f92a84e hello.corespring.MyBookRepository@cf67838
    }
}
```

위에서 소개했던 3가지 말고도 Autowire는 타입으로만 주입받는게 아니라, 이름도 봐. (추천하지는 않음)

```java
@Service
public class BookService {

    @Autowired
    BookRepository myBookRepository;
	//이렇게 주입받을 bean 의 이름이랑 field 이름을 맞춰주는거야.
    public void printBookRepository() {
        System.out.println(myBookRepository.getClass()); // 결과 class hello.corespring.MyBookRepository
    }
}
```

지금까지는 Autowire 의 사용방법을 보았고 그럼 이게 어떻게 동작하는걸까

[BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html) 라는 라이프 사이클 인터페이스의 구현체의 의해서 동작하는거야.

빈을 만들고 빈의 인스턴스를 만든 다음에 빈의 Initialization 라는 라이프 사이클이 있는데, 이전/이후에 부가적인 작업을 할 수 있는 또 다른 라이프 사이클 콜백이 존재해. 그거야.

```java
@Service
public class BookService implements InitializingBean {
    //2. 인터페이스를 사용하거나

    @Autowired
    BookRepository myBookRepository;

    public void printBookRepository() {
        System.out.println(myBookRepository.getClass());
    }
	//1. 어노테이션 기반으로 선언하거나
    @PostConstruct
    public void setUp() {
        //빈이 만들어진 다음에 해야할 일
    }
    
    //InitializingBean 인터페이스를 구현하면 필요한 메소드
    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
```

![1643576427688](/1643576427688.png)

11, 14번을 보면 두 가지의 메소드 콜백을 더 제공해주는거야. 그 중에서 우리는 AutowiredAnnotationBeanPostProcessor 가 동작을 해서 Autowired 에노테이션을 처리해주는거야. 여기에 해당하는 bean 을 찾아서 주입을 해준다는 거지. 언제? Initialization 전에. 

위에서 썼던 @PostConstruct 는 이미 bean 이 주입이 다 됐다고 생각하고 진행하는거야. 그러면 BookServiceRunner 를 통해 진행했던 걸 

```java
@Service
public class BookService {

    @Autowired
    BookRepository myBookRepository;

   @PostConstruct
    public void setUp() {
        //여기서 출력을 해도 되는거지.
        System.out.println(myBookRepository.getClass());
    }
}
```

그럼 BookServiceRunner 클래스가 필요없게 돼.

그리고 console 창을 보면 출력 위치가 좀 달라. 

![1643576917828](/1643576917828.png)

1번이 PostConstruct , 2번이 Runner 클래스

Runner는 Spring Boot 가 제공해주는 인터페이스야. 애플리케이션이 완전히 구동이 됐을 때 그 다음 일을 하는거고. PostConstruct 라이프 사이클 콜백 같은 경우는 위의 12번 단계에서 일을 하는거야. (InitializingBean's afterPropertiesSet) 애플리케이션 구동 중에 찍히는거지.

마지막으로 이게 어떻게 동작하냐면

빈팩토리(ApplicationContext) 가 자기 안에 등록이 되어있는  BeanPostProcessor 타입의 빈을 찾아, 그 중에 하나인 AutowiredAnnotationBeanPostProcessor 얘가 등록이 되어있는거지. 찾아서 다른 일반적인 빈들한테 BeanPostProcessor 를 적용하는거야. BeanPostProcessor 에 있는 실제 어노테이션을 처리하는 로직, 머 그니까 AutowiredAnnotationBeanPostProcessor 얘도 빈으로 등록이 되어 있다는 거야.

```java
//AutowiredAnnotationBeanPostProcessor 빈으로 등록되어 있다는 사실 확인.
@Component
public class MyRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        AutowiredAnnotationBeanPostProcessor bean = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
        System.out.println(bean);
        // 결과 org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@fe34b86
    }
}
```

