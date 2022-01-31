<h3>스프링 프레임워크 핵심 기술</h3>

8강 , MessageSource

국제화 (i18n) 기능을 제공하는 인터페이스. 메세지를 다국화하는 방법이야. ApplicationContext에 있는 기능임.

6강에서 말했듯이 Environment 처럼 ApplicationContext 를 주입받을 수 있으면 MessageSource 를 주입받을 수 있다.

resources 폴더에 messages_ko.properties, messages_en.properties 생성

```properties
#messages_ko.properties 파일
greeting=안녕, {0}
```

```properties
#messages_en.properties 파일
greeting=hiiiii {0}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(messageSource.getClass()); //결과 class org.springframework.context.support.ResourceBundleMessageSource
        System.out.println(messageSource.getMessage("greeting", new String[]{"core"}, Locale.KOREA)); //결과, 안녕, core
        System.out.println(messageSource.getMessage("greeting", new String[]{"core"}, Locale.ENGLISH)); //결과, hiiiii core
}
```

우리가 별다른 행위를 하지 않아도 spring boot 애플리케이션은 messages 로 시작하는 프로퍼티를 메세지 소스로 다 읽어줌. 원래는 빈으로 등록을 해야하는데 spring boot 가 이미 빈으로 등록을 한 상태야. 메세지들을 읽어들이는 class org.springframework.context.support.ResourceBundleMessageSource 빈으로 등록이 되어있어. 이 빈이 Messages 라는 resources bundle을 읽게되는거야.

![1643594878146](https://user-images.githubusercontent.com/43261300/151729719-21da99b1-3b04-4cc2-917a-c7d25365bc50.png)

알아서 messages Bundle 로 묶어줌.

릴로딩 기능이 있는 메세지 소스 사용하기

```java
@SpringBootApplication
public class CoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreSpringApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		var messagesSource = new ReloadableResourceBundleMessageSource();
		messagesSource.setBasename("classpath:/messages");
		messagesSource.setDefaultEncoding("UTF-8");
		messagesSource.setCacheSeconds(3);
		return messagesSource;
	}

}
```

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            System.out.println(messageSource.getMessage("greeting", new String[]{"core"}, Locale.KOREA));
            System.out.println(messageSource.getMessage("greeting", new String[]{"core"}, Locale.ENGLISH));
            Thread.sleep(1000);
        }
    }
}
```

