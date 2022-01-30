<h3>스프링 프레임워크 핵심 기술</h3>

1강 , 스프링 IoC 컨테이너와 빈

boot는 프레임워크가 아니라 툴이야

1/ 스프링 IOC 컨테이너와 빈

Inversion Of Control 의존관계주입.

어떤객체가 (ex, Service클래스 타입의 객체가) 가 사용할 객체 (ex.Repository)를 의존관계를 new Repository() 로 생성해서 쓰는게 아니라 어떤 장치 (ex.생성자) 를 사용해서  주입을 받아 사용하는 방법을 IOC라고 한다.

스프링 IOC 컨테이너의 최상위에 있는 인터페이스는 BeanFactory 라는 인터페이스임. 가장 핵심적인 클래스이기도 함. 다양한 라이프 사이클을 통해 스프링이 여러가지 기능들을 제공할 수 있는거야. 컨테이너 내부의 빈들을 가공한다던가 등. 실제로 어떤 빈팩토리를 사용해서 xml 설정과 자바 설정을 어떤 식으로 사용할 수 있었는지. 스프링 부트가 없을 때. 

컨테이너 안에 들어있는 객체를 빈이라고 함.

컨테이너라고 부르는 이유는 IOC 기능을 제공하는 빈들을 담고 있기 때문이야.

일반 VO는 IOC 컨테이너에 존재하는 빈은 아니야, getter, setter 가 있으니까 자바 빈이라고는 할 수 있겠지만. (자바 빈 스펙을 준수하니까. 한 번 찾아보기)

왜 service 와 repository 는 빈으로 등록했는가. 의존성 주입 때문이기도 하고, bean 의 scope 때문이기도 함. 애플리케이션 전반적으로 bookservice 인스턴스는 하나만 만들어서 사용되면 돼. 이렇게 싱글톤으로 객체를 만들어 사용하고 싶을 때 사용하면 편함. ioc 는 기본적으로 싱글톤 scope으로 등록됨. 

싱글톤은 하나만 만들어서 사용, 프로포토타입은 매번 다른 객체를 사용.

기본적으로 빈을 등록할 때 아무런 어노테이션을 붙이지 않았다면 싱글톤으로 등록됨. 그러니까 우리가 인스턴스를 받아서 쓸 경우 모두 같은 객체일거야. 싱글톤은 하나랬으니까. 메모리 부분에서도 효율적이지. 이미 컨테이너 안에 미리 만들어놨던 객체를 사용하기 때문에 런타임시에 성능 최적화에도 유리해. 프로포토타입은 매번 만들어서 사용하거든. 특히, DB나 일을 하는 Repository 같은 경우는 만드는데 비용이 비싼 편인데 싱글톤으로 쉽게 사용할 수 있다면 큰 장점인거야. 

그밖에도. 의존성 관리나 라이프 사이클 인터페이스를 지원받는게 빈으로 등록했을 때 장점이야.

라이프 사이클은 예를들어, 어떤 빈이 만들어졌을 때 나는 뭔가 추가적인 작업을 하고 싶다. PostConstruct 어노테이션 처럼. 라이프 사이클 콜백에 해당하는 어노테이션을 찾아 메소드가 호출되는거야. 굉장히 다양해.

//source

우리는 bookrepository 를 구현하지 않고서는 bookservice 만 테스트를 할 수 없어. bookservice 가 코드가 있어도 사용하는 bookrepository 가 null을 return 하니까. 이게 의존성에 대한 문제야. 의존성을 가진 bookservice 는 단위테스트를 하기 힘들어. 

그런데, 여기서 직접 의존성을 주입한 경우라면 테스트가 더더욱 힘들어지는거야. 의존성을 바꿔줄 수 없으니까. 그나마 다행으로 의존성 주입을 받을 수 있도록 코드를 생성했기에 테스트할 때 얼마든지 가짜 객체를 만들어서 의존성 주입을 할 수 있어.

스프링 IOC 컨테이너에 중요한 인터페이스가 2개 있는데, 하나는 BeanFactory 이고, 실질적으로 가장 많이 쓸 빈팩토리는 ApplicationContext 임. 얘도 빈팩토리를 상속받아서 빈팩토리이긴 한데, 추가적으로 [ApplicationEventPublisher](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/ApplicationEventPublisher.html), [BeanFactory](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/beans/factory/BeanFactory.html), [EnvironmentCapable](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/env/EnvironmentCapable.html), [HierarchicalBeanFactory](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/beans/factory/HierarchicalBeanFactory.html), [ListableBeanFactory](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/beans/factory/ListableBeanFactory.html), [MessageSource](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/context/MessageSource.html), [ResourceLoader](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/io/ResourceLoader.html), [ResourcePatternResolver](https://docs.spring.io/spring-framework/docs/5.0.8.RELEASE/javadoc-api/org/springframework/core/io/support/ResourcePatternResolver.html)

가 있다.

```java
//BookService
@Service
public class BookService {

    BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book save(Book book) {
        book.setCreated(new Date());
        book.setBookStatus(BookStatus.DRAFT);
        return bookRepository.save(book);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("==================");
        System.out.println("Hello");
    }
}

//BookRepository
@Repository
public class BookRepository {

    public Book save(Book book) {
        return null;
    }

}

//Book
//bean 은 아니야, 스프링 IOC 컨테이너가 관리하는 객체가 아니기 때문이야. 그냥 java bean 이라고는 할 수 있을거야.
public class Book {

    private Date created;

    private BookStatus bookStatus;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

}

//BookStatus
public enum BookStatus {

    DRAFT, PUBLISHED;
}

//BookServiceTest
@ExtendWith(SpringExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository; //가짜객체

    @Test
    public void save() {
        Book book = new Book();

        when(bookRepository.save(book)).thenReturn(book);   //save라는 메소드 호출될 때 book 이 들어오면 book을 리턴해.
      //  BookRepository bookRepository = new BookRepository();
        BookService bookService = new BookService(bookRepository);

        Book result = bookService.save(book);

        assertThat(book.getCreated()).isNotNull();
        assertThat(book.getBookStatus()).isEqualTo(BookStatus.DRAFT);
        assertThat(result).isNotNull();

    }
}
```

