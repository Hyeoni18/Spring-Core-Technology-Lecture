<h3>스프링 프레임워크 핵심 기술</h3>

18강 , Null-safety

스프링 프레임워크 5에 추가된 Null 관련 애노테이션
● @NonNull
● @Nullable
● @NonNullApi (패키지 레벨 설정)
● @NonNullFields (패키지 레벨 설정)

목적
● (툴의 지원을 받아) 컴파일 시점에 최대한 NullPointerException을 방지하는 것

null의 허용 유무를 어노테이션으로 마킹을 해놓고 툴(ex. IntelliJ)의 지원을 받아서 컴파일 타임의 NullPointerException 발생을 미연에 방지할 수 있음.

EventService, AppRunner 생성

```java
@Service
public class EventService {

    @NonNull
    public String craeteEvent(@NonNull String name) {
        return "hello " + name;
    }
}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    EventService eventService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        eventService.craeteEvent(null);
    }
}
```

이러면 아무것도 안 떠. warning 이라도 주면 좋을 텐데.

![1643737452328](https://user-images.githubusercontent.com/43261300/152021883-c825785e-0b31-4b60-9bf5-bcbfea70fb69.png)

체크된 두 개를 추가해주면 이후에 코딩을 할 때

![1643737618259](https://user-images.githubusercontent.com/43261300/152022376-4edf9a3d-0269-4d36-8fa3-c17c302eb8ed.png)

![1643737659356](https://user-images.githubusercontent.com/43261300/152022511-b0c95f94-dd97-4a0a-8147-0de7e81082cd.png)

이런 안내문을 볼 수 있어.

그리고 이런 기능을 

```tex
Reactor 및 Spring Data와 같은 다른 일반적인 라이브러리는 유사한 무효화 배열을 사용하는 null-safe API를 제공하여 스프링 애플리케이션 개발자에게 일관된 전체 환경을 제공합니다.
```

라고 하니까. 스프링 데이터 리파지토리에서 nonnull, nullable 어노테이션을 활용할 수 있어. 

[Core Technologies (spring.io)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#null-safety)

@NonNullApi , @NonNullFields 둘은 패키지 레벨에 설정하는건데. 패키지 파일 생성하고 안에 설정하는거야.

package-info.java 생성

```java
@NonNullApi //이 패키지 이하의 모든 리턴 타입. 파라미터에 nonnull을 적용하는 것과 마찬가지 기능을 함. 이 어노테이션으로 기본값을 nonnull을 주고 null을 허용하는 곳에만 nullable 어노테이션을 주는 행위가 가능해.
package hello.corespring;

import org.springframework.lang.NonNullApi;
```

