<h3>스프링 프레임워크 핵심 기술</h3>

14강 , 데이터 바인딩 추상화: Converter와 Formatter

스프링 3.0 부터 들어온 Converter와 Formatter.

앞 강의에서 본 프로퍼티 에디터가 가진 단점 때문에 converter 가 생김. 그리고 다른 단점이 하나 더 있는데 Object 와 String 간의 변환만 할 수 있음.

그런데 데이터 바인딩이 서로 다른 타입 간의 가능한거잖아. 문자열이 아니라 A타입과 B타입의 변환. 좀 더 제너럴 한거지. 일반적인 바인딩이 가능한거지. 프로퍼티 에디터가 가진 상태정보를 저장하는 단점을 없앤 컨버터가 인터페이스가 새로 생김.

EventConverter 생성

```java
public class EventConverter {
    //컨버터는 컨버터라는 인터페이스를 구현하면 되는데, 제네릭 타입으로 2개를 받음. Source와 Target.

    //String 을 Event 로 변환하는 메소드
    public static class StringToEventConverter implements Converter<String, Event> {
        @Override
        public Event convert(String source){
            return new Event(Integer.parseInt(source)); //소스를 받아서 이벤트로 변환
        }
    }
    
    //Event 를 String 으로 변환하는 메소드
    public static class EventToStringConverter implements Converter<Event, String>{
        @Override
        public String convert(Event source){
            return source.getId().toString();   //이벤트(소스)를 문자열로 변경
        }
    }

}
```

이 컨버트가 프로퍼티 에디터랑 같은 일을 하는거고, 얘네는 빈으로 등록도 할 수 있어. 근데 얘네를 이제 어떻게 등록해서 쓰냐면, 컨버트 레지스트리에 등록을 해야 해. 이 인터페이스를 직접 쓸 일은 없지만 스프링 부트 없이 MVC 를 쓴다면, WebConfig 를 쓴다고 가정하고. web용 Configuration 를 만들었었겠지.  거기에 오버라이드로 addFormatters 를 하는거야.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EventConverter.StringToEventConverter());
    }//이 레지스트리에 컨버터를 등록해주면 돼.
}
```

그럼 우리가 스프링 mvc 웹 설정에 넣어준 이 컨버터가 모든 컨트롤러에 동작을 할거야. 그러니 컨트롤러에서 요청한 1이 이 컨버트에서 이벤트 타입으로 변환이 되겠지.

근데 기본적으로는 이런 컨버터를 등록하지 않아도 Integer 같은 타입은 컨버터나 포매터가 자동으로 변환 해줘

```java
@GetMapping("/event/{id}")
    public String getEvent(@PathVariable Integer id) {
        System.out.println(id);
        return id.toString();
    }
```

이제 스프링이 조금 더 웹 쪽에 특화되어 있는 인터페이스를 만들어서 제공해주는데 그게 포매터야.

어떻게 만드냐면,

```java
//얘도 컨버터처럼 Thread-safe 함. 그래서 빈으로 등록 가능함 , 그렇다는 얘기는 주입을 받을 수도 있다는 말임.
// @Component
public class EventFormatter implements Formatter<Event> {
    //우리가 포매터로 처리할 타입을 하나 줘 <Evnet>, 그러면 포매터에 구현해야 하는 메소드 2개가 있음.

    //1. 나는 Locale 정보를 받아서 메세지를 만들고 싶다.
    //@Autowired
    //MessageSource messageSource;

    //프로퍼티 에디터랑 비슷해, 문자열->객체, 객체->문자열, 다른 점은 Locale 정보를 기반으로 바꿀 수 있다는거야.
    @Override
    public Event parse(String text, Locale locale) throws ParseException {
        return new Event(Integer.parseInt(text));
    }

    @Override
    public String print(Event object, Locale locale) {
        // 2. 그럼 이 객체에서 필요한 메세지 코드를 추출하고, 전달받은 locale 정보를 넣어서 거기에 해당하는 메세지를 내보낼 수도 있음.
        //messageSource.getMessage("title", locale);

        return object.getId().toString();
    }
}
```

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new EventFormatter());
        //컨버터처럼 포매터를 등록.
    }
}
```

이러한 데이터바인딩 작업을 컨버전서비스가 일을 하게 돼. 원래 데이터 바인더를 사용했다면 이제부터는 컨버전서비스를 사용하고 있는거지.

인터페이스를 통해 등록되는 컨버터와 포매터는 컨버전 서비스에 등록이 되는거고 우리는 그걸 통해 실제 변환하는 작업을 했던거지.

[ConversionService (Spring Framework 5.3.15 API)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/convert/ConversionService.html)

```tex
실제 변환 작업은 이 인터페이스를 통해서 쓰레드-세이프하게 사용할 수 있음.
 스프링 MVC, 빈 (value) 설정, SpEL에서 사용한다.
 DefaultFormattingConversionService
 FormatterRegistry
 ConversionService
 여러 기본 컴버터와 포매터 등록 해 줌.
```

 스프링이 제공해주는 구현체 중에 ConversionService 타입의 빈으로 DefaultFormattingConversionService 클래스가 자주 사용됨. FormatterRegistry & ConversionService 두 가지의 인터페이스를 다 구현해 줌.

![1643666639752](https://user-images.githubusercontent.com/43261300/151880601-308cb6f9-625b-4cc9-9cd7-2020b0c45756.png)

위에서 컨버터는 컨버터 레지스트리에, 포매터는 포매터 레지스트리에 등록을 해야했어, 근데 사실 포매터는 컨버터를 상속받고 있는거야. 그래서 포매터에는 컨버터도 등록할 수 있어.

```java
@Component
public class AppRunner implements ApplicationRunner {

    //컨버전 서비스를 빈으로 받아서 사용하는 일도 없을거야.
    @Autowired
    ConversionService conversionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //실제로 프로그래밍으로 직접 인터페이스를 써서 컨버팅 해야한다면 이걸 사용하면 되는거지. 근데 보통 이런 일은 없을거야.
        // conversionService.convert();

        System.out.println(conversionService.getClass().toString()); //WebConversionService
        //DefaultFormattingConversionService 가 아니라 WebConversionService 나오는데,,, 이건 스프링 부트가 제공해주는 클래스야. WebConversionService를 상속해서 만들어진거야. 그래서 더 많은 기능을 가지고 있어.
    }
}
```

스프링 부트
 웹 애플리케이션인 경우에 DefaultFormattingConversionSerivce를 상속하여 만든
WebConversionService를 빈으로 등록해 준다.
 Formatter와 Converter 빈을 찾아 자동으로 등록해 준다.

위에서 WebConfig 에서 등록했던 과정이 필요없는거야.

 Formatter와 Converter가 빈으로 등록이 되어 있다면 그 빈들은 자동으로 컨버전 서비스에 등록이 돼. 누가 해주냐면 스프링 부트가.

```java
//컨버터를 빈으로 등록
public class EventConverter {
    @Component
    public static class StringToEventConverter implements Converter<String, Event> {
        @Override
        public Event convert(String source){
            return new Event(Integer.parseInt(source));
        }
    }
    @Component
    public static class EventToStringConverter implements Converter<Event, String>{
        @Override
        public String convert(Event source){
            return source.getId().toString();
        }
    }
}
```

```java
//포매터를 빈으로 등록
@Component
public class EventFormatter implements Formatter<Event> {

    @Override
    public Event parse(String text, Locale locale) throws ParseException {
        return new Event(Integer.parseInt(text));
    }

    @Override
    public String print(Event object, Locale locale) {
        return object.getId().toString();
    }
}
```

```java
//테스트
@ExtendWith(SpringExtension.class)
//WebMvcTest 어노테이션은 슬라이싱 테스트라고 해서, 부트와 관련된건데, 계층형 테스트임. 웹과 관련된 빈만 등록을 해주는. 주로 컨트롤러만 등록이 돼. 그러니까 컨버터와 포매터가 제대로 빈으로 등록 안되면 테스트가 깨질 우려가 있음.
@WebMvcTest({EventFormatter.class, EventController.class}) //이런 경우에 이런식으로 빈으로 등록을 하고 테스트를 할 수 있음.
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

```java
@ExtendWith(SpringExtension.class)
@WebMvcTest({EventConverter.StringToEventConverter.class, EventController.class}) 
//그냥 클래스만 준다고 빈으로 등록을 해주진 않아. 컴포넌트 스캔으로 빈으로 등록이 가능해야 해. 
//그래서 이 테스트를 할 때 필요한 빈을 이렇게 정의해주는 것도 좋아. 눈으로 볼 수 있으면 좋잖아.
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getTest() throws Exception {
        mockMvc.perform(get("/event/1"))    
                .andExpect(status().isOk()) 
          .andExpect(content().string("1")); 
    }

}
```

보통은 데이터바인딩을 만들 때 웹과 관련해서 만들기 때문에 포매터를 사용하는 것을 추천.

컨버터를 써도 괜찮지만. StringToEventConverter 처럼 문자열을 이벤트로 바꾸는 것 정도.

그런데 대부분은 JPA를 사용할 때 VO 들은 @Entity 처럼 이미 컨버터가 들어있어. 

그리고 등록되어 있는 컨버터를 보는 방법은 

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ConversionService conversionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(conversionService);
       //굉장히 많음.
    }
}
```

내가 만든 컨버터도 있음

![1643668134575](https://user-images.githubusercontent.com/43261300/151883721-0dbf9924-b051-4e98-b0c8-4bf1be5ac982.png)

