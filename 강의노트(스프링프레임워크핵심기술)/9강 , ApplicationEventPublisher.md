<h3>스프링 프레임워크 핵심 기술</h3>

9강 , ApplicationEventPublisher

이벤트 프로그래밍에 필요한 인터페이스 제공. 옵저버 패턴 구현체.

MyEvent 생성

```java
package hello.corespring;

import org.springframework.context.ApplicationEvent;

//이 이벤트는 빈으로 등록되는 게 아님.
public class MyEvent extends ApplicationEvent {

    private int date;

    public MyEvent(Object source) {
        super(source); //이벤트를 발생시킨 소스를 전달.
    }

    public MyEvent(Object source, int date) {
        super(source);
        this.date = date; //내가 원하는 데이터를 담아서 보낼 수 있는 이벤트가 될 수 있음.
    }

    public int getDate() {
        return date;
    }
}

```

스프링 4.2 이전에는 항상 ApplicationEvent 라는 클래스를 상속받아야 해. 

근데 이 이벤트를 발생시키는 기능을 ApplicationContext가 가지고 있는거야.  AppRunner 생성.

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        applicationEventPublisher.publishEvent(new MyEvent(this, 100));
    }
}
```

그럼 이제 이벤트를 받아서 처리하는 핸들러는, 일단 빈으로 등록이 되어야 함. MyEventHandler 생성

```java
@Component
public class MyEventHandler implements ApplicationListener<MyEvent> {

    @Override
    public void onApplicationEvent(MyEvent event) {
        System.out.println("이벤트 받음. 테이터는 "+event.getDate());
    }
}
```

스프링 4.2 이전에는 ApplicationListener 를 구현했어야 해.

실행시키면 AppRunner 가 실행되면서 applicationEventPublisher에서 이벤트를 발생시킴. 그럼 MyEvent 이 이벤트가 등록되어 있는 빈 중에서 MyEventHandler 가 받아서 처리하게 됨.

그치만 4.2 이후부터는 제약사항이 사라짐.

```java
public class MyEvent {

    private int date;

    private Object source; //이벤트를 발생시킨 소스를 갖고 싶다면 적어줘도 됨.

    public MyEvent(Object source, int date) {
        this.source = source;
        this.date = date; 
    }

    public Object getSource() {
        return source;
    }

    public int getDate() {
        return date;
    }
}
//스프링 프레임워크가 추구하는 철학과 같은 소스
//비침투성, 현재 코드에는 스프링 패키지가 전혀 들어있지 않음.
//가장 깔끔한 POJO임.
//스프링 프레임워크 코드가 현재 코드에 노출되지 않는 것. POJO 기반의 프로그래밍.
//테스트할 때도 편함. 유지보수도 쉬워짐.
```

이벤트(MyEvent)는 만들었는데 핸들러는 어떻게 되느냐.

마찬가지임. 특정한 클래스를 구현하지 않아도 됨.

대신, 빈으로는 등록이 되어야 함. 스프링이 누구한테 이벤트를 전달해야 하는지 알아야 하기 때문임.

이벤트는 빈이 아님.

```java
@Component
public class MyEventHandler {

    @EventListener //이벤트를 처리하는 메소드 위에다 Override 대신 추가해주면 됨.
    public void handle(MyEvent event) { //메소드 명도 자유롭게 변경 가능.
        System.out.println("이벤트 받음. 테이터는 "+event.getDate());
    }
}
```



그리고 좀 더 살펴보면, 이벤트 핸들러가 여러개 있을 때.

```java
@Component
public class AnotherHandler {

    @EventListener
    public void handle(MyEvent myEvent) {
        System.out.println("Another "+ myEvent.getDate());
    }
}
```

이럴경우, 두 핸들러 모두 실행이 됨.

근데 기본적으로는 순차적으로 실행이 됨.

순서는 뭐가 먼저 실행될지는 모르지만 차례대로 실행이 된다는 의미임. 알고 싶으면 쓰레드를 찍어보면 됨.

각 핸들러에 System.out.println(Thread.currentThread().toString()); 추가. 

근데 특정 순서를 정하고 싶으면 그것도 가능함.

```java
@Component
public class MyEventHandler {

    @EventListener //이벤트를 처리하는 메소드 위에다 Override 대신 추가해주면 됨.
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void handle(MyEvent event) { 
        System.out.println(Thread.currentThread().toString());
        System.out.println("이벤트 받음. 테이터는 "+event.getDate());
    }
}
```

@Order 를 설정해주면 됨.

전

![1643597233242](https://user-images.githubusercontent.com/43261300/151732366-9bbb57ef-b04d-4168-96fc-35a0d8850239.png)

후

![1643597263286](https://user-images.githubusercontent.com/43261300/151732400-c324642e-ec51-423a-9049-0d67e53e542d.png)

근데 이제 비동기 적으로 실행하고 싶다. 그러면 Async를 쓰면 됨. 근데 이렇게 되면 Order 가 의미가 없어짐. 그리고 이 상태에서 실행한다해도 main 쓰레드에서 실행이 될거임. Async 어노테이션을 붙인다고 해서 Async 하게 동작하는건 아님. 

Async 하게 동작하고 싶으면 main 에 @EnableAsync 어노테이션을 설정해줘야해.

```java
@SpringBootApplication
@EnableAsync
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

}
```

![1643597558006](https://user-images.githubusercontent.com/43261300/151732726-af1f5e52-c0af-4389-8e34-5ce4c366d39a.png)

이제 스프링이 기본으로 제공해주는 ApplicationContext 관련된 이벤트가 있음.

이 이벤트를 처리하는 핸들러를 추가해볼거임.  

```java
@Component
public class MyEventHandler {

    @EventListener
    @Async
    public void handle(ContextRefreshedEvent event) {
        System.out.println("ContextRefreshedEvent ");
    }

    @EventListener
    @Async
    public void handle(ContextClosedEvent event) {
        System.out.println("ContextClosedEvent ");
    }
}

```

이 이벤트들을 확장해서 제공해주는게 spring boot 임. 이건 다른 강의 참고.

스프링이 제공하는 기본 이벤트
● ContextRefreshedEvent: ApplicationContext를 초기화 했더나 리프래시 했을 때 발생.
● ContextStartedEvent: ApplicationContext를 start()하여 라이프사이클 빈들이 시작
신호를 받은 시점에 발생.
● ContextStoppedEvent: ApplicationContext를 stop()하여 라이프사이클 빈들이 정지
신호를 받은 시점에 발생.
● ContextClosedEvent: ApplicationContext를 close()하여 싱글톤 빈 소멸되는 시점에
발생.
● RequestHandledEvent: HTTP 요청을 처리했을 때 발생.