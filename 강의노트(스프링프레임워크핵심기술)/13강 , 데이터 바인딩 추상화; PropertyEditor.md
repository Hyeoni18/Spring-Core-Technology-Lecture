<h3>스프링 프레임워크 핵심 기술</h3>

13강 , 데이터 바인딩 추상화: PropertyEditor

데이터 바인딩이란 것은 어떤 프로퍼티 값을 타켓 객체에 설정하는 기능 자체를 말함. (기술적 관점)

사용자 입력값을 애플리케이션 도메인 모델에 동적으로 변환해 넣어주는 기능 (사용자 관점)

할당할 때 왜 바인딩이 필요하냐면 입력값은 대부분 “문자열”인데, 그 값을 객체가 가지고 있는 int, long, Boolean,
Date 등 심지어 Event, Book 같은 도메인 타입으로도 변환해서 넣어주는 기능

[DataBinder (Spring Framework 5.3.15 API)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/validation/DataBinder.html)

이 기능은 주로 웹 MVC 에 사용하지만 프로퍼티 에디터를 사용하는 데이터 바인더는 스프링 웹 MVC 뿐만 아니라 xml 설정 파일에 입력한 문자열을 빈이 가지고 있는 적절한 타입으로 변환해서 넣어줄때도 사용. 또는 스프링 Expression Language 에서도 사용됨. 

데이터바인딩에 관련된 기능을 여러 인터페이스로 적절히 추상화 시켜놓았고 그걸 공부할거야.

가장 고전적인 방식

Event, EventController, Test 생성

```java
public class Event {

    private Integer id;

    private String title;

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

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
```

```java
@RestController
public class EventController {

    @GetMapping("/event/{event}")
    public String getEvent(@PathVariable Event event) {
        System.out.println(event);
        return event.getId().toString();
    }
}
```

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getTest() throws Exception {
        mockMvc.perform(get("/event/1"))    //요청을 보내면
                .andExpect(status().isOk()) //응답이 200으로 나와서 문제가 없어야 하고
                .andExpect(content().string("1"));  //결과가 1이 나와야 해
    }

}
```

** 테스트 관련 참고 https://scshim.tistory.com/317

위의 소스를 돌려보면 에러가 발생함. 500 서버에러 발생

Failed to convert value of type 'java.lang.String' to required type 'hello.corespring.Event'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.lang.String' to required type 'hello.corespring.Event': no matching editors or conversion strategy found

왜냐하면 문자열로 들어온 1을 event 타입으로 변환할 수 없기 때문임. 

이제 변환해보자, EventEditor 생성

```java
public class EventEditor extends PropertyEditorSupport {
    //implement PropertyEditor 를 직접 구현해도 되지만, 메소드가 많아서 PropertyEditorSupport 를 상속받음. 그러면 구현하려는 메소드만 선택해서 구현 가능.
    //보통 이 2개를 많이 구현함. getAsText, setAsText
    //우리에게 필요한 건 텍스트를 이벤트로 변환하는 것. 그래서 사실 setAsText 만 구현해도 됨.
    @Override
    public String getAsText() {
        Event evnet = (Event) getValue(); //프로퍼티 에디터가 받은 객체를 getValue로 가져올 수 있음. 근데 이걸로 가져오면 Object 타입으로 나옴. 그래서 타입을 변환시켜줌
        return evnet.getId().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(new Event(Integer.parseInt(text))); //생성자에 전달, setValue 에 넣어준다.
    }
    //여기서 getValue, setValue 에서 공유하는 값이 프로퍼티 에디터가 가지고 있는 값이야. 이 값이 서로 다른 쓰레드에게 공유가 되는데. 얘는 스테이트 풀이야. 상태를 저장하고 있음. 쓰레드 세이프하지 않음. 그래서 이 프로퍼티 에디터에 구현체들은 여러 쓰레드에 공유해서 사용하면 안됨. 즉, 얘를 빈으로 등록해서 쓰면 안된다는거야. 1번이 2번 정보를 수정하고 5번이 1번 정보를 수정하는 경우가 생길 수도 있어. 절대로 프로퍼티 에디터를 빈으로 등록해서 쓰면 안돼. 쓰레드 스코프. 의 빈으로 만들어서 쓰는건 괜찮아. 프로토 타입, 싱글톤 타입으로 얘기했던 부분. 추가로 한 쓰레드 내에서만 유효한 그런 스코프의 빈도 있음. 그렇게만 정의해서 쓰면 그나마 괜찮지만. 아예 안하는걸 추천함. 다른 쓰레드랑 공유해서 쓰면 안돼.
    //그럼 어떻게 이 프로퍼티 에디터를 사용해?
    //방법은 InitBinder 라고 해서 Contorller 에 등록을 하는 방법이 있어.
}
```

```java
@RestController
public class EventController {

    @InitBinder
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Event.class, new EventEditor());
    }
    //이런식으로 이벤트 클래스 타입을 처리할 프로퍼티 에디터를 등록할 수 있음.

    @GetMapping("/event/{event}")
    public String getEvent(@PathVariable Event event) {
        System.out.println(event);
        return event.getId().toString();
    }
}
```

이제 테스트를 실행하면, 컨트롤러가 어떤 요청을 처리하기 전에 정의된 데이터 바인더에 들어있는 프로퍼티 에디터를 사용하게 됨. 그렇게 되면 문자열로 들어온 1을 숫자로 변환해서 이벤트 객체로 바꾸는 일이 (setValue) 벌어지게 됨. 테스트가 성공함.

이 방법은 고전적이고, 빈으로 등록도 못하고, 사용하기 불편해. 다음 시간에 다른 방법을 사용해볼거야.



