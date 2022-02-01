package hello.corespring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerfAspect {

    @Around("bean(simpleEventService)") //bean 이 가지고 있는 모든 퍼블릭 메서드에 적용됨.
     public Object logPerf(ProceedingJoinPoint pip) throws Throwable {
        long begin = System.currentTimeMillis();
        Object retVal = pip.proceed();
        System.out.println(System.currentTimeMillis() - begin);
        return retVal;
    }

    //그냥 어떤 메서드가 실행되기 이전에 뭔가를 하고싶다.
    @Before("bean(simpleEventService)")
    public void hello() {
        System.out.println("HELLO");
    }
}
