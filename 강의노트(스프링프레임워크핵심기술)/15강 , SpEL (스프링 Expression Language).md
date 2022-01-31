<h3>스프링 프레임워크 핵심 기술</h3>

15강 , SpEL (스프링 Expression Language)

jsp 에서 "${sessionScope.cart.numberOfItems > 0}" 을 사용해봤을거야. 비슷한거야.

스프링 EL이란?
 객체 그래프를 조회하고 조작하는 기능을 제공한다.
 Unified EL과 비슷하지만, <b>메소드 호출을 지원</b>하며, 문자열 템플릿 기능도 제공한다.
 OGNL, MVEL, JBOss EL 등 자바에서 사용할 수 있는 여러 EL이 있지만, SpEL은
모든 스프링 프로젝트 전반에 걸쳐 사용할 EL로 만들었다.
 스프링 3.0 부터 지원.

```tex
실제로 어디서 쓰나?
 @Value 애노테이션
 @ConditionalOnExpression 애노테이션
 스프링 시큐리티
 메소드 시큐리티, @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter
 XML 인터셉터 URL 설정
 ...
 스프링 데이터
 @Query 애노테이션
 Thymeleaf
 ..
# 코어 단에서만 쓰이지 않고 여러 프로젝트에서 널리 사용되고 있음.
```

[Core Technologies (spring.io)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions-language-ref) 레퍼런스 참고하면 다양한 표현식을 볼 수 있음. 예시를 보자.

```java
@Component
public class AppRunner implements ApplicationRunner { //ApplicationRunner 라는 인터페이스를 구현하면 스프링 부트가 실행된 다음 바로 실행이 돼.

    @Value("#{1 + 1}")
    int value;

    @Value("#{'hello ' + 'world'}")
    String greeting;

    @Value("#{1 eq 1}")
    boolean trueOrFalse;

    @Value("Hello")
    String hello;

    // #은 표현식, $는 프로퍼티를 표현하는거야
    @Value("${my.value}")
    int myValue;

    //이때 주의할 점이 표현식 안에서는 프로퍼티를 사용할 수 있지만 프로퍼티에서는 표현식을 사용할 수 없음.
    @Value("#{${my.value} eq 200}")
    boolean isMyValue100;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("===============================");
        System.out.println(value);
        System.out.println(greeting);
        System.out.println(trueOrFalse);
        //이 빈이 만들어질 때 Value 라는 어노테이션 안에 사용한 값을 SpEL로 파싱 해서 평가하고 결과값을 넣어준 거야.
        System.out.println(hello); //그냥 값을 넣어도 되는데 ${} 이렇게 쓰면 표현식으로 인식해서 평가하고 실행하는거야.
        System.out.println(myValue);
        System.out.println(isMyValue100);
    }
}
```

```xml
# application.properties 에 내용 추가
my.value = 100
```

```java
@Component
public class Sample {

    private int data = 200;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

@Component
public class AppRunner implements ApplicationRunner { 
    //빈에 있는 값을 가져와 찍을 수도 있음.
    @Value("#{sample.data}")
    int data;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(data);
    }
}
```

이러한 SpEL이 어디서 어떻게 쓰이고 있는지를 알아보자.

@ConditionalOnExpression 는 선택적으로 빈을 등록하거나 설정파일을 읽을 때 사용하는 어노테이션.

스프링 시큐리티의 경우.

[15. Expression-Based Access Control (spring.io)](https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html)

```xml
<http use-expressions="true">
    <intercept-url pattern="/admin*"
        access="hasRole('admin') and hasIpAddress('192.168.1.0/24')"/>
    ...
  </http>
# 이런 함수들은 EvaluationContext 에서 오는거야.
EvaluationContext 에서 어떤 bean 을 만들어주면 그 bean이 제공하는 함수를 쓸 수 있어. 그래서 이러한 함수들, 스프링 시큐리티 같은 경우는 그런 함수들에서 오고 그런 함수들은 @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter 같은 어노테이션도 쓸 수 있어.
```

 [SpEL support in Spring Data JPA @Query definitions](https://spring.io/blog/2014/07/15/spel-support-in-spring-data-jpa-query-definitions)

```JAVA
@Query("select u from User u where u.age = ?#{[0]}")
List<User> findUsersByAge(int age);

@Query("select u from User u where u.firstname = :#{#customer.firstname}")
List<User> findUsersByCustomersFirstname(@Param("customer") Customer customer);
//스프링 데이터에서 쓰는 @Query 어노테이션도 마찬가지야. #{#customer.firstname} 이거는 파라미터로 받은 필드값을 참고해서 쓸 수도 있고.
//#{[0]} 이렇게 인덱스 기반으로도 쓸 수 있어. 인덱스에 도메인이 들어오는 경우에는 도메인이 가지고 있는 특정한 값을 참고해야 할 때 유용할 거야.

//Advanced SpEL expressions
@Query("select u from User u where u.emailAddress = ?#{principal.emailAddress}")
List<User> findCurrentUserWithCustomQuery();
//principal 이라는 객체가 이미 들어있고, 현재 유저의 이메일 기준으로 읽어들이는 것도 가능.
```

이런 정보들이 다 EvaluationContext 에 들어있는거지.

스프링 Expression의 기반을 이해하려면 ExpressionParser와 EvaluationContext 클래스를 이해하면 좋아.

ExpressionParser 사용하는 것을 한 번 보면.

```java
@Component
public class AppRunner implements ApplicationRunner { 
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("2 + 100"); //이미 parseExpression 안에 표현식이 들어있기에 "" 이렇게만 작성해주면 된다.
        Integer value = expression.getValue(Integer.class); //어떤 타입으로 가져올지 정하면
        System.out.println(value); //그 값이 찍히는거야.
        //이때, 스프링 Expression Language 도 컨버전 서비스를 사용하는거야. 해당하는 타입으로 변환할 때. 
    }
}
```

그리고 스프링 프로젝트 뿐만 아니라 

[Thymeleaf에서 SpEL로 Enum 접근하기 :: Outsider's Dev Story](https://blog.outsider.ne.kr/997)

Thymeleaf 라는 view 템플릿 엔진에서도 지원해.

```
#{T(kr.ne.outsider.Codes).values()}
```

