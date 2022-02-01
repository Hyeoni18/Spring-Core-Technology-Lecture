package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    EventService eventService; //인터페이스 타입으로 항상 주입을 받아야 해. 인터페이스가 있으면 인터페이스 타입으로 받는게 제일 좋아.
    //클라이언트는 이벤트 서비스를 주입받지만, 그 이벤트 서비스로 내가 Primary 라고 설정해둔 빈을 가져다가 쓰게 될거야.
    @Override
    public void run(ApplicationArguments args) throws Exception {
        eventService.craeteEvent();
        eventService.publishEvent();
        eventService.deleteEvent();
    }
}
