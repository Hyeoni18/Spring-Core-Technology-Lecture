package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MyEventHandler {

    @EventListener //이벤트를 처리하는 메소드 위에다 Override 대신 추가해주면 됨.
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void handle(MyEvent event) { //메소드 명도 자유롭게 변경 가능.
        System.out.println(Thread.currentThread().toString());
        System.out.println("이벤트 받음. 테이터는 "+event.getDate());
    }

    @EventListener
    @Async
    public void handle(ContextRefreshedEvent event) {
        System.out.println("ContextRefreshedEvent ");
        var applicationContext = event.getApplicationContext();
        System.out.println(applicationContext);
    }

    @EventListener
    @Async
    public void handle(ContextClosedEvent event) {
        System.out.println("ContextClosedEvent ");
        var applicationContext = event.getApplicationContext();
        System.out.println(applicationContext);
    }
}
