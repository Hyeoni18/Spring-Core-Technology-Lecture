<h3>스프링 프레임워크 핵심 기술</h3>

11강 , Resource 추상화

지금부터는 Spring 레퍼런스의 많은 양을 차지하는 추상화 중 일부분을 공부할거야.

스프링에서 Resource 를 추상화 시켰는데, 

뭐를 추상화 했냐면 java.net.URL을 감싼거야. org.springframework.core.io.Resource 로 감싸서 실제 로우레벨에 있는 리소스에 접근할 수 있는 기능을 만든거임.

리소스 자체를 추상화 시킨거야. 가져오는 기능은 10강에서 얘기했음. 아무튼 이 리소스 자체를 추상화 시킨거야.

이유는, 기존에 있던 java.net.URL 클래스가 클래스 패스 기준으로 url 을 가져오는 기능이 없었고, 

,, 여러가지 prefix 기능을 지원해 http, ftp, https 등을 url 이 지원을 하는데 이제 스프링 입장에서는  클래스 패스에서 가져오는 기능도 (어차피 동일하게 리소스를 가져오는거니까 ) 원래는 클래스패스를 가져올 때 리소스로더.겟리소스 이런식으로 가져왔었는데 조금 더 방법을 통일시킨거지. 하나의 인터페이스로. 리소스로. 방법은 겟리소스고 그런 리소스를 지칭하는 것을 리소스라고 통일시킨거야.

추상화를 한 이유는 ...

클래스패스 기준으로 리소스 읽어오는 기능 부재
ServletContext를 기준으로 상대 경로로 읽어오는 기능 부재
새로운 핸들러를 등록하여 특별한 URL 접미사를 만들어 사용할 수는 있지만 구현이
복잡하고 편의성 메소드가 부족하다. 

가 있다.

리소스라는 인터페이스를 보면, 스프링 내부에서 많이 사용 돼. 우리 ApplicationContext 처음에 만들 때 xml 에서도 만들고 java에서도 만들기도 하고 했잖아. 

근데 xml 파일에서 만들 때 xml 파일을 우리가

```java
ApplicationContext context = new ClassPathXmlApplicationContext("application.xml"); // 2강 참고
var ctx = new ClassPathXmlApplicationContext("application.xml"); // 동일한 방법
```

이렇게 클래스 패스로 가져왔잖아.

그럼 이 xml 을 바로 리소스라는 걸로 추상화 돼. 내부적으로 문자열이 변환이 돼.  문자열 자체가 리소스에 있는 getResource 문자열로 가. location 에 해당 돼.

```java
Resource resource = resourceLoader.getResource("classpath:test.txt"); 
```

그래서 실질적으로 내부적으로 resource 를 쓰고 있는거야. 계속 사용하고 있었던거지.

외에도 파일 시스템.

```java
var ctx = new FileSystemXmlApplicationContext("file.xml");
```

이 컨텍스트도 있어, 똑같이 생겼는데 위랑 뭐가 다르냐면 위는 클래스패스를 기준으로 xml 문자열이 준 location 에 있는 설정파일을 찾는거고 지금 이건 파일 시스템 경로 기준으로 이 문자열에 해당하는 리소스를 찾아서 빈 설정파일로 사용하는거야. 그니까 내부적으로는 다른 구현체를 쓰는거지.

일단 리소스 인터페이스는, 항상 존재한다고 생각하지는 않아. 그래서 resource.exists(); 로 확인할 수 있고, 읽을 수 있는지, 열려 있는지, 파일인지 디렉토리인지 확인할 수 있고, uri로 변환하기 url로 변환하기 등. 그리고 모든 리소스를 파일로 가져올 수 있는것도 아니야. 아무튼 이렇게 여러가지의 메소드가 있어.

그리고 리소스의 구현체로는 

```tex
UrlResource: java.net.URL 참고, 기본으로 지원하는 프로토콜 http, https, ftp, file, jar.
ClassPathResource: 지원하는 접두어 classpath:
FileSystemResource
ServletContextResource: 웹 애플리케이션 루트에서 상대 경로로 리소스 찾는다. (가장 많이 사용)
```

가 있다.

클래스패스는 우리가 사용했듯이 접두어로 쓰게 되면 클래스패스 리소스로 리졸빙을 하는거야.

왜 ServletContextResource 를 가장 많이 쓰냐면 읽어들이는 리소스 타입이 ApplicationContext 와 관련이 있기 때문임. 

```tex
Resource의 타입은 locaion 문자열과 ApplicationContext의 타입에 따라 결정 된다.
ClassPathXmlApplicationContext -> ClassPathResource
FileSystemXmlApplicationContext -> FileSystemResource
WebApplicationContext -> ServletContextResource (이건 interface 일거야. 구현체중 GenericWebApplicationContext 를 가장 많이 쓰게 될거야.)
```

아무튼 WebApplicationContext 이하로는 전부 ServletContextResource 쓰게 될거야. WebApplication Root 부터 찾게 되는거지.

그래서, 사실상 ClassPathXmlApplicationContext("application.xml"); 이런식으로 문자열을 그냥 주는 것은 자기가 사용할 context가 무엇인지에 따라 달라짐. 

그니까 만약에 ResourceLoader resourceLoader; 도 ApplicationContext  자체잖아?

```java
Resource resource = resourceLoader.getResource("classpath:test.txt"); //이렇게 뒤에 문자열을 주지않아도.
```

 ApplicationContext 가 ClassPathXmlApplicationContext 였다. 그러면 뒤에 아무런 문자열을 주지 않아도 이 문자열을 클래스 패스 기준으로 읽어오는거야. 

ApplicationContext의 타입에 상관없이 리소스 타입을 강제하려면 java.net.URL
접두어(+ classpath:)중 하나를 사용할 수 있다. (추천하는 방법)
○ classpath:me/whiteship/config.xml -> ClassPathResource
○ file:///some/resource/path/config.xml -> FileSystemResource

왜 추천하냐면 대부분 WebApplicationContext -> ServletContextResource 을 사용하겠지만, 이 리소스가 어디서 오는지. 이 스트링에 해당하는게 어디서 오는지 코드만 보고서는 알기 어려움. 근데 접두어가 있으면 좀 더 명시적이잖아. 써야 파일경로로 오는구나 클래스패스 기준으로 오는구나 알 수 있잖아.

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(resourceLoader.getClass()); //class org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext

        Resource resource = resourceLoader.getResource("classpath:test.txt");
        System.out.println(resource.getClass()); //class org.springframework.core.io.ClassPathResource
        System.out.println(resource.exists()); //true
        System.out.println(resource.getDescription()); //class path resource [test.txt]
        System.out.println(Files.readString(Path.of(resource.getURI()))); //hello spring

        Resource resource2 = resourceLoader.getResource("test.txt"); //classpath 라는 문자열을 지우면, 위에서 WebServerApplicationContext 나왔으니까 기본적으로 ServletContextResource 가 되어야 해.
        //그러면 ServletContextResource 는 WebApplicationRoot 즉 컨텍스트 루트, 컨텍스트 패스부터 찾게되는거야. 그 리소스를. (Tomcat started on port(s): 8080 (http) with context path '')
        //그런데 스프링부트가 띄워주는 내장형 톰캣에는 컨텍스트 패스가 지정되어 있지 않음. 따라서 리소스를 찾을 수 없음.
        System.out.println(resource2.getClass()); //class org.springframework.web.context.support.ServletContextResource
        System.out.println(resource2.exists()); // 그러니까 false 가 찍히겠지

        System.out.println(resource2.getDescription()); //ServletContext resource [/test.txt]
        System.out.println(Files.readString(Path.of(resource2.getURI()))); //그리고 여기서 오류가 날거야. 없는 파일을 읽으려고 했으니까.

        //이렇게 스프링부트 기반의 애플리케이션을 작성할 때, 보통(특히 jsp 를 사용하지 않을 때) 클래스 패스 기준으로 많은 리소스를 사용함.
        //따라서 클래스패스 접두어를 사용하는 것을 추천함. 그냥 리소스 이름만 적으면 ServletContextResource로 리졸빙이 된다는 것을 이해 해야 해.
    }
}
```

```xml
# resources 폴더에 test.txt 파일
hello spring
```



추가적으로 내용이 더 있지만, 

리소스를 빈으로도 등록할 때도 마찬가지임. 그냥 문자열만 쓰면 빈 설정 파일을 읽어들이는 ApplicationContext가 달라져. 그러니까 항상 classpath 를 명시해주는게 좋아. 파일경로를 읽을 때도 root 를 읽고 싶으면 file:/// 3개를 붙여줘야하고. 와일드 카드를 쓸 수도 있어. 클래스패스에 *을 쓸 수도 있고. 외의 내용은 레퍼런스를 참고하는게 좋아.