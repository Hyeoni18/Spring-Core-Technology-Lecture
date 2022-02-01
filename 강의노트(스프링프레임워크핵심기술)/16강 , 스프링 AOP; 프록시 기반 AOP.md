<h3>스프링 프레임워크 핵심 기술</h3>

16강 , 스프링 AOP: 프록시 기반 AOP

프록시 패턴은 인터페이스가 있어. 클라이언트는 인터페이스 타입으로 프록시 객체를 사용하게 돼. 그리고 프록시 객체는 원래 타겟 객체를 참조하고 있어. 둘은 같은 타입이야. 원래 해야할 일은 타겟이 가지고 있고 프록시 객체가 타겟이 가지고 있는 객체를 감싸서 실체 클라이언트 요청을 처리하게 되는거야. 

![1643677277915](https://user-images.githubusercontent.com/43261300/151897674-8b6aa7dc-4dd2-4d54-915e-099086362284.png)

이 패턴의 목적은 접근 제어 또는 부가 기능을 추가하기 위해서야. 예제를 보여줄게.

EventService, SimpleEventService 생성

```java
public interface EventService {
//해당 인터페이스가 서브젝트야.
    void craeteEvent();

    void publishEvent();

}
```

```java
@Service
public class SimpleEventService implements EventService{
//이건 그냥 타겟 객체야. 리얼 서브젝트에 해당하는 클래스야.
    @Override
    public void craeteEvent() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("created an event");
    }

    @Override
    public void publishEvent() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Published an event");
    }
}
```

이제 이걸 사용해볼게, AppRunner 생성

```java
@Component
public class AppRunner implements ApplicationRunner {
//client 코드가 되는거야.
    @Autowired
    EventService eventService; //인터페이스 타입으로 항상 주입을 받아야 해. 인터페이스가 있으면 인터페이스 타입으로 받는게 제일 좋아.

    @Override
    public void run(ApplicationArguments args) throws Exception {
        eventService.craeteEvent();
        eventService.publishEvent();
    }
}
```

그러면 이제 Client, Subject, Real Subject 파일이 준비가 된거야.

프록시로 어떤 일을 할거냐면, Real Subject 클래스를 건드리지 않고 클라이언트 코드도 건드리지 않고, 기능을 추가해볼거야. 어떤 기능이냐면 메소드들이 실행되는 시간을 측정하는 기능.

보통 Real Subject 에 

```java
@Service
public class SimpleEventService implements EventService{
    @Override
    public void craeteEvent() {
        long begin = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("created an event");

        System.out.println(System.currentTimeMillis()-begin);
        //이런식으로 추가해주면 될텐데
    }

    @Override
    public void publishEvent() {
        long begin = System.currentTimeMillis();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Published an event");

        System.out.println(System.currentTimeMillis()-begin);
        //이렇게 반복되는 게 Crosscutting Concerns 임. 이런 코드가 이 클래스 이 메소드에만 들어갈까? 더 많은 곳에 들어갈 수도 있는 거지.
    }
    
    public void deleteEvent() {
        System.out.println("Delete an event");
    }
    //그리고 얘는 적용하고 싶지 않아.
}
```

근데 이렇게 소스를 수정하고 싶지 않아. 

ProxySimpleEventService 를 생성해보자.

```java
//이 클래스의 타입이 subject랑 동일해야 해. 같은 인터페이스를 구현해야 하는거야.
@Primary // 같은 타입이 여러개 있으면 얘가 먼저야.
@Service // 빈으로 등록할거야.
public class ProxySimpleEventService implements EventService{

    @Autowired
    //이론적으로는 인터페이스 타입의 빈을 받는 걸 추천하지만 이 프록시 같은 경우는 real subejct 빈을 받아서 써야 해.
    // 그런 경우에는 이렇게 하면 bean 주입 받아 사용할 수 있어.
    // 또는 EventService 타입을 받지만 simpleEventService 빈의 이름을 기반으로 주입 받아도 됨.
    //EventService simpleEventService; 이렇게
    SimpleEventService simpleEventService;

    @Override
    public void craeteEvent() {
        //여기서부턴 위임을 하는거야. 프록시가 하는 일은 델리게이션 하는거야. 전부 다 프록시가 하는 일은 델리게이션만 하는거야.
        //근데 여기서 시간을 재는 기능을 넣어주는거야.
        long begin = System.currentTimeMillis();
        simpleEventService.craeteEvent();
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void publishEvent() {
        long begin = System.currentTimeMillis();
        simpleEventService.publishEvent();
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void deleteEvent() {
        simpleEventService.deleteEvent();
    }
}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    EventService eventService; 
    //클라이언트는 이벤트 서비스를 주입받지만, 그 이벤트 서비스로 내가 Primary 라고 설정해둔 빈을 가져다가 쓰게 될거야.
    @Override
    public void run(ApplicationArguments args) throws Exception {
        eventService.craeteEvent();
        eventService.publishEvent();
        eventService.deleteEvent();
    }
}
```

여기서 잠깐 스프링 부트랑 관련된 부분을 하자면,

스프링애플리케이션을 웹애플리케이션을 기본으로 띄우는데 그걸 안 띄우고 그냥 자바 메인 메서드 실행하듯이 서버모드가 아닌 상태로 띄우는 방법이 있어. 스프링 부트 강좌에서 다뤘던 내용이야.

```java
@SpringBootApplication
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CoreSpringApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

}
```

이렇게 실행하게 되면 내가 끄지 않아도 자동으로 서버가 꺼짐. 웹 서버가 기동되지 않기 때문에 조금 더 빠르기도 할거야. 이렇게 하는게 이상적이긴 해.

다시 돌아와서, 이렇게 프록시를 만드는데도 문제가 있어. 이렇게 하면 원래 클래스랑 클라이언트를 건드리지 않고 부가기능을 추가할 수 있었어. 근데 문제가 뭐냐면 프록시 클래스에도 중복 코드가 생긴다는 점. 프록시 클래스를 만드는데 드는 비용과 수고. 클래스를 계속 만들고 모든 메서드를 델리게이션을 해주고 그리고 이런 기능이 또 다른 클래스에도 적용이 되야 한다고 생각한다면. 그럼 그 모든 클래스에 프록시 클래스를 만들고 모든 메서드를 델리게이션 하고 중복된 코드를 심어야 하는거야? 

우리는 지금 프록시를 클래스로 만들어서 컴파일해서 썼지만. 동적으로 프록시 객체를 만드는 방법도 있어. 여기서 동적이란 말은 런타임. 즉,  애플리케이션이 동작하는 중에 동적으로 어떤 객체의 프록시 객체를 만드는 방법이야. 그 방법을 기반으로 스프링 IOC 컨테이너가 제공하는 방법과 혼합해서 같이 사용해서 이 문제를 간단하게 해결할거야.

그게 스프링 AOP 야.

스프링 AOP
● 스프링 IoC 컨테이너가 제공하는 기반 시설과 Dynamic 프록시를 사용하여 여러
복잡한 문제 해결.
● 동적 프록시: 동적으로 프록시 객체 생성하는 방법
○ 자바가 제공하는 방법은 인터페이스 기반 프록시 생성.
○ CGlib은 클래스 기반 프록시도 지원.
● 스프링 IoC: 기존 빈을 대체하는 동적 프록시 빈을 만들어 등록 시켜준다.
○ 클라이언트 코드 변경 없음.
○ AbstractAutoProxyCreator implements BeanPostProcessor(어떤 빈이 등록이 되면 그 빈을 가공할 수 있는 라이프 사이클 인터페이스)



지금 이 경우에 스프링 AOP 가 어떻게 적용이 될거냐면,

SimpleEventService 가 빈으로 등록이 되면 스프링이 AbstractAutoProxyCreator 라는 BeanPostProcessor 로 이 SimpleEventService 를 감싸는 프록시 빈을 만들어서 그 빈을 SimpleEventService  대신에 등록을 해주는거야.

이런 일을 해주는 클래스가 바로 AbstractAutoProxyCreator 임. 

https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/aop/framework/autoproxy/AbstractAutoProxyCreator.html

BeanPostProcessor 의 구현체이고, 즉 AbstractAutoProxyCreator 도 빈 인스턴스를 만든다음에  이거를 감싼 AOP 프록시 빈을 만들어주는 역할을 실제로 하는거야. 그리고 부가기능도 적용해주고. 더 자세한 내용은 토비의 스프링3에 자세히 나와있음. 책을 참고해보자.

