package hello.corespring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

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

    //빈에 있는 값을 가져와 찍을 수도 있음.
    @Value("#{sample.data}")
    int data;

    //메소드를 호출하는 기능도 있고
    //

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
        System.out.println(data);

        //ExpressionParser 사용
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("2 + 100"); //이미 parseExpression 안에 표현식이 들어있기에 "" 이렇게만 작성해주면 된다.
        Integer value = expression.getValue(Integer.class); //어떤 타입으로 가져올지 정하면
        System.out.println(value); //그 값이 찍히는거야.
        //이때, 스프링 Expression Language 도 컨버전 서비스를 사용하는거야. 해당하는 타입으로 변환할 때.
    }
}
