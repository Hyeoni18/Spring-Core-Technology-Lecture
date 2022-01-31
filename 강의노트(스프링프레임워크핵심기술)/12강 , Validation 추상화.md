<h3>스프링 프레임워크 핵심 기술</h3>

12강 , Validation 추상화

스프링 프레임워크가 제공하는 추상화 중 하나인 Validation 추상화를 살펴볼거야.

애플리케이션에서 사용하는 객체들을 검증할 때 사용하는 인터페이스. Validator 를 제공해.

주로 스프링 MVC 에서 사용하긴 하지만, 웹 계층용 전용 Validator 는 아니야. 계층형 아키텍처를 사용하고 있다면, 웹이든 서비스든 데이터레이어든 어떤 레이어든 상관없이 모두 사용할 수 있는 일반적인 인터페이스임. 

구현체 중 하나로, JSR-303(Bean Validation 1.0)과 JSR-349(Bean Validation 1.1)을
지원한다. (LocalValidatorFactoryBean)

[LocalValidatorFactoryBean (Spring Framework 5.3.15 API)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/validation/beanvalidation/LocalValidatorFactoryBean.html)

Bean Validation이 제공하는 여러 Validation 용 어노테이션을 사용해서 어떤 객체의 데이터를 검증할 수 있음.

Bean Validation은 자바 표준 스펙이고 JEE스펙임.  [Jakarta Bean Validation - Home](https://beanvalidation.org/)

[NotBlank (Jakarta Bean Validation API 2.0.2) (jboss.org)](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/) 이런 어노테이션들로 어떤 빈에 있는 데이터를 검증할 수 있는 기능이야.

우선 Validator 는. 스프링이 제공하는 인터페이스에는 두 가지 메소드를 구현해야 하는데 그중 하나가 supports.

내가 검증해야하는 인스턴스의 클래스가 Validator 가 지원하는 검증할 수 있는 클래스인지 확인하는 메서드를 구현해야하고, validate 라는 메소드도 구현해야해. 실질적으로 검증이 일어나는 메소드.

Event, EventValidator, AppRunner 생성

```java
public class Event {

    Integer id;

    String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
```

```java
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Event.class.equals(clazz); //Event 클래스 타입의 인스턴스를 검증할거야. 파라미터로 넘어오는 클래스의 타입이 이벤트인지 확인하고.
    }

    @Override
    public void validate(Object target, Errors errors) {
       //rejectIfEmptyOrWhitespace는 타이틀이 empty 면 안된다는 의미야. 그러니까 errors의 에러를 담아줄거야. 왜냐면 지금 title 값이 없잖아.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "notempty", "Empty title is now allowed");
        //위에서 errorCode 는 전에 배운 메세지소스에서 키 값이야. notempty.title 식으로 가져오지 않는 이유는 뒤에서 설명.
         //errors 에다가, 이벤트 title 이 그렇다면, errorCode 로 notempty 라고 줄거야, 디폴트로는 Empty title is now allowed 사용할거야.
    }

}
```

```java
@Component
public class AppRunner implements ApplicationRunner {
//위에서 만든 EventValidator를 사용할거야.
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Event event = new Event();
        EventValidator eventValidator = new EventValidator();
        //스프링의 Errors 가 필요해.
        Errors error = new BeanPropertyBindingResult(event, "event"); //기본 구현체로 BeanPropertyBindingResult를 사용. 처음에 오는 매개변수는 target이야, 어떤 객체를 검사할 것이며, 어떤 이름이냐. 두 번째는 어떤 이름이냐. 이름은 중요하진 않음.
        //이런 과정들은 스프링 MVC 에서 사용하면 이런 값들은 자동으로 넘어가기 떄문에 우리가 직접 클래스(BeanPropertyBindingResult)를 사용할 경우는 거의 없음. 하지만 Errors 인터페이스는 자주 보게 될 거야.
//이제 검증을 해보자.
        eventValidator.validate(event, error); //event 객체를 검사할거고, errors에 검증 에러를 담아줄거야. 라는 의미.
        System.out.println(error.hasErrors());
//errors 에 에러가 있냐.
        error.getAllErrors().forEach(e -> {
            System.out.println("====== error code======="); //모든 에러를 가져와서 순차적으로 순회
            Arrays.stream(e.getCodes()).forEach(System.out::println); //에러 코드를 찍어볼거야. notempty
            System.out.println(e.getDefaultMessage());
            //디폴트 메세지.
        });

    }
}
```

///출력

![1643654699882](https://user-images.githubusercontent.com/43261300/151853757-38acff85-2792-4eca-9936-8ddc104a95be.png)

내가 만든 에러 코드 notempty 외에 title 등 3가지를 더 추가해줌. validator가. 

notempty.title 처럼 원하는 에러메세지를 읽어오면 되는거야. 화면에 보여줄때는. 또는 api 응답으로 만들때도 마찬가지임.

위와 같은 방법은 가장 원시적인 방법임.

그리고 validation 을 할때, Utils 만 쓰는건 아님.

errors 직접 넣을 수도 있음

```java
@Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "notempty", "Empty title is now allowed");
        //이렇게, 타겟은 이벤트 타입이니 변환해.
        Event event = (Event)target;
        //만약 이게 널이다. 
        if (event.getTitle() == null) {
            errors.reject();
            errors.rejectValue();
                //error에 reject 시키면서 직접 에러코드랑, 메시지 담으면 돼. 그리고 어떤필드에 해당하는건지.
        }
    }
```

1. 여러 필드에 걸쳐서 종합적으로 발생하는 에러면 그냥 reject.
2. 객체가 가지고 있는 특정 필드에 관련된 에러라면 rejectValue 에 담으면 돼.



그런데 최근에는 이렇게 validate를 직접 쓰는게 아니라.

스프링이 제공해주는 

스프링 부트 2.0.5 이상 버전을 사용할 때
● LocalValidatorFactoryBean 빈으로 자동 등록
● JSR-380(Bean Validation 2.0.1) 구현체로 hibernate-validator 사용.
● https://beanvalidation.org

를 그냥 사용할 수 있음.

```java
public class Event {

    Integer id;

    @NotEmpty
    String title;

    @Min(0)
    Integer limit;

    @Email
    String eamil;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getEamil() {
        return eamil;
    }

    public void setEamil(String eamil) {
        this.eamil = eamil;
    }
}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired //의존성을 주입받아 그냥 사용할 수 있음.
    Validator validator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(validator.getClass());

        Event event = new Event();
        event.setLimit(-1);
        event.setEamil("aaaa");
        Errors error = new BeanPropertyBindingResult(event, "event");

        validator.validate(event, error);

        System.out.println(error.hasErrors());

        error.getAllErrors().forEach(e -> {
            System.out.println("====== error code=======");
            Arrays.stream(e.getCodes()).forEach(System.out::println);
            System.out.println(e.getDefaultMessage());
        });

    }
}
```

근데 여기서 @NotEmpty, @Min(0), @Email 등은 validation 을 import 받아 사용해야하는데, 스프링부트 2.3.0 변경사항 중 Validation 이 분리되었다는 것을 알 수 있다. 그래서 pom.xml 에 추가적으로 설정을 해줘야 사용 가능함.

```xml
<dependency>			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
```

이런식으로 검증할 수 있음.

이렇게 어노테이션으로 검증할 수 있는 것들은 validator 없이도 충분히 검증할 수 있다. 어노테이션 말고 복잡한 로직으로 검증을 해야하면 validator 를 직접 만들어야 겠지.

이해가 잘 안되니 참고하기 :

https://engkimbs.tistory.com/728?category=767795