package hello.corespring;

import java.lang.annotation.*;

//@Retention(RetentionPolicy.SOURCE) //어노테이션을 만들 때 주의할 점은, 여기 Retention 을 class 이상으로 줘야 해. 기본값이 클래스임. 해당 어노테이션이 없어도 클래스까지는 적용 된다는 얘기야.
//RetentionPolicy 라는 것은 이 어노네이션 정보를 얼마나 유지할 것인가. 임. 클래스는 클래스 파일까지 유지하겠다 라는 의미. 컴파일하고 클래스파일이 나오는데 그 안에도 이 어노테이션 정보가 남아 있다는 의미.
//근데 CLASS 가 아니라 SOURCE 로 변경하면. 컴파일 하고 사라짐. 그러니까 변경하면 안돼. 그리고 굳이 런타임으로 변경할 필요도 없고. 그러니까 그냥 기본값으로 두고 사용해도 괜찮아.
@Documented
@Target(ElementType.METHOD) //타겟정보는 메서드 라는 의미
public @interface PerfLogging {

}
