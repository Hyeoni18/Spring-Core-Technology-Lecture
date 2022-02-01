package hello.corespring;

import org.springframework.stereotype.Service;

@Service
public class SimpleEventService implements EventService{
//이건 그냥 타겟 객체야. 리얼 서브젝트에 해당하는 클래스야.
    @Override
    public void craeteEvent() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("created an event");
    }

    @Override
    public void publishEvent() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Published an event");
    }

    public void deleteEvent() {
        System.out.println("Delete an event");
    }
}
