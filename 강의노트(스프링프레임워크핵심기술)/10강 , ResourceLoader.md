<h3>스프링 프레임워크 핵심 기술</h3>

10강 , ResourceLoader

리소스를 읽어오는 기능. 

AppRunner 생성

```java
@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:test.txt"); //파일이 없어도 됨
        System.out.println(resource.exists()); //있는지 없는지를 확인할거니까.
        //결과, false
    }
}
```

이제 파일을 생성함. resources 폴더에 test.txt 생성

그러면 이제 리소스 폴더에 있는 파일들이 빌드할 때 타겟 폴더 밑으로 들어가면서 클래스 패스에 들어감. 그러면 클래스 패스 기준으로 파일을 찾게됨. 왜냐하면 우리가 classpath 로 줬으니까. 다른 방법도 있는데 다음 시간에.

안에 내용을 찍으려면

Files.readString(Path.of(resource.getURI())) 로 찍어주면 됨. (java 11 기준, readString 메서드)

